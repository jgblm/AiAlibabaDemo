# Ch04 - Spring AI Embedding 示例

本模块展示如何在 Spring AI 中使用 Embedding（文本嵌入）功能。

## 什么是 Embedding？

Embedding 是将文本转换为向量（数字数组）的过程。这些向量捕捉了文本的语义信息，可以用于：
- **语义搜索**：根据含义而非关键词搜索
- **相似度计算**：判断两段文本的相似程度
- **RAG（检索增强生成）**：构建知识库问答系统
- **文本分类和聚类**：基于语义对文本进行分组

## 启动应用

```bash
cd ch04-embedding
mvn spring-boot:run
```

应用将在端口 8004 上启动。

## API 示例

### 1. 单个文本嵌入

将单个文本转换为向量：

```
GET http://localhost:8004/api/embedding/single?text=我喜欢编程
```

响应示例：
```json
{
  "text": "我喜欢编程",
  "vectorLength": 1024,
  "embeddings": "[0.12, -0.98, 0.33, ...]",
  "sampleValues": [0.12, -0.98, 0.33, ...]
}
```

### 2. 批量文本嵌入

一次性将多个文本转换为向量：

```
POST http://localhost:8004/api/embedding/batch
Content-Type: application/json

["我喜欢编程", "机器学习很有趣", "深度学习"]
```

### 3. 高级嵌入（带元数据）

获取更详细的嵌入信息，包括模型和使用情况：

```
GET http://localhost:8004/api/embedding/advanced?text=Spring+AI+很强大
```

### 4. 文本相似度比较

计算两段文本的语义相似度：

```
GET http://localhost:8004/api/similarity/compare?text1=我喜欢编程&text2=我热爱编码
```

响应示例：
```json
{
  "text1": "我喜欢编程",
  "text2": "我热爱编码",
  "similarity": 0.85,
  "interpretation": "高度相似"
}
```

### 5. 语义搜索

在文档集合中搜索与查询最相似的文档：

```
POST http://localhost:8004/api/similarity/search
Content-Type: application/json

{
  "query": "人工智能技术",
  "documents": [
    "机器学习是AI的重要分支",
    "深度学习用于图像识别和自然语言处理",
    "Python是一种流行的编程语言",
    "神经网络模拟人脑的工作方式",
    "今天天气真不错"
  ]
}
```

响应会按相似度从高到低排序返回结果。

## 核心概念

### EmbeddingModel

Spring AI 提供了 `EmbeddingModel` 接口，这是使用嵌入功能的核心抽象：

```java
@Autowired
private EmbeddingModel embeddingModel;

// 简单用法：单个文本
float[] embeddings = embeddingModel.embed("文本内容");

// 批量用法：多个文本
EmbeddingResponse response = embeddingModel.embedForResponse(List.of("文本1", "文本2"));
```

### 余弦相似度

本示例使用余弦相似度来计算两个向量之间的相似程度：
- 值范围：-1 到 1
- 1 表示完全相同
- 0 表示无关
- -1 表示完全相反

## 配置说明

在 `application.yml` 中配置 Embedding 模型：

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key
      base-url: https://your-api-endpoint
      embedding:
        options:
          model: text-embedding-v3
```

## 实际应用

Embedding 技术在以下场景中非常有用：

1. **智能搜索**：用户搜索"如何学习编程"，可以找到"编程入门教程"
2. **推荐系统**：根据用户喜欢的内容推荐相似内容
3. **去重**：检测重复或高度相似的内容
4. **情感分析**：将文本转换为向量后进行分类
5. **RAG 系统**：结合向量数据库构建知识问答系统

## 测试

使用项目中的 `requests.http` 文件可以直接在 IDE 中测试所有 API。
