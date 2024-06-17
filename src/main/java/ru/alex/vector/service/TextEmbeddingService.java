package ru.alex.vector.service;

import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.alex.vector.database.DocumentEntry;
import ru.alex.vector.database.DocumentRepository;
import ru.alex.vector.util.FloatToArray;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextEmbeddingService {

    // Логгер для записи информационных и ошибочных сообщений
    private static final Logger logger = LoggerFactory.getLogger(TextEmbeddingService.class);

    // Сервис для получения векторного представления текста
    private final EmbeddingService embeddingService;

    // Сервис для получения ответа на запрос с помощью API Яндекса GPT
    private final GptService gptService;

    // Репозиторий для работы с базой данных документов
    private final DocumentRepository documentRepository;

    // Утилита для преобразования массива double в массив float
    private final FloatToArray floatToArray;

    // Конструктор класса, инициализирующий поля сервисов EmbeddingService и GptService, и репозитория DocumentRepository
    @Autowired
    public TextEmbeddingService(EmbeddingService embeddingService, GptService gptService, DocumentRepository documentRepository, FloatToArray floatToArray) {
        this.embeddingService = embeddingService;
        this.gptService = gptService;
        this.documentRepository = documentRepository;
        this.floatToArray = floatToArray;
    }

    // Метод для получения наиболее похожего документа на основе запроса и ответа на запрос с помощью API Яндекса GPT
    public String getMostSimilarDoc(String query) throws Exception {
        try {
            // Получение векторного представления запроса с помощью метода getEmbedding класса EmbeddingService
            RealVector queryEmbedding = embeddingService.getEmbedding(query, "query");

            // Преобразование векторного представления запроса в массив float
            float[] queryEmbeddingArray = floatToArray.toFloatArray(queryEmbedding.toArray());

            // Логирование векторного представления запроса
            logger.info("Query embedding: {}", queryEmbeddingArray);

            // Извлечение наиболее похожих документов на основе векторного представления запроса с помощью метода findMostSimilarDocument класса DocumentRepository
            List<DocumentEntry> mostSimilarDocs = documentRepository.findMostSimilarDocument(queryEmbeddingArray, PageRequest.of(0, 5));
            if (mostSimilarDocs.isEmpty()) {
                return "No similar document found.";
            }

            // Извлечение содержимого наиболее похожих документов
            List<String> mostSimilarDocContents = new ArrayList<>();
            for (DocumentEntry doc : mostSimilarDocs) {
                mostSimilarDocContents.add(doc.getContent());
            }

            // Логирование содержимого наиболее похожих документов
            logger.info("Most similar documents content: {}", mostSimilarDocContents);

            // Получение ответа на запрос с помощью метода getGptResponse класса GptService
            String gptResponse = gptService.getGptResponse(query, mostSimilarDocContents);

            // Формирование HTML-строки с запросом, наиболее похожими документами и ответом на запрос
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("<h1>Query:</h1><p>").append(query).append("</p>");
            for (int i = 0; i < mostSimilarDocContents.size(); i++) {
                responseBuilder.append("<h1>Most Similar Document Content ").append(i + 1).append(":</h1><p>")
                        .append(mostSimilarDocContents.get(i)).append("</p>");
            }
            responseBuilder.append("<h1>GPT Response:</h1><p>").append(gptResponse).append("</p>");

            // Возврат HTML-строки с запросом, наиболее похожими документами и ответом на запрос
            return responseBuilder.toString();
        } catch (Exception e) {
            logger.error("Error occurred while processing the request: ", e);
            throw e;
        }
    }

}
