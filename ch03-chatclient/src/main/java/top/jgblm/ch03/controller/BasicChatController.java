package top.jgblm.ch03.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 基础 ChatClient 使用示例
 * 演示 ChatClient 的基本用法
 */
@RestController
@RequestMapping("/basic")
public class BasicChatController {

    private final ChatClient chatClient;

    public BasicChatController(ChatClient.Builder chatClientBuilder) {
        // 通过 Builder 构建 ChatClient 实例
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 最简单的文本对话
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 带系统提示词的对话
     */
    @GetMapping("/chat-with-system")
    public String chatWithSystem(@RequestParam String message) {
        return chatClient.prompt()
                .system("你是一个专业的 AI 助手，请用简洁的中文回答问题")
                .user(message)
                .call()
                .content();
    }

    /**
     * 流式输出
     */
    @GetMapping("/stream")
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * 设置温度参数
     */
    @GetMapping("/chat-with-temperature")
    public String chatWithTemperature(
            @RequestParam String message,
            @RequestParam(defaultValue = "0.7") double temperature) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .temperature(temperature)
                .build();
        
        return chatClient.prompt()
                .user(message)
                .options(options)
                .call()
                .content();
    }
}
