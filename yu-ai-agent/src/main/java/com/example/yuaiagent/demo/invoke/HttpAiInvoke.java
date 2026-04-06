package com.example.yuaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpAiInvoke {

    // 替换为你的DashScope API Key
    private static final String DASHSCOPE_API_KEY = TestApiKey.API_KEY;
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public static String callQwenPlus(String userQuestion) {
        // 构建请求体参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-plus");

        // 构建input.messages数组
        List<Map<String, String>> messages = new ArrayList<>();
        // system消息
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are a helpful assistant.");
        messages.add(systemMsg);
        // user消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userQuestion);
        messages.add(userMsg);

        Map<String, Object> input = new HashMap<>();
        input.put("messages", messages);
        requestBody.put("input", input);

        // 构建parameters参数
        Map<String, String> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);

        // 发送POST请求（使用Hutool的JSONUtil转JSON字符串）
        try (HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + DASHSCOPE_API_KEY)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody)) // 改用Hutool的JSON工具
                .execute()) {

            if (response.getStatus() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("请求失败，状态码：" + response.getStatus() + "，响应：" + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("调用API失败", e);
        }
    }

    // 测试
    public static void main(String[] args) {
        try {
            String result = callQwenPlus("你是谁？");
            System.out.println("响应结果：" + result);
            // 可选：用Hutool解析响应
            JSONObject resultJson = JSONUtil.parseObj(result);
            System.out.println("解析后的回复：" + resultJson.getByPath("output.choices[0].message.content"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}