package com.mvc.springdatajdbc.repository;

import com.mvc.springdatajdbc.dto.page.PageRequest;
import com.mvc.springdatajdbc.entity.Book;
import com.mvc.springdatajdbc.util.SortableFieldUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        Timestamp timestamp = rs.getTimestamp("publication_year");
        if (timestamp != null) {
            book.setPublicationYear(timestamp.toLocalDateTime());
        }
        return book;
    };

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("books")
                .usingGeneratedKeyColumns("id");
    }

    public List<Book> findAllPages(PageRequest pageRequest) {
        log.debug("Пагинация: page={}, size={}, sortBy={}, direction={}",
                pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSortBy(), pageRequest.getDirection());

        var sortableInfo = SortableFieldUtil.getSortableInfo(Book.class);

        String sortField = pageRequest.getSortBy();
        if (!sortableInfo.isSortable(sortField)) {
            sortField = sortableInfo.getDefaultSortField();
        }

        String columnName = sortableInfo.getColumnName(sortField);
        String direction = "DESC".equalsIgnoreCase(pageRequest.getDirection()) ? "DESC" : "ASC";

        String sql = String.format(
                "SELECT * FROM books ORDER BY %s %s LIMIT ? OFFSET ?",
                columnName, direction
        );

        return jdbcTemplate.query(sql, bookRowMapper,
                pageRequest.getSize(),
                pageRequest.getOffset());
    }

    public List<Book> findAll(){
        log.debug("Запрос всех книг");
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    public Optional<Book> findById(Long id){
        validateId(id);

        String sql = "SELECT * FROM books WHERE id = ?";
        try {
            Book book = jdbcTemplate.queryForObject(sql, bookRowMapper, id);
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Книга с ID {} не найдена", id);
            return Optional.empty();
        }
    }

    public Book save(Book book) {
        log.debug("Сохранение книги: {}", book.getTitle());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", book.getTitle());
        parameters.put("author", book.getAuthor());
        parameters.put("publication_year", book.getPublicationYear());

        book.setId(jdbcInsert.executeAndReturnKey(parameters).longValue());

        log.debug("Книга сохранена с ID: {}", book.getId());
        return book;
    }

    public Optional<Book> updateById(Long id, Book book) {
        validateId(id);

        String sql = "UPDATE books SET title = ?, author = ?, publication_year = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                id
        );

        if (rowsAffected > 0) {
            book.setId(id);
            return Optional.of(book);
        }

        log.debug("Книга с ID {} не найдена для обновления", id);
        return Optional.empty();
    }

    public boolean deleteById(Long id) {
        validateId(id);

        log.debug("Удаление книги с ID: {}", id);

        String sql = "DELETE FROM books WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected > 0) {
            log.debug("Книга с ID {} удалена", id);
            return true;
        } else {
            log.debug("Книга с ID {} не найдена", id);
            return false;
        }
    }

    public boolean existsById(Long id) {
        validateId(id);
        String sql = "SELECT EXISTS(SELECT 1 FROM books WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("ID книги должен быть положительным");
        }
    }

    public long getTotalBookCount() {
        String sql = "SELECT COUNT(*) FROM books";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
