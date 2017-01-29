package vu.cltl.storyteller.json;

import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.knowledgestore.GetTriplesFromKnowledgeStore;
import vu.cltl.storyteller.knowledgestore.SparqlGenerator;
import vu.cltl.storyteller.objects.NewsReaderInstance;
import vu.cltl.storyteller.objects.PhraseCount;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by piek on 26/01/2017.
 */
public class JsonRobotHierarchy {

    static OutputStream outputStream = null;

    static String KSSERVICE = "http://145.100.58.139:50053";
    static String KS = ""; //"nwr/wikinews-new";
    static String KSuser = ""; //"nwr/wikinews-new";
    static String KSpass = ""; //"nwr/wikinews-new";
    static String DATA = "";
    static Integer mCount = -1;
    static ArrayList<String> projects = new ArrayList<String>();

    static public void main (String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--data") && args.length > (i + 1)) {
                DATA = args[i+1];
            }
            else if (arg.equals("--mention") && args.length > (i + 1)) {
                try {
                    mCount = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--out") && args.length > (i + 1)) {
                String pathToOutputFile = args[i+1];
                try {
                    outputStream = new FileOutputStream(pathToOutputFile);
                } catch (FileNotFoundException e) {
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

            else if (arg.equalsIgnoreCase("--ks-service") && args.length > (i + 1)) {
                KSSERVICE = args[i + 1];
            }
            else if (arg.equalsIgnoreCase("--ks-user") && args.length > (i + 1)) {
                KSuser = args[i + 1];
            }
            else if (arg.equalsIgnoreCase("--ks-passw") && args.length > (i + 1)) {
                KSpass = args[i + 1];
            }
        }
        HashMap<String, NewsReaderInstance> map = new HashMap<String, NewsReaderInstance>();
        for (int i = 0; i < projects.size(); i++) {
            String project = projects.get(i);
            getJsonListFromKnowledgeStore(KSSERVICE, KSuser,KSpass,project, DATA, map);
        }


        try {
            JSONObject tree = new JSONObject();
            ArrayList<PhraseCount> countedNodes = new ArrayList<PhraseCount>();
            Set keySet = map.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                NewsReaderInstance newsReaderInstance = map.get(key);
                if (newsReaderInstance.countMentions()>=mCount) {
                    PhraseCount phraseCount = new PhraseCount(newsReaderInstance.getUri(), newsReaderInstance.countMentions());
                    countedNodes.add(phraseCount);
                }
            }
            Collections.sort(countedNodes, new Comparator<PhraseCount>() {
                @Override
                public int compare(PhraseCount p1, PhraseCount p2) {
                    return p2.getCount().compareTo(p1.getCount());
                }
            });
            for (int i = 0; i < countedNodes.size(); i++) {
                PhraseCount node = countedNodes.get(i);
                NewsReaderInstance newsReaderInstance = map.get(node.getPhrase());
                JSONObject iObject = newsReaderInstance.toJSONObject();
                tree.append("instance", iObject);
            }
            if (outputStream != null)   {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
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
