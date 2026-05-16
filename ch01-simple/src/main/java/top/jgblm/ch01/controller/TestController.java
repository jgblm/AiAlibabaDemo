package top.jgblm.ch01.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TestController {
    @Resource
    private ChatModel chatModel;

    @GetMapping("/test")
    public String chat(@RequestParam(value = "q",defaultValue = "who are you") String q){
        return chatModel.call(q);
    }

    @GetMapping("/test/stream")
    public Flux<String> streamChat(@RequestParam(value = "q",defaultValue = "who are you") String q){
        return chatModel.stream(q);
    }
}
