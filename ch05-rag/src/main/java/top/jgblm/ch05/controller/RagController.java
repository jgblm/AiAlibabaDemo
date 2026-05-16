package top.jgblm.ch05.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * RAG 问答控制器
 * 展示如何使用 QuestionAnswerAdvisor 实现检索增强生成
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    /**
     * 基础 RAG 问答
     * 使用默认的 QuestionAnswerAdvisor 配置
     * 
     * 示例: GET /api/rag/ask?question=什么是Spring+AI
     */
    @GetMapping("/ask")
    public Map<String, Object> ask(@RequestParam String question) {
        // 创建默认的 QuestionAnswerAdvisor
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore).build();
        
        // 调用 ChatClient，挂载 RAG Advisor
        String answer = chatClient.prompt()
            .user(question)
            .advisors(advisor)  // 挂载 RAG Advisor
            .call()
            .content();
        
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("type", "basic");
        
        return result;
    }

    /**
     * 高级 RAG 问答（自定义搜索参数）
     * 可以配置相似度阈值、返回文档数量等参数
     * 
     * 示例: GET /api/rag/ask-advanced?question=RAG+是如何工作的
     */
    @GetMapping("/ask-advanced")
    public Map<String, Object> askAdvanced(@RequestParam String question) {
        // 创建自定义配置的 QuestionAnswerAdvisor
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder()
                .topK(3)                    // 返回最相关的 3 个文档
                .similarityThreshold(0.5)   // 相似度阈值（0-1之间，越高要求越严格）
                .build())
            .build();
        
        // 调用 ChatClient
        String answer = chatClient.prompt()
            .user(question)
            .advisors(advisor)
            .call()
            .content();
        
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("type", "advanced");
        result.put("config", Map.of(
            "topK", 3,
            "similarityThreshold", 0.5
        ));
        
        return result;
    }

    /**
     * 带系统提示词的 RAG 问答
     * 可以自定义系统提示词，控制模型的回答风格
     * 
     * 示例: GET /api/rag/ask-with-system?question=解释一下+VectorStore
     */
    @GetMapping("/ask-with-system")
    public Map<String, Object> askWithSystem(@RequestParam String question) {
        // 创建自定义系统提示词
        String systemPrompt = """
            你是一个专业的 AI 技术助手。请根据提供的上下文信息回答用户问题。
            如果上下文中没有相关信息，请诚实地告诉用户你不知道答案。
            回答要简洁明了，重点突出。
            """;
        
        // 创建 Advisor
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder()
                .topK(2)
                .similarityThreshold(0.4)
                .build())
            .build();
        
        // 调用 ChatClient，同时设置系统提示词
        String answer = chatClient.prompt()
            .system(systemPrompt)
            .user(question)
            .advisors(advisor)
            .call()
            .content();
        
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("type", "with-system-prompt");
        
        return result;
    }

    /**
     * 向量库搜索测试
     * 直接搜索相关文档，不经过大模型生成
     * 
     * 示例: GET /api/rag/search?query=Spring+AI
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String query) {
        // 直接在向量库中搜索相似文档
        var documents = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(3)
                .similarityThreshold(0.3)
                .build()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("documentCount", documents.size());
        result.put("documents", documents.stream()
            .map(doc -> Map.of(
                "content", doc.getText(),
                "id", doc.getId(),
                "score", doc.getScore()
            ))
            .toList()
        );
        
        return result;
    }
}
