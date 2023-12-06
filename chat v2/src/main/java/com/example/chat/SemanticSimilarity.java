package com.example.chat;

import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.example.chat.KeywordExtraction.keywordExtraction;
import static com.example.chat.SemanticSimilarity.LawSimilarity.calculateSimilarity;

public class SemanticSimilarity {


  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException {

    System.out.println("start to load model");
    modelLoader.loadModel();
    System.out.println("finished");
    System.out.println(semanticSimilarityDirectInDatabase("从法律角度解读这段新闻：正泰诉施耐德专利侵权案在知识产权界乃至整个法律界具有深远的影响，在中国将知识产权作为战略发展规划的今天具有不可估量的影响。\n" +
        "\n" +
        "此案不仅案情重大，而且全面展现了专利侵权诉讼中的审判程序及法律适用问题，其间涉及的诸多专业问题和诉讼技巧也很值得整理、思考和研究。\n" +
        "\n" +
        "本文将本案涉及的重要法律问题提炼出来，结合案情进行探讨，以期引发学界对专利维权相关问题的进一步研究，同时也为国内企业进行跨国知识产权诉讼提供参考。\n" +
        "\n" +
        "因此，本文具有显著的理论和实务价值。\n" +
        "\n" +
        "2006年8月2日，正泰集团股份有限公司(以下简称正泰公司)以侵犯专利权为由，将施耐德电气低压天津有限公司(以下简称施耐德公司)诉至温州市中级人民法院，要求施耐德公司停止被控侵权产品的生产和销售，并赔偿损失50万元。\n" +
        "\n" +
        "涉案专利为正泰公司于1997年11月11日向中国国家知识产权局申请并获得授权的实用新型专利“一种高分段小型断路器”，专利号为ZL97248479.5;被控侵权。\n" +
        "\n" +
        "产品为施耐德公司生产的C65系列小型断路器，2006年8月21日，施耐德公司向国家知识产权局专利复审委员会提出宣告涉案专利无效，并于2007年1月24日向温州市中级人民法院提出中止审理的书面申请。\n" +
        "\n" +
        "2007年2月5日，经过证据保全和对被告财务进行审计，正泰公司根据温州市中级人民法院委托审计的结果申请变更诉讼请求，赔偿金额由50万元增加至 3.3亿余元。"));
  }
  public static List<String> semanticSimilarityDirectInDatabase(String userMessage) throws ExecutionException, InterruptedException {
    List<String> topLaws = new ArrayList<>();
    WordVectorModel wordVectorModel = modelLoader.getModel();
    try {
      String[] userKeywords = keywordExtraction(userMessage, 100).split(",");
      System.out.println(userKeywords);

      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "wj20031012");

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT lawName, lawContent, lawExplain FROM content");

      PriorityQueue<regulationSimilarity> lawSimilarities = new PriorityQueue<>(Comparator.comparingDouble(l -> -l.similarity));

      // 遍历每条法律记录并计算相似度
      while (resultSet.next()) {
        String lawName = resultSet.getString("lawName");
        String lawExplain = resultSet.getString("lawExplain");
        String lawContent=resultSet.getString("lawContent");
        String[] lawKeywords = lawExplain.split(",");

        double similarity = calculateSimilarity(wordVectorModel, userKeywords, lawKeywords);

        lawSimilarities.add(new regulationSimilarity(lawName, lawContent, similarity));
      }

      for (int i = 0; i < 10 && !lawSimilarities.isEmpty(); i++) {
        topLaws.add(lawSimilarities.poll().lawName+":"+lawSimilarities.poll().lawContent+"\\n");
      }
      resultSet.close();
      statement.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return topLaws;
  }

