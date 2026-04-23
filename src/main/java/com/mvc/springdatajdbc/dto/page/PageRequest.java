package com.mvc.springdatajdbc.dto.page;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @PositiveOrZero(message = "Номер страницы не может быть отрицательным")
    private int page = 0;

    @Positive(message = "Количество элементов на странице должно быть положительным")
    private int size = 10;
    private String sortBy = "id";
    private String direction = "ASC";

    public int getOffset() {
        return page * size;
    }
}
