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

            /*�������*/
            JSONObject stationsJson = new JSONObject();   //������ ���� ������� � Json

            Elements li = html.select("#metrodata > div > div > div");   //������ ���� �������, �������� ���������
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

            /*����������*/
            JSONArray connectionsArray = new JSONArray();    //������ ���� ���������� � Json

            Set<String> alreadyExists = new HashSet<>();

            for (Element element : li) {
                String line = element.attributes().get("data-line").trim(); //�����
                Elements el = element.select("p");

                for (Element e : el) {
                    String station = e.text().split(" ", 2)[1].trim(); //������ �������
                    Elements elem = e.select("span");

                    JSONArray connectionArray = new JSONArray();    //���� ��������
                    Set<JSONObject> objects = new HashSet<>();  //�������, � ����� �������� ��������� �� ������

                    JSONObject con10 = new JSONObject();

                    con10.put("line", line);
                    con10.put("station", station);

                    objects.add(con10);

                    for (Element ej : elem) {   //
                        String con = ej.attributes().get("class").trim();
                        String con1;    //����� ����������
                        String stationConnection;   //������� ����������
                        if (!(con.equals("name") || con.equals("num"))) {
                            alreadyExists.add(station);

                            con1 = con.split("-")[3].trim();

                            stationConnection = ej.attributes().get("title")
                                    .split("�")[1].split("�")[0].trim();

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

            /*�����*/
            JSONArray linesJson = new JSONArray();  //������ ���� ����� � Json

            Elements lis = html.select("#metrodata > div > div > span");  //������ ���� �����, ������� ���

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