package com.mvc.springdatajdbc.dto.page;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final long totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    public PageResult(List<T> content, int page, int size, long totalElements) {
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы не может быть отрицательным: " + page);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Размер страницы должен быть больше 0: " + size);
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Общее количество элементов не может быть отрицательным: " + totalElements);
        }
        int corect = 0;

        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        if (totalElements % size == 0) {
            corect = 1;
        }
        this.totalPages = totalElements / size - corect;
        this.first = page == 0;
        this.last = page + 1 >= totalPages;
        this.empty = content == null || content.isEmpty();
    }

    public boolean hasNext() { return !last; }
    public boolean hasPrevious() { return !first; }

    @Override
    public String toString() {
        return String.format("Page %d of %d (size=%d, total=%d)",
                page + 1, totalPages, size, totalElements);
    }
}
