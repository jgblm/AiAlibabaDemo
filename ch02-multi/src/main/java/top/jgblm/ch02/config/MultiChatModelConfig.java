package top.jgblm.ch02.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MultiChatModelConfig {

    // qwen-plus 模型配置
    @Value("${model.config.plus.api-key}")
    private String plusApiKey;

    @Value("${model.config.plus.base-url}")
    private String plusBaseUrl;

    @Value("${model.config.plus.model-name}")
    private String plusModelName;

    // deepseek 模型配置
    @Value("${model.config.deepseek.api-key}")
    private String deepseekApiKey;

    @Value("${model.config.deepseek.base-url}")
    private String deepseekBaseUrl;

    @Value("${model.config.deepseek.model-name}")
    private String deepseekModelName;

    /**
     * 配置第一个 ChatModel - qwen-plus 模型（平衡性能和成本）
     */
    @Bean("qwenPlusChatModel")
    public ChatModel qwenPlusChatModel() {
        // 创建独立的 OpenAiApi 实例
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(plusApiKey)
                .baseUrl(plusBaseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(plusModelName)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    /**
     * 配置第二个 ChatModel - qwen-turbo 模型（速度更快，成本更低）
     */
    @Bean("deepseekChatModel")
    public ChatModel deepseekChatModel() {
        // 创建独立的 OpenAiApi 实例
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(deepseekApiKey)
                .baseUrl(deepseekBaseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(deepseekModelName)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    /**
     * 设置默认的 ChatModel（使用 qwen-plus）
     */
    @Primary
    @Bean
    public ChatModel primaryChatModel() {
        return qwenPlusChatModel();
    }
}
