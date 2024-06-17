package ru.alex.vector.database;

import jakarta.annotation.PostConstruct;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.alex.vector.processor.TextProcessor;
import ru.alex.vector.service.EmbeddingService;
import ru.alex.vector.util.FloatToArray;

import java.util.List;

@Service
public class DatabaseInitializer {

    // Логгер для записи информационных и ошибочных сообщений
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    // Сервис для получения векторного представления текста
    private final EmbeddingService embeddingService;

    // Репозиторий для работы с базой данных документов
    private final DocumentRepository documentRepository;

    // Процессор для извлечения текста из PDF-файла и преобразования вектора в массив float
    private final TextProcessor textProcessor;
    private final FloatToArray floatToArray;

    // Путь к PDF-файлу, из которого извлекается текст
    @Value("${app.filePath}")
    private String filePath;

    // Тип текста, используемый для получения векторного представления
    @Value("${app.textType}")
    private String textType;

    // Конструктор класса, инициализирующий поля сервиса вложений, репозитория документов и процессора текста
    public DatabaseInitializer(EmbeddingService embeddingService, DocumentRepository documentRepository, TextProcessor textProcessor, FloatToArray floatToArray) {
        this.embeddingService = embeddingService;
        this.documentRepository = documentRepository;
        this.textProcessor = textProcessor;
        this.floatToArray = floatToArray;
    }

    // Метод, выполняющий инициализацию базы данных
    @PostConstruct
    public void initializeDatabase() {
        try {
            // Извлечение текста из PDF-файла
            List<String> docTexts = textProcessor.extractTextsFromPDF(filePath);

            // Обработка каждого извлеченного текста
            for (String docText : docTexts) {
                // Проверка наличия документа с таким же содержимым в базе данных
                List<DocumentEntry> existingDocs = documentRepository.findByContent(docText);

                // Если документ не существует, то он добавляется в базу данных
                if (existingDocs.isEmpty()) {
                    // Получение векторного представления текста
                    RealVector embedding = embeddingService.getEmbedding(docText, textType);

                    // Создание нового объекта документа
                    DocumentEntry documentEntry = new DocumentEntry();

                    // Установка содержимого и векторного представления документа
                    documentEntry.setContent(docText);
                    documentEntry.setEmbedding(floatToArray.toFloatArray(embedding.toArray()));

                    // Сохранение документа в базе данных
                    documentRepository.save(documentEntry);
                }
            }

            logger.info("Database initialization completed.");
        } catch (Exception e) {

            logger.error("Error occurred while initializing the database: ", e);
        }
    }
}
