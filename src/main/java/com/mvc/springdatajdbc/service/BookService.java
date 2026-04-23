package com.mvc.springdatajdbc.service;

import com.mvc.springdatajdbc.cache.BookCountCache;
import com.mvc.springdatajdbc.dto.BookDTO;
import com.mvc.springdatajdbc.dto.BookUpdateDTO;
import com.mvc.springdatajdbc.dto.page.PageRequest;
import com.mvc.springdatajdbc.dto.page.PageResult;
import com.mvc.springdatajdbc.entity.Book;
import com.mvc.springdatajdbc.exception.ResourceNotFoundException;
import com.mvc.springdatajdbc.mapper.BookMapper;
import com.mvc.springdatajdbc.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookCountCache bookCountCache;

    @Transactional(readOnly = true)
    public PageResult<BookDTO> findAllPages(PageRequest pageRequest) {
        List<Book> books  = bookRepository.findAllPages(pageRequest);

        long totalElements = bookCountCache.getTotalBookCount();

        List<BookDTO> dtoPage = books.stream()
                .map(bookMapper::toDto)
                .toList();

        return new PageResult<>(
                dtoPage,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalElements
        );
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookDTO findBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return bookMapper.toDto(book);
    }

    @Transactional
    public BookDTO createBook(BookDTO dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("ID не должен передаваться при создании книги");
        }
        Book book = bookMapper.toEntity(dto);
        Book savedBook = bookRepository.save(book);
        bookCountCache.evictCache();
        return bookMapper.toDto(savedBook);
    }

    @Transactional
    public BookDTO updateBook(Long id, BookUpdateDTO dto) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        bookMapper.updateNonNull(dto, existingBook);

        Book updatedBook = bookRepository.updateById(id, existingBook).orElseThrow(() -> new ResourceNotFoundException(id));
        return bookMapper.toDto(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)){
            throw new ResourceNotFoundException(id);
        }
        bookCountCache.evictCache();
        bookRepository.deleteById(id);
    }
}
