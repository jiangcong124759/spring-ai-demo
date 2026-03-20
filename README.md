# Spring AI Demo 2

一个基于 Spring Boot 3、Spring AI Alibaba、DashScope、Redis、SQLite 的 AI 对话示例项目。

这个仓库当前已经覆盖了三类能力：

- AI 文本对话、流式对话、结构化实体返回
- 基于 Redis 的会话记忆与会话详情查询
- 基于 SQLite 的会话管理、知识库管理

同时仓库里还带了一个静态页面示例：

- `GET /snake.html`

项目目前处于“可运行的原型阶段”。核心对话链路和知识库基础 CRUD 已经落地，但知识文件上传、解析、切片、检索增强生成还没有形成完整闭环。

## 技术栈

- Java 17
- Spring Boot 3.5.0
- Spring AI 1.0.0-M2
- Spring AI Alibaba 1.0.0-M2.1
- DashScope / 通义千问
- Redis
- SQLite
- MyBatis-Plus 3.5.5
- SpringDoc OpenAPI / Swagger UI
- Hutool

## 当前能力

### AI 对话接口

- `GET /chat`
  同步文本对话
- `GET /stream`
  流式文本对话
- `GET /entity/chat`
  把模型输出映射为 Java record，再转成 JSON 返回
- `GET /memory/stream`
  记忆对话实验接口
- `GET /memory/redis/stream`
  Redis 记忆对话接口

其中 Redis 记忆由自定义 `RedisChatMemory` 组件实现，消息按 `chat:{conversationId}` 存储。

### 会话管理接口

前缀：`/api/v1/conversations`

- `GET /api/v1/conversations`
  查询会话列表
- `GET /api/v1/conversations/getByConversationId/{id}`
  查询会话详情，并从 Redis 读取历史消息
- `POST /api/v1/conversations/create`
  创建会话
- `POST /api/v1/conversations/{id}/messages`
  通过 SSE 推送模型响应
- `DELETE /api/v1/conversations/{id}`
  删除会话，并同步清理 Redis 记忆

`POST /api/v1/conversations/{id}/messages` 当前会推送以下事件类型：

- `thinking`
- `source`
- `content`
- `done`
- `error`

说明：

- `source` 事件目前是示例数据，不是真实检索结果
- 会话详情接口当前会返回该会话在 Redis 中保存的全部历史消息

### 知识库管理接口

前缀：`/api/v1/knowledge-bases`

- `GET /api/v1/knowledge-bases`
  查询知识库列表
- `GET /api/v1/knowledge-bases/{id}`
  查询知识库详情
- `POST /api/v1/knowledge-bases`
  创建知识库
- `PUT /api/v1/knowledge-bases/{id}`
  更新知识库名称和描述
- `DELETE /api/v1/knowledge-bases/{id}`
  删除知识库，并删除该知识库下的文件记录

知识库列表和详情返回 `KnowledgeBaseVO`，包含：

- `id`
- `name`
- `description`
- `fileCount`
- `totalChunks`
- `createdAt`
- `updatedAt`

当前 `fileCount` 和 `totalChunks` 是通过查询 `knowledge_files` 表实时统计出来的。

### 静态页面

- `GET /snake.html`

一个纯原生 JavaScript 的贪吃蛇小游戏，用来验证 Spring Boot 静态资源访问是否正常。

## 项目结构

```text
src/main/java/com/example/demo
├── config
│   ├── ClientConfig.java          # ChatClient 配置
│   ├── RedisChatMemory.java       # 自定义 Redis ChatMemory
│   ├── MybatisConfig.java         # MyBatis-Plus 配置
│   └── OpenApiConfig.java         # Swagger / OpenAPI 配置
├── controller
│   ├── AiChatController.java
│   ├── AiChatMemoryController.java
│   ├── AiChatMemoryRedisController.java
│   └── AiChatReturnEntityController.java
├── controller/api
│   ├── ConversationsController.java
│   ├── KnowledgeBasesController.java
│   ├── KnowledgeFilesController.java
│   └── MessagesController.java
├── dao
├── entity
│   ├── dto
│   └── vo
└── service

src/main/resources
├── application.yml
└── static/snake.html
```

## 数据存储

### SQLite

默认使用本地 SQLite 文件：

- `my-knowledge-db`

当前已存在表：

- `conversations`
- `messages`
- `knowledge_bases`
- `knowledge_files`

### Redis

Redis 当前主要用于保存聊天上下文，而不是会话主记录。

- 会话元数据：SQLite
- 聊天记忆：Redis

## 快速启动

### 1. 环境准备

