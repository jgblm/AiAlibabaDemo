package top.jgblm.ch05.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * RAG 配置类
 * 初始化内存向量库并加载示例文档
 */
@Configuration
public class RagConfig {

    /**
     * 创建内存向量库并加载示例文档
     * SimpleVectorStore 是 Spring AI 提供的基于内存的向量存储实现
     * 无需外部数据库，适合快速测试和原型开发
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // 创建 SimpleVectorStore
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        
        // 准备示例文档（实际项目中可以从 PDF、数据库、API 等来源加载）
        List<Document> documents = List.of(
            new Document("Spring AI 是 Spring 生态系统中的 AI 应用开发框架，为 Java 开发者提供了统一的 AI 模型访问接口。" +
                        "它支持多种大语言模型（如 OpenAI、Azure OpenAI、Ollama 等），并提供了 ChatClient、EmbeddingModel 等核心抽象。"),
            
            new Document("RAG（Retrieval-Augmented Generation，检索增强生成）是一种结合信息检索和文本生成的技术。" +
                        "它通过从知识库中检索相关文档，然后将检索结果作为上下文提供给大模型，从而生成更准确、更有依据的回答。"),
            
            new Document("SimpleVectorStore 是 Spring AI 提供的轻量级内存向量存储实现。" +
                        "它基于 JVM 内存存储向量数据，无需依赖任何外部向量数据库，也无需配置文件或启动额外服务，" +
                        "非常适合快速测试、原型开发和学习使用。但重启后数据会丢失，生产环境应使用专业的向量数据库。"),
            
            new Document("Embedding（文本嵌入）是将文本转换为高维向量的过程。这些向量捕捉了文本的语义信息，" +
                        "可以用于计算文本相似度、语义搜索、聚类等任务。在 RAG 系统中，Embedding 用于将文档和查询转换为向量，" +
                        "以便进行相似度检索。"),
            
            new Document("QuestionAnswerAdvisor 是 Spring AI 提供的开箱即用的 RAG 顾问组件。" +
                        "它会自动将用户问题向量化、检索向量库、将检索结果注入到 Prompt 中，整个过程对调用方透明。" +
                        "开发者只需在 ChatClient 中挂载这个 Advisor，就能轻松实现 RAG 功能。"),
            
            new Document("VectorStore 是 Spring AI 框架中统一向量存储操作的顶级接口。" +
                        "它的设计目标是屏蔽不同向量数据库（如 Chroma、Milvus、Redis、PGVector 等）的底层差异，" +
                        "提供标准化的向量写入、删除、相似性检索 API，让业务代码无需关心具体使用的是哪种向量库。")
        );
        
        // 将文档添加到向量库（自动完成 Embedding + 存储）
        vectorStore.add(documents);
        
        return vectorStore;
    }
}
