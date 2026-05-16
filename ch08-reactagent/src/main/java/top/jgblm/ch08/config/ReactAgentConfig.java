package top.jgblm.ch08.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ReactAgent 配置类
 * 配置带记忆功能的 ReactAgent
 */
@Slf4j
@Configuration
public class ReactAgentConfig {

    /**
     * 创建带 MemorySaver 的 ReactAgent
     * MemorySaver 会在内存中保存对话历史，支持多轮对话
     */
    @Bean
    public ReactAgent reactAgent(ChatModel chatModel) {
        log.info("创建 ReactAgent...");
        
        return ReactAgent.builder()
                .name("assistant")
                .model(chatModel)
                .instruction("你是一个智能助手，可以帮助用户解答各种问题。请提供专业、准确的回答。")
                .saver(new MemorySaver())  // 使用内存保存器保存对话历史
                .build();
    }

    /**
     * 创建自定义指令的 ReactAgent
     * 用于特定场景（如代码助手）
     */
    @Bean
    public ReactAgent codeAssistantAgent(ChatModel chatModel) {
        log.info("创建代码助手 ReactAgent...");
        
        return ReactAgent.builder()
                .name("code_assistant")
                .model(chatModel)
                .instruction("""
                    你是一名专业的编程助手，擅长解答编程相关问题。
                    要求：
                    1. 提供清晰、准确的代码示例
                    2. 解释代码的关键部分
                    3. 指出可能的优化点
                    4. 使用中文回答
                    5. 保持回答简洁明了
                    """)
                .saver(new MemorySaver())
                .build();
    }
}
