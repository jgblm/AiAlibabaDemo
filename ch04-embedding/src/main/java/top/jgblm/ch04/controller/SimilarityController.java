package top.jgblm.ch04.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding 高级应用示例控制器
 * 展示如何使用向量进行相似度计算和语义搜索
 */
@RestController
@RequestMapping("/api/similarity")
public class SimilarityController {

    private final EmbeddingModel embeddingModel;

    public SimilarityController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 计算两个文本的相似度
     * 示例: GET /api/similarity/compare?text1=我喜欢编程&text2=我热爱编码
     */
    @GetMapping("/compare")
    public Map<String, Object> compareSimilarity(
            @RequestParam String text1,
            @RequestParam String text2) {
        
        // 生成两个文本的向量
        float[] vector1 = embeddingModel.embed(text1);
        float[] vector2 = embeddingModel.embed(text2);
        
        // 计算余弦相似度
        double similarity = cosineSimilarity(vector1, vector2);
        
        Map<String, Object> result = new HashMap<>();
        result.put("text1", text1);
        result.put("text2", text2);
        result.put("similarity", similarity);
        result.put("interpretation", interpretSimilarity(similarity));
        
        return result;
    }

    /**
     * 语义搜索示例
     * 在文档集合中搜索与查询最相似的文档
     * 示例: POST /api/similarity/search
     * Body: {
     *   "query": "人工智能技术",
     *   "documents": ["机器学习是AI的分支", "深度学习用于图像识别", "今天天气不错"]
     * }
     */
    @PostMapping("/search")
    public Map<String, Object> semanticSearch(@RequestBody SearchRequest request) {
        // 生成查询向量
        float[] queryVector = embeddingModel.embed(request.getQuery());
        
        // 为所有文档生成向量并计算相似度
        List<DocumentResult> results = request.getDocuments().stream()
            .map(doc -> {
                float[] docVector = embeddingModel.embed(doc);
                double similarity = cosineSimilarity(queryVector, docVector);
                return new DocumentResult(doc, similarity);
            })
            .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity())) // 按相似度降序排序
            .toList();
        
        Map<String, Object> result = new HashMap<>();
        result.put("query", request.getQuery());
        result.put("results", results);
        
        return result;
    }

    /**
     * 计算余弦相似度
     */
    private double cosineSimilarity(float[] vector1, float[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("向量维度必须相同");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }

    /**
     * 解释相似度值
     */
    private String interpretSimilarity(double similarity) {
        if (similarity >= 0.9) {
            return "非常相似";
        } else if (similarity >= 0.7) {
            return "高度相似";
        } else if (similarity >= 0.5) {
            return "中等相似";
        } else if (similarity >= 0.3) {
            return "低度相似";
        } else {
            return "几乎不相似";
        }
    }

    /**
     * 内部类：搜索请求
     */
    static class SearchRequest {
        private String query;
        private List<String> documents;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public List<String> getDocuments() {
            return documents;
        }

        public void setDocuments(List<String> documents) {
            this.documents = documents;
        }
    }

    /**
     * 内部类：文档结果
     */
    static class DocumentResult {
        private String document;
        private double similarity;

        public DocumentResult(String document, double similarity) {
            this.document = document;
            this.similarity = similarity;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(double similarity) {
            this.similarity = similarity;
        }
    }
}
