package hr.tis.praksa.repository;

import hr.tis.praksa.model.ProductsMetadata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryInMemory implements ProductRepository {

    private final String DOES_NOT_EXIST = "Record doesn't exist.";


    private static final List<ProductsMetadata> PRODUCTS_METADATA_LIST = new ArrayList<>();

    @Override
    public Long insertProducts(ProductsMetadata productsMetadata) {
        productsMetadata.setId((long) (PRODUCTS_METADATA_LIST.size() + 1));
        PRODUCTS_METADATA_LIST.add(productsMetadata);
        return (long) PRODUCTS_METADATA_LIST.size();
    }

    @Override
    public BigDecimal fetchSumOfPrices(LocalDate createdDate) {
        return calculateSumOfPrices(getLatestProductRecord(createdDate).getProducts());
    }

    @Override
    public BigDecimal fetchSumOfPrices(Long id) {
        for (ProductsMetadata productsMetadata : PRODUCTS_METADATA_LIST) {
            if (productsMetadata.getId().equals(id)) {
                return calculateSumOfPrices(productsMetadata.getProducts());
            }
        }
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    /*
    Dohvatiti najažurniji zapis za taj datum
     */
    @Override
    public ProductsMetadata fetchProductsRecord(LocalDate createdDate) {
        return getLatestProductRecord(createdDate);
    }

    @Override
    public ProductsMetadata fetchProductsRecord(Long id) {
        return getLatestProductRecord(id);
    }

    @Override
    public Integer fetchProductsRecordCount() {
        return PRODUCTS_METADATA_LIST.size();
    }

    private ProductsMetadata getLatestProductRecord(LocalDate createdDate) {
        ProductsMetadata latestProductRecord = null;

        for (ProductsMetadata productsMetadata : PRODUCTS_METADATA_LIST) {
            LocalDate recordDate = productsMetadata.getCreatedTime();

            if (latestProductRecord == null || recordDate.equals(createdDate) && latestProductRecord.getId() < productsMetadata.getId()) {
                latestProductRecord = productsMetadata;
            }
        }

        if (latestProductRecord == null)
            throw new RuntimeException(DOES_NOT_EXIST);

        return latestProductRecord;
    }

    private ProductsMetadata getLatestProductRecord(Long id) {
        for (ProductsMetadata productsMetadata : PRODUCTS_METADATA_LIST) {
            if (productsMetadata.getId().equals(id)) {
                return productsMetadata;
            }
        }
        throw new RuntimeException(DOES_NOT_EXIST);
    }

}
