package com.example.yuaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://img1.baidu.com/it/u=1477866272,3650610878&fm=253&app=138&f=JPEG?w=1427&h=800";
        String fileName = "logo.jpg";
        String result = tool.downloadResource(url, fileName);
        Assertions.assertNotNull(result);
    }
}