package top.jgblm.ch10.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 数学计算工具
 * 演示带多个参数的 Tool 定义
 */
@Component
public class CalculatorTool {

    @Tool(description = "计算两个数的加法")
    public String add(
            @ToolParam(description = "第一个数") double a,
            @ToolParam(description = "第二个数") double b) {
        double result = a + b;
        return String.format("%.2f + %.2f = %.2f", a, b, result);
    }

    @Tool(description = "计算两个数的乘法")
    public String multiply(
            @ToolParam(description = "被乘数") double a,
            @ToolParam(description = "乘数") double b) {
        double result = a * b;
        return String.format("%.2f × %.2f = %.2f", a, b, result);
    }

    @Tool(description = "计算一个数的平方根")
    public String sqrt(
            @ToolParam(description = "需要计算平方根的数，必须为非负数") double number) {
        if (number < 0) {
            return "错误：不能对负数计算平方根";
        }
        double result = Math.sqrt(number);
        return String.format("√%.2f = %.4f", number, result);
    }

    @Tool(description = "计算百分比，返回 number 的 percent 百分之多少")
    public String percentage(
            @ToolParam(description = "总数") double number,
            @ToolParam(description = "百分比，如 25 表示 25%") double percent) {
        double result = number * percent / 100;
        return String.format("%.2f 的 %.2f%% = %.2f", number, percent, result);
    }
}
