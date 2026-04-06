package com.example.yuaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "AI超级智能体.txt";
        String result = tool.readFile(fileName);
        Assertions.assertNotNull(result);
    }

    @Test
    void writeFile() {
        String fileName = "AI超级智能体.txt";
        String content = "本人正在学习ai智能体";
        FileOperationTool tool = new FileOperationTool();
        String result = tool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
    }
}