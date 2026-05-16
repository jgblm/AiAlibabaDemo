package top.jgblm.ch07.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 人物信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {
    private String name;        // 姓名
    private Integer age;        // 年龄
    private String occupation;  // 职业
    private String city;        // 城市
    private String description; // 个人描述
}
