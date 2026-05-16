package top.jgblm.ch10.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.web.bind.annotation.*;
import top.jgblm.ch10.tools.CalculatorTool;
import top.jgblm.ch10.tools.DateTimeTool;
import top.jgblm.ch10.tools.WeatherTool;

import java.util.Map;

/**
 * Tool (Function Calling) 示例控制器
 * 展示 Spring AI 中如何定义和使用 Tool
 */
@Slf4j
@RestController
@RequestMapping("/tool")
public class ToolController {

    private final ChatModel chatModel;
    private final WeatherTool weatherTool;
    private final CalculatorTool calculatorTool;
    private final DateTimeTool dateTimeTool;

    public ToolController(ChatModel chatModel,
                          WeatherTool weatherTool,
                          CalculatorTool calculatorTool,
                          DateTimeTool dateTimeTool) {
        this.chatModel = chatModel;
        this.weatherTool = weatherTool;
        this.calculatorTool = calculatorTool;
        this.dateTimeTool = dateTimeTool;
    }

    /**
     * 1. 使用天气工具 - 模型自动决定是否调用工具
     * 当用户询问天气相关问题时，模型会自动调用 WeatherTool
     */
    @PostMapping("/weather")
    public Map<String, String> weatherChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "北京今天天气怎么样？");

        log.info("天气工具请求: {}", message);

        ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(weatherTool)
                .build();

        String response = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolProvider)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        log.info("天气工具响应: {}", response);
        return Map.of("message", message, "response", response);
    }

    /**
     * 2. 使用计算器工具
     * 演示模型如何根据用户问题自动选择合适的计算方法
     */
    @PostMapping("/calculator")
    public Map<String, String> calculatorChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "123 加 456 等于多少？");

        log.info("计算器工具请求: {}", message);

        ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(calculatorTool)
                .build();

        String response = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolProvider)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        log.info("计算器工具响应: {}", response);
        return Map.of("message", message, "response", response);
    }

    /**
     * 3. 使用日期时间工具
     */
    @PostMapping("/datetime")
    public Map<String, String> dateTimeChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "现在几点了？");

        log.info("日期时间工具请求: {}", message);

        ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(dateTimeTool)
                .build();

        String response = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolProvider)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        log.info("日期时间工具响应: {}", response);
        return Map.of("message", message, "response", response);
    }

    /**
     * 4. 注册所有工具 - 模型智能选择
     * 将多个工具同时注册，模型会根据用户问题自动选择合适的工具
     */
    @PostMapping("/smart")
    public Map<String, String> smartChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "帮我看看上海明天天气如何，另外算一下 256 的平方根");

        log.info("智能工具请求: {}", message);

        ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(weatherTool, calculatorTool, dateTimeTool)
                .build();

        String response = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolProvider)
                .defaultSystem("你是一个智能助手，可以使用提供的工具来帮助用户。请根据用户的问题选择合适的工具，并用中文回答。")
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        log.info("智能工具响应: {}", response);
        return Map.of("message", message, "response", response);
    }

    /**
     * 5. 指定系统提示 + 工具组合
     * 演示通过系统提示引导模型如何使用工具
     */
    @PostMapping("/guided")
    public Map<String, String> guidedChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "我计划下周去北京出差，需要了解天气情况");

        log.info("引导式工具请求: {}", message);

        ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(weatherTool, dateTimeTool)
                .build();

        String response = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolProvider)
                .defaultSystem("""
                        你是一个旅行助手。当用户提到出行计划时：
                        1. 先查询目的地的天气预报
                        2. 告诉用户当前日期
                        3. 根据天气情况给出出行建议
                        请用中文回答。
                        """)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        log.info("引导式工具响应: {}", response);
        return Map.of("message", message, "response", response);
    }
}
