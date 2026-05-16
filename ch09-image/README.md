# Ch09-Image: Spring AI 图片生成示例

本模块展示了 Spring AI 中如何使用 ImageModel 生成图片，包括基础生成、自定义配置、批量生成等场景。

## 功能特性

1. **基础图片生成** - 使用默认配置生成图片
2. **自定义尺寸和质量** - 指定图片大小和质量等级
3. **批量生成** - 一次生成多张图片
4. **艺术风格** - 生成特定艺术风格的图片
5. **产品效果图** - 生成商业级产品照片

## 核心概念

### ImageModel

`ImageModel` 是 Spring AI 提供的图片生成接口，支持：
- DALL-E 3（OpenAI）
- Stable Diffusion
- 其他兼容的图片生成模型

```java
ImageModel imageModel; // 自动注入

ImagePrompt prompt = new ImagePrompt("提示词");
ImageResponse response = imageModel.call(prompt);
String imageUrl = response.getResult().getUrl();
```

### ImagePrompt

封装图片生成的提示词和选项：

```java
// 基础用法
ImagePrompt prompt = new ImagePrompt("一只可爱的猫咪");

// 带自定义选项
OpenAiImageOptions options = OpenAiImageOptions.builder()
    .withQuality("hd")
    .withHeight(1024)
    .withWidth(1024)
    .build();
ImagePrompt prompt = new ImagePrompt("提示词", options);
```

### OpenAiImageOptions

配置图片生成的参数：
- `withQuality()` - 质量等级（standard / hd）
- `withHeight()` / `withWidth()` - 图片尺寸
- `withN()` - 生成数量
- `withModel()` - 指定模型

## 启动应用

```bash
cd ch09-image
mvn spring-boot:run
```

应用将在端口 8009 上启动。

## API 接口

| 接口 | 方法 | 说明 | 示例 |
|------|------|------|------|
| `/image/generate` | POST | 基础图片生成 | `{"prompt": "一只猫咪"}` |
| `/image/generate/custom` | POST | 自定义配置 | `{"prompt": "...", "size": "1024x1024"}` |
| `/image/generate/batch` | POST | 批量生成 | `{"prompt": "...", "count": 2}` |
| `/image/generate/artistic` | POST | 艺术风格 | `{"subject": "山水", "style": "水墨画"}` |
| `/image/generate/product` | POST | 产品效果图 | `{"product": "手表", "scene": "桌面"}` |

## 使用示例

### 1. 基础图片生成

```bash
curl -X POST http://localhost:8009/image/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "一只可爱的猫咪坐在窗台上"}'
```

响应：
```json
{
  "success": true,
  "imageUrl": "https://...",
  "prompt": "一只可爱的猫咪坐在窗台上",
  "elapsedMs": 5234
}
```

### 2. 自定义尺寸和质量

```bash
curl -X POST http://localhost:8009/image/generate/custom \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "未来主义城市",
    "size": "1024x1024",
    "quality": "standard"
  }'
```

### 3. 批量生成

```bash
curl -X POST http://localhost:8009/image/generate/batch \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "一朵玫瑰花",
    "count": 2
  }'
```

响应包含多个图片 URL：
```json
{
  "success": true,
  "imageUrls": ["https://url1", "https://url2"],
  "count": 2
}
```

### 4. 艺术风格

```bash
curl -X POST http://localhost:8009/image/generate/artistic \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "山水风景",
    "style": "中国水墨画风格"
  }'
```

### 5. 产品效果图

```bash
curl -X POST http://localhost:8009/image/generate/product \
  -H "Content-Type: application/json" \
  -d '{
    "product": "智能手表",
    "scene": "大理石桌面，柔和光线"
  }'
```

## 提示词技巧

### 好的提示词特征

1. **具体描述**
   - ❌ "一只猫"
   - ✅ "一只橘色的波斯猫，坐在阳光下的窗台上，眼神温柔"

2. **包含风格**
   - "油画风格"、"水彩画"、"赛博朋克"、"极简主义"

3. **指定光线和氛围**
   - "柔和的自然光"、"温暖的黄昏"、"神秘的夜景"

4. **强调质量**
   - "高质量"、"细节丰富"、"专业摄影"

### 提示词模板

```
[主体] + [动作/状态] + [环境/背景] + [风格] + [光线] + [质量要求]

示例：
一只可爱的柯基犬（主体）+ 在草地上奔跑（动作）+ 
阳光明媚的公园（环境）+ 写实风格（风格）+ 
自然光（光线）+ 高分辨率，细节清晰（质量）
```

## 技术栈

- Spring Boot 3.5.14
- Spring AI 1.1.2
- Spring AI OpenAI
- Java 21
- Lombok

## 注意事项

### 1. API 密钥
确保在 `application.yml` 中配置了有效的 API 密钥。

### 2. 模型支持
当前配置使用 DALL-E 3，需要确认 API 服务商支持该模型。

### 3. 图片尺寸
DALL-E 3 支持的尺寸：
- 1024x1024（方形）
- 1024x1792（纵向）
- 1792x1024（横向）

### 4. 成本考虑
- DALL-E 3 按图片收费
- HD 质量比 standard 贵
- 批量生成会增加成本

### 5. 超时设置
图片生成可能需要较长时间（10-30秒），确保超时配置足够：
```yaml
spring:
  ai:
    openai:
      timeout:
        read: 60s
```

### 6. 错误处理
可能的错误：
- API 密钥无效
- 提示词违反内容政策
- 网络超时
- 配额用尽

## 实际应用场景

### 1. 电商产品图
自动生成产品展示图，节省摄影成本。

### 2. 社交媒体内容
为文章、博客生成配图。

### 3. 设计原型
快速生成设计概念图。

### 4. 教育材料
创建教学插图。

### 5. 游戏素材
生成游戏背景、角色概念图。

## 扩展方向

### 1. 图片编辑
使用 ImageEdit 功能修改现有图片。

### 2. 图片变体
基于已有图片生成变体。

### 3. 本地存储
将生成的图片保存到本地或云存储：
```java
// 下载图片并保存
URL url = new URL(imageUrl);
Files.copy(url.openStream(), Paths.get("image.png"));
```

### 4. 图片缓存
缓存生成的图片，避免重复生成。

### 5. 异步处理
对于批量生成，使用异步任务：
```java
@Async
public CompletableFuture<String> generateImageAsync(String prompt) {
    // 异步生成
}
```

## 常见问题

### Q1: 生成的图片 URL 有效期多久？
A: DALL-E 生成的 URL 通常有效期为 1 小时，建议及时下载保存。

### Q2: 如何保存生成的图片？
A: 可以使用 Java 的 HTTP 客户端下载图片：
```java
try (InputStream in = new URL(imageUrl).openStream()) {
    Files.copy(in, Paths.get("output.png"));
}
```

### Q3: 支持中文提示词吗？
A: 大多数模型支持中文提示词，但英文效果可能更好。

### Q4: 如何控制生成图片的风格？
A: 在提示词中明确描述风格，如"油画风格"、"水彩画"、"赛博朋克"等。

### Q5: 生成失败怎么办？
A: 检查：
1. API 密钥是否有效
2. 提示词是否违反内容政策
3. 网络连接是否正常
4. 配额是否充足
