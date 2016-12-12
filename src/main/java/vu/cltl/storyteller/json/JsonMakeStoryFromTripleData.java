package vu.cltl.storyteller.json;

import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.input.NafTokenLayerIndex;
import vu.cltl.storyteller.knowledgestore.GetMentionsFromKnowledgeStore;
import vu.cltl.storyteller.knowledgestore.GetTriplesFromKnowledgeStore;
import vu.cltl.storyteller.knowledgestore.KnowledgeStoreQueryApi;
import vu.cltl.storyteller.objects.TrigTripleData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by piek on 11/12/2016.
 */
public class JsonMakeStoryFromTripleData {

    static boolean LOG = false;
    static String KSSERVICE = ""; //https://knowledgestore2.fbk.eu";
    static String KS = ""; //"nwr/wikinews-new";
    static String KSuser = ""; //"nwr/wikinews-new";
    static String KSpass = ""; //"nwr/wikinews-new";
    static String log = "";
    static String pathToTokenIndexFile = "";
    static public void main (String[] args) {
        long startTime = System.currentTimeMillis();

        try {
            String sparqlQuery = "";
            if (args.length==0) {
                String[] q = new String[]{"--ks-limit","1000","--entityPhrase", "bank;money", "--entityType", "dbp:Bank", "--entityInstance", "dbpedia:Rabo",
                        "eventPhrase", "kill", "eventType", "eso:Killing", "--topic", "eurovoc:16789", "--grasp", "POSITIVE"};
                sparqlQuery = KnowledgeStoreQueryApi.createSparqlQuery(q);
            }
            else {
                sparqlQuery = KnowledgeStoreQueryApi.createSparqlQuery(args);

            }
            log += args.toString()+"\n";
            log += "sparqlQuery = " + sparqlQuery+"\n";
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equalsIgnoreCase("--ks-service") && args.length > (i + 1)) {
                    KSSERVICE = args[i + 1];
                    log += " -- KS Service = " + KSSERVICE;
                }
                else if (arg.equalsIgnoreCase("--ks-user") && args.length > (i + 1)) {
                    KSuser = args[i + 1];
                    log += " -- KS User = " + KSuser;
                }
                else if (arg.equalsIgnoreCase("--ks-passw") && args.length > (i + 1)) {
                    KSpass = args[i + 1];
                    log += " -- KS Passw = " + KSpass;
                }
                else if (arg.equalsIgnoreCase("--token-index") && args.length > (i + 1)) {
                    pathToTokenIndexFile = args[i + 1];
                    log += " -- token-index = " + pathToTokenIndexFile+"\n";
                }
                else if (arg.equalsIgnoreCase("--log")) {
                    LOG = true;
                }
            }
            log+='\n';
            TrigTripleData trigTripleData = new TrigTripleData();
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }

            GetTriplesFromKnowledgeStore.readTriplesFromKs(sparqlQuery,trigTripleData);
            long estimatedTime = System.currentTimeMillis() - startTime;
            log += " -- Time elapsed to get results from KS:" + estimatedTime / 1000.0+"\n";
            ArrayList<JSONObject> storyObjects = makeUpStory(trigTripleData, 1, 30);
            addPerspectiveToStory(storyObjects);
            if (pathToTokenIndexFile.isEmpty()) {
                addSnippetsToStoryFromKnowledgeStore(storyObjects, KSSERVICE);
            }
            else {
                addSnippetsToStoryFromIndexFile(storyObjects, pathToTokenIndexFile);
            }
            JSONObject story = writeStory(storyObjects);
/*            String str = "{ \"timeline\":\n";
            jsonOut.write(str.getBytes());
            jsonOut.write(timeLineObject.toString(0).getBytes());
            str = "}\n"*/;

            System.out.print(story.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LOG) {
            try {
                OutputStream logFos = new FileOutputStream("log");
                logFos.write(log.getBytes());
                logFos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static public ArrayList<JSONObject> makeUpStory(TrigTripleData trigTripleData, int climaxThreshold, int topicThreshold) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
            jsonObjects = JsonStoryUtil.getJSONObjectArray(trigTripleData);
            jsonObjects = JsonStoryUtil.createStoryLinesForJSONArrayList(jsonObjects, climaxThreshold, topicThreshold);
            JsonStoryUtil.minimalizeActors(jsonObjects);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjects;
    }

    static public void addPerspectiveToStory (ArrayList<JSONObject> jsonObjects) {
        GetTriplesFromKnowledgeStore.integrateAttributionFromKs(jsonObjects);
    }

    static public void addSnippetsToStoryFromKnowledgeStore (ArrayList<JSONObject> jsonObjects, String KSSERVICE) throws JSONException {
            log += GetMentionsFromKnowledgeStore.createSnippetIndexFromMentions(jsonObjects, KSSERVICE, KS, KSuser, KSpass);
    }

    static public void addSnippetsToStoryFromKnowledgeStore (ArrayList<JSONObject> jsonObjects, String KSSERVICE, String KS, String KSuser, String KSpass) throws JSONException {
            log += GetMentionsFromKnowledgeStore.createSnippetIndexFromMentions(jsonObjects, KSSERVICE, KS, KSuser, KSpass);
    }

    static public void addSnippetsToStoryFromIndexFile (ArrayList<JSONObject> jsonObjects, String pathToTokenIndex) throws JSONException {
            log += NafTokenLayerIndex.createSnippetIndexFromMentions(jsonObjects, pathToTokenIndex);
    }
     static public JSONObject writeStory (ArrayList<JSONObject> jsonObjects) throws JSONException {
         int nEvents = jsonObjects.size();
         int nActors = JsonStoryUtil.countActors(jsonObjects);
         int nMentions = JsonStoryUtil.countMentions(jsonObjects);
         int nStories = JsonStoryUtil.countGroups(jsonObjects);
         JSONObject finalStoryObject = JsonStorySerialization.createJsonStory(jsonObjects, nEvents, nActors, nMentions, nStories);
         log += " -- story_cnt = " + nStories + ", event_cnt = " + nEvents + ", mention_cnt = " + nMentions + ", actor_cnt = " + nActors;
         return finalStoryObject;
     }
}
