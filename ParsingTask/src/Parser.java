import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Parser {
    public static void main(String[] args) {
        Document html;
        try {
            html = Jsoup.connect("https://skillbox-java.github.io/").get();
            parseDocument parseD = new parseDocument(html);
            parseD.parseDoc();
        } catch (IOException e) {
            System.out.println("Что-то пошло не так...");
        }
    }
}
