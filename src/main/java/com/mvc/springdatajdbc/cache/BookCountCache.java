package com.mvc.springdatajdbc.cache;

import com.mvc.springdatajdbc.repository.BookRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class BookCountCache {

    private final BookRepository bookRepository;

    public BookCountCache(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Cacheable(value = "bookCount")
    public long getTotalBookCount() {
        return bookRepository.getTotalBookCount();
    }

    @CacheEvict(value = "bookCount", allEntries = true)
    public void evictCache() {}
}
