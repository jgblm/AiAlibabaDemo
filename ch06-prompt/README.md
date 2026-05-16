# Ch06-Prompt: Spring AI Prompt 使用示例

本模块展示了 Spring AI 中 Prompt 的各种使用方式，包括基础用法、模板化、系统消息、多轮对话等。

## 功能特性

1. **基础 Prompt** - 直接传入字符串
2. **Prompt 对象** - 封装用户消息
3. **PromptTemplate** - 模板化 Prompt，支持参数替换
4. **系统消息** - 设置角色和上下文
5. **多轮对话** - 包含对话历史
6. **ChatClient** - 使用更高级的 API
7. **流式响应** - 实时输出结果
8. **参数化模板** - 动态生成提示词
9. **角色扮演** - 让 AI 扮演特定角色
10. **格式化输出** - 要求特定格式的回答

## 启动应用

```bash
mvn spring-boot:run
```

应用将在端口 8006 上启动。

## API 接口

所有接口都可以通过 `requests.http` 文件进行测试，或者直接在浏览器中访问：

| 接口 | 说明 | 示例参数 |
|------|------|----------|
| `/prompt/basic` | 基础 Prompt 使用 | `q=你是谁？` |
| `/prompt/object` | 使用 Prompt 对象 | `q=什么是机器学习？` |
| `/prompt/template` | 模板化 Prompt | `name=张三&city=北京` |
| `/prompt/system` | 带系统消息的 Prompt | `q=什么是人工智能？` |
| `/prompt/conversation` | 多轮对话 Prompt | 无参数 |
| `/prompt/chatclient` | ChatClient 和 Prompt | `topic=Spring Boot` |
| `/prompt/stream` | 流式 Prompt 响应 | `q=写一首关于春天的诗` |
| `/prompt/parametric` | 带参数的模板 Prompt | `language=Java&level=初级` |
| `/prompt/roleplay` | 角色扮演 Prompt | `role=面试官&position=Java开发工程师` |
| `/prompt/format` | 格式化输出 Prompt | `topic=微服务架构` |

## 核心概念

### Prompt
Prompt 是与 AI 模型交互的基础，它包含了要发送给模型的消息。

### PromptTemplate
PromptTemplate 允许你创建带有占位符的模板，然后动态填充这些占位符来生成最终的 Prompt。

### System Message
系统消息用于设置 AI 的行为和角色，它在对话开始前就定义了 AI 应该如何回应。

### ChatClient
ChatClient 是 Spring AI 提供的高级 API，简化了与聊天模型的交互。

## 技术栈

- Spring Boot 3.5.14
- Spring AI 1.1.2
- Spring AI Alibaba 1.1.2.0
- Java 21