- JDK 17 或更高版本
- 可用的 DashScope API Key
- 可连接的 Redis 实例

### 2. 配置说明

当前 `src/main/resources/application.yml` 里已经包含：

- DashScope API Key
- Redis 地址和密码
- SQLite 数据源
- Swagger 地址

但这类敏感信息不建议继续硬编码在仓库里。更合适的做法是使用：

- 环境变量
- 本地私有配置文件
- 启动参数覆盖

建议至少外置以下配置：

```yaml
spring:
  data:
    redis:
      host: your-redis-host
      port: 6379
      password: your-redis-password
  ai:
    dashscope:
      api-key: your-dashscope-api-key
      chat:
        options:
          model: qwen-max
```

### 3. 启动项目

仓库中的 `mvnw` 当前没有执行权限，建议直接这样启动：

```bash
sh ./mvnw spring-boot:run
```

启动后可访问：

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`
- `http://localhost:8080/snake.html`

## 测试

执行测试：

```bash
sh ./mvnw test
```

当前只有一个 Spring Boot 上下文加载测试，但在当前代码状态下测试可通过。

## 接口示例

### 基础文本聊天

```bash
curl "http://localhost:8080/chat?input=你好，请介绍一下Spring AI"
```

### 流式聊天

```bash
curl "http://localhost:8080/stream?input=请分三点介绍Spring AI"
```

### Redis 记忆聊天

```bash
curl "http://localhost:8080/memory/redis/stream?prompt=我叫张三&chatId=demo-001"
curl "http://localhost:8080/memory/redis/stream?prompt=你还记得我叫什么吗&chatId=demo-001"
```

### 创建会话

```bash
curl -X POST "http://localhost:8080/api/v1/conversations/create" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "RAG 测试会话",
    "knowledgeBaseIds": "kb_demo_001"
  }'
```

### 发送 SSE 会话消息

```bash
curl -N -X POST "http://localhost:8080/api/v1/conversations/{conversationId}/messages" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "请解释一下什么是 RAG",
    "stream": true
  }'
```

### 查询知识库列表

```bash
curl "http://localhost:8080/api/v1/knowledge-bases"
```

### 创建知识库

```bash
curl -X POST "http://localhost:8080/api/v1/knowledge-bases" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spring AI 文档库",
    "description": "用于演示知识库基础管理"
  }'
```

### 更新知识库

```bash
curl -X PUT "http://localhost:8080/api/v1/knowledge-bases/kb_xxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spring AI 文档库（更新）",
    "description": "更新后的描述"
  }'
```

## 关键实现

### `ChatClient` 配置

`src/main/java/com/example/demo/config/ClientConfig.java`

统一创建 `ChatClient`，默认挂载：

- `SimpleLoggerAdvisor`
- `MessageChatMemoryAdvisor`

### 自定义 Redis 记忆

`src/main/java/com/example/demo/config/RedisChatMemory.java`

实现了 Spring AI 的 `ChatMemory` 接口，负责：

- 写入消息
- 读取最近消息
- 清理会话记忆
- 区分 `user / assistant / system` 消息类型

### 会话服务

`src/main/java/com/example/demo/service/impl/ConversationsServiceImpl.java`

当前负责：

- 创建会话
- 查询会话详情
- 读取 Redis 历史消息
- SSE 推送模型响应
- 删除会话时同步清理 Redis

### 知识库服务

`src/main/java/com/example/demo/service/impl/KnowledgeBasesServiceImpl.java`

当前负责：

- 生成知识库 ID
- 创建知识库
- 查询知识库列表
- 统计文件数量和总分块数
- 更新知识库
- 删除知识库及其文件记录

## 当前限制

- `KnowledgeFilesController` 仍是空实现，占位为主。
- `MessagesController` 仍是空实现，占位为主。
- `ConversationsController` 里的 `PUT` 更新接口尚未实现。
- `AiChatMemoryController` 没有像 Redis 版本那样显式挂载 `MessageChatMemoryAdvisor`，更适合作为实验接口看待。
- SSE 中的 `source` 事件目前是写死的模拟数据。
- 还没有文件上传、切片、向量化、召回、真正的 RAG 链路。
- 测试覆盖率较低，目前只有上下文加载测试。

## 后续建议

如果下一步要把它从 Demo 推进到可交付项目，建议优先做这几件事：

1. 把 Redis 和 DashScope 敏感配置外置。
2. 补齐知识文件上传与解析流程。
3. 接入真实检索结果，而不是固定 `source` 事件。
4. 补充 `knowledgeFiles` 和 `messages` 的完整 API。
5. 为 SSE 和知识库流程增加集成测试。
