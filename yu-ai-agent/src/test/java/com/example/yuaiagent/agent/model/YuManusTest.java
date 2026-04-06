package com.example.yuaiagent.agent.model;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class YuManusTest {

    @Resource
    private YuManus yuManus;

    @Test
    void run(){
        String userPrompt = """  
                我的另一半居住在广州花都区，请帮我找到 5 公里内合适的约会地点， 
                并结合一些网络图片，制定一份详细的约会计划，需要帮我下载图片，
                并以 PDF 格式输出""";
        String result = yuManus.run(userPrompt);
        Assertions.assertNotNull(result);
    }
}