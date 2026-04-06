package com.example.yuaiagent.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 维护 SseEmitter连接的停止状态，线程安全
 */
@Component
public class SseConnectionManager {

    // requestId --> 停止标识, AtomicBoolean保证多线程安全
    private final Map<String, AtomicBoolean> stopFlags = new ConcurrentHashMap<>();

    // 注册连接，生成停止标识
    public AtomicBoolean registerConnection(String requestId) {
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        stopFlags.put(requestId, stopFlag);
        return stopFlag;
    }

    // 标记连接为停止状态
    public boolean markConnectionStopped(String requestId) {
        AtomicBoolean stopFlg = stopFlags.get(requestId);
        if (stopFlg != null) {
            stopFlg.set(true);
            return true;
        }
        return false;
    }

    // 移除连接，清理资源
    public void removeConnection(String requestId) {
        stopFlags.remove(requestId);
    }

}
