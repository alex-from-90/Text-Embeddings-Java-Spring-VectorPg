package ru.alex.vector.service;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    private final ApiService apiService;

    // Идентификатор папки в yandexCloud для получения векторного представления
    @Value("${app.yandex.embed.folderId}")
    private String folderId;

    // ApiKey для эмбедингов в yandexCloud
    @Value("${app.yandex.ebmed.apiKey}")
    private String iamToken;

    // URL для получения векторного представления
    @Value("${embed.url}")
    private String embedUrl;

    public EmbeddingService(ApiService apiService) {
        this.apiService = apiService;
    }

    // Метод для получения векторного представления текста с помощью API Яндекса
    public RealVector getEmbedding(String text, String textType) throws Exception {
        logger.info("Generating embedding for text: {}", text);

        // Создание URI модели для получения векторного представления
        String modelUri = "emb://" + folderId + "/text-search-" + textType + "/latest";

        // Создание JSON-объекта с URI модели и текстом
        JSONObject json = new JSONObject();
        json.put("modelUri", modelUri);
        json.put("text", text);

        // Отправка JSON-объекта на сервер с помощью метода postRequest класса ApiService
        String response = apiService.postRequest(embedUrl, json.toString(), iamToken, folderId);

        logger.debug("Received response: {}", response);

        // Парсинг ответа сервера из JSON-строки в объект JSONObject
        JSONObject jsonResponse = new JSONObject(response);

        // Проверка наличия поля "embedding" в объекте JSONObject
        if (!jsonResponse.has("embedding")) {
            logger.error("Response JSON does not contain 'embedding' field: {}", jsonResponse);
            throw new Exception("Embedding not found in response");
        }

        // Извлечение массива векторного представления из объекта JSONObject
        JSONArray embeddingArray = jsonResponse.getJSONArray("embedding");
        double[] embedding = new double[embeddingArray.length()];
        for (int i = 0; i < embeddingArray.length(); i++) {
            embedding[i] = embeddingArray.getDouble(i);
        }

        // Добавление задержки в 100 мс после каждого запроса
        Thread.sleep(100);

        // Преобразование массива double в объект RealVector и возврат результата
        return new ArrayRealVector(embedding);
    }
}
