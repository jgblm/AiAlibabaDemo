package top.jgblm.ch02.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class MultiChatController {

    @Resource
    @Qualifier("qwenPlusChatModel")
    private ChatModel qwenPlusChatModel;

    @Resource
    @Qualifier("deepseekChatModel")
    private ChatModel deepseekChatModel;


    /**
     * 使用 qwen-plus 模型聊天（平衡性能和成本）
     */
    @GetMapping("/plus")
    public String chatWithQwenPlus(@RequestParam(value = "q", defaultValue = "你是谁？") String q) {
        return qwenPlusChatModel.call(q);
    }

    /**
     * 使用 qwen-turbo 模型聊天（速度更快，成本更低）
     */
    @GetMapping("/deepseek")
    public String chatWithQwenTurbo(@RequestParam(value = "q", defaultValue = "你是谁？") String q) {
        return deepseekChatModel.call(q);
    }

    /**
     * 流式输出 - qwen-plus
     */
    @GetMapping("/plus/stream")
    public Flux<String> streamWithQwenPlus(@RequestParam(value = "q", defaultValue = "你是谁？") String q) {
        return qwenPlusChatModel.stream(q);
    }

    /**
     * 流式输出 - qwen-turbo
     */
    @GetMapping("/deepseek/stream")
    public Flux<String> streamWithQwenTurbo(@RequestParam(value = "q", defaultValue = "你是谁？") String q) {
        return deepseekChatModel.stream(q);
    }

    /**
     * 同时调用三个模型并返回结果对比
     */
    @GetMapping("/compare")
    public String compareModels(@RequestParam(value = "q", defaultValue = "简单介绍一下你自己") String q) {
        String plusResponse = qwenPlusChatModel.call(q);
        String deepseekResponse = deepseekChatModel.call(q);

        return """
                === qwen-plus 响应（平衡型）===
                %s
                
                === deepseek 响应（快速型）===
                %s
                
                """.formatted(plusResponse, deepseekResponse);
    }
}
