package com.apulaz.books;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class BooksApplication {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BooksServlet.class, "/books");
        server.setHandler(servletHandler);
        server.start();
    }
}
