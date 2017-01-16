package vu.cltl.storyteller.knowledgestore;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.json.JsonStoryUtil;
import vu.cltl.storyteller.objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static com.hp.hpl.jena.rdf.model.ResourceFactory.createStatement;

/**
 * Created by filipilievski on 2/7/16.
 */
public class GetTriplesFromKnowledgeStore {
    public static int DEBUG = 0;
    public static int qCount = 0;
    public static String service = "https://knowledgestore2.fbk.eu";
    public static String serviceEndpoint = "https://knowledgestore2.fbk.eu/nwr/wikinews-new/sparql";
    public static String user = "nwr_partner";
    public static String pass = "ks=2014!";

    HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());
   // public static TrigTripleData trigTripleData = new TrigTripleData();

    static public void setServicePoint (String service, String ks) {
        if (ks.isEmpty()) {
            serviceEndpoint = service+ "/sparql";
        }
        else {
            serviceEndpoint = service + "/" + ks + "/sparql";
        }
    }

    static public void setServicePoint (String service, String ks, String username, String password) {
        //serviceEndpoint = "https://knowledgestore2.fbk.eu/"+ks+"/sparql";
        setServicePoint(service, ks);
        user = username;
        pass = password;
    }


    public static ArrayList<Statement> readTriplesFromKs(String subjectUri, String sparqlQuery){

        ArrayList<Statement> triples = new ArrayList<Statement>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());
        try {
            qCount++;
            QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
            ResultSet resultset = x.execSelect();
            while (resultset.hasNext()) {
                QuerySolution solution = resultset.nextSolution();
                String relString = solution.get("predicate").toString();
                RDFNode obj = solution.get("object");
                Model model = ModelFactory.createDefaultModel();
                Resource subj = model.createResource(subjectUri);
                Statement s = createStatement(subj, ResourceFactory.createProperty(relString), obj);
                triples.add(s);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return triples;
    }

    public static ArrayList<JSONObject> readAttributionFromKs(ArrayList<JSONObject> targetEvents){
        long startTime = System.currentTimeMillis();
        HashMap<String, PerspectiveJsonObject> perspectiveMap = new HashMap<String, PerspectiveJsonObject>();

        ArrayList<JSONObject> pEvents = new ArrayList<JSONObject>();
        ArrayList<String> uris = new ArrayList<String>();
        HashMap<String, JSONObject> eventMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < targetEvents.size(); i++) {
            JSONObject targetEvent = targetEvents.get(i);
            try {
                String eventUri = targetEvent.getString("instance");
                eventMap.put(eventUri, targetEvent);
                if (!eventUri.startsWith("<")) eventUri = "<"+eventUri+">";
                uris.add(eventUri);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        String sparqlQuery = SparqlGenerator.makeAttributionQuery(uris);
      //  System.out.println("sparqlQuery = " + sparqlQuery);
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());
        try {
            QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
            ResultSet resultset = x.execSelect();
            while (resultset.hasNext()) {
                QuerySolution solution = resultset.nextSolution();
                String event = "";
                String mention = "";
                String attribution = "";
                String cite = "";
                String author = "";
                String label = "";
                String comment = "";
                try { event = solution.get("event").toString(); } catch (Exception e) { }
                try { mention = solution.get("mention").toString(); } catch (Exception e) { }
                try { attribution = solution.get("attribution").toString(); } catch (Exception e) { }
                try { cite = solution.get("cite").toString(); } catch (Exception e) { }
                try { author = solution.get("author").toString(); } catch (Exception e) { }
                try { label = solution.get("label").toString(); } catch (Exception e) { }
                try { comment = solution.get("comment").toString(); } catch (Exception e) { }

                ArrayList<String> perspectives = PerspectiveJsonObject.normalizePerspectiveValue(attribution);
                if (!perspectives.isEmpty()) {
                    JSONObject targetEvent = eventMap.get(event);
                    if (targetEvent != null) {
                        if (perspectiveMap.containsKey(mention)) {
                            PerspectiveJsonObject perspectiveJsonObject = perspectiveMap.get(mention);
                            perspectiveJsonObject.addAttribution(perspectives);
                            perspectiveMap.put(mention, perspectiveJsonObject);
                        }
                        else {
                            PerspectiveJsonObject perspectiveJsonObject = new PerspectiveJsonObject(perspectives, author, cite, comment, event, label, mention, targetEvent);
                            perspectiveMap.put(mention, perspectiveJsonObject);
                        }

                    } else {
                      //  System.out.println("mention no target event = " + mention);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set keySet = perspectiveMap.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            PerspectiveJsonObject perspectiveJsonObject = perspectiveMap.get(key);
            try {
                JSONObject perspectiveEvent = JsonStoryUtil.createSourcePerspectiveEvent(perspectiveJsonObject);
                pEvents.add(perspectiveEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" * Perspective Time elapsed:"+estimatedTime/1000.0);
        //System.out.println("pEvents = " + pEvents.size());
        return pEvents;
    }


    public static void integrateAttributionFromKs(ArrayList<JSONObject> targetEvents){
        long startTime = System.currentTimeMillis();
        HashMap<String, ArrayList<PerspectiveJsonObject>> perspectiveMap = new HashMap<String, ArrayList<PerspectiveJsonObject>>();

        ArrayList<String> uris = new ArrayList<String>();
        HashMap<String, JSONObject> eventMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < targetEvents.size(); i++) {
            JSONObject targetEvent = targetEvents.get(i);
            try {
                String eventUri = targetEvent.getString("instance");
                eventMap.put(eventUri, targetEvent);
                if (!eventUri.startsWith("<")) eventUri = "<"+eventUri+">";
                uris.add(eventUri);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        String sparqlQuery = SparqlGenerator.makeAttributionQuery(uris);
       // System.out.println("sparqlQuery = " + sparqlQuery);
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());
        try {
            QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
            ResultSet resultset = x.execSelect();
            while (resultset.hasNext()) {
                QuerySolution solution = resultset.nextSolution();
                String event = "";
                String mention = "";
                String attribution = "";
                String cite = "";
                String author = "";
                String label = "";
                String comment = "";
                try { event = solution.get("event").toString(); } catch (Exception e) { }
                try { mention = "<"+solution.get("mention").toString()+">"; } catch (Exception e) { }
                try { attribution = solution.get("attribution").toString(); } catch (Exception e) { }
                try { cite = solution.get("cite").toString(); } catch (Exception e) { }
                try { author = solution.get("author").toString(); } catch (Exception e) { }
                try { label = solution.get("label").toString(); } catch (Exception e) { }
                try { comment = solution.get("comment").toString(); } catch (Exception e) { }

                if (author.isEmpty() && cite.isEmpty()) {
                    author = "unknown";
                }
                ArrayList<String> perspectives = PerspectiveJsonObject.normalizePerspectiveValue(attribution);
                if (!perspectives.isEmpty()) {
                    JSONObject targetEvent = eventMap.get(event);
                    if (targetEvent != null) {
                        PerspectiveJsonObject perspectiveJsonObject = new PerspectiveJsonObject(perspectives, author, cite, comment, event, label, mention, targetEvent);
                        if (perspectiveMap.containsKey(mention)) {
                            ArrayList<PerspectiveJsonObject> perspectiveJsonObjects = perspectiveMap.get(mention);
                            perspectiveJsonObjects.add(perspectiveJsonObject);
                            perspectiveMap.put(mention, perspectiveJsonObjects);
                        }
                        else {
                            ArrayList<PerspectiveJsonObject> perspectiveJsonObjects = new ArrayList<PerspectiveJsonObject>();
                            perspectiveJsonObjects.add(perspectiveJsonObject);
                            perspectiveMap.put(mention, perspectiveJsonObjects);
                        }

                    } else {
                      //  System.out.println("Error: mention without target event = " + mention);
                    }
                }
                else {
                  //  System.out.println("No perspectives for this event.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < targetEvents.size(); i++) {
            JSONObject mEvent = targetEvents.get(i);
            JSONArray mMentions = null;
            try {
                mMentions = (JSONArray) mEvent.get("mentions");
            } catch (JSONException e) {
               // e.printStackTrace();
            }
            if (mMentions!=null) {
                for (int m = 0; m < mMentions.length(); m++) {
                    try {
                        JSONObject mentionObject = (JSONObject) mMentions.get(m);
                        String uriString = mentionObject.getString("uri");
                        JSONArray offsetArray = mentionObject.getJSONArray("char");
                        String mention = JsonStoryUtil.getURIforMention(uriString, offsetArray);
                        if (perspectiveMap.containsKey(mention)) {
                            ArrayList<PerspectiveJsonObject> perspectiveJsonObjects = perspectiveMap.get(mention);
                            ArrayList<PerspectiveJsonObject> nonDefaultPerspectives = PerspectiveJsonObject.keepNonDefaultPerspectives(perspectiveJsonObjects);
                            if (nonDefaultPerspectives.size()>0) {
                                for (int j = 0; j < nonDefaultPerspectives.size(); j++) {
                                    PerspectiveJsonObject perspectiveJsonObject = nonDefaultPerspectives.get(j);
                                    JsonStoryUtil.addPerspectiveToMention(mentionObject, perspectiveJsonObject);
                                }
                            }
                            else {
                                for (int j = 0; j < perspectiveJsonObjects.size(); j++) {
                                    PerspectiveJsonObject perspectiveJsonObject = perspectiveJsonObjects.get(j);
                                    JsonStoryUtil.addPerspectiveToMention(mentionObject, perspectiveJsonObject);
                                }
                            }
                        }
                        else {
                            PerspectiveJsonObject dymmyPerspective = new PerspectiveJsonObject();
                            JsonStoryUtil.addPerspectiveToMention(mentionObject, dymmyPerspective);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                /*try {
                    System.out.println("No mentions for target = "+mEvent.getString("instance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
       // System.out.println(" -- Time elapsed adding perspective :"+estimatedTime/1000.0);
    }

    public static ArrayList<String> readEventIdsFromKs(String sparqlQuery)throws Exception {
        ArrayList<String> eventIds = new ArrayList<String>();
        //System.out.println("serviceEndpoint = " + serviceEndpoint);
        //System.out.println("sparqlQuery = " + sparqlQuery);
        //System.out.println("user = " + user);
        //System.out.println("pass = " + pass);
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            //System.out.println("solution.toString() = " + solution.toString());
            //( ?event = <http://www.newsreader-project.eu/data/Dasym-Pilot/425819_relink_dominant.naf#ev24> )
            String currentEvent = solution.get("event").toString();
            //System.out.println("currentEvent = " + currentEvent);
            //http://www.newsreader-project.eu/data/Dasym-Pilot/425819_relink_dominant.naf#ev24
            if (!eventIds.contains(currentEvent)) {
                eventIds.add(currentEvent);
            }
        }
        return eventIds;
    }


    public static void getEventDataFromKs(String sparqlQuery, TrigTripleData trigTripleData)throws Exception {
        //System.out.println("serviceEndpoint = " + serviceEndpoint);
        //System.out.println("sparqlQuery = " + sparqlQuery);
        //System.out.println("user = " + user);
        //System.out.println("pass = " + pass);
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        Property inDateTimeProperty = ResourceFactory.createProperty("http://www.w3.org/TR/owl-time#inDateTime");
        Property beginTimeProperty = ResourceFactory.createProperty("http://www.w3.org/TR/owl-time#hasBeginning");
        Property endTimeProperty = ResourceFactory.createProperty("http://www.w3.org/TR/owl-time#hasEnd");


        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();
        String oldEvent="";
        ArrayList<Statement> instanceRelations = new ArrayList<Statement>();
        ArrayList<Statement> otherRelations = new ArrayList<Statement>();
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String relString = solution.get("relation").toString();
            String currentEvent = solution.get("event").toString();
            RDFNode obj = solution.get("object");
            String objUri = obj.toString();
            Statement s = createStatement((Resource) solution.get("event"), ResourceFactory.createProperty(relString), obj);
            if (isSemRelation(relString) || isESORelation(relString) || isFNRelation(relString) || isPBRelation(relString))
            {
                otherRelations.add(s);
                if (isSemTimeRelation(relString)) {
                    if (solution.get("indatetime")!=null){
//                            System.out.println("in " + solution.get("indatetime"));
                        String uri = ((Resource) obj).getURI();
                        Statement s2 = createStatement((Resource) obj, inDateTimeProperty, solution.get("indatetime"));
                        if (trigTripleData.tripleMapInstances.containsKey(uri)) {
                            ArrayList<Statement> triples = trigTripleData.tripleMapInstances.get(uri);
                            triples.add(s2);
                            trigTripleData.tripleMapInstances.put(uri, triples);
                        } else {
                            ArrayList<Statement> triples = new ArrayList<Statement>();
                            triples.add(s2);
                            trigTripleData.tripleMapInstances.put(uri, triples);
                        }
                    }
                    else {
                        if (solution.get("begintime")!=null){
//                            System.out.println("begin " + solution.get("begintime"));
                            String uri = ((Resource) obj).getURI();
                            Statement s2 = createStatement((Resource) obj, beginTimeProperty, solution.get("begintime"));
                            if (trigTripleData.tripleMapInstances.containsKey(uri)) {
                                ArrayList<Statement> triples = trigTripleData.tripleMapInstances.get(uri);
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            } else {
                                ArrayList<Statement> triples = new ArrayList<Statement>();
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            }
                        }
                        else if (solution.get("endtime")!=null) {
//                            System.out.println("end " + solution.get("endtime"));
                            String uri = ((Resource) obj).getURI();
                            Statement s2 = createStatement((Resource) solution.get("object"), endTimeProperty, solution.get("endtime"));
                            if (trigTripleData.tripleMapInstances.containsKey(uri)) {
                                ArrayList<Statement> triples = trigTripleData.tripleMapInstances.get(uri);
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            } else {
                                ArrayList<Statement> triples = new ArrayList<Statement>();
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            }
                        }
                    }
                }
            }
            else // Instances
            {
                instanceRelations.add(s);
            }

            if (!oldEvent.equals("")) {
                if (!currentEvent.equals(oldEvent)){
                    if (instanceRelations.size()>0){
                        trigTripleData.tripleMapInstances.put(oldEvent, instanceRelations);
                    }
                    if (otherRelations.size()>0){
                        trigTripleData.tripleMapOthers.put(oldEvent, otherRelations);
                    }
                    instanceRelations = new ArrayList<Statement>();
                    otherRelations = new ArrayList<Statement>();
                }
            }
            oldEvent=currentEvent;
        }
     //   System.out.println(" * instance statements = "+trigTripleData.tripleMapInstances.size());
     //   System.out.println(" * sem statements = " + trigTripleData.tripleMapOthers.size());
    }


    public static void readTriplesFromKs(String sparqlQuery, TrigTripleData trigTripleData)throws Exception {
        //System.out.println("serviceEndpoint = " + serviceEndpoint);
        //System.out.println("sparqlQuery = " + sparqlQuery);
        //System.out.println("user = " + user);
        //System.out.println("pass = " + pass);
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        Property inDateTimeProperty = ResourceFactory.createProperty("http://www.w3.org/TR/owl-time#inDateTime");
        Property beginTimeProperty = ResourceFactory.createProperty("http://www.w3.org/TR/owl-time#hasBeginning");
        Property endTimeProperty = ResourceFactory.createProperty("http://www.w3.org/TR/owl-time#hasEnd");


        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();
        String oldEvent="";
        ArrayList<Statement> instanceRelations = new ArrayList<Statement>();
        ArrayList<Statement> otherRelations = new ArrayList<Statement>();
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String relString = solution.get("relation").toString();
            String currentEvent = solution.get("event").toString();
            RDFNode obj = solution.get("object");
            String objUri = obj.toString();
            Statement s = createStatement((Resource) solution.get("event"), ResourceFactory.createProperty(relString), obj);
            if (isSemRelation(relString) || isESORelation(relString) || isFNRelation(relString) || isPBRelation(relString))
            {
                otherRelations.add(s);
                if (isSemTimeRelation(relString)) {
                    if (solution.get("indatetime")!=null){
//                            System.out.println("in " + solution.get("indatetime"));
                        String uri = ((Resource) obj).getURI();
                        Statement s2 = createStatement((Resource) obj, inDateTimeProperty, solution.get("indatetime"));
                        if (trigTripleData.tripleMapInstances.containsKey(uri)) {
                            ArrayList<Statement> triples = trigTripleData.tripleMapInstances.get(uri);
                            triples.add(s2);
                            trigTripleData.tripleMapInstances.put(uri, triples);
                        } else {
                            ArrayList<Statement> triples = new ArrayList<Statement>();
                            triples.add(s2);
                            trigTripleData.tripleMapInstances.put(uri, triples);
                        }
                    }
                    else {
                        if (solution.get("begintime")!=null){
//                            System.out.println("begin " + solution.get("begintime"));
                            String uri = ((Resource) obj).getURI();
                            Statement s2 = createStatement((Resource) obj, beginTimeProperty, solution.get("begintime"));
                            if (trigTripleData.tripleMapInstances.containsKey(uri)) {
                                ArrayList<Statement> triples = trigTripleData.tripleMapInstances.get(uri);
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            } else {
                                ArrayList<Statement> triples = new ArrayList<Statement>();
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            }
                        }
                        else if (solution.get("endtime")!=null) {
//                            System.out.println("end " + solution.get("endtime"));
                            String uri = ((Resource) obj).getURI();
                            Statement s2 = createStatement((Resource) solution.get("object"), endTimeProperty, solution.get("endtime"));
                            if (trigTripleData.tripleMapInstances.containsKey(uri)) {
                                ArrayList<Statement> triples = trigTripleData.tripleMapInstances.get(uri);
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            } else {
                                ArrayList<Statement> triples = new ArrayList<Statement>();
                                triples.add(s2);
                                trigTripleData.tripleMapInstances.put(uri, triples);
                            }
                        }
                    }
                }
            }
            else // Instances
            {
                instanceRelations.add(s);
            }

            if (!oldEvent.equals("")) {
                if (!currentEvent.equals(oldEvent)){
                    if (instanceRelations.size()>0){
                        trigTripleData.tripleMapInstances.put(oldEvent, instanceRelations);
                    }
                    if (otherRelations.size()>0){
                        trigTripleData.tripleMapOthers.put(oldEvent, otherRelations);
                    }
                    instanceRelations = new ArrayList<Statement>();
                    otherRelations = new ArrayList<Statement>();
                }
            }
            oldEvent=currentEvent;
        }
     //   System.out.println(" * instance statements = "+trigTripleData.tripleMapInstances.size());
     //   System.out.println(" * sem statements = " + trigTripleData.tripleMapOthers.size());
    }

    public static SimpleTaxonomy getTaxonomyFromKnowledgeStore(String sparqlQuery)throws Exception {
        SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String subClass = solution.get("child").toString();
            RDFNode obj = solution.get("parent");
            String superClass = obj.toString();
            if (!subClass.equals(superClass)) {
                simpleTaxonomy.subToSuper.put(subClass, superClass);
                if (simpleTaxonomy.superToSub.containsKey(superClass)) {
                    ArrayList<String> subs = simpleTaxonomy.superToSub.get(superClass);
                    if (!subs.contains(subClass)) {
                        subs.add(subClass);
                        simpleTaxonomy.superToSub.put(superClass, subs);
                    }
                }
                else {
                    ArrayList<String> subs = new ArrayList<String>();
                    subs.add(subClass);
                    simpleTaxonomy.superToSub.put(superClass, subs);
                }
            }
        }
        return simpleTaxonomy;
    }

    public static ArrayList<PhraseCount>  getCountsFromKnowledgeStore(String sparqlQuery)throws Exception {
        ArrayList<PhraseCount> cntPredicates = new ArrayList<PhraseCount>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();

        //// The problem is that the full hiearchy is given for
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String value = solution.get("value").toString();
            String count = solution.get("count").toString();
            int idx = count.indexOf("^^");
            if (idx>-1) count = count.substring(0, idx);
            /*System.out.println("instance = " + instance);
            System.out.println("type = " + type);
            System.out.println("count = " + count);*/

            if (!value.isEmpty()) {
                PhraseCount phraseCount = new PhraseCount(value,  Integer.parseInt(count));
                cntPredicates.add(phraseCount);
            }
        }
        return cntPredicates;
    }


    public static HashMap<String, TypedPhraseCount>  getTypesAndInstanceCountsFromKnowledgeStore(String sparqlQuery)throws Exception {
        HashMap<String, TypedPhraseCount> cntPredicates = new HashMap<String, TypedPhraseCount>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();

        //// The problem is that the full hiearchy is given for
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String instance = solution.get("a").toString();
            String type = solution.get("type").toString();
            String count = solution.get("count").toString();
            int idx = count.indexOf("^^");
            if (idx>-1) count = count.substring(0, idx);
            /*System.out.println("instance = " + instance);
            System.out.println("type = " + type);
            System.out.println("count = " + count);*/

            if (!instance.isEmpty()) {
                if (cntPredicates.containsKey(instance)) {
                    TypedPhraseCount typedPhraseCount = cntPredicates.get(instance);
                    typedPhraseCount.addType(type);
                    cntPredicates.put(instance, typedPhraseCount);
                } else {
                    TypedPhraseCount typedPhraseCount = new TypedPhraseCount(instance, Integer.parseInt(count));
                    typedPhraseCount.addType(type);
                    cntPredicates.put(instance, typedPhraseCount);
                }
            }
        }
        return cntPredicates;
    }

    public static HashMap<String, TypedPhraseCount>  getLabelsTypesAndInstanceCountsFromKnowledgeStore(String sparqlQuery)throws Exception {
        HashMap<String, TypedPhraseCount> cntPredicates = new HashMap<String, TypedPhraseCount>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();

        //// The problem is that the full hiearchy is given for
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String label = solution.get("label").toString();
            String instance = solution.get("a").toString();
            String type = solution.get("type").toString();
            String count = solution.get("count").toString();
            int idx = count.indexOf("^^");
            if (idx>-1) count = count.substring(0, idx);
            /*System.out.println("instance = " + instance);
            System.out.println("type = " + type);
            System.out.println("count = " + count);
            System.out.println("label = " + label);
*/
            if (!instance.isEmpty()) {
                if (cntPredicates.containsKey(instance)) {
                    TypedPhraseCount typedPhraseCount = cntPredicates.get(instance);
                    typedPhraseCount.addType(type);
                    typedPhraseCount.addLabel(label);
                    cntPredicates.put(instance, typedPhraseCount);
                } else {
                    TypedPhraseCount typedPhraseCount = new TypedPhraseCount(instance, Integer.parseInt(count));
                    typedPhraseCount.addType(type);
                    typedPhraseCount.addLabel(label);
                    cntPredicates.put(instance, typedPhraseCount);
                }
            }
        }
        return cntPredicates;
    }

    public static HashMap<String, TypedPhraseCount>  getTypesAndLabelCountsFromKnowledgeStore(String sparqlQuery)throws Exception {
        HashMap<String, TypedPhraseCount> cntPredicates = new HashMap<String, TypedPhraseCount>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();

        //// The problem is that the full hiearchy is given for
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String label = solution.get("label").toString();
            String type = solution.get("type").toString();
            String count = solution.get("count").toString();
            int idx = count.indexOf("^^");
            if (idx>-1) count = count.substring(0, idx);
            /*System.out.println("label = " + label);
            System.out.println("type = " + type);
            System.out.println("count = " + count);*/

            if (!label.isEmpty()) {
                if (cntPredicates.containsKey(label)) {
                    TypedPhraseCount typedPhraseCount = cntPredicates.get(label);
                    typedPhraseCount.addType(type);
                    cntPredicates.put(label, typedPhraseCount);
                } else {
                    TypedPhraseCount typedPhraseCount = new TypedPhraseCount(label, Integer.parseInt(count));
                    typedPhraseCount.addType(type);
                    cntPredicates.put(label, typedPhraseCount);
                }
            }
        }
        return cntPredicates;
    }

    public static HashMap<String, Integer>  getTopicsAndLabelCountsFromKnowledgeStore(String sparqlQuery)throws Exception {
        HashMap<String, Integer> cntPredicates = new HashMap<String, Integer>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();

        //// The problem is that the full hiearchy is given for
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String topic = solution.get("topic").toString();
            String type = "";
            String count = solution.get("count").toString();
            int idx = count.indexOf("^^");
            if (idx>-1) count = count.substring(0, idx);
            /*System.out.println("label = " + label);
            System.out.println("type = " + type);
            System.out.println("count = " + count);*/
            if (!topic.isEmpty()) {
                cntPredicates.put(topic,  Integer.parseInt(count));
            }
        }
        return cntPredicates;
    }
/*
 public static HashMap<String, ArrayList<PhraseCount>>  getTopicsAndLabelCountsFromKnowledgeStore(String sparqlQuery,
                                                                                                     EuroVoc euroVoc,
                                                                                                     SimpleTaxonomy simpleTaxonomy)throws Exception {
        HashMap<String, ArrayList<PhraseCount>> cntPredicates = new HashMap<String, ArrayList<PhraseCount>>();
        HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

        QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
        ResultSet resultset = x.execSelect();

        //// The problem is that the full hiearchy is given for
        while (resultset.hasNext()) {
            QuerySolution solution = resultset.nextSolution();
            String topic = solution.get("topic").toString();
            String type = "";
            String count = solution.get("count").toString();
            int idx = count.indexOf("^^");
            if (idx>-1) count = count.substring(0, idx);
            */
/*System.out.println("label = " + label);
            System.out.println("type = " + type);
            System.out.println("count = " + count);*//*

            if (!topic.isEmpty()) {
*/
/*                if (euroVoc.uriLabelMap.containsKey(topic)) {
                    String label = euroVoc.uriLabelMap.get(topic);
                    if (simpleTaxonomy.labelToConcept.containsKey(label)) {
                        type = simpleTaxonomy.labelToConcept.get(label);
                        PhraseCount phraseCount = new PhraseCount(label, Integer.parseInt(count));
                        if (cntPredicates.containsKey(type)) {
                            ArrayList<PhraseCount> phrases = cntPredicates.get(type);
                            phrases.add(phraseCount);
                            cntPredicates.put(type, phrases);
                        } else {
                            ArrayList<PhraseCount> phrases = new ArrayList<PhraseCount>();
                            phrases.add(phraseCount);
                            cntPredicates.put(type, phrases);
                        }
                    } else {
                        System.out.println("Could not find label = " + label);
                    }
                } else {
                    System.out.println("Could not find concept = " + topic);
                }*//*


                if (euroVoc.uriLabelMap.containsKey(topic)) {
                    String label = euroVoc.uriLabelMap.get(topic);
                    if (simpleTaxonomy.labelToConcept.containsKey(label)) {
                        type = simpleTaxonomy.labelToConcept.get(label);
                        PhraseCount phraseCount = new PhraseCount(label, Integer.parseInt(count));
                        if (cntPredicates.containsKey(type)) {
                            ArrayList<PhraseCount> phrases = cntPredicates.get(type);
                            phrases.add(phraseCount);
                            cntPredicates.put(type, phrases);
                        } else {
                            ArrayList<PhraseCount> phrases = new ArrayList<PhraseCount>();
                            phrases.add(phraseCount);
                            cntPredicates.put(type, phrases);
                        }
                    } else {
                        System.out.println("Could not find label = " + label);
                    }
                } else {
                    System.out.println("Could not find concept = " + topic);
                }
                */
/*
                if (simpleTaxonomy.conceptToLabels.containsKey(topic)) {
                    ArrayList<String> labels = simpleTaxonomy.conceptToLabels.get(topic);
                    if (labels.size()>0) {
                        String label = simpleTaxonomy.labelToConcept.get(labels.get(0));
                        PhraseCount phraseCount = new PhraseCount(label, Integer.parseInt(count));
                        if (cntPredicates.containsKey(topic)) {
                            ArrayList<PhraseCount> phrases = cntPredicates.get(type);
                            phrases.add(phraseCount);
                            cntPredicates.put(topic, phrases);
                        } else {
                            ArrayList<PhraseCount> phrases = new ArrayList<PhraseCount>();
                            phrases.add(phraseCount);
                            cntPredicates.put(topic, phrases);
                        }
                    } else {
                        System.out.println("Could not find labels = " + topic);
                    }
                } else {
                    System.out.println("Could not find concept = " + topic);
                }*//*

            }
        }
        return cntPredicates;
    }
*/

     public static HashMap<String, ArrayList<PhraseCount>>  getCountsFromKnowledgeStore(String sparqlQuery, String type)throws Exception {
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = new HashMap<String, ArrayList<PhraseCount>>();
            HttpAuthenticator authenticator = new SimpleAuthenticator(user, pass.toCharArray());

            QueryExecution x = QueryExecutionFactory.sparqlService(serviceEndpoint, sparqlQuery, authenticator);
            ResultSet resultset = x.execSelect();
            while (resultset.hasNext()) {
                QuerySolution solution = resultset.nextSolution();
                String phrase = solution.get("a").toString();
                String count = solution.get("count").toString();
                int idx = count.indexOf("^^");
                if (idx>-1) count = count.substring(0, idx);
                /*System.out.println("phrase = " + phrase);
                System.out.println("count = " + count);*/
                PhraseCount phraseCount = new PhraseCount(phrase, Integer.parseInt(count));
                if (cntPredicates.containsKey(type)) {
                    ArrayList<PhraseCount> phrases = cntPredicates.get(type);
                    phrases.add(phraseCount);
                    cntPredicates.put(type, phrases);
                } else {
                    ArrayList<PhraseCount> phrases = new ArrayList<PhraseCount>();
                    phrases.add(phraseCount);
                    cntPredicates.put(type, phrases);
                }
            }
            return cntPredicates;
    }


    private static boolean isEventUri (String subject) {
        String name = subject;
        int idx = subject.lastIndexOf("#");
        if (idx>-1) name = name.substring(idx);
        return name.toLowerCase().startsWith("#ev");
    }

    private static boolean isSemRelation(String relation) {
        return relation.startsWith("http://semanticweb.cs.vu.nl/2009/11/sem/");
    }

    private static boolean isSemTimeRelation(String relation) {
        return relation.startsWith("http://semanticweb.cs.vu.nl/2009/11/sem/hasTime");
    }

    private static boolean isDenotedByRelation(String relation) {
        //http://groundedannotationframework.org/gaf#denotedBy
        return relation.endsWith("denotedBy");
    }
    //<http://groundedannotationframework.org/grasp#hasAttribution>
    public static boolean isAttributionRelation(String relation) {
        return relation.endsWith("hasAttribution");
    }

    public static boolean isProvRelation(String relation) {
        return relation.equals("http://www.w3.org/ns/prov#wasAttributedTo");
    }

    private static boolean isFNRelation(String relation) {
        return relation.startsWith("http://www.newsreader-project.eu/ontologies/framenet/");
    }
    private static boolean isPBRelation(String relation) {
        return relation.startsWith("http://www.newsreader-project.eu/ontologies/propbank/");
    }
    private static boolean isESORelation(String relation) {
        if (relation.indexOf("hasPreSituation")>-1  ||
            relation.indexOf("quantity-attribute")>-1 ||
            relation.indexOf("hasPostSituation")>-1 ||
            relation.indexOf("hasDuring")>-1) {
            return false;
        }
        return relation.startsWith("http://www.newsreader-project.eu/domain-ontology");
    }


}
