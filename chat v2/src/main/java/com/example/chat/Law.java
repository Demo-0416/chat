package com.example.chat;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Law {
   private Integer lno;
   private String name;
   private String content;
   private String explain;


}
