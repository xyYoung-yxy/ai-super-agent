package com.example.yuaiagent.controller;

import com.example.yuaiagent.agent.model.YuManus;
import com.example.yuaiagent.app.LoveApp;
import com.example.yuaiagent.config.SseConnectionManager;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private SseConnectionManager sseConnectionManager;

    /**
     * 同步调用 AI 恋爱大师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId){
        return loveApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用 AI 恋爱大师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId){
        return loveApp.doChatByStream(message, chatId);
    }


    /**
     * SSE 流式调用 AI 恋爱大师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId){
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 恋爱大师应用
     * 新增停止检查逻辑
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/server_emitter")
    public SseEmitter doChatWithLoveAppServerSseEmitter(String message, String chatId, String requestId){
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3分钟超时

        // 注册连接，获取停止标识
        AtomicBoolean stopFlag = sseConnectionManager.registerConnection(requestId);

        // 获取 Flux响应式数据流并且直接通过订阅推送给 SseEmitter，每次发送前检查是否停止
        loveApp.doChatByStream(message, chatId)
                .takeWhile(chunk -> !stopFlag.get()) // 逐个检查停止标识，为 true则立即中断数据流
                .subscribe(
                        // 正常发送数据流
                        chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                },
                        e -> {
                            sseConnectionManager.markConnectionStopped(requestId);
                            sseConnectionManager.removeConnection(requestId);
                            sseEmitter.completeWithError(e);
                        },
                        () -> {
                            sseConnectionManager.removeConnection(requestId);
                            sseEmitter.complete();
                        });

        // 兜底处理
        sseEmitter.onTimeout(()->{
            sseConnectionManager.markConnectionStopped(requestId);
            sseConnectionManager.removeConnection(requestId);
            sseEmitter.complete();
        });
        sseEmitter.onError(e -> {
            sseConnectionManager.markConnectionStopped(requestId);
            sseConnectionManager.removeConnection(requestId);
            sseEmitter.complete();
        });
        sseEmitter.onCompletion(()-> sseConnectionManager.removeConnection(requestId));

        // 返回
        return sseEmitter;
    }

    /**
     * 流式调用 Manus 超级智能体
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message, String requestId){
        YuManus yuManus = new YuManus(allTools, dashscopeChatModel, sseConnectionManager);
        return yuManus.runStream(message, requestId);
    }


    /**
     * 停止接口
     * @param requestId
     * @return
     */
    @PostMapping("/stop")
    public boolean stopAIReply(@RequestParam String requestId){
        return sseConnectionManager.markConnectionStopped(requestId);
    }

}
