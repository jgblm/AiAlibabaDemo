package top.jgblm.ch10.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 天气查询工具
 * 模拟天气API，演示 @Tool 注解的基本用法
 */
@Component
public class WeatherTool {

    @Tool(description = "根据城市名称查询当前天气信息，包括温度、天气状况和湿度")
    public String getWeather(
            @ToolParam(description = "城市名称，如：北京、上海、广州") String city) {
        // 模拟天气数据
        String[] conditions = {"晴天", "多云", "阴天", "小雨", "大雨"};
        String condition = conditions[Math.abs(city.hashCode()) % conditions.length];

        int temp = 15 + Math.abs(city.hashCode() % 20);
        int humidity = 40 + Math.abs(city.hashCode() % 40);

        return String.format("城市：%s | 天气：%s | 温度：%d°C | 湿度：%d%%", city, condition, temp, humidity);
    }

    @Tool(description = "根据城市名称查询未来三天的天气预报")
    public String getWeatherForecast(
            @ToolParam(description = "城市名称") String city) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("城市：%s 未来三天天气预报：\n", city));

        LocalDateTime now = LocalDateTime.now();
        String[] conditions = {"晴天", "多云", "阴天", "小雨", "大雨", "雷阵雨"};

        for (int i = 1; i <= 3; i++) {
            String date = now.plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd"));
            String condition = conditions[Math.abs((city + i).hashCode()) % conditions.length];
            int highTemp = 20 + Math.abs((city + i).hashCode()) % 15;
            int lowTemp = highTemp - 5 - Math.abs((city + i).hashCode()) % 8;
            sb.append(String.format("  %s: %s, %d°C/%d°C\n", date, condition, highTemp, lowTemp));
        }

        return sb.toString();
    }
}
