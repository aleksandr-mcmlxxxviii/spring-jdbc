package com.mvc.springdatajdbc.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookDTO {

    @Null(message = "ID не должен передаваться")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 255, message = "Название от 1 до 255 символов")
    private String title;

    @NotBlank(message = "Автор не может быть пустым")
    @Size(min = 1, max = 255, message = "Автор от 1 до 255 символов")
    private String author;

    @Past(message = "Дата публикации должна быть в прошлом")
    @NotNull(message = "Дата должна быть указана")
    private LocalDateTime publicationYear;
}
