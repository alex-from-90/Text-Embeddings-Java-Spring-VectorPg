package ru.alex.vector.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

// Класс для выполнения POST-запросов к API
@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    // Метод для выполнения POST-запроса к API с заданными параметрами
    public String postRequest(String url, String json, String apiKey, String folderId) throws Exception {
        logger.info("Sending POST request to URL: {}", url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            // Установка JSON-строки в качестве тела запроса
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            // Установка заголовков Authorization и x-folder-id для аутентификации и указания папки
            post.setHeader("Authorization", "Api-Key " + apiKey);
            post.setHeader("x-folder-id", folderId);

            // Выполнение запроса и чтение ответа из потока ввода
            try (CloseableHttpResponse response = httpClient.execute(post);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String responseString = result.toString();

                logger.debug("Full server response: {}", responseString);

                return responseString;
            }
        }
    }
}
