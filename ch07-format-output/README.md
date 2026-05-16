# Ch07-Format-Output: Spring AI 格式化输出示例

本模块展示了 Spring AI 中如何实现结构化输出（Formatted Output），将 AI 的自然语言响应转换为结构化的 Java 对象。

## 功能特性

1. **BeanOutputConverter** - 使用转换器将 AI 响应转换为 Java Bean
2. **entity() 方法** - Spring AI 1.0+ 提供的简洁 API
3. **列表数据提取** - 处理多个对象的场景
4. **自定义格式** - 使用 Map 接收灵活的数据结构
5. **情感分析** - 实际应用场景示例

## 核心概念

### 1. BeanOutputConverter
`BeanOutputConverter` 是 Spring AI 提供的工具类，用于将 AI 的文本响应转换为指定的 Java 对象。

```java
BeanOutputConverter<PersonInfo> converter = new BeanOutputConverter<>(PersonInfo.class);
String aiResponse = chatClient.prompt()
    .user(userSpec -> userSpec
        .text(template)
        .param("format", converter.getFormat()))
    .call()
    .content();
PersonInfo personInfo = converter.convert(aiResponse);
```

### 2. entity() 方法
更简洁的方式，直接指定目标类型：

```java
WeatherInfo weatherInfo = chatClient.prompt()
    .user(prompt)
    .call()
    .entity(WeatherInfo.class);
```

### 3. 处理泛型类型
对于列表等泛型类型，使用 `ParameterizedTypeReference`：

```java
BeanOutputConverter<List<PersonInfo>> converter = new BeanOutputConverter<>(
    new ParameterizedTypeReference<List<PersonInfo>>() {}
);
```

## 启动应用

```bash
cd ch07-format-output
mvn spring-boot:run
```

应用将在端口 8007 上启动。

## API 接口

| 接口 | 说明 | 示例参数 |
|------|------|----------|
| `/format/person` | 提取人物信息 | `text=我叫张三...` |
| `/format/book` | 提取书籍信息 | `description=《Java编程思想》...` |
| `/format/weather` | 获取天气信息 | `city=北京` |
| `/format/persons` | 提取多个人物 | 无参数 |
| `/format/custom` | 自定义格式输出 | `topic=人工智能` |
| `/format/review` | 产品评论分析 | `review=这款手机...` |

## 使用场景

### 1. 信息提取
从非结构化文本中提取结构化数据：
- 人物信息
- 产品信息
- 事件信息

### 2. 内容生成
生成符合特定格式的内容：
- JSON 数据
- 报表数据
- 配置文件

### 3. 数据分析
对文本进行分析和分类：
- 情感分析
- 关键词提取
- 主题分类

### 4. 数据转换
将自然语言转换为程序可用的数据结构：
- API 参数
- 数据库记录
- 配置项

## 技术栈

- Spring Boot 3.5.14
- Spring AI 1.1.2
- Spring AI Alibaba 1.1.2.0
- Java 21
- Lombok

## 注意事项

1. **模型选择**：建议使用支持 JSON 输出的模型（如 qwen-plus、gpt-4 等）
2. **提示词设计**：在提示词中明确说明需要 JSON 格式
3. **错误处理**：AI 可能返回无效的 JSON，需要添加异常处理
4. **温度参数**：设置较低的温度值（如 0.1-0.3）以获得更稳定的输出

## 最佳实践

1. **定义清晰的 DTO**：使用明确的字段名和类型
2. **提供格式说明**：在提示词中包含 `{format}` 占位符
3. **验证结果**：检查转换后的对象是否符合预期
4. **降级策略**：当解析失败时，提供默认值或重试机制
