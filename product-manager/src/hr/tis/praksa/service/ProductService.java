package hr.tis.praksa.service;

import hr.tis.praksa.model.ProductsMetadata;

import java.time.LocalDate;

public interface ProductService {
    ProductsMetadata fetchProductsFromWeb();

    ProductsMetadata saveProductsFromWeb();

    ProductsMetadata getProductsForDate(LocalDate createdDate);
}
