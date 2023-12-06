package com.example.chat;

import com.hankcs.hanlp.HanLP;
import java.util.List;

public class KeywordExtraction {
  public static String keywordExtraction(String text, int amount) {
    // 提取关键词
    List<String> keywordList = HanLP.extractKeyword(text, amount);
    return keywordList.toString().substring(1, keywordList.toString().length() - 1).replaceAll("[\\s\\t\\n\\r]+", "");
  }

}
