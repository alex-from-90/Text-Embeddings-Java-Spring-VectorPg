package ru.alex.vector.processor;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Класс для обработки текста из PDF-файла
@Component
public class TextProcessor {

    // Логгер для записи информационных и ошибочных сообщений
    private static final Logger logger = LoggerFactory.getLogger(TextProcessor.class);

    // Метод для извлечения текста из PDF-файла и разбиения его на сегменты
    public List<String> extractTextsFromPDF(String filePath) {
        // Загрузка документов из PDF-файла с помощью FileSystemDocumentLoader и ApachePdfBoxDocumentParser
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(
                Paths.get(filePath),
                new ApachePdfBoxDocumentParser()
        );

        // Разбивка документов на сегменты с помощью DocumentByParagraphSplitter
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(600, 0);
        List<String> result = new ArrayList<>();
        for (Document document : documents) {
            List<TextSegment> textSegments = splitter.split(document);
            for (TextSegment segment : textSegments) {
                // Добавление текста сегмента в список результатов
                result.add(segment.text());
            }
        }

        logger.info("Number of segments: " + result.size());
        return result;
    }
}
