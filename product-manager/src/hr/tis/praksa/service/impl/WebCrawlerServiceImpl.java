package hr.tis.praksa.service.impl;

import hr.tis.praksa.model.Product;
import hr.tis.praksa.model.ProductsMetadata;
import hr.tis.praksa.service.WebCrawlerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WebCrawlerServiceImpl implements WebCrawlerService {

    private final String URL = "https://www.konzum.hr/web/posebne-ponude";
    private final int MAX_NUM_OF_ITERATIONS = 2;

    @Override
    public ProductsMetadata fetchProducts() {
        List<Product> productList = new ArrayList<>();

        String title = null;
        int i = 0;

        while (true) {
            if (i == MAX_NUM_OF_ITERATIONS)
                break;

            Document doc = null;

            try {
                doc = Jsoup.connect(URL + "?page=" + (i + 1)).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (title == null)
                title = doc.title();

            Elements products = doc.select("div.product-wrapper");

            if (products.isEmpty())
                break;

            for (Element product : products) {
                Element imgElement = product.selectFirst("img");
                String altText = "";
                if (imgElement != null) {
                    altText = imgElement.attr("alt");
                } else {
                    System.out.println("Nema naziva.");
                }

                Element price = product.selectFirst("span.price--kn");
                String value = "";
                if (price != null && price.hasText()) {

                    Element priceDecimal = product.selectFirst("span.price--li");

                    if (priceDecimal != null && priceDecimal.hasText()) {
                        value = price.text() + "." + priceDecimal.text();
                    } else {
                        value = price.text() + "." + "00";
                    }

                    Product p = new Product();
                    p.setName(altText);
                    BigDecimal v = new BigDecimal(value);
                    p.setPrice(v);
                    productList.add(p);
                }
            }
            i++;
        }
        return new ProductsMetadata(null, LocalDate.now(), title, productList);
    }
}