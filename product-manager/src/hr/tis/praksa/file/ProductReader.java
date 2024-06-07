package hr.tis.praksa.file;

import hr.tis.praksa.model.Product;
import hr.tis.praksa.model.ProductsMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductReader {

    public static ProductsMetadata read(String fileName) {

        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(FileSystemConfiguration.PRODUCTS_FILES_FOLDER_PATH.resolve(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                products.add(mapToProduct(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProductsMetadata productsMetadata = new ProductsMetadata();
        String[] name = fileName.split("_");

        productsMetadata.setId(Long.valueOf(name[0]));
        productsMetadata.setCreatedTime(LocalDate.parse(name[1]));
        productsMetadata.setTitle(name[2]);

        productsMetadata.setProducts(products);

        return productsMetadata;

    }

    private static Product mapToProduct(String line) {
        String name = line.substring(0, 100).trim();
        String priceString = line.substring(100, 110).trim();
        BigDecimal price = new BigDecimal(priceString);
        return new Product(name, price);
    }
}
