package com.apulaz.books;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BooksServletIT {
    private static final String URL = "http://localhost:%s/books%s";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static Server server;
    private static int serverPort;
    private static int mysqlPort = 3307;

    @BeforeEach
    void start() throws Exception {
        server = new Server();

        Runtime.getRuntime().exec(new String[]{
                "docker", "run", "--name", "mysql-test-%s".formatted(mysqlPort), "-di", "--rm", "--network=host",
                "-e", "MYSQL_ROOT_PASSWORD=root",
                "-e", "MYSQL_DATABASE=books",
                "-e", "MYSQL_ROOT_HOST=%",
                "-e", "MYSQL_TCP_PORT=%s".formatted(mysqlPort),
                "mysql/mysql-server:latest"
        }).waitFor();

        Thread.sleep(10000);
        Connector connector = new ServerConnector(server);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BooksServlet.class, "/books");
        server.setHandler(servletHandler);

        server.start();
        serverPort = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    @AfterEach
    void clean() throws Exception {
        server.stop();
        server.join();
        Runtime.getRuntime().exec(new String[]{"docker", "stop", "mysql-test-%s".formatted(mysqlPort)}).waitFor();
    }

    @Test
    void shouldAddBook() throws Exception {
        String requestJson = """
        {
            "title": "cool book",
            "author": "me",
            "date": "2023-11-19"
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL.formatted(serverPort, "")))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        String expectedJson = """
        {
            "id": 1,
            "title": "cool book",
            "author": "me",
            "date": "2023-11-19"
        }
        """;

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(expectedJson, response.body());
    }
}
