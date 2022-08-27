import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class parseDocument {
    Document html;
    parseDocument(Document html) {
        this.html = html;
    }
    void parseDoc () {
        try {
            JSONObject metro = new JSONObject();//"map.json");

            /*Станции*/
            JSONObject stationsJson = new JSONObject();   //Список всех станций в Json

            Elements li = html.select("#metrodata > div > div > div");   //Список всех станций, проблемы устранены
            for (Element element : li) {
                JSONArray station = new JSONArray();
                Elements el = element.select("p");

                for (Element e : el) {
                    String text = e.text().split(" ", 2)[1].trim();
                    station.put(text);
                }
                stationsJson.put(element.attributes().get("data-line"), station);
            }

            metro.put("stations", stationsJson);

            /*Соединения*/
            JSONArray connectionsArray = new JSONArray();    //Список всех соединений в Json

            Set<String> alreadyExists = new HashSet<>();

            for (Element element : li) {
                String line = element.attributes().get("data-line").trim(); //Линия
                Elements el = element.select("p");

                for (Element e : el) {
                    String station = e.text().split(" ", 2)[1].trim(); //Каждая станция
                    Elements elem = e.select("span");

                    JSONArray connectionArray = new JSONArray();    //Одна развязка
                    Set<JSONObject> objects = new HashSet<>();  //Объекты, в одной развязке совпадать не должны

                    JSONObject con10 = new JSONObject();

                    con10.put("line", line);
                    con10.put("station", station);

                    objects.add(con10);

                    for (Element ej : elem) {   //
                        String con = ej.attributes().get("class").trim();
                        String con1;    //Линия соединения
                        String stationConnection;   //Станция соединения
                        if (!(con.equals("name") || con.equals("num"))) {
                            alreadyExists.add(station);

                            con1 = con.split("-")[3].trim();

                            stationConnection = ej.attributes().get("title")
                                    .split("«")[1].split("»")[0].trim();

                            if (alreadyExists.contains(stationConnection)
                                    && (!stationConnection.equals(station))
                            )   continue;

                            JSONObject con20 = new JSONObject();

                            con20.put("line", con1);
                            con20.put("station", stationConnection);

                            objects.add(con20);

                            for (JSONObject object : objects) {
                                connectionArray.put(object);
                                System.out.println(object);
                            }
                            System.out.println("JAAAAAAAAAAAAA   " + objects.size());
                        }
                    }
                }
            }

           metro.put("connections", connectionsArray);

            /*Линии*/
            JSONArray linesJson = new JSONArray();  //Список всех линий в Json

            Elements lis = html.select("#metrodata > div > div > span");  //Список всех линий, проблем нет

            for (Element element : lis) {
                JSONObject line = new JSONObject();
                line.put("number", element.attributes().get("data-line"));
                line.put("name", element.text());
                linesJson.put(line);
            }

            metro.put("lines", linesJson);


            try {
                Path path = Path.of("map.json");
                Files.write(path, String.valueOf(metro).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}