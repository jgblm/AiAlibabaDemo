package top.jgblm.ch07.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 天气信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfo {
    private String city;        // 城市
    private String date;        // 日期
    private String condition;   // 天气状况
    private Integer temperature;// 温度
    private Integer humidity;   // 湿度
    private String wind;        // 风向
}
