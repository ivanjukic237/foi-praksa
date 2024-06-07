package hr.tis.praksa;

import com.sun.net.httpserver.HttpServer;
import hr.tis.praksa.controller.ProductsController;
import hr.tis.praksa.db.Database;

import java.io.*;
import java.net.InetSocketAddress;


public class App {


    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        Database.init();

        ProductsController productsController = new ProductsController();
        productsController.createContext(server);

        server.start();
        System.out.println("Server started on port 8080.");

    }

}
