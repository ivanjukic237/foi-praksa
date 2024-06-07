package hr.tis.praksa.file;

import hr.tis.praksa.model.Product;
import hr.tis.praksa.model.ProductsMetadata;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;


public class ProductWriter {

    public static void writeProducts(ProductsMetadata productsMetadata) {

        String filePath = String.format("%s_%s_%s.txt",
                productsMetadata.getId(),
                productsMetadata.getCreatedTime(),
                productsMetadata.getTitle());

        try (BufferedWriter writer = Files.newBufferedWriter(
                FileSystemConfiguration.PRODUCTS_FILES_FOLDER_PATH.resolve(filePath))) {
            for (Product product : productsMetadata.getProducts()) {
                writer.write(String.format("%-100s%10s", product.getName(), product.getPrice().toString()));
                writer.newLine();
            }
            System.out.println("File was written successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
