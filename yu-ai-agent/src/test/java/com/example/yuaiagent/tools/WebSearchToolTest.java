package com.example.yuaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String searchApiKey;
    @Test
    void searchWeb() {
        WebSearchTool tool = new WebSearchTool(searchApiKey);
        String query = "学习Java的路线";
        String result = tool.searchWeb(query);
        Assertions.assertNotNull(result);
    }
}