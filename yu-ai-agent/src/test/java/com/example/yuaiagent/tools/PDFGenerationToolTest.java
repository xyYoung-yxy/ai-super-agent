package com.example.yuaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String content = "wblyanzhen，正在学习超级智能体";
        String fileName = "智能体.pdf";
        String result = tool.generatePDF(content, fileName);
        Assertions.assertNotNull(result);
    }
}