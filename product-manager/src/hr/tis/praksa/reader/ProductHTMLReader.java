package hr.tis.praksa.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ProductHTMLReader {

    public static byte[] fetchProductsHTML(String filePath) throws IOException {
        File fileToSend = new File(filePath);

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><style>");
        htmlBuilder.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        htmlBuilder.append("ul { list-style-type: none; padding: 0; }");
        htmlBuilder.append("li { margin-bottom: 10px; background-color: #f0f0f0; padding: 10px; border-radius: 5px; }");
        htmlBuilder.append("</style></head><body><ul>");

        BufferedReader br = new BufferedReader(new FileReader(fileToSend, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            htmlBuilder.append("<li>").append(line).append("</li>");
        }
        br.close();

        htmlBuilder.append("</ul></body></html>");

        return htmlBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

}
