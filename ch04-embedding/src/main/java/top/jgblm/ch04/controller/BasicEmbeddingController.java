package top.jgblm.ch04.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding 基础示例控制器
 * 展示如何使用 Spring AI 的 EmbeddingModel 将文本转换为向量
 */
@RestController
@RequestMapping("/api/embedding")
public class BasicEmbeddingController {

    private final EmbeddingModel embeddingModel;

    public BasicEmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 单个文本嵌入
     * 示例: GET /api/embedding/single?text=我喜欢编程
     */
    @GetMapping("/single")
    public Map<String, Object> embedSingle(@RequestParam String text) {
        // 调用 embedding model 生成向量
        float[] embeddings = embeddingModel.embed(text);
        
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("vectorLength", embeddings.length);
        result.put("embeddings", Arrays.toString(embeddings));
        result.put("sampleValues", Arrays.copyOfRange(embeddings, 0, Math.min(10, embeddings.length)));
        
        return result;
    }

    /**
     * 批量文本嵌入
     * 示例: POST /api/embedding/batch
     * Body: ["我喜欢编程", "机器学习很有趣", "深度学习"]
     */
    @PostMapping("/batch")
    public Map<String, Object> embedBatch(@RequestBody List<String> texts) {
        // 批量生成向量
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        
        Map<String, Object> result = new HashMap<>();
        result.put("inputCount", texts.size());
        result.put("outputCount", response.getResults().size());
        
        // 提取每个文本的向量信息
        List<Map<String, Object>> embeddings = response.getResults().stream()
            .map(embeddingResult -> {
                Map<String, Object> embeddingMap = new HashMap<>();
                float[] vector = embeddingResult.getOutput();
                embeddingMap.put("vectorLength", vector.length);
                embeddingMap.put("sampleValues", Arrays.copyOfRange(vector, 0, Math.min(5, vector.length)));
                return embeddingMap;
            })
            .toList();
        
        result.put("embeddings", embeddings);
        
        return result;
    }

    /**
     * 使用 EmbeddingRequest 进行更精细的控制
     * 示例: GET /api/embedding/advanced?text=Spring+AI+很强大
     */
    @GetMapping("/advanced")
    public Map<String, Object> embedAdvanced(@RequestParam String text) {
        // 创建 EmbeddingRequest，可以配置更多选项
        EmbeddingRequest request = new EmbeddingRequest(
            List.of(text),
            null  // 使用默认配置，也可以传入特定的 EmbeddingOptions
        );
        
        EmbeddingResponse response = embeddingModel.call(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("model", response.getMetadata().getModel());
        result.put("usage", response.getMetadata().getUsage());
        
        if (!response.getResults().isEmpty()) {
            float[] vector = response.getResults().get(0).getOutput();
            result.put("vectorLength", vector.length);
            result.put("sampleValues", Arrays.copyOfRange(vector, 0, Math.min(10, vector.length)));
        }
        
        return result;
    }
}
