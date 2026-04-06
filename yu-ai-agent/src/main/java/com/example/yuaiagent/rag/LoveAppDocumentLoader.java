package com.example.yuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *文档加载器类
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 读取所有 Markdown 文档并转换为 Document 列表
     * @return
     */
    public List<Document> loadMarkdownDocuments() {
        List<Document> documents = new ArrayList<>();
        try {
            // 方法1：使用ResourcePatternResolver
            log.info("Method 1: Using ResourcePatternResolver");
            try {
                Resource[] resources = resourcePatternResolver.getResources("classpath*:document/*.md");
                log.info("Found {} resources with classpath*:document/*.md", resources.length);
                for (Resource resource : resources) {
                    log.info("Resource URL: {}", resource.getURL());
                    String filename = resource.getFilename();
                    MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                            .withHorizontalRuleCreateDocument(true)
                            .withIncludeBlockquote(false)
                            .withIncludeCodeBlock(false)
                            .withAdditionalMetadata("filename", filename)
                            .build();
                    MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                    documents.addAll(reader.get());
                }
            } catch (Exception e) {
                log.warn("Error with ResourcePatternResolver: {}", e.getMessage());
            }
            
            // 方法2：使用ClassLoader
            log.info("Method 2: Using ClassLoader");
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                java.io.File documentDir = new java.io.File(classLoader.getResource("document").toURI());
                log.info("Document directory exists: {}", documentDir.exists());
                log.info("Document directory path: {}", documentDir.getAbsolutePath());
                if (documentDir.exists() && documentDir.isDirectory()) {
                    java.io.File[] mdFiles = documentDir.listFiles((dir, name) -> name.endsWith(".md"));
                    if (mdFiles != null) {
                        log.info("Found {} md files in directory", mdFiles.length);
                        for (java.io.File mdFile : mdFiles) {
                            log.info("MD file path: {}", mdFile.getAbsolutePath());
                            org.springframework.core.io.FileSystemResource fileResource = new org.springframework.core.io.FileSystemResource(mdFile);
                            String filename = mdFile.getName();
                            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                                    .withHorizontalRuleCreateDocument(true)
                                    .withIncludeBlockquote(false)
                                    .withIncludeCodeBlock(false)
                                    .withAdditionalMetadata("filename", filename)
                                    .build();
                            MarkdownDocumentReader reader = new MarkdownDocumentReader(fileResource, config);
                            documents.addAll(reader.get());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Error with ClassLoader: {}", e.getMessage());
            }
            
            // 方法3：尝试具体文件路径
            log.info("Method 3: Trying specific file paths");
            try {
                String[] filenames = {
                    "恋爱常见问题和回答 - 单身篇.md",
                    "恋爱常见问题和回答 - 已婚篇.md",
                    "恋爱常见问题和回答 - 恋爱篇.md"
                };
                for (String filename : filenames) {
                    try {
                        Resource resource = resourcePatternResolver.getResource("classpath:document/" + filename);
                        if (resource.exists()) {
                            log.info("Found specific file: {}", filename);
                            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                                    .withHorizontalRuleCreateDocument(true)
                                    .withIncludeBlockquote(false)
                                    .withIncludeCodeBlock(false)
                                    .withAdditionalMetadata("filename", filename)
                                    .build();
                            MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                            documents.addAll(reader.get());
                        } else {
                            log.warn("Specific file not found: {}", filename);
                        }
                    } catch (Exception e) {
                        log.warn("Error with specific file {}: {}", filename, e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Error with specific file paths: {}", e.getMessage());
            }
            
            log.info("Total documents loaded: {}", documents.size());
        } catch (Exception e) {
            log.error("Unexpected error in loadMarkdownDocuments: {}", e.getMessage());
        }
        return documents;
    }
}
