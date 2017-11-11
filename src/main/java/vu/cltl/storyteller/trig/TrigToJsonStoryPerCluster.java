package vu.cltl.storyteller.trig;

import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.input.EuroVoc;
import vu.cltl.storyteller.input.NafTokenLayerIndex;
import vu.cltl.storyteller.json.JsonStorySerialization;
import vu.cltl.storyteller.json.JsonStoryUtil;
import vu.cltl.storyteller.objects.TrigTripleData;
import vu.cltl.storyteller.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by piek on 1/3/14.
 */
public class TrigToJsonStoryPerCluster {

    static TrigTripleData trigTripleData = new TrigTripleData();
    static HashMap<String, ArrayList<String>> iliMap = new HashMap<String, ArrayList<String>>();
    static int nEvents = 0;
    static int nActors = 0;
    static int nMentions = 0;
    static int nStories = 0;
    static String year = "";
    static EuroVoc euroVoc = new EuroVoc();
    static String log = "";

    static public void main (String[] args) {
        trigTripleData = new TrigTripleData();
        String demo = "/Users/piek/Desktop/DEMO/Storyteller/mockup/UncertaintyVisualization/app/data/brexit2";
        String project = "NewsReader storyline";
        String trigfolder = "/Users/piek/Desktop/ecb/ecb.trig";
        String trigfile = "";
        String euroVocFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.label.concept.gz";
        String pathToTokenIndex = "/Users/piek/Desktop/ecb/ecb.token.index.gz";
        log = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--trig-folder") && args.length>(i+1)) {
                trigfolder = args[i+1];
            }

            else if (arg.equals("--tokens") && args.length>(i+1)) {
                pathToTokenIndex = args[i+1];
            }
            else if (arg.equals("--year") && args.length>(i+1)) {
                year = args[i+1];
            }
            else if (arg.equals("--eurovoc") && args.length>(i+1)) {
                euroVocFile = args[i+1];
                euroVoc.readEuroVoc(euroVocFile,"en");
            }
            else if (arg.equals("--project") && args.length>(i+1)) {
                project = args[i+1];
            }
        }

        ArrayList<File> trigFiles = new ArrayList<File>();
        if (!trigfolder.isEmpty()) {
            System.out.println("trigfolder = " + trigfolder);
            trigFiles = Util.makeRecursiveFileList(new File(trigfolder), ".trig");
        }
        else if (!trigfile.isEmpty()) {
            System.out.println("trigfile = " + trigfile);
            trigFiles.add(new File(trigfile));
        }
        if (trigFiles.size()>0) {
            System.out.println("trigFiles.size() = " + trigFiles.size());
        }


        try {
            ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
            for (int i = 0; i < trigFiles.size(); i++) {
                File trigFile = trigFiles.get(i);
                trigTripleData = TrigTripleReader.readTripleFromTrigFiles(trigFiles);
                ArrayList<JSONObject> jsonLocalObjects = JsonStoryUtil.getSimpleJSONObjectArray(trigTripleData);

                System.out.println("creating one story...");
                jsonLocalObjects = JsonStoryUtil.createOneStoryForJSONArrayList(jsonLocalObjects,
                        0,
                        false,
                        "",
                        null,
                        0);
                for (int j = 0; j < jsonLocalObjects.size(); j++) {
                    JSONObject jsonObject = jsonLocalObjects.get(j);
                    jsonObjects.add(jsonObject);
                }
            }
            if (!pathToTokenIndex.isEmpty()) {
                log += NafTokenLayerIndex.createSnippetIndexFromMentions(jsonObjects, pathToTokenIndex);
            }


            nEvents = jsonObjects.size();
            nActors = JsonStoryUtil.countActors(jsonObjects);
            nMentions = JsonStoryUtil.countMentions(jsonObjects);
            nStories = JsonStoryUtil.countGroups(jsonObjects);

            JsonStorySerialization.writeJsonObjectArrayWithStructuredData(demo, "", project,
                    jsonObjects, null, nEvents, nStories, nActors, nMentions, "polls", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("story_cnt = " + nStories);
        System.out.println("event_cnt = " + nEvents);
        System.out.println("mention_cnt = "+ nMentions);
        System.out.println("actor_cnt = " + nActors);
    }
}
