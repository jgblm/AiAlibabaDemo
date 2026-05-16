package top.jgblm.ch06.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Prompt 示例控制器
 * 展示 Spring AI 中 Prompt 的各种使用方式
 */
@Slf4j
@RestController
public class PromptController {
    
    @Resource
    private ChatModel chatModel;
    
    private final ChatClient chatClient;
    
    public PromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 1. 基础 Prompt 使用 - 直接传入字符串
     */
    @GetMapping("/prompt/basic")
    public String basicPrompt(@RequestParam(value = "q", defaultValue = "你是谁？") String q) {
        try {
            log.info("收到请求: {}", q);
            String result = chatModel.call(q);
            log.info("响应成功");
            return result;
        } catch (Exception e) {
            log.error("调用失败", e);
            return "抱歉，发生了错误：" + e.getMessage();
        }
    }

    /**
     * 2. 使用 Prompt 对象 - 封装用户消息
     */
    @GetMapping("/prompt/object")
    public String promptObject(@RequestParam(value = "q", defaultValue = "你是谁？") String q) {
        Prompt prompt = new Prompt(q);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 3. 使用 PromptTemplate - 模板化 Prompt
     */
    @GetMapping("/prompt/template")
    public String promptTemplate(
            @RequestParam(value = "name", defaultValue = "张三") String name,
            @RequestParam(value = "city", defaultValue = "北京") String city) {
        
        String template = """
            请帮我写一封简短的邮件。
            收件人：{name}
            城市：{city}
            
            邮件内容要友好简洁。
            """;
        
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("name", name, "city", city));
        
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 4. 带系统消息的 Prompt - 设置角色和上下文
     */
    @GetMapping("/prompt/system")
    public String promptWithSystem(
            @RequestParam(value = "q", defaultValue = "什么是人工智能？") String q) {
        
        String systemMessage = """
            你是一位专业的技术讲师，擅长用简单易懂的语言解释复杂的技术概念。
            你的回答应该：
            1. 简洁明了
            2. 使用生活中的例子
            3. 避免过多技术术语
            """;
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemMessage),
                new UserMessage(q)
        ));
        
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 5. 多轮对话 Prompt - 包含对话历史
     */
    @GetMapping("/prompt/conversation")
    public String conversationPrompt() {
        // 使用 List<Message> 构建多轮对话历史
        List<Message> messages = List.of(
                new UserMessage("我想学习编程，应该从哪里开始？"),
                new AssistantMessage("建议从 Python 开始，它语法简单，适合初学者。"),
                new UserMessage("Python 能做什么？"),
                new AssistantMessage("Python 可以做网站开发、数据分析、人工智能、自动化脚本等。"),
                new UserMessage("那我该怎么学习？")
        );
        
        Prompt prompt = new Prompt(messages);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 6. 使用 ChatClient 和 Prompt - 更高级的 API
     */
    @GetMapping("/prompt/chatclient")
    public String chatClientWithPrompt(
            @RequestParam(value = "topic", defaultValue = "Spring Boot") String topic) {
        
        return chatClient.prompt()
                .user("请介绍一下" + topic + "的主要特点")
                .call()
                .content();
    }

    /**
     * 7. 流式 Prompt 响应
     */
    @GetMapping(value = "/prompt/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamPrompt(@RequestParam(value = "q", defaultValue = "写一首关于春天的诗") String q) {
        log.info("开始流式请求: {}", q);
        Prompt prompt = new Prompt(q);
        return chatModel.stream(prompt)
                .map(response -> {
                    if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                        return "";
                    }
                    String text = response.getResult().getOutput().getText();
                    return text != null ? text : "";
                })
                .filter(text -> !text.isEmpty())
                .doOnNext(text -> log.debug("流式输出: {}", text))
                .doOnError(e -> log.error("流式调用失败", e))
                .onErrorResume(e -> {
                    log.error("流式调用失败", e);
                    return Flux.just("抱歉，发生了错误：" + e.getMessage());
                });
    }

    /**
     * 8. 带参数的模板 Prompt - 动态生成提示词
     */
    @GetMapping("/prompt/parametric")
    public String parametricPrompt(
            @RequestParam(value = "language", defaultValue = "Java") String language,
            @RequestParam(value = "level", defaultValue = "初级") String level) {
        
        String template = """
            作为一名经验丰富的{language}开发工程师，
            请为{level}水平的开发者提供5个学习建议。
            每个建议请用一句话说明。
            """;
        
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("language", language, "level", level));
        
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 9. 角色扮演 Prompt - 让 AI 扮演特定角色
     */
    @GetMapping("/prompt/roleplay")
    public String rolePlayPrompt(
            @RequestParam(value = "role", defaultValue = "面试官") String role,
            @RequestParam(value = "position", defaultValue = "Java 开发工程师") String position) {
        
        String systemMessage = "你现在是一名" + role + "，正在面试一位应聘" + position + "职位的候选人。";
        String userMessage = "请问我一个关于这个职位的技术问题。";
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemMessage),
                new UserMessage(userMessage)
        ));
        
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 10. 格式化输出 Prompt - 要求特定格式的回答
     */
    @GetMapping("/prompt/format")
    public String formattedPrompt(
            @RequestParam(value = "topic", defaultValue = "微服务架构") String topic) {
        
        String template = """
            请介绍{topic}，并按照以下格式输出：
            
            ## 定义
            （用一句话定义）
            
            ## 优点
            1. （第一个优点）
            2. （第二个优点）
            3. （第三个优点）
            
            ## 缺点
            1. （第一个缺点）
            2. （第二个缺点）
            
            ## 适用场景
            （列出2-3个适用场景）
            """;
        
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("topic", topic));
        
        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}
