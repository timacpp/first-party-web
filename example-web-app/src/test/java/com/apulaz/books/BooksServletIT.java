package com.apulaz.books;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.assertj.core.api.Assertions;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BooksServletIT {
    private static final String URL = "http://localhost:8081/books";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Server HTTP_SERVER = new Server(8081);

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
            .withUsername("root")
            .withPassword("root")
            .withDatabaseName("books")
            .withEnv("MYSQL_ROOT_HOST", "%")
            .withEnv("MYSQL_TCP_PORT", "3307")
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(3307), new ExposedPort(3306)))
            ));

    @BeforeAll
    static void start() throws Exception {
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BooksServlet.class, "/books");
        HTTP_SERVER.setHandler(servletHandler);
        HTTP_SERVER.start();
    }

    @AfterAll
    static void stop() throws Exception {
        HTTP_SERVER.stop();
        HTTP_SERVER.join();
    }

    @Test
    @Order(1)
    void shouldRespondWithEmptyJsonArrayWhenGettingNoBooksAndNoneExist() throws Exception {
        HttpRequest findRequest = HttpRequest.newBuilder().uri(buildURI()).GET().build();
        HttpResponse<String> response = HTTP_CLIENT.send(findRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("[]");
    }

    @Test
    @Order(2)
    void shouldRespondWithNotFoundWhenFindingNonExistingBook() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildURI("?id=1"))
                .GET()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @Order(3)
    void shouldRespondWithNotFoundWhenDeletingNonExistingBook() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildURI("?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @Order(4)
    void shouldRespondWithBadRequestForInvalidJson() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildURI())
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(400);
        Assertions.assertThat(response.body()).contains("\"message\": \"Invalid book json\"");
    }

    @Test
    @Order(5)
    void shouldPostBook() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildURI())
                .POST(HttpRequest.BodyPublishers.ofString(bookJson()))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo(bookJson(1));
    }

    @Test
    @Order(6)
    void shouldGetBookById() throws Exception {
        HttpRequest findRequest = HttpRequest.newBuilder().uri(buildURI("?id=1")).GET().build();
        HttpResponse<String> response = HTTP_CLIENT.send(findRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo(bookJson(1));
    }

//    @Test
//    @Order(7)
//    void shouldFindAllBooks() throws Exception {
//        HttpRequest findRequest = HttpRequest.newBuilder().uri(buildURI()).GET().build();
//        HttpResponse<String> response = HTTP_CLIENT.send(findRequest, HttpResponse.BodyHandlers.ofString());
//
//        Assertions.assertThat(response.statusCode()).isEqualTo(200);
//        Assertions.assertThat(response.body()).isEqualTo(
//                        """
//                        [
//                            %s
//                        ]""".formatted(bookJson(1)));
//    }

    @Test
    @Order(8)
    void shouldPatchBook() throws Exception {
        HttpRequest updateRequest = HttpRequest.newBuilder().uri(buildURI("?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(bookJson(1, "new name")))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).contains("\"title\": \"new name\"");
    }

    @Test
    @Order(9)
    void shouldDeleteBookById() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildURI("?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
    }


    private URI buildURI() throws URISyntaxException {
        return buildURI("");
    }

    private URI buildURI(String endpoint) throws URISyntaxException {
        return new URI(URL + endpoint);
    }

    private String bookJson() {
        return """
        {
            "title": "cool book",
            "author": "me",
            "date": "2023-11-19"
        }""";
    }

    private String bookJson(long id) {
        return bookJson(id, "cool book");
    }


    private String bookJson(long id, String title) {
        return """
        {
            "id": %d,
            "title": "%s",
            "author": "me",
            "date": "2023-11-19"
        }""".formatted(id, title);
    }
}
