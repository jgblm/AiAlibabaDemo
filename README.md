# AiAlibabaDemo

Spring AI + OpenAI 兼容接口多模块演示项目。

## 项目结构

| 模块 | 端口 | 说明 | 使用模型 |
|------|------|------|----------|
| `ch01-simple` | 8001 | 最简单的对话示例 | qwen3.6-flash |
| `ch02-multi` | 8002 | 多模型切换示例 | qwen3.5-flash / qwen-plus / deepseek-v3 |
| `ch03-chatclient` | 8003 | ChatClient API 使用示例 | deepseek-v3 |
| `ch04-embedding` | 8004 | 文本向量化 (Embedding) 示例 | text-embedding-v3 |
| `ch05-rag` | 8005 | RAG (检索增强生成) 示例 | deepseek-v3 + text-embedding-v3 |
| `ch06-prompt` | 8006 | Prompt 模板使用示例 | qwen-plus |
| `ch07-format-output` | 8007 | 格式化输出示例 | qwen-flash |
| `ch08-reactagent` | 8008 | React Agent 智能体示例 | qwen-flash |
| `ch09-image` | 8009 | AI 图片生成示例 | dall-e-3 |
| `ch10-tool` | 8010 | 工具调用 (Function Calling) 示例 | qwen-plus |

## 配置说明

> **安全提醒**: 每个模块的 `src/main/resources/application.yml` 包含 API Key 等敏感信息，已配置 `.gitignore` 忽略。你需要参照以下模板，在每个模块下手动创建 `application.yml`。

### 方式一：直接复制模板

在每个模块的 `src/main/resources/` 目录下创建 `application.yml`，内容如下：

#### ch01-simple (`src/main/resources/application.yml`, 端口 8001)

```yaml
server:
  port: 8001

spring:
  ai:
    openai:
      chat:
        options:
          model: qwen3.6-flash
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
```

#### ch02-multi (`src/main/resources/application.yml`, 端口 8002)

```yaml
server:
  port: 8002

spring:
  ai:
    openai:
      # 默认配置（会被各个模型的独立配置覆盖）
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      chat:
        options:
          # 默认模型
          model: qwen3.5-flash

# 各个模型的独立配置
model:
  config:
    # qwen-plus 模型配置
    plus:
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      model-name: qwen-plus

    # deepseek 模型配置
    deepseek:
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      model-name: deepseek-v3
```

#### ch03-chatclient (`src/main/resources/application.yml`, 端口 8003)

```yaml
server:
  port: 8003
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  ai:
    openai:
      # API Key
      api-key: ${AI_API_KEY}
      # Base URL（OpenAI 兼容接口地址）
      base-url: ${AI_BASE_URL}
      chat:
        options:
          # 默认模型
          model: deepseek-v3
```

#### ch04-embedding (`src/main/resources/application.yml`, 端口 8004)

```yaml
server:
  port: 8004

spring:
  ai:
    openai:
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      embedding:
        options:
          model: text-embedding-v3
```

#### ch05-rag (`src/main/resources/application.yml`, 端口 8005)

```yaml
server:
  port: 8005

spring:
  ai:
    openai:
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      chat:
        options:
          model: deepseek-v3
      embedding:
        options:
          model: text-embedding-v3
```

#### ch06-prompt (`src/main/resources/application.yml`, 端口 8006)

```yaml
server:
  port: 8006
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  ai:
    openai:
      chat:
        options:
          model: qwen-plus
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
```

#### ch07-format-output (`src/main/resources/application.yml`, 端口 8007)

```yaml
server:
  port: 8007
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  ai:
    openai:
      chat:
        options:
          model: qwen-flash
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
```

#### ch08-reactagent (`src/main/resources/application.yml`, 端口 8008)

```yaml
server:
  port: 8008
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  ai:
    openai:
      chat:
        options:
          model: qwen-flash
          temperature: 0.7
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      # 超时配置
      timeout:
        connect: 60s
        read: 180s
```

#### ch09-image (`src/main/resources/application.yml`, 端口 8009)

```yaml
server:
  port: 8009
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  ai:
    openai:
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}/v1
      # 图片生成配置
      image:
        options:
          model: dall-e-3
          quality: standard
          response-format: url
```

#### ch10-tool (`src/main/resources/application.yml`, 端口 8010)

```yaml
server:
  port: 8010
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  ai:
    openai:
      chat:
        options:
          model: qwen-plus
          temperature: 0.7
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}
      timeout:
        connect: 60s
        read: 180s
```

### 方式二：使用环境变量

以上模板已全部使用 `${}` 占位符引用环境变量，你可以设置系统环境变量：

| 环境变量 | 说明 | 默认值 |
|----------|------|--------|
| `AI_API_KEY` | 你的 API Key | **必填，无默认值** |
| `AI_BASE_URL` | API 接口地址 | 按实际服务商填写 |

**Windows (CMD)**:
```cmd
set AI_API_KEY=sk-your-api-key-here
```

**Windows (PowerShell)**:
```powershell
$env:AI_API_KEY="sk-your-api-key-here"
```

**macOS / Linux**:
```bash
export AI_API_KEY=sk-your-api-key-here
```

**在 IDEA 中运行时**: 可以在运行配置的 `Environment variables` 中添加：
```
AI_API_KEY=sk-your-api-key-here;AI_BASE_URL=https://your-api-endpoint.com/api
```

## 快速开始

1. **下载依赖**
   ```bash
   mvn clean install -DskipTests
   ```

2. **配置 API Key**
   - 方式一：在每个模块的 `src/main/resources/` 下创建 `application.yml`，将 `\${AI_API_KEY}` 替换为你的真实 API Key
   - 方式二：设置环境变量 `AI_API_KEY`

3. **运行指定模块**
   ```bash
   # 运行 ch01-simple
   cd ch01-simple
   mvn spring-boot:run
   ```

## 注意事项

- `.gitignore` 已配置 `**/application.yml`，该文件不会被 Git 追踪
- 请勿将任何包含真实 API Key 的文件提交到 Git 仓库
- 各模块独立运行，端口互不冲突（8001~8010）
- 本项目使用的是 OpenAI 兼容接口，可对接任何兼容 OpenAI API 的服务商
