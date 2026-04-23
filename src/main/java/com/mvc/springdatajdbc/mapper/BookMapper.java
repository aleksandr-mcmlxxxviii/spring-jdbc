package com.mvc.springdatajdbc.mapper;

import com.mvc.springdatajdbc.dto.BookDTO;
import com.mvc.springdatajdbc.dto.BookUpdateDTO;
import com.mvc.springdatajdbc.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublicationYear(dto.getPublicationYear());
        return book;
    }

    public BookDTO toDto(Book entity) {
        if (entity == null) {
            return null;
        }

        BookDTO dto = new BookDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setPublicationYear(entity.getPublicationYear());
        return dto;
    }

    public void updateNonNull(BookUpdateDTO source, Book target) {
        if (source.getTitle() != null) {
            target.setTitle(source.getTitle());
        }
        if (source.getAuthor() != null) {
            target.setAuthor(source.getAuthor());
        }
        if (source.getPublicationYear() != null) {
            target.setPublicationYear(source.getPublicationYear());
        }
    }
}
