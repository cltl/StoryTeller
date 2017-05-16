package vu.cltl.storyteller.json;

import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.input.EuroVoc;
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
 * Demonstration class that shows all the functions to query the KnowledgeStore for EventCentricKnowledgeGraphs.
 * The result of the query is stored internally in a data structure TrigTripleData.
 * It converts the event RDF from the TrigTripleData to JSON, creates a story structure and returns the JSON structure.
 * Additional function are provided to enrich the JSON with perspective values and text snippets
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
    static EuroVoc euroVoc = null;
    static EuroVoc euroVocBlackList = null;
    static String pathToEuroVocFile = "";
    static String pathToEuroVocBlackListFile = "";
    static String roleNs = "";


    /**
     * The main function needs a number of parameters to run:
     * --ks-service     http address of the KnowledgeStore service
     * --ks-user        (optional) if the KnowledgeStore is user protected a username is required
     * --ks-passw       (optional) if the KnowledgeStore is user protected a psswords is required
     * --ks-limit       (optional) limits the number of events returned by the KnowledgeStore.
     *                  The default value is set to 500 events
     * --token-index    (optional) path to the NafTokenIndex file (gzipped) that is needed to create text snippets for the results
     *                  Without the token-index, the KnowledgeStore is queried, which is much slower
     * --eurovoc        (optional) Path to the EuroVoc topic label file. This is needed to provide readable labels for topic identifiers
     * --eurovoc-blacklist (optinal) Path to a text file that provides topics that should be ignored to make storyline groupings
     * --log            (optional) switch to turn on logging. If ommitted there is no logging of the queries.
     *                  If specified some logging of the querying is done
     *
     * A number of query types that can be combined
     * --entityPhrase
     * --entityInstance
     * --entityType
     * --eventPhrase
     * --eventType
     * --topic
     * --author
     * --cited
     * --grasp
     *
     * @param args
     *
     * The main function carries out the complete search and conversion and returs a JSON stream as a result.
     * The usage of this class is demonstrated in the shell script tellstory.sh with a variety of queries.
     */
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
            log += KnowledgeStoreQueryApi.log+"\n";
            log += "sparqlQuery = " + sparqlQuery+"\n";
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equalsIgnoreCase("--ks-service") && args.length > (i + 1)) {
                    KSSERVICE = args[i + 1];
                    log += " -- KS Service = " + KSSERVICE+"\n";
                }
                else if (arg.equalsIgnoreCase("--ks-user") && args.length > (i + 1)) {
                    KSuser = args[i + 1];
                    log += " -- KS User = " + KSuser+"\n";
                }
                else if (arg.equalsIgnoreCase("--ks-passw") && args.length > (i + 1)) {
                    KSpass = args[i + 1];
                    log += " -- KS Passw = " + KSpass+"\n";
                }
                else if (arg.equalsIgnoreCase("--token-index") && args.length > (i + 1)) {
                    pathToTokenIndexFile = args[i + 1];
                    log += " -- token-index = " + pathToTokenIndexFile+"\n";
                }
                else if (arg.equalsIgnoreCase("--roles") && args.length>(i+1)) {
                    roleNs = args[i+1];
                    log += " -- showing roles = " + roleNs;
                }
                else if (arg.equalsIgnoreCase("--eurovoc") && args.length > (i + 1)) {
                    pathToEuroVocFile = args[i + 1];
                    log += " -- eurovoc = " + pathToEuroVocFile+"\n";
                    euroVoc = new EuroVoc();
                    euroVoc.readEuroVoc(pathToEuroVocFile, "en");
                }
                else if (arg.equalsIgnoreCase("--eurovoc-blacklist") && args.length > (i + 1)) {
                    pathToEuroVocBlackListFile = args[i + 1];
                    log += " -- eurovoc-blacklist = " + pathToEuroVocBlackListFile+"\n";
                    euroVocBlackList = new EuroVoc();
                    euroVoc.readEuroVoc(pathToEuroVocBlackListFile, "en");
                }
                else if (arg.equalsIgnoreCase("--log")) {
                    LOG = true;
                }
            }
            log+="\n";
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
            ArrayList<JSONObject> storyObjects = makeUpStory(trigTripleData, 1, 30, roleNs, euroVoc, euroVocBlackList);
            log += " -- Result: "+storyObjects.size()+" events.\n";
            if (storyObjects.size()>0) {
                addPerspectiveToStory(storyObjects);
                if (pathToTokenIndexFile.isEmpty()) {
                    addSnippetsToStoryFromKnowledgeStore(storyObjects, KSSERVICE);
                } else {
                    addSnippetsToStoryFromIndexFile(storyObjects, pathToTokenIndexFile);
                }
               /* JSONObject story = writeStory(storyObjects);
                System.out.print(story.toString(4));*/
            }
            JSONObject story = writeStory(storyObjects);
            System.out.print(story.toString(4));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LOG) {
            try {
                OutputStream logFos = new FileOutputStream("log", true);  /// append version
               // OutputStream logFos = new FileOutputStream("log");
                logFos.write(log.getBytes());
                logFos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Given the RDF triples in trigTripleData it creates an ArrayList with JSON objects in which each object
     * represents an EventCentricKnowledgeGraph and stories consists of events with the same group name.
     * Groupings are based on sharing of topics and participants across events. The topicThreshold determines
     * the proportion of overlap the coarseness of the groupings. High thresholds results in many different small
     * groupings with high topic sharing, whereas a low threshold results in few groups with low overlap.
     *
     * The climaxThreshold excludes events that are not salient or prominent enough.
     *
     * The euroVoc data is used to label the stories with topic labels.
     *
     * The euroVocBlacklist is used to exclude topics from making groupings
     *
     * @param trigTripleData
     * @param climaxThreshold
     * @param topicThreshold
     * @param euroVoc
     * @param euroVocBlackList
     * @return
     */
    static public ArrayList<JSONObject> makeUpStory(TrigTripleData trigTripleData,
                                                    int climaxThreshold,
                                                    int topicThreshold,
                                                    String roles,
                                                    EuroVoc euroVoc, EuroVoc euroVocBlackList) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
            jsonObjects = JsonStoryUtil.getJSONObjectArray(trigTripleData);
            jsonObjects = JsonStoryUtil.createStoryLinesForJSONArrayList(jsonObjects, climaxThreshold, topicThreshold);
            if (!roles.equalsIgnoreCase("all") )
                if (roles.isEmpty()) {
                    JsonStoryUtil.minimalizeActors(jsonObjects);
                }
                else {
                    JsonStoryUtil.selectActors(jsonObjects, roles);
                }
            if (euroVoc!=null) {
                JsonStoryUtil.renameStories(jsonObjects, euroVoc, euroVocBlackList);
            }

        } catch (JSONException e) {
           // e.printStackTrace();
        }
        return jsonObjects;
    }

    /**
     * Adds perspective layer to the JSON events
     * @param jsonObjects
     */
    static public void addPerspectiveToStory (ArrayList<JSONObject> jsonObjects) {
        GetTriplesFromKnowledgeStore.integrateAttributionFromKs(jsonObjects);
    }

    /**
     * Adds snippets to the events by querying the KnowledgeStore
     * @param jsonObjects
     * @param KSSERVICE
     * @throws JSONException
     */
    static public void addSnippetsToStoryFromKnowledgeStore (ArrayList<JSONObject> jsonObjects, String KSSERVICE) throws JSONException {
            log += GetMentionsFromKnowledgeStore.createSnippetIndexFromMentions(jsonObjects, KSSERVICE, KS, KSuser, KSpass)+"\n";
    }

    /**
     * Adds snippets to the events by querying the KnowledgeStore
     * @param jsonObjects
     * @param KSSERVICE
     * @param KS
     * @param KSuser
     * @param KSpass
     * @throws JSONException
     */
    static public void addSnippetsToStoryFromKnowledgeStore (ArrayList<JSONObject> jsonObjects, String KSSERVICE, String KS, String KSuser, String KSpass) throws JSONException {
            log += GetMentionsFromKnowledgeStore.createSnippetIndexFromMentions(jsonObjects, KSSERVICE, KS, KSuser, KSpass)+"\n";
    }

    /**
     * Adds snippets to the events by throught the NafTokenIndex
     * @param jsonObjects
     * @param pathToTokenIndex
     * @throws JSONException
     */
    static public void addSnippetsToStoryFromIndexFile (ArrayList<JSONObject> jsonObjects, String pathToTokenIndex) throws JSONException {
            log += NafTokenLayerIndex.createSnippetIndexFromMentions(jsonObjects, pathToTokenIndex)+"\n";
    }

    /**
     * Writes the story structure as a stream in JSON format
     * @param jsonObjects
     * @return
     * @throws JSONException
     */
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
