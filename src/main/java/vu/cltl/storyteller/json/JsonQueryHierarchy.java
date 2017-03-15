package vu.cltl.storyteller.json;

import org.json.JSONObject;
import vu.cltl.storyteller.input.EsoReader;
import vu.cltl.storyteller.input.EuroVoc;
import vu.cltl.storyteller.input.FrameNetReader;
import vu.cltl.storyteller.knowledgestore.GetTriplesFromKnowledgeStore;
import vu.cltl.storyteller.knowledgestore.SparqlGenerator;
import vu.cltl.storyteller.objects.PhraseCount;
import vu.cltl.storyteller.objects.SimpleTaxonomy;
import vu.cltl.storyteller.objects.TypedPhraseCount;
import vu.cltl.storyteller.util.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 11/12/2016.
 * This class generates JSON hiearchies for querying the data. Different hiearchies are created for different data types:
 * - light and dark entities
 * - non-entities
 * - events
 * - topics
 * - authors
 * - cited sources
 * - perspective values
 */
public class JsonQueryHierarchy {

    static boolean DEBUG = false;
    static OutputStream outputStream = null;
    static String fnPath = "/Code/vu/newsreader/vua-resources/frAllRelation.xml";
    static String esoPath = "/Code/vu/newsreader/vua-resources/ESO_Version2.owl";
    static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.label.concept.gz";
    //static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.csv.gz";
    static String euroVocHierarchyFile = "/Code/vu/newsreader/vua-resources/eurovoc_in_skos_core_concepts.rdf.gz";
    static String entityTypeFile = "/Code/vu/newsreader/vua-resources/instance_types_en.ttl.gz";
    static String entityHierarchyFile = "/Code/vu/newsreader/vua-resources/DBpediaHierarchy_parent_child.tsv";
    static String entityCustomFile = "";

    static String KSSERVICE = "http://130.37.53.35:50053";
    static String KS = ""; //"nwr/wikinews-new";
    static String KSuser = ""; //"nwr/wikinews-new";
    static String KSpass = ""; //"nwr/wikinews-new";
    static boolean ALLEVENTYPES = false;
    static String DATA = "events";
    static Integer mCount = -1;

