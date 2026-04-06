package com.example.yuaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailSendToolTest {

    @Autowired
    private EmailSendTool emailSendTool;
    private static final String TEST_TO = "1298987274@qq.com";
    private static final String TEST_SUBJECT = "wblyanzhen,新年快乐！";
    private static final String TEST_CONTENT = "祝我自己新年快乐！好运不断，天天有喜";
    @Test
    void sendTextEmail() {
        String result = emailSendTool.sendTextEmail(TEST_TO, TEST_SUBJECT, TEST_CONTENT);
        Assertions.assertNotNull(result);
    }
}