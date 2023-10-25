package com.apulaz.books;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "book")
public class Book {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "date", nullable = false)
    private Date date;

    public Book() {

    }

    public Book(String title, String author, Date date) {
        this(null, title, author, date);
    }

    public Book(Long id, String title, String author, Date date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
    }

    public String toJson() {
        return """
                {
                    "id": %d,
                    "title": "%s",
                    "author": "%s",
                    "date": "%s"
                }""".formatted(id, title, author, DATE_FORMAT.format(date));
    }

    public static Book fromJson(String json) throws IOException {
        String title = json.substring(json.indexOf("title")).split("\"", 4)[2];
        String author = json.substring(json.indexOf("author")).split("\"", 4)[2];

        try {
            Date date = DATE_FORMAT.parse(json.substring(json.indexOf("date")).split("\"", 4)[2]);
            return new Book(title, author, date);
        } catch (ParseException exception) {
            throw new IOException(exception);
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }
}
