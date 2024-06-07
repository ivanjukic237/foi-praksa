package hr.tis.praksa.service.impl;

import hr.tis.praksa.model.ProductsMetadata;
import hr.tis.praksa.repository.ProductRepository;
import hr.tis.praksa.service.ProductService;
import hr.tis.praksa.service.WebCrawlerService;

import java.time.LocalDate;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final WebCrawlerService webCrawlerService = new WebCrawlerServiceImpl();

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductsMetadata fetchProductsFromWeb() {
        return webCrawlerService.fetchProducts();
    }

    @Override
    public ProductsMetadata saveProductsFromWeb() {
        ProductsMetadata productsMetadata = webCrawlerService.fetchProducts();

        Long id = productRepository.insertProducts(productsMetadata);

        return productRepository.fetchProductsRecord(id);
    }

    @Override
    public ProductsMetadata getProductsForDate(LocalDate createdDate) {
        return productRepository.fetchProductsRecord(createdDate);
    }


   /* private static void db() throws IOException {
        productRepository = new ProductRepositoryDB();


        productRepository.insertProducts(new WebCrawlerServiceImpl().fetchProducts());

        System.out.println(productRepository.fetchProductsRecord(2L));
        System.out.println(productRepository.fetchProductsRecord(LocalDate.now()));
        System.out.println(productRepository.fetchSumOfPrices(2L));
        System.out.println(productRepository.fetchSumOfPrices(LocalDate.now()));
    }

    private static void file() throws IOException {
        productRepository = new ProductRepositoryFile();


        productRepository.insertProducts(new WebCrawlerServiceImpl().fetchProducts());

        System.out.println(productRepository.fetchProductsRecord(2L));
        System.out.println(productRepository.fetchProductsRecord(LocalDate.now()));
        System.out.println(productRepository.fetchSumOfPrices(2L));
        System.out.println(productRepository.fetchSumOfPrices(LocalDate.now()));
    }


    private static void memory() {
        productRepository = new ProductRepositoryInMemory();

        ProductsRecord productsRecord = new ProductsRecord();

        productsRecord.setId(1L);
        productsRecord.setCreatedTime(LocalDate.now());
        productsRecord.setTitle("TITLE");
        productsRecord.setProducts(
                Arrays.asList(new Product("NAME", BigDecimal.ZERO),
                        new Product("NAME", BigDecimal.TEN)));
        productRepository.insertProducts(productsRecord);

        ProductsRecord productsRecord1 = new ProductsRecord();

        productsRecord1.setId(2L);
        productsRecord1.setCreatedTime(LocalDate.now());
        productsRecord1.setTitle("TITLE");
        productsRecord1.setProducts(
                Arrays.asList(new Product("NAME", BigDecimal.TEN),
                        new Product("NAME", BigDecimal.TEN)));
        productRepository.insertProducts(productsRecord1);


        System.out.println(productRepository.fetchProductsRecord(1L));
        System.out.println(productRepository.fetchProductsRecord(LocalDate.now()));
        System.out.println(productRepository.fetchSumOfPrices(1L));
        System.out.println(productRepository.fetchSumOfPrices(LocalDate.now()));
    }*/

}
