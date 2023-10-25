package com.apulaz.books;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BooksServlet extends HttpServlet {
    private static final String ERROR_RESPONSE = """
    {
        "error": "%s",
        "message": "%s"
    }
    """;

    public BooksService booksService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.booksService = new BooksService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String responseJson;

        if (request.getParameter("id") != null) {
            long id = Long.parseLong(request.getParameter("id"));
            Book book = booksService.find(id);
            response.setStatus(HttpServletResponse.SC_OK);
            responseJson = book != null ? book.toJson() : null;
        } else {
            List<Book> books = booksService.findAll();
            responseJson = "[\n" + books.stream().map(Book::toJson).collect(Collectors.joining(",\n")) + "\n]";
        }

        if (responseJson != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(responseJson);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestJson = request.getReader().lines().collect(Collectors.joining("\n"));
        Long id = Optional.ofNullable(request.getParameter("id")).map(Long::parseLong).orElse(null);

        Book patch = Book.fromJson(requestJson);
        patch.setId(id);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(booksService.upsert(patch).toJson());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        if (request.getParameter("id") != null) {
            long id = Long.parseLong(request.getParameter("id"));
            boolean removed = booksService.delete(id);
            response.setStatus(removed ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
        } else {
            booksService.deleteAll();
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            super.service(request, response);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(ERROR_RESPONSE.formatted(exception.getClass().getSimpleName(), exception.getMessage()));
        } finally {
            response.setContentType("application/json");
        }
    }
}
