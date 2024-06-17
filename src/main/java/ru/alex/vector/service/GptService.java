package ru.alex.vector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GptService {

    private static final Logger logger = LoggerFactory.getLogger(GptService.class);

    private final ApiService apiService;

    // API-ключ для yandexGpt
    @Value("${gpt.yandex.apiKey}")
    private String apiKey;

    // Идентификатор папки для запроса к GPT
    @Value("${gpt.yandex.folderId}")
    private String folderId;

    // URL для запроса к GPT
    @Value("${gpt.url}")
    private String gptUrl;

    // Конструктор класса, инициализирующий поле сервиса API
    public GptService(ApiService apiService) {
        this.apiService = apiService;
    }

    // Метод для извлечения текста ответа из JSON-строки
    private static String extractTextFromJson(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"text\":\"") + 8;
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }

    // Метод для получения ответа на запрос с помощью API Яндекса GPT
    public String getGptResponse(String query, List<String> mostSimilarDocContents) throws Exception {
        String role = "user";

        // Формирование запроса к GPT на основе входного запроса и списка наиболее похожих документов
        StringBuilder sb = new StringBuilder();
        sb.append("Ответьте на следующий вопрос: ").append(query).append("\n\n");
        sb.append("Основывайте свой ответ на следующей информации:%s\n");
        sb.append("Не добавляй в свой ответ ничего лишнего. Отвечай точно по тексту\n");
        for (String docContent : mostSimilarDocContents) {
            sb.append("- ").append(docContent).append("\n");
        }

        // Экранирование специальных символов в запросе
        String gptQuery = sb.toString()
                .replace("\n", "\\n")  // Экранирование новой строки
                .replace("\"", "\\\"") // Экранирование кавычек
                .replace("\r", "\\r"); // Экранирование символа возврата каретки

        // Формирование тела запроса в виде JSON-строки
        String requestBodyTemplate = "{\"modelUri\":\"gpt://%s/yandexgpt-lite\",\"completionOptions\":{\"stream\":false,\"temperature\":0.6,\"maxTokens\":\"2000\"},\"messages\":[{\"role\":\"%s\",\"text\":\"%s\"}]}";
        String requestBody = String.format(requestBodyTemplate, folderId, role, gptQuery);

        logger.info("Sending GPT request with body: {}", requestBody);

        return extractTextFromJson(apiService.postRequest(gptUrl, requestBody, apiKey, folderId));
    }
}
