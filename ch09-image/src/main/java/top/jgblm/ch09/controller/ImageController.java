package top.jgblm.ch09.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 图片生成示例控制器
 * 展示 Spring AI 中的图片生成功能
 */
@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageModel imageModel;

    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    /**
     * 1. 基础图片生成 - 使用默认配置
     */
    @PostMapping("/generate")
    public Map<String, Object> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "一只可爱的猫咪坐在窗台上，阳光透过窗户洒进来");
        
        log.info("生成图片，提示词: {}", prompt);
        long startTime = System.currentTimeMillis();
        
        try {
            ImagePrompt imagePrompt = new ImagePrompt(prompt);
            ImageResponse response = imageModel.call(imagePrompt);
            
            if (response == null || response.getResult() == null) {
                return Map.of("error", "未生成图片");
            }
            
            String imageUrl = response.getResult().getOutput().getUrl();
            long elapsed = System.currentTimeMillis() - startTime;
            
            log.info("图片生成成功，耗时: {}ms, URL: {}", elapsed, imageUrl);
            
            return Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "prompt", prompt,
                    "elapsedMs", elapsed
            );
        } catch (Exception e) {
            log.error("图片生成失败", e);
            return Map.of(
                    "success", false,
                    "error", "图片生成失败：" + e.getMessage()
            );
        }
    }

    /**
     * 2. 自定义图片尺寸和质量
     */
    @PostMapping("/generate/custom")
    public Map<String, Object> generateCustomImage(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.getOrDefault("prompt", "一个未来主义的城市景观");
        String size = (String) request.getOrDefault("size", "1024x1024");
        String quality = (String) request.getOrDefault("quality", "standard");
        
        log.info("生成自定义图片，提示词: {}, 尺寸: {}, 质量: {}", prompt, size, quality);
        long startTime = System.currentTimeMillis();
        
        try {
            // 创建自定义选项
            OpenAiImageOptions options = OpenAiImageOptions.builder()
                    .quality(quality)
                    .height(parseSize(size)[0])
                    .width(parseSize(size)[1])
                    .build();
            
            ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
            ImageResponse response = imageModel.call(imagePrompt);
            
            if (response == null || response.getResult() == null) {
                return Map.of("error", "未生成图片");
            }
            
            String imageUrl = response.getResult().getOutput().getUrl();
            long elapsed = System.currentTimeMillis() - startTime;
            
            log.info("自定义图片生成成功，耗时: {}ms", elapsed);
            
            return Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "prompt", prompt,
                    "size", size,
                    "quality", quality,
                    "elapsedMs", elapsed
            );
        } catch (Exception e) {
            log.error("自定义图片生成失败", e);
            return Map.of(
                    "success", false,
                    "error", "图片生成失败：" + e.getMessage()
            );
        }
    }

    /**
     * 3. 批量生成多张图片
     */
    @PostMapping("/generate/batch")
    public Map<String, Object> generateBatchImages(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.getOrDefault("prompt", "一朵盛开的玫瑰花");
        int count = (int) request.getOrDefault("count", 2);
        
        // 限制最多生成 4 张
        count = Math.min(count, 4);
        
        log.info("批量生成 {} 张图片，提示词: {}", count, prompt);
        long startTime = System.currentTimeMillis();
        
        try {
            OpenAiImageOptions options = OpenAiImageOptions.builder()
                    .N(count)
                    .build();
            
            ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
            ImageResponse response = imageModel.call(imagePrompt);
            
            // 获取所有生成的图片
            List<String> imageUrls = new java.util.ArrayList<>();
            if (response != null && response.getResults() != null) {
                for (var result : response.getResults()) {
                    if (result != null && result.getOutput() != null) {
                        imageUrls.add(result.getOutput().getUrl());
                    }
                }
            }
            
            long elapsed = System.currentTimeMillis() - startTime;
            
            log.info("批量图片生成成功，数量: {}, 耗时: {}ms", imageUrls.size(), elapsed);
            
            return Map.of(
                    "success", true,
                    "imageUrls", imageUrls,
                    "count", imageUrls.size(),
                    "prompt", prompt,
                    "elapsedMs", elapsed
            );
        } catch (Exception e) {
            log.error("批量图片生成失败", e);
            return Map.of(
                    "success", false,
                    "error", "图片生成失败：" + e.getMessage()
            );
        }
    }

    /**
     * 4. 艺术风格图片生成
     */
    @PostMapping("/generate/artistic")
    public Map<String, Object> generateArtisticImage(@RequestBody Map<String, String> request) {
        String subject = request.getOrDefault("subject", "山水风景");
        String style = request.getOrDefault("style", "油画风格");
        
        String prompt = String.format("%s，%s，高质量，细节丰富，艺术感强", subject, style);
        
        log.info("生成艺术风格图片: {}", prompt);
        long startTime = System.currentTimeMillis();
        
        try {
            OpenAiImageOptions options = OpenAiImageOptions.builder()
                    .quality("hd")  // 高质量
                    .build();
            
            ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
            ImageResponse response = imageModel.call(imagePrompt);
            
            if (response == null || response.getResult() == null) {
                return Map.of("error", "未生成图片");
            }
            
            String imageUrl = response.getResult().getOutput().getUrl();
            long elapsed = System.currentTimeMillis() - startTime;
            
            return Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "subject", subject,
                    "style", style,
                    "prompt", prompt,
                    "elapsedMs", elapsed
            );
        } catch (Exception e) {
            log.error("艺术图片生成失败", e);
            return Map.of(
                    "success", false,
                    "error", "图片生成失败：" + e.getMessage()
            );
        }
    }

    /**
     * 5. 产品效果图生成
     */
    @PostMapping("/generate/product")
    public Map<String, Object> generateProductImage(@RequestBody Map<String, String> request) {
        String product = request.getOrDefault("product", "智能手机");
        String scene = request.getOrDefault("scene", "现代简约风格的桌面");
        
        String prompt = String.format(
                "%s放置在%s上，专业产品摄影，柔和光线，高分辨率，商业级质量",
                product, scene
        );
        
        log.info("生成产品效果图: {}", prompt);
        
        try {
            ImagePrompt imagePrompt = new ImagePrompt(prompt);
            ImageResponse response = imageModel.call(imagePrompt);
            
            if (response == null || response.getResult() == null) {
                return Map.of("error", "未生成图片");
            }
            
            String imageUrl = response.getResult().getOutput().getUrl();
            
            return Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "product", product,
                    "scene", scene,
                    "prompt", prompt
            );
        } catch (Exception e) {
            log.error("产品图生成失败", e);
            return Map.of(
                    "success", false,
                    "error", "图片生成失败：" + e.getMessage()
            );
        }
    }

    /**
     * 解析尺寸字符串，如 "1024x1024" -> [1024, 1024]
     */
    private int[] parseSize(String size) {
        String[] parts = size.split("x");
        if (parts.length == 2) {
            try {
                return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
            } catch (NumberFormatException e) {
                log.warn("无效的尺寸格式: {}, 使用默认 1024x1024", size);
            }
        }
        return new int[]{1024, 1024};
    }
}
