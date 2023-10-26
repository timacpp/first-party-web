package com.apulaz.books;

import java.time.Instant;
import java.util.Date;


import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.apulaz.books.mocks.EntityManagerMock;

public class BooksServiceTest {
    private EntityManager entityManagerMock;

    private BooksService booksService;

    @BeforeEach
    void setup() {
        entityManagerMock = new EntityManagerMock();
        booksService = new BooksService(entityManagerMock);
    }

    @Test
    void shouldFindBook() {
        Book book = createBook();
        entityManagerMock.persist(book);
        Assertions.assertThat(booksService.find(1L)).isEqualTo(book);
    }

    @Test
    void shouldFindAllBooks() {
        Book book1 = createBook();
        entityManagerMock.persist(book1);
        Book book2 = createBook();
        entityManagerMock.persist(book2);
        Assertions.assertThat(booksService.findAll()).containsExactly(book1, book2);
    }

    @Test
    void shouldInsertBook() {
        Book book = booksService.upsert(createBook());
        Assertions.assertThat(booksService.find(1L)).isEqualTo(book);
    }

    @Test
    void shouldUpdateBook() {
        Book book = booksService.upsert(createBook("cool book"));
        booksService.upsert(createBook(book.getId(), "even cooler book"));
        Assertions.assertThat(booksService.find(1L))
                .extracting("title")
                .isEqualTo("even cooler book");
    }

    @Test
    void shouldDeleteBook() {
        Book book = booksService.upsert(createBook());
        Assertions.assertThat(booksService.delete(book.getId())).isTrue();
        Assertions.assertThat(booksService.find(book.getId())).isNull();
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingBook() {
        Assertions.assertThat(booksService.delete(1L)).isFalse();
    }

    @Test
    void shouldDeleteAllBooks() {
        booksService.upsert(createBook());
        booksService.upsert(createBook());
        booksService.deleteAll();
        Assertions.assertThat(booksService.findAll()).isEmpty();
    }

    private Book createBook() {
        return createBook("cool book");
    }

    private Book createBook(String title) {
        return createBook(null, title);
    }

    private Book createBook(Long id, String title) {
        return new Book(id, title, "me", Date.from(Instant.EPOCH));
    }
}

