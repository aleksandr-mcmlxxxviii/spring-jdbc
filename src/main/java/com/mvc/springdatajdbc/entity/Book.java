package com.mvc.springdatajdbc.entity;

import com.mvc.springdatajdbc.annotation.Sortable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table("books")
public class Book {

    @Id
    @Sortable(priority = 1, defaultOrder = "ASC")
    private Long id;

    @Sortable
    private String title;

    @Sortable
    private String author;

    @Sortable(columnName = "publication_year")
    private LocalDateTime publicationYear;
}
