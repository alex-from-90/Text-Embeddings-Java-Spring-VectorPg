package ru.alex.vector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alex.vector.service.TextEmbeddingService;

@RestController
public class TextEmbeddingController {

    private final TextEmbeddingService textEmbeddingService;

    @Autowired
    public TextEmbeddingController(TextEmbeddingService textEmbeddingService) {
        this.textEmbeddingService = textEmbeddingService;
    }

    @GetMapping("/most-similar-doc")
    public String getMostSimilarDoc(@RequestParam String query) throws Exception {
        return textEmbeddingService.getMostSimilarDoc(query);
    }
}
