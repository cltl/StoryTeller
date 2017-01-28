package vu.cltl.storyteller.json;

import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.knowledgestore.GetTriplesFromKnowledgeStore;
import vu.cltl.storyteller.knowledgestore.SparqlGenerator;
import vu.cltl.storyteller.objects.NewsReaderInstance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 26/01/2017.
 */
public class JsonRobotHierarchy {

    static boolean DEBUG = false;
    static String fnPath = "/Code/vu/newsreader/vua-resources/frAllRelation.xml";
    static String esoPath = "/Code/vu/newsreader/vua-resources/ESO_Version2.owl";
    static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.label.concept.gz";
    //static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.csv.gz";
    static String euroVocHierarchyFile = "/Code/vu/newsreader/vua-resources/eurovoc_in_skos_core_concepts.rdf.gz";
    static String entityTypeFile = "/Code/vu/newsreader/vua-resources/instance_types_en.ttl.gz";
    static String entityHierarchyFile = "/Code/vu/newsreader/vua-resources/DBpediaHierarchy_parent_child.tsv";
    static String entityCustomFile = "";

    static String KSSERVICE = "http://145.100.58.139:50053";
    static String KS = ""; //"nwr/wikinews-new";
    static String KSuser = ""; //"nwr/wikinews-new";
    static String KSpass = ""; //"nwr/wikinews-new";
    static boolean ALLEVENTYPES = false;
    static String DATA = "events";
    static Integer mCount = -1;
    static ArrayList<String> projects = new ArrayList<String>();

    static public void main (String[] args) {
        DEBUG = true;
        //ALLEVENTYPES = true;
        DATA = "light-entity";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--data") && args.length > (i + 1)) {
                DATA = args[i+1];
            }
            if (arg.equals("--mention") && args.length > (i + 1)) {
                try {
                    mCount = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--projects") && args.length > (i + 1)) {
                String [] projectString = args[i+1].split(";");
                for (int j = 0; j < projectString.length; j++) {
                    String s = projectString[j];
                    projects.add(s);
                }
            }
            else if (arg.equals("--eso") && args.length > (i + 1)) {
                esoPath = args[i+1];
            }
            else if (arg.equals("--framenet") && args.length > (i + 1)) {
                fnPath = args[i+1];
            }
            else if (arg.equals("--eurovoc-label") && args.length > (i + 1)) {
                euroVocLabelFile = args[i+1];
            }
            else if (arg.equals("--eurovoc-core") && args.length > (i + 1)) {
                euroVocHierarchyFile = args[i+1];
            }
            else if (arg.equals("--entity-hierarchy") && args.length > (i + 1)) {
                entityHierarchyFile = args[i+1];
            }
            else if (arg.equals("--entity-type") && args.length > (i + 1)) {
                entityTypeFile = args[i+1];
            }
            else if (arg.equals("--entity-custom") && args.length > (i + 1)) {
                entityCustomFile = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--ks-service") && args.length > (i + 1)) {
                KSSERVICE = args[i + 1];
            }
            else if (arg.equalsIgnoreCase("--ks-user") && args.length > (i + 1)) {
                KSuser = args[i + 1];
            }
            else if (arg.equalsIgnoreCase("--ks-passw") && args.length > (i + 1)) {
                KSpass = args[i + 1];
            }
            else if (arg.equalsIgnoreCase("--debug")) {
                DEBUG = true;
            }
            else if (arg.equalsIgnoreCase("--all-events")) {
                ALLEVENTYPES = true;
            }
        }
        projects.add("<http://www.newsreader-project.eu/project/London>");
        projects.add("<http://www.newsreader-project.eu/project/AI>");
        HashMap<String, NewsReaderInstance> map = new HashMap<String, NewsReaderInstance>();
        for (int i = 0; i < projects.size(); i++) {
            String project = projects.get(i);
            getJsonListFromKnowledgeStore(KSSERVICE, KSuser,KSpass,project, DATA, map);
        }


        try {
            JSONObject tree = new JSONObject();
            Set keySet = map.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                NewsReaderInstance newsReaderInstance = map.get(key);
                JSONObject iObject = newsReaderInstance.toJSONObject();
                tree.append("instance", iObject);
            }
            OutputStream fos = new FileOutputStream(DATA+"-instances.json");
            fos.write(tree.toString(0).getBytes());
            fos.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static public void getJsonListFromKnowledgeStore (String KSSERVICE, String KSuser, String KSpass,
                                                                 String project,
                                                                 String instanceType,
                                                                 HashMap<String, NewsReaderInstance> map) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            String sparqlPhrases = "";
            if (DATA.equals("light-entity")) {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForLightEntityProjectFromKs(project);
            }
            else if (DATA.equals("dark-entity")) {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForDarkEntityProjectFromKs(project);

            }
            else if (DATA.equals("non-entity")) {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForNonEntityProjectFromKs(project);
            }
            if (!sparqlPhrases.isEmpty()) {
                System.out.println("sparqlPhrases = " + sparqlPhrases);
                GetTriplesFromKnowledgeStore.getJSONCountlistFromKnowledgeStore(sparqlPhrases, instanceType, project, map);
                System.out.println("map.size() = " + map.size());
            }
            else {
                System.out.println("Unsupported DATA type = " + DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
