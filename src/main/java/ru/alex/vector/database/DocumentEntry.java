package ru.alex.vector.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

// Класс, представляющий сущность документа в базе данных
@Entity
@Table(name = "vector_store")
@Getter
@Setter
public class DocumentEntry {
    // Уникальный идентификатор документа, генерируемый автоматически
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID id;

    // Векторное представление документа, сохраненное в базе данных
    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 256) // Укажите правильное количество измерений вектора
    private float[] embedding;

    // Содержимое документа, сохраненное в базе данных
    @Column(columnDefinition = "text")
    private String content;
}
