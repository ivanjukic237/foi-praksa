package hr.tis.praksa.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import hr.tis.praksa.reader.ProductHTMLReader;
import hr.tis.praksa.repository.ProductRepositoryDB;
import hr.tis.praksa.service.WebCrawlerService;
import hr.tis.praksa.service.impl.WebCrawlerServiceImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProductsController {

    private final ProductRepositoryDB productRepositoryDB = new ProductRepositoryDB();

    public void createContext(HttpServer server) {
        server.createContext("/hello", new HelloHandler());
        server.createContext("/generate-products", new GenerateProductsHandler());
        server.createContext("/get-products", new GetProductsHandler());
        server.createContext("/select-files", new SelectFileHandler());
    }

    private class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>FOI praksa</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f0f0f0;
                        text-align: center;
                        padding-top: 50px;
                    }
                    h1 {
                        color: #333;
                    }
                </style>
            </head>
            <body>
                <h1>Bok, svijete!</h1>
                <p>Šaljemo nekakav HTML koji nam se rendera na sranici :)</p>
            </body>
            </html>
            """;

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");

            try {
                byte[] bytesResponse = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytesResponse.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytesResponse);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                byte[] errorResponse = "Internal Server Error :(".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, errorResponse.length);
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse);
                os.close();
            }
        }
    }

    private class SelectFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response;
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            try {
                File fileToSend = new File(getClass().getClassLoader().getResource("select-file.html").getFile());
                response = readFileToBytes(fileToSend);
                exchange.sendResponseHeaders(200, response.length);
            } catch (Exception e) {
                e.printStackTrace();
                response = "Interna pogreška :(".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, response.length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    private class GetProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String queryString = exchange.getRequestURI().getQuery();

            Map<String, String> queryParams = parseQueryParams(queryString);
            
            System.out.println(queryParams.get("date"));

            byte[] response;
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            try {
                response = ProductHTMLReader.fetchProductsHTML("products.txt");
                exchange.sendResponseHeaders(200, response.length);
            } catch (Exception e) {
                e.printStackTrace();
                response = "Interna pogreška :(".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, response.length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    private class GenerateProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            byte[] response;
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            try {
                WebCrawlerService webCrawlerService = new WebCrawlerServiceImpl();
                //ProductWriter.writeProducts(webCrawlerService.fetchProducts());
                response = "Datoteka je uspješno generirana :)".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
            } catch (Exception e) {
                e.printStackTrace();
                response = "Interna pogreška :(".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, response.length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString != null) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }

    private byte[] readFileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        fis.close();
        bos.close();
        return bos.toByteArray();
    }
}
