# ai超级智能小助手项目

## 项目简介

ai超级智能小助手 是一个基于 Spring Boot 和 Vue 3 构建的智能代理系统，集成了多种 AI 能力和工具，提供了恋爱助手等应用场景。

### 主要功能

- **AI Agent 框架**：基于 ReAct 模式的智能代理实现
- **恋爱助手**：提供恋爱相关问题的智能回答
- **工具集成**：支持邮件发送、文件操作、PDF 生成、网页抓取、网络搜索等多种工具
- **RAG 能力**：集成检索增强生成，提高回答质量
- **多模型支持**：支持阿里云百炼、灵积大模型等

## 项目结构

```
yu-ai-agent-all/
├── yu-ai-agent/          # 后端代码 (Spring Boot)
│   ├── src/              # 源代码
│   ├── pom.xml           # Maven 依赖配置
│   └── Dockerfile        # Docker 构建文件
├── yu-ai-agent-frontend/ # 前端代码 (Vue 3)
│   ├── src/              # 源代码
│   ├── package.json      # npm 依赖配置
│   └── Dockerfile        # Docker 构建文件
└── README.md             # 项目说明文档
```

## 技术栈

### 后端
- **Spring Boot 3.4.4**：应用框架
- **Java 21**：开发语言
- **PostgreSQL**：向量数据库
- **Spring AI**：AI 集成框架
- **LangChain4J**：语言链框架
- **阿里云百炼**：大模型服务
- **Knife4j**：API 文档

### 前端
- **Vue 3**：前端框架
- **Vue Router**：路由管理
- **Axios**：HTTP 客户端
- **Vite**：构建工具

## 快速开始

### 前提条件

- **JDK 21** 或更高版本
- **Maven 3.6** 或更高版本
- **Node.js 16** 或更高版本
- **PostgreSQL** 数据库
- **阿里云 API 密钥**（用于大模型服务）
- **QQ 邮箱授权码**（用于邮件发送功能）

### 后端配置

1. 配置数据库连接
   - 编辑 `yu-ai-agent/src/main/resources/application.yaml` 文件
   - 修改数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://your-database-host/yu_ai_agent
       username: your-username
       password: your-password
   ```

2. 配置邮箱服务
   - 编辑 `yu-ai-agent/src/main/resources/application.yaml` 文件
   - 修改邮箱配置：
   ```yaml
   spring:
     mail:
       host: smtp.qq.com
       port: 465
       username: your-qq-email@qq.com
       password: your-qq-email-authorization-code
   ```

3. 配置 API 密钥
   - 创建 `application-local.yaml` 文件，添加搜索 API 密钥：
   ```yaml
   search-api:
     api-key: your-search-api-key
   ```

### 前端配置

1. 安装依赖
   ```bash
   cd yu-ai-agent-frontend
   npm install
   ```

2. 配置 API 地址
   - 根据后端服务地址修改前端 API 调用地址

## 运行项目

### 运行后端

```bash
cd yu-ai-agent
mvn spring-boot:run
```

后端服务将在 `http://localhost:8426/api` 启动

### 运行前端

```bash
cd yu-ai-agent-frontend
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

## API 文档

后端提供了 Swagger API 文档，可通过以下地址访问：

- **Swagger UI**：`http://localhost:8426/api/swagger-ui.html`
- **Knife4j UI**：`http://localhost:8426/api/doc.html`

## 项目部署

### 后端部署

#### 使用 Maven 构建

```bash
cd yu-ai-agent
mvn clean package
java -jar target/yu-ai-agent-0.0.1-SNAPSHOT.jar
```

#### 使用 Docker 构建

```bash
cd yu-ai-agent
docker build -t yu-ai-agent .
docker run -p 8426:8426 --name yu-ai-agent yu-ai-agent
```

### 前端部署

#### 使用 npm 构建

```bash
cd yu-ai-agent-frontend
npm run build
# 构建产物将在 dist 目录
```

#### 使用 Docker 构建

```bash
cd yu-ai-agent-frontend
docker build -t yu-ai-agent-frontend .
docker run -p 80:80 --name yu-ai-agent-frontend yu-ai-agent-frontend
```

## 功能模块

### 1. AI Agent

- **ReActAgent**：基于 ReAct 模式的智能代理
- **ToolCallAgent**：支持工具调用的代理
- **YuManus**：自定义智能代理实现

### 2. 恋爱助手应用

- 基于 RAG 技术，提供恋爱相关问题的智能回答
- 支持单身、恋爱、已婚等不同场景的问题

### 3. 工具集成

- **EmailSendTool**：邮件发送工具
- **FileOperationTool**：文件操作工具
- **PDFGenerationTool**：PDF 生成工具
- **WebScrapingTool**：网页抓取工具
- **WebSearchTool**：网络搜索工具
- **TerminalOperationTool**：终端操作工具
- **ResourceDownloadTool**：资源下载工具

### 4. RAG 能力

- 基于 PostgreSQL 向量存储
- 支持 Markdown 文档加载
- 提供语义搜索和相似度匹配

## 开发指南

### 后端开发

1. **添加新工具**：在 `yu-ai-agent/src/main/java/com/example/yuaiagent/tools/` 目录下创建新的工具类
2. **添加新应用**：在 `yu-ai-agent/src/main/java/com/example/yuaiagent/app/` 目录下创建新的应用类
3. **添加新代理**：在 `yu-ai-agent/src/main/java/com/example/yuaiagent/agent/model/` 目录下创建新的代理类

### 前端开发

1. **添加新页面**：在 `yu-ai-agent-frontend/src/views/` 目录下创建新的页面组件
2. **添加新路由**：在 `yu-ai-agent-frontend/src/router/index.js` 文件中添加新的路由配置
3. **添加新组件**：在 `yu-ai-agent-frontend/src/components/` 目录下创建新的组件

## 测试

### 后端测试

```bash
cd yu-ai-agent
mvn test
```

### 前端测试

```bash
cd yu-ai-agent-frontend
npm test
```

## 常见问题

1. **数据库连接失败**：检查数据库地址、用户名和密码是否正确
2. **邮件发送失败**：检查邮箱配置和授权码是否正确
3. **API 调用失败**：检查 API 密钥是否正确配置
4. **前端无法连接后端**：检查后端服务是否启动，以及前端 API 地址是否正确

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系项目维护者。