  public static List<String> semanticSimilarityDirectInRegulations(String userMessage) throws ExecutionException, InterruptedException {
    List<String> topLaws = new ArrayList<>();
    WordVectorModel wordVectorModel = modelLoader.getModel();
    try {
      String[] userKeywords = keywordExtraction(userMessage, 100).split(",");
      System.out.println(userKeywords);

      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "wj20031012");

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT lawName, lawContent, lawExplain FROM content");

      PriorityQueue<regulationSimilarity> lawSimilarities = new PriorityQueue<>(Comparator.comparingDouble(l -> -l.similarity));

      // 遍历每条法律记录并计算相似度
      while (resultSet.next()) {
        String lawName = resultSet.getString("lawName");
        String lawExplain = resultSet.getString("lawExplain");
        String lawContent=resultSet.getString("lawContent");
        String[] lawKeywords = lawExplain.split(",");

        double similarity = calculateSimilarity(wordVectorModel, userKeywords, lawKeywords);

        lawSimilarities.add(new regulationSimilarity(lawName, lawContent, similarity));
      }

      for (int i = 0; i < 10 && !lawSimilarities.isEmpty(); i++) {
        topLaws.add(lawSimilarities.poll().lawName+"："+lawSimilarities.poll().lawContent+"\\n");
      }
      resultSet.close();
      statement.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return topLaws;
  }
  public static String semanticSimilarity(String userMessage,WordVectorModel wordVectorModel) {
    List<String> topLaws = new ArrayList<>();
    System.out.println("接收到模型");
    try {
      System.out.println("开始计算相似度");
      String[] userKeywords = keywordExtraction(userMessage, 50).split(",");

      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/laws", "root", "123456");

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT lawName, lawExplain FROM belong");

      PriorityQueue<LawSimilarity> lawSimilarities = new PriorityQueue<>(Comparator.comparingDouble(l -> -l.similarity));

      // 遍历每条法律记录并计算相似度
      while (resultSet.next()) {
        String lawName = resultSet.getString("lawName");
        String lawExplain = resultSet.getString("lawExplain");
        String[] lawKeywords = lawExplain.split(",");

        double similarity = calculateSimilarity(wordVectorModel, userKeywords, lawKeywords);
        lawSimilarities.add(new LawSimilarity(lawName, similarity));
      }

      for (int i = 0; i < 5 && !lawSimilarities.isEmpty(); i++) {
        topLaws.add(lawSimilarities.poll().lawName);
      }
      resultSet.close();
      statement.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return findTopRegulations(topLaws, userMessage, wordVectorModel,3).toString();
  }

  private static Map<String, List<String>> findTopRegulations(List<String> topLaws, String userMessage, WordVectorModel model, int numberOfRegulations) {
    Map<String, List<String>> lawToRegulationsMap = new HashMap<>();
    try {
      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/laws", "root", "123456");
      for (String lawName : topLaws) {
        List<regulationSimilarity> regulationSimilarities = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT lawContent, lawExplain FROM content WHERE lawName = ?");
        preparedStatement.setString(1, lawName);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
          String lawContent = resultSet.getString("lawContent");
          String lawExplain = resultSet.getString("lawExplain");
          String[] regulationKeywords = lawExplain.split(",");
          double similarity = calculateSimilarity(model, keywordExtraction(userMessage, 50).split(","), regulationKeywords);
          regulationSimilarities.add(new regulationSimilarity(lawName, lawContent, similarity));
        }

        // Sort and limit the results
        regulationSimilarities.sort(Comparator.comparingDouble(l -> -l.similarity));
        List<String> topRegulations = regulationSimilarities.stream()
            .limit(numberOfRegulations)
            .map(l -> l.lawContent)
            .collect(Collectors.toList());
        lawToRegulationsMap.put(lawName, topRegulations);

        resultSet.close();
        preparedStatement.close();
      }
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return lawToRegulationsMap;
  }



  static class regulationSimilarity {
    String lawName;
    String lawContent;
    double similarity;

    regulationSimilarity(String lawName, String lawContent, double similarity) {
      this.lawName = lawName;
      this.lawContent = lawContent;
      this.similarity = similarity;
    }
  }

  // 辅助类用于存储法律名称和相似度
  static class LawSimilarity {
    String lawName;
    double similarity;

    LawSimilarity(String lawName, double similarity) {
      this.lawName = lawName;
      this.similarity = similarity;
    }


    static double calculateSimilarity(WordVectorModel model, String[] group1, String[] group2) {
      Vector vectorGroup1 = averageVector(model, group1);
      Vector vectorGroup2 = averageVector(model, group2);
      return vectorGroup1.cosineForUnitVector(vectorGroup2);
    }

    private static Vector averageVector(WordVectorModel model, String[] keywords) {
      Vector average = new Vector(300);
      int count = 0;
      for (String keyword : keywords) {
        Vector vector = model.query(keyword);
        System.out.println(vector);
        if (vector != null) {
          average.addToSelf(vector);
          count++;
        } else
          System.out.println("关键词不在模型中: " + keyword);
      }
      if (count > 0) {
        average.normalize();
      }
      return average;
    }
  }
}
