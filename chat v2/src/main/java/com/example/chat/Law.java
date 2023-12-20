package com.example.chat;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Law {
    // 编号
    private Integer lno;
    // 名称
    private String name;
    // 内容
    private String content;
    // 解释
    private String explain;


}