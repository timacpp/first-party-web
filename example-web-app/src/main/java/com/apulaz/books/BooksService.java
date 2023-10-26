package com.apulaz.books;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

    public Book upsert(Book book) {
        entityManager.getTransaction().begin();

        if (book.getId() != null) {
            entityManager.merge(book);
        } else {
            entityManager.persist(book);
        }

        entityManager.getTransaction().commit();
        return book;
    }

    public boolean delete(long id) {
        entityManager.getTransaction().begin();

        Book book = find(id);
        if (book != null) {
            entityManager.remove(book);
        }

        entityManager.getTransaction().commit();
        return book != null;
    }

    public void deleteAll() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("TRUNCATE TABLE book").executeUpdate();
        entityManager.getTransaction().commit();
    }
}
