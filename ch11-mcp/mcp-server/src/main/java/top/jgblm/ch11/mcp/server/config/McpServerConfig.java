package top.jgblm.ch11.mcp.server.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.jgblm.ch11.mcp.server.service.WeatherService;

@Configuration
public class McpServerConfig {
    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService)
    {

        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }
}