    static public void main (String[] args) {
        //org.apache.log4j.BasicConfigurator.configure();
        //DEBUG = true;
        //ALLEVENTYPES = true;
        //DATA = "perspectives";
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
            else if (arg.equals("--out") && args.length > (i + 1)) {
                String pathToOutputFile = args[i+1];
                try {
                    outputStream = new FileOutputStream(pathToOutputFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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
        if (DATA.equalsIgnoreCase("light-entities")) getJsonLightEntityHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile);
        else if (DATA.equalsIgnoreCase("dark-entities")) getJsonDarkEntityHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile, entityCustomFile);
        else if (DATA.equalsIgnoreCase("concepts")) getJsonConceptHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile, entityTypeFile);
        else if (DATA.equalsIgnoreCase("events")) getJsonEventHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, esoPath, fnPath);
        else if (DATA.equalsIgnoreCase("topics")) getJsonHierarchyFromEurovocAndKnowledgeStore(KSSERVICE, KSuser, KSpass, euroVocLabelFile, euroVocHierarchyFile);
        else if (DATA.equalsIgnoreCase("authors")) getJsonHierarchyAuthorsKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile);
        else if (DATA.equalsIgnoreCase("cited")) getJsonHierarchyCiteKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile);
        else if (DATA.equalsIgnoreCase("perspectives")) getPerspectiveCountsFromKnowledgeStore(KSSERVICE, KSuser, KSpass);
        else System.out.println("unsupported data type:"+DATA);
        if (outputStream!=null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static public void getPerspectiveCountsFromKnowledgeStore(String KSSERVICE,
                                                              String KSuser,
                                                              String KSpass) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForAttributionValuesFromKs();
            ArrayList<PhraseCount> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore(sparqlPhrases);
            if (DEBUG) {
                System.out.println("nr. of perspectives = " + cntPredicates.size());
            }
            JSONObject jsonPerspectives = new JSONObject();
            for (int i = 0; i < cntPredicates.size(); i++) {
                PhraseCount phraseCount = cntPredicates.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", phraseCount.getPhrase());
                jsonObject.put("count", phraseCount.getCount());
                jsonPerspectives.append("perspectives",jsonObject);
            }
            if (outputStream!=null) {
                outputStream.write(jsonPerspectives.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(jsonPerspectives.toString(0).getBytes());
            }

        }
        catch (Exception e) {
        e.printStackTrace();
        }
    }

    static public void getJsonLightEntityHierarchyFromKnowledgeStore (String KSSERVICE,
                                                           String KSuser,
                                                           String KSpass,
                                                           String pathToHierarchyFile) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }

            if (DEBUG)  System.out.println("Reading hierarchy into simpleTaxonomy = " + entityHierarchyFile);

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
            if (DEBUG) {
                System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForLightEntityFromKs();
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getLabelsTypesAndInstanceCountsFromKnowledgeStore(sparqlPhrases, null);
            if (DEBUG) System.out.println("Nr. of instances with types and phrases, cntTypedPredicates.size() = " + cntTypedPredicates.size());
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            ArrayList<String> tops = simpleTaxonomy.getTops();
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "entity", "lightEntity", "", tops, mCount, 1, cnt, cntPredicates, cntTypedPredicates);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonDarkEntityHierarchyFromKnowledgeStore (String KSSERVICE,
                                                                     String KSuser,
                                                                     String KSpass,
                                                                     String pathToHierarchyFile,
                                                                     String hierarchyMappingFile) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            if (DEBUG)  System.out.println("Reading hierarchy into simpleTaxonomy = " + pathToHierarchyFile);

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
           // simpleTaxonomy.readSimpleTaxonomyFromFile(hierarchyMappingFile);
            if (DEBUG) {
                System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForDarkEntitiesFromKs();
            if (DEBUG)  System.out.println("Reading hiearchy mapping file = " + hierarchyMappingFile);
            HashMap<String, String> map = Util.ReadFileToStringStringHashMap(hierarchyMappingFile);
            if (DEBUG) System.out.println("Nr. of hierarchy mappings = " + map.size());
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getLabelsTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases, map);
            if (DEBUG) System.out.println("Nr. of instances with types and phrases, cntTypedPredicates.size() = " + cntTypedPredicates.size());
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "entity", "darkEntity", "", tops, mCount, 1, cnt, cntPredicates, cntTypedPredicates);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonConceptHierarchyFromKnowledgeStore (String KSSERVICE,
                                                                  String KSuser,
                                                                  String KSpass,
                                                                  String pathToHierarchyFile,
                                                                  String pathToTypeFile) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            if (DEBUG)  System.out.println("Reading hierarchy into simpleTaxonomy = " + pathToHierarchyFile);

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
            if (DEBUG) {
                System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseSkosRelatedTypeCountsFromKs();
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases);
            if (DEBUG) System.out.println("Nr. of instances with types and phrases, cntTypedPredicates.size() = " + cntTypedPredicates.size());
            //HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getTypesAndLabelCountsFromKnowledgeStore (sparqlPhrases);
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());

            if (DEBUG)  System.out.println("Reading instance to type mapping into simpleTaxonomy for relevant instances = " + pathToTypeFile);
            simpleTaxonomy.readSimpleTaxonomyFromDbpFile(pathToTypeFile, cntPredicates.keySet());

            if (DEBUG) {
                System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }

            ArrayList<String> tops = simpleTaxonomy.getTops();

            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "concept", "nonEntity", "", tops, mCount, 1, cnt, cntPredicates, null);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/*        if (DEBUG) {
        System.out.println("eso = " + esoPath);
        System.out.println("framenet = " + fnPath);
        System.out.println("eurovoc-label = " + euroVocLabelFile);
        System.out.println("eurovoc-core = " + euroVocHierarchyFile);
        System.out.println("entity-hierarchy = " + entityHierarchyFile);
        System.out.println("entity-type = " + entityTypeFile);
        System.out.println("entity-custom = " + entityCustomFile);
    }*/

    static public void getJsonHierarchyAuthorsKnowledgeStoreOrg (String KSSERVICE, String KSuser, String KSpass) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForAuthorsFromKs();
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases, "authors");
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "author", "agent", "", tops, mCount, 1, cnt, cntPredicates, null);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyAuthorsKnowledgeStore (String KSSERVICE, String KSuser, String KSpass, String pathToHierarchyFile) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            if (DEBUG)  System.out.println("Reading hierarchy into simpleTaxonomy = " + pathToHierarchyFile);

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
            // simpleTaxonomy.readSimpleTaxonomyFromFile(hierarchyMappingFile);
            if (DEBUG) {
                System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForAuthorsFromKs();
            //HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getLabelsTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases, null);
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getInstanceCountsFromKnowledgeStore (sparqlPhrases);
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();

            simpleTaxonomy.jsonTree(tree, "author", "agent", "", tops, mCount, 1, cnt, cntPredicates, null);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyCiteKnowledgeStore (String KSSERVICE, String KSuser, String KSpass, String pathToHierarchyFile) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            if (DEBUG)  System.out.println("Reading hierarchy into simpleTaxonomy = " + pathToHierarchyFile);

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
            // simpleTaxonomy.readSimpleTaxonomyFromFile(hierarchyMappingFile);
            if (DEBUG) {
                System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForCitedSourcesFromKs();
            //HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getLabelsTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases, null);
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases, null);
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "cite", "agent", "", tops, mCount, 1, cnt, cntPredicates, null);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyCiteKnowledgeStoreOrg (String KSSERVICE, String KSuser, String KSpass) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForCitedSourcesFromKs();
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases, "cited");
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "cite", "agent", "", tops, mCount, 1, cnt, cntPredicates, null);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates event hiearchy from KS data using ESO and FRAMENET ontologies
     * @param KSSERVICE
     * @param KSuser
     * @param KSpass
     * @param esoPath
     * @param fnPath
     */
    static public void getJsonEventHierarchyFromKnowledgeStore (String KSSERVICE, String KSuser, String KSpass,
                                                                 String esoPath, String fnPath) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            if (DEBUG)  System.out.println("Reading eso into simpleTaxonomy = " + esoPath);

            EsoReader esoReader = new EsoReader();
            esoReader.parseFile(esoPath);
            SimpleTaxonomy simpleTaxonomy = esoReader.simpleTaxonomy;
            if (DEBUG) System.out.println("ESO");
            if (DEBUG) System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
            if (DEBUG) System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
            if (DEBUG) System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());

            if (DEBUG)  System.out.println("Reading framenet into simpleTaxonomy = " + fnPath);

            FrameNetReader frameNetReader = new FrameNetReader();
            frameNetReader.parseFile(fnPath);
            simpleTaxonomy.addToTaxonymy(frameNetReader.subToSuperFrame);
            if (DEBUG) System.out.println("FRAMENET");
            if (DEBUG) System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
            if (DEBUG) System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
            if (DEBUG) System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());



            String sparqlPhrases = "";
            if (ALLEVENTYPES) {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForEventAnyTypeCountsFromKs();
            }
            else {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForEventEsoFramenetTypeCountsFromKs();
            }

            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getTypesAndLabelCountsFromKnowledgeStore (sparqlPhrases);
            //// Does not seem to work
            //cntTypedPredicates = mergeSynonyms(cntTypedPredicates);
            preferOneType(cntTypedPredicates, "http://www.newsreader-project.eu/domain-ontology");
            if (ALLEVENTYPES) {
                preferOneType(cntTypedPredicates, "http://www.newsreader-project.eu/ontologies/framenet");
                preferOneType(cntTypedPredicates, "http://globalwordnet.org/ili/");
            }
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            cntPredicates= nameSpace(cntPredicates, " http://globalwordnet.org/ili/", "ili");
            cntPredicates= nameSpace(cntPredicates, "http://www.newsreader-project.eu/domain-ontology", "eso");
            cntPredicates = nameSpace(cntPredicates, "http://www.newsreader-project.eu/ontologies/framenet/", "fn");
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            if (ALLEVENTYPES) {
                simpleTaxonomy.topEvents(cntPredicates.keySet().iterator());
                if (DEBUG) System.out.println("ALL EVENT TYPES");
                if (DEBUG) System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                if (DEBUG) System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
                if (DEBUG) System.out.println("simpleTaxonomy.getTops().size() = " + simpleTaxonomy.getTops().size());
            }

            ArrayList<String> tops = simpleTaxonomy.getTops();
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "eso", "event", "", tops, 1, mCount, cnt, cntPredicates, cntTypedPredicates);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static public void getJsonHierarchyFromEurovocAndKnowledgeStore (String KSSERVICE, String KSuser, String KSpass,
                                                                     String euroVocLabelFile,
                                                                     String euroVocHierarchyFile) {
        try {
            if (!KSSERVICE.isEmpty()) {
                if (KSuser.isEmpty()) {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS);
                }
                else {
                    GetTriplesFromKnowledgeStore.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
                }
            }
            EuroVoc euroVoc = new EuroVoc();
            euroVoc.readEuroVoc(euroVocLabelFile, "en");
            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromSkosFile(euroVocHierarchyFile);

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForTopicCountsFromKs();


            ArrayList<String> tops = simpleTaxonomy.getTops();
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = GetTriplesFromKnowledgeStore.getTopicsAndLabelCountsFromKnowledgeStore(sparqlPhrases);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            //@TODO add names to hierarchy types
            simpleTaxonomy.jsonTopicTree(tree, "topic", "", tops, 1, euroVoc.uriLabelMap, cnt);

            if (outputStream!=null) {
                outputStream.write(tree.toString(0).getBytes());
                outputStream.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * This function selects from multiple types the most specific parent and creates a HashMap from types to children represented through phraseCounts.
     * This step is necessary since the KnowledgeStore generates a list of types traversing the full hierarchy and we get them in an arbitrary order
     * @param cntTypedPredicates
     * @param simpleTaxonomy
     * @return
     */
    static HashMap<String, ArrayList<PhraseCount>> deriveTypePhraseCountsFromTypedPhrases (HashMap<String, TypedPhraseCount> cntTypedPredicates, SimpleTaxonomy simpleTaxonomy) {
        HashMap<String, ArrayList<PhraseCount>> cntPredicates = new HashMap<String, ArrayList<PhraseCount>>();
        Set keySet = cntTypedPredicates.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            TypedPhraseCount typedPhraseCount = cntTypedPredicates.get(key);
            //System.out.println("typedPhraseCount types = " + typedPhraseCount.getTypes().toString());
            String mostSpecificType = simpleTaxonomy.getMostSpecificChild(typedPhraseCount.getTypes());
            if (mostSpecificType.isEmpty()) {
                for (int i = 0; i < typedPhraseCount.getTypes().size(); i++) {
                    String type = typedPhraseCount.getTypes().get(i);
                   // System.out.println("type = " + type);
                    if (cntPredicates.containsKey(type)) {
                        ArrayList<PhraseCount> phrases = cntPredicates.get(type);
                        phrases.add(typedPhraseCount.castToPhraseCount());
                        cntPredicates.put(type, phrases);
                    } else {
                        ArrayList<PhraseCount> phrases = new ArrayList<PhraseCount>();
                        phrases.add(typedPhraseCount.castToPhraseCount());
                        cntPredicates.put(type, phrases);
                    }
                }
            }
            else {
              //  System.out.println("mostSpecificType = " + mostSpecificType);
                if (cntPredicates.containsKey(mostSpecificType)) {
                    ArrayList<PhraseCount> phrases = cntPredicates.get(mostSpecificType);
                    phrases.add(typedPhraseCount.castToPhraseCount());
                    cntPredicates.put(mostSpecificType, phrases);
                } else {
                    ArrayList<PhraseCount> phrases = new ArrayList<PhraseCount>();
                    phrases.add(typedPhraseCount.castToPhraseCount());
                    cntPredicates.put(mostSpecificType, phrases);
                }
            }
        }
        return cntPredicates;
    }


    static HashMap<String, TypedPhraseCount> mergeSynonyms (HashMap<String, TypedPhraseCount> cntTypedPredicates) {
        HashMap<String, TypedPhraseCount> mergedTypedPredicates = new HashMap<String, TypedPhraseCount>();
        Set keySet = cntTypedPredicates.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            TypedPhraseCount typedPhraseCount = cntTypedPredicates.get(key);
            ArrayList<String> nonIliTypes = new ArrayList<String>();
            ArrayList<String> iliTypes = new ArrayList<String>();
            for (int i = 0; i < typedPhraseCount.getTypes().size(); i++) {
                String type = typedPhraseCount.getTypes().get(i);
                if (type.indexOf("globalwordnet") == -1) {
                   nonIliTypes.add(type);
                }
                else {
                    iliTypes.add(type);
                }
            }
           // System.out.println("iliTypes.toString() = " + iliTypes.toString());
           // System.out.println("nonIliTypes = " + nonIliTypes.toString());
            if (iliTypes.size()>0) {
                for (int i = 0; i < iliTypes.size(); i++) {
                    String type = iliTypes.get(i);
                    if (mergedTypedPredicates.containsKey(type)) {
                        TypedPhraseCount iliTypedPhraseCount = mergedTypedPredicates.get(type);
                        for (int j = 0; j < nonIliTypes.size(); j++) {
                            String nonIliType = nonIliTypes.get(j);
                            iliTypedPhraseCount.addType(nonIliType);
                        }
                        iliTypedPhraseCount.addLabel(key);
                        iliTypedPhraseCount.addCount(typedPhraseCount.getCount());
                        mergedTypedPredicates.put(type, iliTypedPhraseCount);
                    }
                    else {
                        TypedPhraseCount iliTypedPhraseCount = new TypedPhraseCount(type, typedPhraseCount.getCount());
                        for (int j = 0; j < nonIliTypes.size(); j++) {
                            String nonIliType = nonIliTypes.get(j);
                            iliTypedPhraseCount.addType(nonIliType);
                        }
                        iliTypedPhraseCount.addLabel(key);
                        mergedTypedPredicates.put(type, iliTypedPhraseCount);
                    }
                }
            }
            else{
                mergedTypedPredicates.put(key, typedPhraseCount);
            }
        }
        return mergedTypedPredicates;
    }

    static void preferOneType (HashMap<String, TypedPhraseCount> cntTypedPredicates, String uri) {
        Set keySet = cntTypedPredicates.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            TypedPhraseCount typedPhraseCount = cntTypedPredicates.get(key);
            ArrayList<String> types = typedPhraseCount.getTypes();
            ArrayList<String> selectedTypes = new ArrayList<String>();
            for (int i = 0; i < types.size(); i++) {
                String type = types.get(i);
                if (type.startsWith(uri)) {
                    selectedTypes.add(type);
                }
            }
            if (selectedTypes.size()>0) {
                // System.out.println("selectedTypes.toString() = " + selectedTypes.toString());
                typedPhraseCount.setTypes(selectedTypes);
                cntTypedPredicates.put(key, typedPhraseCount);
            }
        }
    }

    static HashMap<String, ArrayList<PhraseCount>> nameSpace(HashMap<String, ArrayList<PhraseCount>> cntPredicates, String URI, String ns) {
        HashMap<String, ArrayList<PhraseCount>> newMap = new HashMap<String, ArrayList<PhraseCount>>();
        Set keySet = cntPredicates.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            ArrayList<PhraseCount> phraseCounts = cntPredicates.get(key);
            if (key.startsWith(URI)) {
                int idx = key.lastIndexOf("#");
                if (idx == -1) idx = key.lastIndexOf("/");
                if (idx>-1) {
                    key = ns+":"+key.substring(idx+1);
                }
            }
            newMap.put(key, phraseCounts);
        }
        return newMap;
    }

    static public HashMap<String, Integer> cntPhrases (HashMap<String, ArrayList<PhraseCount>> map) {
        HashMap<String, Integer> countMap = new HashMap<String, Integer>();
        Set keySet = map.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            ArrayList<PhraseCount> phrases = map.get(key);
            Integer sum = 0;
            for (int i = 0; i < phrases.size(); i++) {
                PhraseCount phraseCount = phrases.get(i);
                sum += phraseCount.getCount();
            }
           // System.out.println("key = " + key+":"+sum);
            countMap.put(key, sum);
        }
        return countMap;
    }


}
