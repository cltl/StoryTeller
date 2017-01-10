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

import java.io.FileOutputStream;
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
    static String fnPath = "/Code/vu/newsreader/vua-resources/frAllRelation.xml";
    static String esoPath = "/Code/vu/newsreader/vua-resources/ESO_Version2.owl";
    //static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.label.concept.gz";
    static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.csv.gz";
    static String euroVocHierarchyFile = "/Code/vu/newsreader/vua-resources/eurovoc_in_skos_core_concepts.rdf.gz";
    static String entityTypeFile = "/Code/vu/newsreader/vua-resources/instance_types_en.ttl.gz";
    static String entityHierarchyFile = "/Code/vu/newsreader/vua-resources/DBpediaHierarchy_parent_child.tsv";
    static String entityCustomFile = "";
    static String KSSERVICE = "http://145.100.58.139:50053";
    static String KS = ""; //"nwr/wikinews-new";
    static String KSuser = ""; //"nwr/wikinews-new";
    static String KSpass = ""; //"nwr/wikinews-new";
    static boolean ALLEVENTYPES = false;

    static public void main (String[] args) {
        DEBUG = true;
        ALLEVENTYPES = true;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--eso") && args.length > (i + 1)) {
                esoPath = args[i+1];
            }
            else if (arg.equals("--eurovoc-label") && args.length > (i + 1)) {
                euroVocLabelFile = args[i+1];
            }
            else if (arg.equals("--eurovoc-core") && args.length > (i + 1)) {
                euroVocHierarchyFile = args[i+1];
            }
            else if (arg.equals("--entity-hiearchy") && args.length > (i + 1)) {
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



        getJsonLightEntityHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile);
        getJsonDarkEntityHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass);
        getJsonConceptHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, entityHierarchyFile, entityTypeFile);
        getJsonEventHierarchyFromKnowledgeStore(KSSERVICE, KSuser, KSpass, esoPath, fnPath);
        getJsonHierarchyFromEurovocAndKnowledgeStore(KSSERVICE, KSuser, KSpass, euroVocLabelFile, euroVocHierarchyFile);
        getJsonHierarchyAuthorsKnowledgeStore(KSSERVICE, KSuser, KSpass);
        getJsonHierarchyCiteKnowledgeStore(KSSERVICE, KSuser, KSpass);
     //   getPerspectiveCountsFromKnowledgeStore(KSSERVICE, KSuser, KSpass);

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

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForLightEntityFromKs();
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getLabelsTypesAndInstanceCountsFromKnowledgeStore(sparqlPhrases);
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            ArrayList<String> tops = simpleTaxonomy.getTops();
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "entity", "", tops, 1, cnt, cntPredicates, cntTypedPredicates);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("light-entities.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
            }
            if (!DEBUG) System.out.write(tree.toString(0).getBytes());        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonDarkEntityHierarchyFromKnowledgeStore (String KSSERVICE,
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

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForDarkEntitiesFromKs();
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getLabelsTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases);
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "entity", "", tops, 1, cnt, cntPredicates, cntTypedPredicates);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("dark-entities.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
            }
            if (!DEBUG) System.out.write(tree.toString(0).getBytes());        } catch (Exception e) {
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

            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseSkosRelatedTypeCountsFromKs();
            HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getTypesAndInstanceCountsFromKnowledgeStore (sparqlPhrases);
            //HashMap<String, TypedPhraseCount> cntTypedPredicates = GetTriplesFromKnowledgeStore.getTypesAndLabelCountsFromKnowledgeStore (sparqlPhrases);
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = deriveTypePhraseCountsFromTypedPhrases(cntTypedPredicates, simpleTaxonomy);
            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());

            simpleTaxonomy.readSimpleTaxonomyFromDbpFile(pathToTypeFile, cntPredicates.keySet());

            ArrayList<String> tops = simpleTaxonomy.getTops();
           // simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
/*            if (DEBUG) {
                OutputStream tree = new FileOutputStream("tree.txt");
                simpleTaxonomy.printTree(tree);
                tree.close();
            }*/
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "concept", "", tops, 1, cnt, cntPredicates, null);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("non-entities.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
            }
            if (!DEBUG) System.out.write(tree.toString(0).getBytes());        } catch (Exception e) {
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
            EsoReader esoReader = new EsoReader();
            esoReader.parseFile(esoPath);
            SimpleTaxonomy simpleTaxonomy = esoReader.simpleTaxonomy;
            if (DEBUG) System.out.println("ESO");
            if (DEBUG) System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
            if (DEBUG) System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
            FrameNetReader frameNetReader = new FrameNetReader();
            frameNetReader.parseFile(fnPath);
            simpleTaxonomy.addToTaxonymy(frameNetReader.subToSuperFrame);
            if (DEBUG) System.out.println("FRAMENET");
            if (DEBUG) System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
            if (DEBUG) System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());



            String sparqlPhrases = "";
            if (ALLEVENTYPES) {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForEventAnyTypeCountsFromKs();
            }
            else {
                sparqlPhrases = SparqlGenerator.makeSparqlQueryForEventEsoFramenetTypeCountsFromKs();
            }

            if (DEBUG) {
                OutputStream tree = new FileOutputStream("tree.txt");
                simpleTaxonomy.printTree(tree);
                tree.close();
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
                if (DEBUG) System.out.println("TOPPED");
                if (DEBUG) System.out.println("simpleTaxonomy.superToSub.size() = " + simpleTaxonomy.superToSub.size());
                if (DEBUG) System.out.println("simpleTaxonomy.subToSuper.size() = " + simpleTaxonomy.subToSuper.size());
            }

            ArrayList<String> tops = simpleTaxonomy.getTops();
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "event", "", tops, 1, cnt, cntPredicates, cntTypedPredicates);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("events.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
            }
            if (!DEBUG) System.out.write(tree.toString(0).getBytes());
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

            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getTopicsAndLabelCountsFromKnowledgeStore(sparqlPhrases, euroVoc,simpleTaxonomy);

            if (DEBUG) System.out.println("cntPredicates.size() = " + cntPredicates.size());

            ArrayList<String> tops = simpleTaxonomy.getTops();
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            //@TODO add names to hierarchy types
            simpleTaxonomy.jsonTree(tree, "topic", "", tops, 1, cnt, cntPredicates, null);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("topics.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   static public void getJsonHierarchyAuthorsKnowledgeStore (String KSSERVICE, String KSuser, String KSpass) {
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
            String sparqlTaxonomy = SparqlGenerator.makeSparqlQueryForTaxonomyFromKs(cntPredicates.keySet());
            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            ArrayList<String> tops = simpleTaxonomy.getTops();
            simpleTaxonomy.addTypesToTops(cntPredicates.keySet().iterator(), tops);
            if (DEBUG) System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            if (DEBUG) System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "author", "", tops, 1, cnt, cntPredicates, null);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("authors.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
            }
            else {
                System.out.write(tree.toString(0).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyCiteKnowledgeStore (String KSSERVICE, String KSuser, String KSpass) {
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
            simpleTaxonomy.jsonTree(tree, "cite", "", tops, 1, cnt, cntPredicates, null);
            if (DEBUG) {
                OutputStream fos = new FileOutputStream("cited.debug.json");
                fos.write(tree.toString(0).getBytes());
                fos.close();
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
