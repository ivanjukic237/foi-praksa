package hr.tis.praksa.repository;

import hr.tis.praksa.db.Database;
import hr.tis.praksa.db.DatabaseException;
import hr.tis.praksa.model.Product;
import hr.tis.praksa.model.ProductsMetadata;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryDB implements ProductRepository {

    private final String DOES_NOT_EXIST = "Record doesn't exist.";

    public void insertTestData() {
        try (Connection connection = Database.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("RUNSCRIPT FROM 'classpath:insert-test-data.sql'");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public List<String> fetchAll() {
        String querySQL = "SELECT * FROM KORISNICI";

        List<String> users = new ArrayList<>();

        try (Connection connection = Database.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(querySQL);

            while (resultSet.next()) {
                Long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                users.add(String.format("%s %s", id, name));
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return users;
    }

    public String fetchKorisnik(Long id) {
        String querySQL = "SELECT * FROM KORISNICI WHERE ID = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                return String.format("%s %s", resultSet.getLong("ID"), resultSet.getString("NAME"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Long insertProducts(ProductsMetadata productsMetadata) {
        String recordSQL = "INSERT INTO PRODUCTS_METADATA (CREATED_TIME, TITLE) VALUES (?, ?)";
        String productSQL = "INSERT INTO PRODUCTS (NAME, PRICE, PRODUCTS_METADATA_ID) VALUES (?, ?, ?)";

        long recordId;

        try (Connection connection = Database.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement recordStmt = connection.prepareStatement(recordSQL, Statement.RETURN_GENERATED_KEYS)) {
                recordStmt.setDate(1, Date.valueOf(productsMetadata.getCreatedTime()));
                recordStmt.setString(2, productsMetadata.getTitle());
                recordStmt.executeUpdate();

                try (ResultSet generatedKeys = recordStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        recordId = generatedKeys.getLong(1);

                        try (PreparedStatement productStmt = connection.prepareStatement(productSQL)) {
                            for (Product product : productsMetadata.getProducts()) {
                                productStmt.setString(1, product.getName());
                                productStmt.setBigDecimal(2, product.getPrice());
                                productStmt.setLong(3, recordId);
                                productStmt.addBatch();
                            }
                            productStmt.executeBatch();
                        }
                    } else {
                        throw new RuntimeException("Creating PRODUCTS_METADATA failed, no ID obtained.");
                    }
                }
            }
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return recordId;
    }

    @Override
    public BigDecimal fetchSumOfPrices(LocalDate createdDate) {
        String querySQL = "SELECT SUM(p.PRICE) AS total FROM " +
                "PRODUCTS p LEFT JOIN PRODUCTS_METADATA PR on p.PRODUCTS_METADATA_ID = PR.ID" +
                " WHERE PR.CREATED_TIME = ? " +
                "ORDER BY PR.CREATED_TIME DESC LIMIT 1";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {

            preparedStatement.setString(1, createdDate.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBigDecimal("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public BigDecimal fetchSumOfPrices(Long productRecordId) {
        String querySQL = "SELECT SUM(PRICE) AS total FROM PRODUCTS where PRODUCTS_METADATA_ID = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {

            preparedStatement.setLong(1, productRecordId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBigDecimal("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public ProductsMetadata fetchProductsRecord(LocalDate createdDate) {
        ProductsMetadata productsMetadata = fetchProductsRecordLazy(createdDate);

        productsMetadata.setProducts(fetchProducts(productsMetadata.getId()));

        return productsMetadata;
    }

    @Override
    public ProductsMetadata fetchProductsRecord(Long id) {
        ProductsMetadata productsMetadata = fetchProductsRecordLazy(id);

        productsMetadata.setProducts(fetchProducts(productsMetadata.getId()));

        return productsMetadata;
    }

    @Override
    public Integer fetchProductsRecordCount() {
        return null;
    }

    private List<Product> fetchProducts(Long productRecordId) {
        String querySQL = "SELECT * FROM PRODUCTS WHERE PRODUCTS_METADATA_ID = ?";


        List<Product> products = new ArrayList<>();

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(querySQL)) {

            statement.setLong(1, productRecordId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setName(resultSet.getString("NAME"));
                product.setPrice(resultSet.getBigDecimal("PRICE"));
                products.add(product);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return products;
    }


    private ProductsMetadata fetchProductsRecordLazy(LocalDate createdDate) {
        String querySQL = "SELECT * FROM PRODUCTS_METADATA WHERE CREATED_TIME = ?";

        ProductsMetadata productsMetadata = null;

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(querySQL)) {

            statement.setDate(1, Date.valueOf(createdDate));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                if (id == 0)
                    throw new RuntimeException(DOES_NOT_EXIST);

                productsMetadata = new ProductsMetadata();
                productsMetadata.setId(id);
                productsMetadata.setTitle(resultSet.getString("TITLE"));
                productsMetadata.setCreatedTime(resultSet.getDate("CREATED_TIME").toLocalDate());

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productsMetadata;
    }

    private ProductsMetadata fetchProductsRecordLazy(Long productRecordId) {
        String querySQL = "SELECT * FROM PRODUCTS_METADATA WHERE ID = ?";

        ProductsMetadata productsMetadata = null;

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(querySQL)) {

            statement.setLong(1, productRecordId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                if (id == 0)
                    throw new RuntimeException(DOES_NOT_EXIST);

                productsMetadata = new ProductsMetadata();
                productsMetadata.setId(resultSet.getLong("ID"));
                productsMetadata.setTitle(resultSet.getString("TITLE"));
                productsMetadata.setCreatedTime(resultSet.getDate("CREATED_TIME").toLocalDate());

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productsMetadata;
    }
}
