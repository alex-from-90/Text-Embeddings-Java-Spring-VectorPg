package ru.alex.vector.database;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Интерфейс репозитория для работы с сущностью DocumentEntry в базе данных
@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntry, Long> {

    // Метод для поиска документа, который наиболее похож на заданный вектор embedding
    @Query("FROM DocumentEntry ORDER BY l2_distance(embedding, :embedding)")
    List<DocumentEntry> findMostSimilarDocument(@Param("embedding") float[] embedding, Pageable pageable);

    // Метод для поиска документов по заданному содержимому content
    @Query("FROM DocumentEntry WHERE content = :content")
    List<DocumentEntry> findByContent(@Param("content") String content);
}
