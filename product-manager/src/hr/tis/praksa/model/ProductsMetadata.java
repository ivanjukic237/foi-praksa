package hr.tis.praksa.model;

import java.time.LocalDate;
import java.util.List;

public class ProductsMetadata {

    private Long id;
    private LocalDate createdTime;
    private String title;
    private List<Product> products;

    public ProductsMetadata(Long id, LocalDate createdTime, String title, List<Product> products) {
        this.id = id;
        this.createdTime = createdTime;
        this.title = title;
        this.products = products;
    }

    public ProductsMetadata() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDate createdTime) {
        this.createdTime = createdTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(title)
                .append(" ")
                .append(createdTime);

        for (Product product : products) {
            stringBuilder.append(product);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
