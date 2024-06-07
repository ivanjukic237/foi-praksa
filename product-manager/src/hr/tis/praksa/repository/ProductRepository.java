package hr.tis.praksa.repository;

import hr.tis.praksa.model.Product;
import hr.tis.praksa.model.ProductsMetadata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ProductRepository {

    Long insertProducts(ProductsMetadata productsMetadata);

    BigDecimal fetchSumOfPrices(LocalDate createdDate);

    BigDecimal fetchSumOfPrices(Long id);

    ProductsMetadata fetchProductsRecord(LocalDate createdDate);

    ProductsMetadata fetchProductsRecord(Long id);

    Integer fetchProductsRecordCount();

    default BigDecimal calculateSumOfPrices(List<Product> products) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Product product : products) {
            sum = sum.add(product.getPrice());
        }
        return sum;
    }

}
