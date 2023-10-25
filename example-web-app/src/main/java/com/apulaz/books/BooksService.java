package com.apulaz.books;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;

public class BooksService {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("books");
    private final EntityManager entityManager;

    public BooksService() {
        entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public BooksService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Book find(long id) {
        return entityManager.find(Book.class, id);
    }

    public List<Book> findAll() {
        return entityManager.createQuery("SELECT book FROM Book book", Book.class).getResultList();
    }

    @Transactional
    public Book upsert(Book book) {
        if (book.getId() != null) {
            entityManager.merge(book);
        } else {
            entityManager.persist(book);
        }

        return book;
    }

    @Transactional
    public boolean delete(long id) {
        Book book = find(id);
        if (book != null) {
            entityManager.remove(book);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteAll() {
        entityManager.createNativeQuery("TRUNCATE TABLE book").executeUpdate();
    }
}
