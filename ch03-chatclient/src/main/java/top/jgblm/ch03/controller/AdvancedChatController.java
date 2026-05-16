package top.jgblm.ch03.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 高级 ChatClient 使用示例
 * 演示 ChatClient 的高级功能：提示词模板、结构化输出、多轮对话等
 */
@RestController
@RequestMapping("/advanced")
public class AdvancedChatController {

    private final ChatClient chatClient;

    public AdvancedChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 使用提示词模板
     */
    @GetMapping("/template")
    public String useTemplate(@RequestParam String topic) {
        String template = """
                请按照以下要求介绍 {topic}：
                1. 简要定义
                2. 主要特点
                3. 应用场景
                
                请用简洁的中文回答。
                """;

        String prompt = template.replace("{topic}", topic);
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 多轮对话（带上下文）
     */
    @GetMapping("/conversation")
    public String conversation(@RequestParam List<String> messages) {
        var promptBuilder = chatClient.prompt();

        // 添加历史消息
        for (int i = 0; i < messages.size() - 1; i++) {
            if (i % 2 == 0) {
                promptBuilder.user(messages.get(i));
            } else {
                promptBuilder.system(messages.get(i));
            }
        }

        // 添加最后一条用户消息
        String lastMessage = messages.get(messages.size() - 1);
        return promptBuilder.user(lastMessage).call().content();
    }

    /**
     * 结构化输出 - 返回 JSON 格式
     */
    @PostMapping("/structured-json")
    public String structuredJson(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");

        String systemPrompt = """
                请以 JSON 格式提供关于 {topic} 的信息，包含以下字段：
                - name: 名称
                - description: 描述
                - category: 分类
                - features: 特点列表（数组）
                
                只返回 JSON，不要有其他内容。
                """.replace("{topic}", topic);

        return chatClient.prompt()
                .system(systemPrompt)
                .call()
                .content();
    }

    /**
     * 角色扮演
     */
    @GetMapping("/role-play")
    public String rolePlay(
            @RequestParam String role,
            @RequestParam String question) {

        String systemPrompt = switch (role.toLowerCase()) {
            case "teacher" -> "你是一位经验丰富的老师，善于用简单易懂的方式解释复杂概念";
            case "doctor" -> "你是一位专业的医生，提供健康建议，但强调需要咨询真实医生";
            case "programmer" -> "你是一位资深程序员，擅长代码优化和最佳实践";
            case "translator" -> "你是一位专业翻译，精通中英文互译";
            default -> "你是一位友好的助手";
        };

        return chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();
    }

    /**
     * 文本总结
     */
    @PostMapping("/summarize")
    public String summarize(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        int maxLength = Integer.parseInt(request.getOrDefault("maxLength", "100"));

        String prompt = """
                请将以下文本总结为不超过 %d 字的摘要：
                
                %s
                """.formatted(maxLength, text);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 代码生成
     */
    @GetMapping("/code-generation")
    public String generateCode(
            @RequestParam String language,
            @RequestParam String requirement) {

        String prompt = """
                请用 %s 语言实现以下功能：
                %s
                
                要求：
                1. 代码简洁高效
                2. 包含必要的注释
                3. 遵循最佳实践
                
                只返回代码，不要有其他说明。
                """.formatted(language, requirement);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 翻译功能
     */
    @GetMapping("/translate")
    public String translate(
            @RequestParam String text,
            @RequestParam(defaultValue = "en") String targetLang) {

        String prompt = """
                将以下文本翻译为%s：
                
                %s
                
                只返回翻译结果，不要有其他内容。
                """.formatted(getLanguageName(targetLang), text);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String getLanguageName(String code) {
        return switch (code.toLowerCase()) {
            case "en" -> "英文";
            case "zh" -> "中文";
            case "ja" -> "日文";
            case "ko" -> "韩文";
            case "fr" -> "法文";
            case "de" -> "德文";
            default -> "目标语言";
        };
    }
}
