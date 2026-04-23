package com.mvc.springdatajdbc.controller;

import com.mvc.springdatajdbc.dto.BookDTO;
import com.mvc.springdatajdbc.dto.BookUpdateDTO;
import com.mvc.springdatajdbc.dto.page.PageRequest;
import com.mvc.springdatajdbc.dto.page.PageResult;
import com.mvc.springdatajdbc.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.debug("REST request to get all books");
        List<BookDTO> books = bookService.findAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        log.debug("REST request to get book with id: {}", id);
        BookDTO book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO dto) {
        log.debug("REST request to create book: {}", dto);
        BookDTO createdBook = bookService.createBook(dto);
        return ResponseEntity
                .created(URI.create("/api/books/" + createdBook.getId()))
                .body(createdBook);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateDTO dto) {
        BookDTO updatedBook = bookService.updateBook(id, dto);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageResult<BookDTO>> getBooksPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        PageRequest pageRequest = new PageRequest(page, size, sortBy, direction);
        PageResult<BookDTO> result = bookService.findAllPages(pageRequest);

        return ResponseEntity.ok(result);
    }
}
