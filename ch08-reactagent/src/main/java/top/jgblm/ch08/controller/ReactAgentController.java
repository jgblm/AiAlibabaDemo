package top.jgblm.ch08.controller;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ReactAgent 示例控制器
 * 展示如何使用 ReactAgent 和 MemorySaver
 */
@Slf4j
@RestController
@RequestMapping("/agent")
public class ReactAgentController {

    private final ReactAgent reactAgent;
    private final ReactAgent codeAssistantAgent;
    
    // 用于存储不同会话的 threadId
    private final Map<String, String> sessionThreads = new ConcurrentHashMap<>();

    public ReactAgentController(ReactAgent reactAgent, 
                               @org.springframework.beans.factory.annotation.Qualifier("codeAssistantAgent") 
                               ReactAgent codeAssistantAgent) {
        this.reactAgent = reactAgent;
        this.codeAssistantAgent = codeAssistantAgent;
    }

    /**
     * 1. 基础对话 - 使用默认 Agent
     */
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "你好");
        String threadId = getOrCreateThreadId(request.get("sessionId"));
        
        log.info("收到消息: {}, threadId: {}", message, threadId);
        long startTime = System.currentTimeMillis();
        
        try {
            AssistantMessage result = reactAgent.call(message);
            String response = result.getText();
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("响应成功，耗时: {}ms, 响应: {}", elapsed, response);
            return response;
        } catch (Exception e) {
            log.error("调用失败", e);
            return "抱歉，发生了错误：" + e.getMessage();
        }
    }

    /**
     * 2. 代码助手对话 - 使用代码专用 Agent
     */
    @PostMapping("/code/chat")
    public String codeChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "如何编写一个 Hello World 程序？");
        String threadId = getOrCreateThreadId(request.get("sessionId"));
        
        log.info("代码助手收到消息: {}, threadId: {}", message, threadId);
        
        try {
            AssistantMessage result = codeAssistantAgent.call(message);
            String response = result.getText();
            log.info("代码助手响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("调用失败", e);
            return "抱歉，发生了错误：" + e.getMessage();
        }
    }

    /**
     * 3. 多轮对话示例 - 展示 MemorySaver 的记忆功能
     */
    @PostMapping("/conversation")
    public Map<String, Object> conversation(
            @RequestParam String sessionId,
            @RequestBody Map<String, String> request) {
        
        String message = request.get("message");
        String threadId = getOrCreateThreadId(sessionId);
        
        log.info("会话 {} 收到消息: {}", sessionId, message);
        long startTime = System.currentTimeMillis();
        
        try {
            AssistantMessage result = reactAgent.call(message);
            String response = result.getText();
            long elapsed = System.currentTimeMillis() - startTime;
            
            Map<String, Object> responseMap = Map.of(
                    "sessionId", sessionId,
                    "threadId", threadId,
                    "message", message,
                    "response", response,
                    "elapsedMs", elapsed
            );
            log.info("会话 {} 响应成功，耗时: {}ms", sessionId, elapsed);
            return responseMap;
        } catch (Exception e) {
            log.error("调用失败", e);
            return Map.of(
                    "sessionId", sessionId,
                    "error", "调用失败：" + e.getMessage()
            );
        }
    }

    /**
     * 4. 流式输出 - 实时返回响应
     */
    @PostMapping(value = "/stream")
    public String streamChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "你好");
        String threadId = getOrCreateThreadId(request.get("sessionId"));
        
        log.info("流式请求: {}", message);
        
        try {
            AssistantMessage result = reactAgent.call(message);
            String response = result.getText();
            log.info("流式响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("调用失败", e);
            return "抱歉，发生了错误：" + e.getMessage();
        }
    }

    /**
     * 5. 获取会话信息
     */
    @GetMapping("/session/{sessionId}")
    public Map<String, String> getSessionInfo(@PathVariable String sessionId) {
        String threadId = sessionThreads.get(sessionId);
        return Map.of(
                "sessionId", sessionId,
                "threadId", threadId != null ? threadId : "未创建"
        );
    }

    /**
     * 6. 清除会话历史
     */
    @DeleteMapping("/session/{sessionId}")
    public Map<String, String> clearSession(@PathVariable String sessionId) {
        String threadId = sessionThreads.remove(sessionId);
        log.info("清除会话: {}, threadId: {}", sessionId, threadId);
        
        return Map.of(
                "sessionId", sessionId,
                "status", "cleared"
        );
    }

    /**
     * 7. 复杂任务处理 - 展示 Agent 的推理能力
     */
    @PostMapping("/task")
    public String handleTask(@RequestBody Map<String, String> request) {
        String task = request.getOrDefault("task", "请帮我分析以下代码的问题：\npublic void test() {\n  int a = 1;\n  int b = 2;\n}");
        
        log.info("处理复杂任务: {}", task.substring(0, Math.min(50, task.length())));
        
        String prompt = String.format("""
                请完成以下任务，并详细说明你的思考过程：
                
                %s
                
                请按以下步骤回答：
                1. 理解任务要求
                2. 分析问题或需求
                3. 提供解决方案
                4. 总结关键点
                """, task);
        
        try {
            AssistantMessage result = reactAgent.call(prompt);
            String response = result.getText();
            log.info("任务完成");
            return response;
        } catch (Exception e) {
            log.error("任务处理失败", e);
            return "抱歉，任务处理失败：" + e.getMessage();
        }
    }

    /**
     * 获取或创建 threadId
     */
    private String getOrCreateThreadId(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "default";
        }
        
        return sessionThreads.computeIfAbsent(sessionId, 
                k -> UUID.randomUUID().toString());
    }
}
