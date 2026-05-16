package top.jgblm.ch07.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.jgblm.ch07.dto.BookInfo;
import top.jgblm.ch07.dto.PersonInfo;
import top.jgblm.ch07.dto.WeatherInfo;

import java.util.List;
import java.util.Map;

/**
 * 格式化输出示例控制器
 * 展示 Spring AI 的结构化输出功能
 */
@Slf4j
@RestController
@RequestMapping("/format")
public class FormatOutputController {

    private final ChatClient chatClient;

    public FormatOutputController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 1. 使用 BeanOutputConverter - 将 AI 响应转换为 Java 对象
     */
    @GetMapping("/person")
    public PersonInfo extractPersonInfo(
            @RequestParam(value = "text", defaultValue = "我叫张三，今年28岁，是一名软件工程师，目前居住在北京。我热爱编程和技术分享。") String text) {
        
        log.info("提取人物信息: {}", text);
        
        // 创建转换器
        BeanOutputConverter<PersonInfo> converter = new BeanOutputConverter<>(PersonInfo.class);
        
        // 构建提示词模板
        String template = """
            从以下文本中提取人物信息，并以 JSON 格式返回：
            
            文本内容：{text}
            
            {format}
            """;
        
        // 调用 AI 并转换结果
        String aiResponse = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(template)
                        .param("text", text)
                        .param("format", converter.getFormat()))
                .call()
                .content();
        
        // 转换为 Java 对象
        PersonInfo personInfo = converter.convert(aiResponse);
        log.info("提取结果: {}", personInfo);
        
        return personInfo;
    }

    /**
     * 2. 提取书籍信息
     */
    @GetMapping("/book")
    public BookInfo extractBookInfo(
            @RequestParam(value = "description", defaultValue = "《Java编程思想》是由Bruce Eckel编写的一本经典Java教程，这本书深入浅出地讲解了Java的核心概念，适合中高级开发者阅读，评分4.8分，标签包括Java、编程、面向对象。") String description) {
        
        log.info("提取书籍信息: {}", description);
        
        BeanOutputConverter<BookInfo> converter = new BeanOutputConverter<>(BookInfo.class);
        
        String template = """
            从以下描述中提取书籍信息，并以 JSON 格式返回：
            
            描述内容：{description}
            
            {format}
            """;
        
        String aiResponse = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(template)
                        .param("description", description)
                        .param("format", converter.getFormat()))
                .call()
                .content();
        
        BookInfo bookInfo = converter.convert(aiResponse);
        log.info("提取结果: {}", bookInfo);
        
        return bookInfo;
    }

    /**
     * 3. 使用 entity() 方法 - 更简洁的方式（Spring AI 1.0+）
     */
    @GetMapping("/weather")
    public WeatherInfo extractWeatherInfo(
            @RequestParam(value = "city", defaultValue = "北京") String city) {
        
        log.info("获取天气信息: {}", city);
        
        String prompt = String.format("""
            请生成%s今天的天气预报信息，包括城市、日期、天气状况、温度、湿度和风向。
            以 JSON 格式返回。
            """, city);
        
        // 直接使用 entity() 方法转换为对象
        WeatherInfo weatherInfo = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(WeatherInfo.class);
        
        log.info("天气信息: {}", weatherInfo);
        
        return weatherInfo;
    }

    /**
     * 4. 提取列表数据 - 多个人物信息
     */
    @GetMapping("/persons")
    public List<PersonInfo> extractMultiplePersons() {
        
        log.info("提取多个人物信息");
        
        String text = """
            团队介绍：
            李四是项目经理，35岁，在上海工作，负责项目管理和客户沟通。
            王五是前端开发工程师，26岁，在深圳，擅长React和Vue框架。
            赵六是后端架构师，32岁，在杭州，专注于微服务架构设计。
            """;
        
        // 使用 ParameterizedTypeReference 处理泛型
        BeanOutputConverter<List<PersonInfo>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<PersonInfo>>() {}
        );
        
        String template = """
            从以下文本中提取所有人物的信息，并以 JSON 数组格式返回：
            
            文本内容：{text}
            
            {format}
            """;
        
        String aiResponse = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(template)
                        .param("text", text)
                        .param("format", converter.getFormat()))
                .call()
                .content();
        
        List<PersonInfo> persons = converter.convert(aiResponse);
        log.info("提取到 {} 个人物", persons.size());
        
        return persons;
    }

    /**
     * 5. 自定义格式 - 使用 Map 接收灵活的数据结构
     */
    @GetMapping("/custom")
    public Map<String, Object> customFormat(
            @RequestParam(value = "topic", defaultValue = "人工智能") String topic) {
        
        log.info("生成关于 {} 的结构化信息", topic);
        
        String prompt = String.format("""
            请生成关于"%s"的结构化信息，包含以下字段：
            - title: 标题
            - definition: 定义
            - applications: 应用领域（数组）
            - challenges: 面临的挑战（数组）
            - future: 未来展望
            
            以 JSON 格式返回。
            """, topic);
        
        // 直接返回 Map
        Map<String, Object> result = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
        
        log.info("生成结果: {}", result);
        
        return result;
    }

    /**
     * 6. 产品评论分析 - 情感分析和关键信息提取
     */
    @GetMapping("/review")
    public Map<String, Object> analyzeReview(
            @RequestParam(value = "review", defaultValue = "这款手机真的很棒！屏幕清晰，运行流畅，拍照效果出色。但是电池续航有点短，希望能改进。总体来说性价比很高，推荐购买。") String review) {
        
        log.info("分析产品评论: {}", review);
        
        String prompt = String.format("""
            请分析以下产品评论，提取以下信息：
            - sentiment: 情感倾向（positive/negative/neutral）
            - rating: 评分（1-5）
            - pros: 优点列表
            - cons: 缺点列表
            - recommendation: 是否推荐（true/false）
            
            评论内容：%s
            
            以 JSON 格式返回。
            """, review);
        
        Map<String, Object> analysis = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
        
        log.info("分析结果: {}", analysis);
        
        return analysis;
    }
}
