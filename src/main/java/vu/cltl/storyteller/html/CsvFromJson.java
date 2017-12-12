package vu.cltl.storyteller.html;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

/**
 * Created by piek on 21/11/2017.
 */
public class CsvFromJson {




    public static void main(String[] args) {
        String  pathToFile = "";
        //pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/events.json";
        //pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/concept.json";
        pathToFile = "/Users/piek/Desktop/CLTL-onderwijs/EnvironmentalAndDigitalHumanities/london/scripts/light.json";
        JSONObject object = readJsonFile(pathToFile);
        makeCsvFile(pathToFile, object, "");

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

    static void makeCsvFile(String filePath, JSONObject jsonObject, String title) {
        try

        {
            OutputStream fos = new FileOutputStream(filePath + ".words.csv");
            String str = "Concept\tInstances\tMentions\n";
            fos.write(str.getBytes());
            try {
                csvTable(fos, jsonObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    public static void  csvTable(OutputStream fos,
                                      JSONObject jsonObject) throws Exception {
        outputNode(fos, jsonObject);
        org.json.simple.JSONArray instances = (org.json.simple.JSONArray) jsonObject.get("instances");
        if (instances != null) {
            for (int i = 0; i < instances.size(); i++) {
                JSONObject o = (JSONObject) instances.get(i);
                csvTable(fos, o);
            }
        }
        org.json.simple.JSONArray children = (org.json.simple.JSONArray) jsonObject.get("children");
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                JSONObject o = (JSONObject) children.get(i);
                csvTable(fos, o);
            }
        }

    }

    public static void outputNode (OutputStream fos,
                                   org.json.simple.JSONObject object) throws IOException {

        String ref = "";
        ref = (String) object.get("name");
        if (ref!=null) {
            String str = ref;
            Long mentionCount = (Long) object.get("mention_count");
            Long instanceCount = (Long) object.get("instance_count");
            String parent = (String) object.get("parent");
            str += "\t";
            if (instanceCount!=null) str += instanceCount;
            str += "\t";
            if (mentionCount!=null) str += mentionCount;
            str += "\t";
            if (parent!=null) str += parent;
            str += "\n";
            fos.write(str.getBytes());
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
