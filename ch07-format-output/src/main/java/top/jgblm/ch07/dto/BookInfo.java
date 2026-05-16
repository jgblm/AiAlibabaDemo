package top.jgblm.ch07.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 书籍信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfo {
    private String title;       // 书名
    private String author;      // 作者
    private String genre;       // 类型
    private Double rating;      // 评分（1-5）
    private List<String> tags;  // 标签列表
    private String summary;     // 简介
}
