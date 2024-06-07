package hr.tis.praksa.repository;

import hr.tis.praksa.file.ProductReader;
import hr.tis.praksa.model.ProductsMetadata;
import hr.tis.praksa.file.FileSystemConfiguration;
import hr.tis.praksa.file.ProductWriter;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductRepositoryFile implements ProductRepository {

    private final String DOES_NOT_EXIST = "Record doesn't exist.";

    @Override
    public Long insertProducts(ProductsMetadata productsMetadata) {
        productsMetadata.setId((long) (fetchProductsRecordCount() + 1));
        ProductWriter.writeProducts(productsMetadata);
        return productsMetadata.getId();
    }

    @Override
    public BigDecimal fetchSumOfPrices(LocalDate createdDate) {
        File latestFile = getLatestFile(createdDate);
        ProductsMetadata productsMetadata = ProductReader.read(latestFile.getName());

        return calculateSumOfPrices(productsMetadata.getProducts());
    }

    @Override
    public BigDecimal fetchSumOfPrices(Long id) {
        File directory = FileSystemConfiguration.PRODUCTS_FILES_FOLDER_PATH.toFile();

        ProductsMetadata productsMetadata = null;

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files == null)
                throw new RuntimeException(DOES_NOT_EXIST);

            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(String.valueOf(id))) {
                    productsMetadata = ProductReader.read(file.getName());
                }
            }
        }
        if (productsMetadata == null)
            throw new RuntimeException(DOES_NOT_EXIST);

        return calculateSumOfPrices(productsMetadata.getProducts());
    }

    @Override
    public ProductsMetadata fetchProductsRecord(LocalDate createdDate) {
        File latestFile = getLatestFile(createdDate);
        return ProductReader.read(latestFile.getName());
    }

    @Override
    public ProductsMetadata fetchProductsRecord(Long id) {
        File directory = FileSystemConfiguration.PRODUCTS_FILES_FOLDER_PATH.toFile();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files == null)
                throw new RuntimeException(DOES_NOT_EXIST);

            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(String.valueOf(id))) {
                    return ProductReader.read(file.getName());
                }
            }
        }
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Integer fetchProductsRecordCount() {
        File directory = FileSystemConfiguration.PRODUCTS_FILES_FOLDER_PATH.toFile();

        int counter = 0;

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files == null)
                return 0;

            for (File file : files) {
                if (file.isFile()) {
                    counter++;
                }
            }
        }
        return counter;
    }


    private File getLatestFile(LocalDate createdDate) {
        File directory = FileSystemConfiguration.PRODUCTS_FILES_FOLDER_PATH.toFile();

        File latestFile = null;
        Long latestId = null;

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files == null)
                throw new RuntimeException(DOES_NOT_EXIST);

            for (File file : files) {
                if (file.isFile() && file.getName().contains(createdDate.toString())) {

                    LocalDate fileDate = LocalDate.parse(file.getName().split("_")[1]);
                    long id = Long.parseLong(file.getName().split("_")[0]);

                    if (latestId == null || fileDate.equals(createdDate) && id > latestId) {
                        latestFile = file;
                        latestId = id;
                    }
                }
            }

            if (latestFile == null)
                throw new RuntimeException(DOES_NOT_EXIST);

            return latestFile;
        }
        throw new RuntimeException(DOES_NOT_EXIST);
    }

}

