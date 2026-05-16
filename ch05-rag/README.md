# Ch05 - Spring AI RAG 示例

本模块展示如何使用 Spring AI 实现简单的 RAG（检索增强生成）功能，使用内存存储向量数据。

## 什么是 RAG？

RAG（Retrieval-Augmented Generation，检索增强生成）是一种结合信息检索和文本生成的技术架构：

1. **检索（Retrieval）**：根据用户问题，从知识库中检索相关文档
2. **增强（Augmented）**：将检索到的文档作为上下文注入到 Prompt 中
3. **生成（Generation）**：大模型基于增强的上下文生成更准确的回答

### RAG 的优势

- ✅ **减少幻觉**：模型基于真实知识回答问题
- ✅ **知识可更新**：只需更新向量库，无需重新训练模型
- ✅ **来源可追溯**：可以知道答案来自哪些文档
- ✅ **成本低**：比微调模型便宜得多

## 启动应用

```bash
cd ch05-rag
mvn spring-boot:run
```

应用将在端口 8005 上启动。

## 核心组件

### 1. SimpleVectorStore（内存向量库）

Spring AI 提供的轻量级内存向量存储实现：
- ✅ 无需外部数据库
- ✅ 无需配置文件
- ✅ 适合快速测试和学习
- ❌ 重启后数据丢失（生产环境应使用专业向量数据库）

### 2. QuestionAnswerAdvisor

Spring AI 提供的开箱即用的 RAG 顾问组件：
- 自动将用户问题向量化
- 在向量库中检索相关文档
- 将检索结果注入到 Prompt 中
- 整个过程对调用方透明

### 3. VectorStore 接口

统一的向量存储操作接口，屏蔽不同向量数据库的差异：
- 支持 Chroma、Milvus、Redis、PGVector 等
- 提供标准化的 API：添加、删除、搜索
- 切换向量库时只需替换实现类

## API 示例

### 1. 基础 RAG 问答

最简单的 RAG 实现，使用默认配置：

```
GET http://localhost:8005/api/rag/ask?question=什么是Spring+AI
```

响应示例：
```json
{
  "question": "什么是Spring AI",
  "answer": "Spring AI 是 Spring 生态系统中的 AI 应用开发框架...",
  "type": "basic"
}
```

### 2. 高级 RAG 问答（自定义参数）

可以配置相似度阈值、返回文档数量等：

```
GET http://localhost:8005/api/rag/ask-advanced?question=RAG+是如何工作的
```

配置说明：
- `topK`: 返回最相关的 N 个文档
- `similarityThreshold`: 相似度阈值（0-1之间，越高要求越严格）

### 3. 带系统提示词的 RAG 问答

自定义系统提示词，控制模型的回答风格：

```
GET http://localhost:8005/api/rag/ask-with-system?question=解释一下+VectorStore
```

### 4. 向量库搜索测试

直接搜索相关文档，不经过大模型生成：

```
GET http://localhost:8005/api/rag/search?query=Spring+AI
```

响应示例：
```json
{
  "query": "Spring AI",
  "documentCount": 3,
  "documents": [
    {
      "content": "Spring AI 是 Spring 生态系统中的...",
      "id": "doc-id-1",
      "score": 0.85
    }
  ]
}
```

## 代码结构

```
ch05-rag/
├── pom.xml                                    # Maven 配置（包含 advisors-vector-store 依赖）
├── README.md                                  # 使用说明
├── requests.http                              # API 测试文件
└── src/main/
    ├── java/top/jgblm/ch05/
    │   ├── Ch05Application.java              # 主应用类
    │   ├── config/
    │   │   └── RagConfig.java                # RAG 配置（初始化向量库和文档）
    │   └── controller/
    │       └── RagController.java            # RAG 控制器
    └── resources/
        └── application.yml                    # 应用配置
```

## 关键代码解析

### 1. 初始化向量库并加载文档

```java
@Bean
public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    // 创建 SimpleVectorStore
    SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    
    // 准备文档
    List<Document> documents = List.of(
        new Document("Spring AI 是..."),
        new Document("RAG 是..."),
        // ... 更多文档
    );
    
    // 添加到向量库（自动完成 Embedding + 存储）
    vectorStore.add(documents);
    
    return vectorStore;
}
```

### 2. 使用 QuestionAnswerAdvisor

```java
// 创建 Advisor
QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
    .searchRequest(SearchRequest.builder()
        .topK(3)                    // 返回最相关的 3 个文档
        .similarityThreshold(0.5)   // 相似度阈值
        .build())
    .build();

// 调用 ChatClient，挂载 Advisor
String answer = chatClient.prompt()
    .user(question)
    .advisors(advisor)  // 挂载 RAG Advisor
    .call()
    .content();
```

## RAG 工作流程

当用户提问时，`QuestionAnswerAdvisor` 在幕后完成了以下步骤：

1. **向量化**：将用户问题通过 `EmbeddingModel` 转换为向量
2. **检索**：在 `SimpleVectorStore` 中进行余弦相似度检索
3. **增强**：将检索到的文档片段拼接到用户 Prompt 中
4. **生成**：将增强后的 Prompt 发给大模型，生成基于上下文的回答

示例 Prompt 结构：
```
【上下文信息】
Spring AI 是 Spring 生态系统中的 AI 应用开发框架...
它支持多种大语言模型...

【用户问题】
什么是 Spring AI？
```

## 示例文档

本模块预置了 6 个关于 Spring AI 和 RAG 的示例文档：
1. Spring AI 介绍
2. RAG 技术说明
3. SimpleVectorStore 介绍
4. Embedding 概念
5. QuestionAnswerAdvisor 说明
6. VectorStore 接口介绍

你可以尝试询问这些问题：
- "什么是 Spring AI？"
- "RAG 是如何工作的？"
- "SimpleVectorStore 有什么特点？"
- "解释一下 VectorStore"
- "QuestionAnswerAdvisor 的作用是什么？"

## 测试

使用项目中的 `requests.http` 文件可以直接在 IDE 中测试所有 API。

## 生产环境建议

虽然本模块使用 `SimpleVectorStore` 进行演示，但在生产环境中应该：

1. **使用专业向量数据库**：
   - Milvus
   - Chroma
   - PGVector (PostgreSQL)
   - Redis Vector
   - Weaviate

2. **文档来源**：
   - 从 PDF、Word 等文件加载
   - 从数据库查询
   - 从 API 获取
   - 网页爬取

3. **文档处理**：
   - 智能分块（避免切断语义）
   - 提取元数据
   - 去重和清洗

4. **性能优化**：
   - 缓存检索结果
   - 异步处理
   - 批量索引

## 下一步

学习了基础的 RAG 后，你可以探索：
- 多轮对话记忆 + RAG
- 文档来源追溯
- 重排序（Re-ranking）优化
- 混合检索（关键词 + 向量）
- Agent + RAG 组合
