package vu.cltl.storyteller.html;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

/**
 * Created by piek on 21/11/2017.
 */
public class HtmlFromJson {


   /* public static void main (String[] args) {
        String pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/events.json";

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(new File(pathToFile));

            JsonNode contactNode = root.path("name");
            if (contactNode.isArray()) {
                // If this node an Arrray?
            }

            for (JsonNode node : contactNode) {
                String type = node.path("name").asText();
                String ref = node.path("mention_count").asText();
                System.out.println("type : " + type);
                System.out.println("ref : " + ref);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /*public static JSONObject getChildren (JSONObject jsonObject) {

    }*/

    /*
     need to remove parent, query, type
     */

    public static void main(String[] args) {
        String pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/events.json";
        //String pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/concept.json";
        // String pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/light.json";
        JSONObject object = readJsonFile(pathToFile);
        findObjectChildren(object);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
        try {
            OutputStream fos = new FileOutputStream(pathToFile + ".html");
            String str = "<html>\n" +
                    "<body>\n" +
                    "<pre>";
            str += mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            str += "</pre></body>\n";
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        makeHtmlTreeFile(pathToFile, object, "");

    }

    private static void findObjectChildren(JSONObject object) {
        if (!object.isEmpty()) {
            object.remove("parent");
            object.remove("type");
            object.remove("query");
        }
        org.json.simple.JSONArray instances = (org.json.simple.JSONArray) object.get("instances");
        if (instances != null) {
            for (int i = 0; i < instances.size(); i++) {
                JSONObject o = null;
                try {
                    o = (JSONObject) instances.get(i);
                    o.remove("parent");
                    o.remove("type");
                    o.remove("query");
                    findObjectChildren(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        org.json.simple.JSONArray children = (org.json.simple.JSONArray) object.get("children");
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                JSONObject o = null;
                try {
                    o = (JSONObject) children.get(i);
                    o.remove("parent");
                    o.remove("type");
                    o.remove("query");
                    findObjectChildren(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void makeHtmlTreeFile(String filePath, JSONObject jsonObject, String title) {
        try

        {
            OutputStream fos = new FileOutputStream(filePath + ".words.html");
            String str = TreeStaticHtml.makeHeader(title) + TreeStaticHtml.makeBodyStart(title, "", 0, 0, 0, 0);
            str += "<div id=\"Entities\" class=\"tabcontent\">\n";
            str += "<div id=\"container\">\n";
            fos.write(str.getBytes());
            try {
                TreeStaticHtml.htmlTableTree(fos, jsonObject, "entity",1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            str = "</div></div>\n";
            fos.write(str.getBytes());
            str = TreeStaticHtml.bodyEnd;
            fos.write(str.getBytes());
            fos.close();

        }
        catch(
        IOException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }


    private static void findObjectChildren1(JSONObject object) {

        if (!object.isEmpty()) {
            object.remove("parent");
            object.remove("type");
            object.remove("query");
            System.out.println("object.toString() = " + object.toString());
        }
        org.json.simple.JSONArray children = (org.json.simple.JSONArray) object.get("children");
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                JSONObject o = null;
                try {
                    o = (JSONObject) children.get(i);
                    o.remove("parent");
                    o.remove("type");
                    o.remove("query");
                    findObjectChildren1(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static public JSONObject readJsonFile (String filePath) {
        JSONObject jsonObject  = null;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(filePath));
            jsonObject = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
