package com.example.yuaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Scanner;

/**
 * AI 询问人类的工具
 */
public class AskHumanTool {

    @Tool(description = "Use this tool to ask human for help.")
    public String askHuman(
            @ToolParam(description = "The question you want to ask human.") String inquire){

        try {
            System.out.println("\n=== AI 向你发起询问 ===");
            System.out.println("问题：" + inquire);
            System.out.println("请输入你的回答：");
            Scanner scanner = new Scanner(System.in);
            String humanAnswer = scanner.nextLine().trim();
            if (humanAnswer.isEmpty()){
                return "humanAnswer is empty.";
            }
            return humanAnswer;
        } catch (Exception e) {
            return "Error asking human:" + e.getMessage();
        }

    }
}
