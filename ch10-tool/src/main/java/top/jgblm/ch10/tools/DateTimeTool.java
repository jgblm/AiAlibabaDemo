package top.jgblm.ch10.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具
 * 演示无参和有参 Tool 的混合使用
 */
@Component
public class DateTimeTool {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool(description = "获取当前日期和时间")
    public String getCurrentDateTime() {
        return "当前时间：" + LocalDateTime.now().format(FORMATTER);
    }

    @Tool(description = "计算两个日期之间的天数差")
    public String daysBetween(
            @ToolParam(description = "开始日期，格式：yyyy-MM-dd") String startDate,
            @ToolParam(description = "结束日期，格式：yyyy-MM-dd") String endDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
            return String.format("从 %s 到 %s 共 %d 天", startDate, endDate, days);
        } catch (Exception e) {
            return "日期格式错误，请使用 yyyy-MM-dd 格式";
        }
    }

    @Tool(description = "判断给定日期是星期几")
    public String getDayOfWeek(
            @ToolParam(description = "日期，格式：yyyy-MM-dd") String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
            return String.format("%s 是 %s", date, weekDays[dayOfWeek.getValue() - 1]);
        } catch (Exception e) {
            return "日期格式错误，请使用 yyyy-MM-dd 格式";
        }
    }
}
