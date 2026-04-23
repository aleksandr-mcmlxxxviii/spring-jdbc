package com.mvc.springdatajdbc.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookUpdateDTO {

    @Positive(message = "ID должен быть положительным")
    private Long id;

    @Size(min = 1, max = 255, message = "Название от 1 до 255 символов")
    private String title;

    @Size(min = 1, max = 255, message = "Автор от 1 до 255 символов")
    private String author;

    @Past(message = "Дата публикации должна быть в прошлом")
    private LocalDateTime publicationYear;
}
