package com.ai.qa.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@Slf4j
public class GeminiRawClient {
    private static final String NEW_KEY3 = "AIzaSyAbxmlgB0gqW8XZj4zIANvUVkLRb0ny604";
    private static final String NEW_KEY2 ="AIzaSyBrqOBWIMG3e6djehrnh9ZT9eWyzBVgM5o";
    private static final String NEW_KEY ="AIzaSyAukmmzd9vZj8TUPGhB3uUlWp_noYquQi8";
    //private static final String KEY = "AIzaSyD246BGbklt-FtwygwPYKam_b6N6Sjxdtc";
    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + NEW_KEY3;
    private static final ObjectMapper MAPPER = new ObjectMapper();

   /* public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        askQuestion("今天天气怎么样");
    }*/

    public  String askQuestion(String question) throws IOException, InterruptedException, URISyntaxException {
        String json = String.format("""
    {
      "contents": [{
        "parts": [{"text": "%s"}]
      }]
    }
    """, question);

        HttpRequest req = HttpRequest.newBuilder(new URI(URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        log.info("GeminiRawClient request:{}",req);
        HttpResponse<String> resp =
                HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        log.info("GeminiRawClient response:{}",resp);
        if (resp.statusCode() != 200) {
            log.info("HTTP " + resp.statusCode() + " " + new String(resp.body()));
            return "我服务得太累了，想休息一下，麻烦待会再问";
        }
       String body = resp.body();

        // 直接反序列化成实体
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(resp.body());
        String answer = root.at("/candidates/0/content/parts/0/text").asText();

        log.info("GeminiRawClient final anwser:{}",answer);
        return answer;
    }
    /*public static void main(String[] args) throws Exception {
        String json = """
                {
                  "contents": [{
                    "parts": [{"text": "用一句话介绍 Gemini"}]
                  }]
                }
                """;
        String text = "今天多少号";
        String json1 = String.format("""
    {
      "contents": [{
        "parts": [{"text": "%s"}]
      }]
    }
    """, text);

        HttpRequest req = HttpRequest.newBuilder(new URI(URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();

        HttpResponse<String> resp =
                HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

        System.out.println("code = " + resp.statusCode());
        System.out.println("body = " + resp.body());
    }*/
}
