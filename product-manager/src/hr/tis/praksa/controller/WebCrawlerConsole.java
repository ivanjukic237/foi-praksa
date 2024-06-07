package hr.tis.praksa.controller;

import hr.tis.praksa.repository.ProductRepositoryDB;
import hr.tis.praksa.repository.ProductRepositoryFile;
import hr.tis.praksa.repository.ProductRepositoryInMemory;
import hr.tis.praksa.service.ProductService;
import hr.tis.praksa.service.impl.ProductServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class WebCrawlerConsole {

    private static ProductService productService = null;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to FOI web-crawler app!\nStart with 'help' command to see the list of commands.");

        while (true) {

            String input = scanner.nextLine().toLowerCase().trim();

            try {
                if (input.equals("help")) {
                    System.out.println("Choose how to save the products from commands: (file, in-memory, db)\n" +
                            "Fetch products from web: fetch-products\n" +
                            "Fetch products for date in format yyyy-MM-dd: fetch-products {date}\n" +
                            "Fetch and save products: save-products\n" +
                            "Exit console: exit");
                } else if (input.equals("fetch-products")) {
                    if (isProductRepositorySet()) {
                        System.out.println(productService.fetchProductsFromWeb());
                    }
                } else if (input.equals("save-products")) {
                    if (isProductRepositorySet()) {
                        System.out.println(productService.saveProductsFromWeb());
                    }
                } else if (input.startsWith("fetch-products")) {
                    if (isProductRepositorySet()) {
                        String[] inputs = input.split(" ");
                        if (inputs.length != 2) {
                            System.out.println("Unknown command.");
                        } else {
                            LocalDate date = getLocalDateFromString(inputs[1]);
                            if (date == null)
                                continue;
                            System.out.println(productService.getProductsForDate(date));
                        }
                    }
                } else if (Arrays.asList("file", "in-memory", "db").contains(input)) {
                    if (input.equals("file")) {
                        productService = new ProductServiceImpl(new ProductRepositoryFile());
                    } else if (input.equals("in-memory")) {
                        productService = new ProductServiceImpl(new ProductRepositoryInMemory());
                    } else {
                        productService = new ProductServiceImpl(new ProductRepositoryDB());
                    }
                } else if (input.equals("exit")) {
                    System.out.println("Exiting console...");
                    break;
                } else {
                    System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isProductRepositorySet() {
        if (productService == null) {
            System.out.println("Choose how to save the products from commands: (file, in-memory, db)");
            return false;
        }
        return true;
    }

    private static LocalDate getLocalDateFromString(String localDate) {
        try {
            return LocalDate.parse(localDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Date is not in the correct format.");
        }
        return null;
    }

}
