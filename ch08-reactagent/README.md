# Ch08-ReactAgent: Spring AI ReactAgent 示例

本模块展示了 Spring AI Alibaba 中如何使用 ReactAgent 和 MemorySaver 实现智能代理和对话记忆功能。

## 功能特性

1. **ReactAgent** - ReAct (Reasoning + Acting) 模式的智能代理
2. **MemorySaver** - 内存级别的对话历史保存
3. **多轮对话** - 支持上下文感知的连续对话
4. **多 Agent 配置** - 不同场景使用不同的 Agent
5. **会话管理** - 支持多会话隔离和管理

## 核心概念

### 1. ReactAgent

ReactAgent 是基于 ReAct 模式的智能代理，能够：
- **推理（Reasoning）**：分析问题，制定解决计划
- **行动（Acting）**：执行具体操作，获取信息
- **观察（Observing）**：根据执行结果调整策略

```java
ReactAgent agent = ReactAgent.builder()
    .name("assistant")
    .model(chatModel)
    .instruction("你是一个智能助手")
    .saver(new MemorySaver())  // 添加记忆功能
    .build();
```

### 2. MemorySaver

`MemorySaver` 是 Spring AI Alibaba 提供的内存级别的状态保存器，用于：
- 保存对话历史
- 维护会话状态
- 支持多轮对话的上下文理解

```java
.saver(new MemorySaver())
```

### 3. ThreadId

每个会话都有一个唯一的 `threadId`，用于：
- 区分不同的对话线程
- 保存和恢复对话状态
- 实现会话隔离

## 启动应用

```bash
cd ch08-reactagent
mvn spring-boot:run
```

应用将在端口 8008 上启动。

## API 接口

| 接口 | 方法 | 说明 | 示例 |
|------|------|------|------|
| `/agent/chat` | POST | 基础对话 | `{"message": "你好", "sessionId": "session001"}` |
| `/agent/code/chat` | POST | 代码助手对话 | `{"message": "如何写单例？"}` |
| `/agent/conversation` | POST | 多轮对话 | `?sessionId=session003` |
| `/agent/stream` | POST | 流式输出 | `{"message": "你好"}` |
| `/agent/session/{id}` | GET | 获取会话信息 | - |
| `/agent/session/{id}` | DELETE | 清除会话 | - |
| `/agent/task` | POST | 复杂任务处理 | `{"task": "分析代码"}` |

## 使用示例

### 1. 基础对话

```bash
curl -X POST http://localhost:8008/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下自己", "sessionId": "session001"}'
```

### 2. 多轮对话（展示记忆功能）

```bash
# 第一轮
curl -X POST "http://localhost:8008/agent/conversation?sessionId=session003" \
  -H "Content-Type: application/json" \
  -d '{"message": "我想学习 Spring Boot，应该从哪里开始？"}'

# 第二轮 - Agent 会记住上一轮的对话
curl -X POST "http://localhost:8008/agent/conversation?sessionId=session003" \
  -H "Content-Type: application/json" \
  -d '{"message": "那我需要学习哪些前置知识？"}'

# 第三轮 - 继续基于上下文对话
curl -X POST "http://localhost:8008/agent/conversation?sessionId=session003" \
  -H "Content-Type: application/json" \
  -d '{"message": "有没有推荐的学习资源？"}'
```

### 3. 代码助手

```bash
curl -X POST http://localhost:8008/agent/code/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "如何用 Java 编写一个单例模式？", "sessionId": "session002"}'
```

### 4. 复杂任务处理

```bash
curl -X POST http://localhost:8008/agent/task \
  -H "Content-Type: application/json" \
  -d '{"task": "请解释快速排序算法的原理，并提供 Java 实现"}'
```

## 技术架构

### ReactAgent 工作流程

```
用户输入 → ReactAgent 
         ↓
    理解问题（Reasoning）
         ↓
    制定计划（Planning）
         ↓
    执行动作（Acting）
         ↓
    观察结果（Observing）
         ↓
    生成回答（Response）
         ↓
    保存到 MemorySaver
         ↓
    返回给用户
```

### MemorySaver 工作原理

```
Session 1 (threadId: xxx) → MemorySaver → 保存对话历史
Session 2 (threadId: yyy) → MemorySaver → 保存对话历史
     ↓
下次请求时根据 threadId 恢复上下文
```

## 实际应用场景

### 1. 智能客服
- 多轮问答
- 上下文理解
- 问题追踪

### 2. 编程助手
- 代码审查
- Bug 分析
- 最佳实践建议

### 3. 教育辅导
- 个性化教学
- 循序渐进指导
- 知识点关联

### 4. 任务自动化
- 复杂问题分析
- 多步骤任务执行
- 决策支持

## 技术栈

- Spring Boot 3.5.14
- Spring AI 1.1.2
- Spring AI Alibaba 1.1.2.0
- Spring AI Alibaba Agent Framework
- Java 21
- Lombok
- Project Reactor (Mono)

## 注意事项

1. **MemorySaver 的限制**
   - 数据存储在内存中，应用重启后会丢失
   - 适合开发和测试环境
   - 生产环境建议使用持久化存储（如 Redis）

2. **会话管理**
   - 需要客户端维护 sessionId
   - 建议设置会话过期时间
   - 定期清理无效会话

3. **性能考虑**
   - 长时间对话会占用较多内存
   - 建议限制对话历史长度
   - 监控内存使用情况

4. **模型选择**
   - 使用支持指令跟随的模型
   - qwen-plus、gpt-4 等效果较好
   - 调整 temperature 参数控制创造性

## 扩展方向

### 1. 持久化存储
```java
// 使用 RedisSaver 替代 MemorySaver
.saver(new RedisSaver(redisConnectionFactory))
```

### 2. 添加工具调用
```java
// 注册自定义工具
.tool(myCustomTool)
.tool(searchTool)
.tool(calculatorTool)
```

### 3. 自定义 Advisor
```java
// 添加日志 Advisor
.advisor(new LoggingAdvisor())
// 添加重试 Advisor
.advisor(new RetryAdvisor())
```

### 4. 流式响应
```java
// 使用 stream 方法实现实时输出
agent.stream(message)
    .doOnNext(chunk -> sendToClient(chunk))
    .subscribe();
```

## 常见问题

### Q1: MemorySaver 的数据会持久化吗？
A: 不会，MemorySaver 仅存储在内存中。应用重启后数据会丢失。生产环境建议使用 RedisSaver 或其他持久化方案。

### Q2: 如何实现会话超时？
A: 可以在 Controller 中添加会话最后访问时间检查，超过阈值则清除会话。

### Q3: ReactAgent 和普通 ChatClient 有什么区别？
A: ReactAgent 支持更复杂的推理和行动循环，可以调用工具、进行多步推理，适合复杂任务。ChatClient 更适合简单的对话场景。

### Q4: 如何调试 ReactAgent 的执行过程？
A: 启用 DEBUG 级别日志可以看到 Agent 的思考过程和每一步的执行详情。
