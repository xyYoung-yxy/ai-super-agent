package com.example.yuaiagent.agent.model;

import com.example.yuaiagent.config.SseConnectionManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基础 agent类，提供状态转换、内存管理和基于步骤的执行循环的基础功能，
 * 子类必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态管理
    private AgentState state = AgentState.IDLE;

    // 执行步骤控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM大模型
    private ChatClient chatClient;

    // memory记忆（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    private final SseConnectionManager sseConnectionManager;


    public BaseAgent(SseConnectionManager sseConnectionManager) {
        this.sseConnectionManager = sseConnectionManager;
    }

    /**
     * 运行代理
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // 1、基础校验
        if(this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state:" + this.state);
        }
        if (StringUtils.isBlank(userPrompt)){
            throw new RuntimeException("Cannot run agent with empty userPrompt");
        }
        // 2、执行，更改状态
        state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();

        try {
            // 执行循环
            for(int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step" + stepNumber + "/" + maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step" + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否达到最大步骤
            if (currentStep >= maxSteps){
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 3、清理资源
            this.cleanup();
        }

    }

    /**
     * 运行代理 （流式输出）
     * 加入停止检查逻辑
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt, String requestId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L); // 5分钟超时

        // 获取停止标识
        AtomicBoolean stopFlag = this.sseConnectionManager.registerConnection(requestId);

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(()->{
            // 1、基础校验
            try {
                if(this.state != AgentState.IDLE) {
                    sseEmitter.send("错误：无法从该状态运行代理：" + this.state);
                    sseEmitter.complete();
                    sseConnectionManager.removeConnection(requestId);    // 清理标识
                    return;
                }
                if (StringUtils.isBlank(userPrompt)){
                    sseEmitter.send("错误：不能使用空提示词运行代理");
                    sseEmitter.complete();
                    sseConnectionManager.removeConnection(requestId);    // 清理标识
                    return;
                }
            } catch (IOException e) {
                sseConnectionManager.removeConnection(requestId);  // 异常时清理
                sseEmitter.completeWithError(e);
                return;
            }
            // 2、执行，更改状态
            state = AgentState.RUNNING;
            // 记录消息上下文
            messageList.add(new UserMessage(userPrompt));
            // 保存结果列表
            List<String> results = new ArrayList<>();

            try {
                // 执行循环
                for(int i = 0; i < maxSteps && state != AgentState.FINISHED && !stopFlag.get(); i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step" + stepNumber + "/" + maxSteps);

                    // 每步执行前再次检查停止标识（避免进入循环后收到停止命令）
                    if (stopFlag.get()){
                        sseEmitter.send("执行中断：用户手动停止代理运行（当前执行到第" + stepNumber + "步）");
                        break;
                    }

                    // 单步执行
                    String stepResult = step();
                    String result = "Step" + stepNumber + ": " + stepResult;
                    results.add(result);
                    // 输出当前每一步的结果到 SSE
                    sseEmitter.send(result);

                    // 每步执行后检查停止标识（及时响应停止命令）
                    if (stopFlag.get()){
                        sseEmitter.send("执行中断：用户手动停止代理运行（当前执行到第" + stepNumber + "步）");
                        break;
                    }
                }
                // 终止原因判断 （手动停止、达到最大步骤）
                if (stopFlag.get()){
                    state = AgentState.FINISHED;
                    sseEmitter.send("执行终止：用户手动停止（共执行" + currentStep + "步）");
                } else if (currentStep >= maxSteps) {
                    // 检查是否达到最大步骤
                        state = AgentState.FINISHED;
                        sseEmitter.send("执行结束：达到最大步骤 (" + maxSteps + ")");
                }

                // 正常完成
                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("Error executing agent", e);
                try {
                    sseEmitter.send("执行错误：" + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }

            } finally {
                // 3、清理资源
                this.cleanup();
                sseConnectionManager.removeConnection(requestId);    // 清理标识
            }
        });

        // 设置超时回调
        sseEmitter.onTimeout(()->{
            this.state = AgentState.ERROR;
            sseConnectionManager.markConnectionStopped(requestId);   // 标记超时停止
            sseConnectionManager.removeConnection(requestId);
            this.cleanup();
            log.warn("SSE connection timeout");
        });

        // 设置完成回调
        sseEmitter.onCompletion(()->{
            if(this.state == AgentState.RUNNING){
                this.state = AgentState.FINISHED;
            }
            sseConnectionManager.removeConnection(requestId);    // 清理标识
            this.cleanup();
            log.info("SSE connection completed");
        });

        return sseEmitter;
    }

    /**
     * 清理资源
     */
    protected void cleanup() {
    }

    /**
     * 定义单个步骤
     * @return
     */
    public abstract String step();
}
