package vu.cltl.storyteller.json;

import org.json.JSONObject;
import vu.cltl.storyteller.input.EsoReader;
import vu.cltl.storyteller.input.EuroVoc;
import vu.cltl.storyteller.knowledgestore.GetTriplesFromKnowledgeStore;
import vu.cltl.storyteller.knowledgestore.SparqlGenerator;
import vu.cltl.storyteller.objects.PhraseCount;
import vu.cltl.storyteller.objects.SimpleTaxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 11/12/2016.
 */
public class JsonQueryHierarchy {

    static String esoPath = "/Code/vu/newsreader/vua-resources/ESO.v2/ESO_V2_Final.owl";
    static String euroVocLabelFile = "/Code/vu/newsreader/vua-resources/mapping_eurovoc_skos.label.concept.gz";
    static String euroVocHierarchyFile = "/Code/vu/newsreader/vua-resources/eurovoc_in_skos_core_concepts.rdf.gz";

    static public void main (String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--ent") && args.length > (i + 1)) {
                //entityPath = args[i+1];
            }
        }
        getJsonHierarchyFromKnowledgeStore();
        getJsonHierarchyFromEsoAndKnowledgeStore(esoPath);
        getJsonHierarchyFromEurovocAndKnowledgeStore(euroVocLabelFile, euroVocHierarchyFile);
        getJsonHierarchyAuthorsKnowledgeStore();
        getJsonHierarchyCiteKnowledgeStore();
    }

    static public void getJsonHierarchyFromKnowledgeStore () {
        try {
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseCountsFromKs("");
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases);
            //System.out.println("cntPredicates.size() = " + cntPredicates.size());
            String sparqlTaxonomy = SparqlGenerator.makeSparqlQueryForTaxonomyFromKs(cntPredicates.keySet());
            SimpleTaxonomy simpleTaxonomy = GetTriplesFromKnowledgeStore.getTaxonomyFromKnowledgeStore(sparqlTaxonomy);
            ArrayList<String> tops = simpleTaxonomy.getTops();
            //System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("dbp:", tops, cnt);
            //System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "entity", "dbp:", tops, 1, cnt, cntPredicates, null);
            System.out.write(tree.toString(0).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyFromEsoAndKnowledgeStore (String esoPath) {
        try {
            EsoReader esoReader = new EsoReader();
            esoReader.parseFile(esoPath);
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseCountsFromKs("");
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases);
            //System.out.println("cntPredicates.size() = " + cntPredicates.size());
            SimpleTaxonomy simpleTaxonomy = esoReader.simpleTaxonomy;
            ArrayList<String> tops = simpleTaxonomy.getTops();
            //System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("dbp:", tops, cnt);
            //System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "event", "eso:", tops, 1, cnt, cntPredicates, null);
            System.out.write(tree.toString(0).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyFromEurovocAndKnowledgeStore (String euroVocLabelFile,
                                                                     String euroVocHierarchyFile) {
        try {
            EuroVoc euroVoc = new EuroVoc();
            euroVoc.readEuroVoc(euroVocLabelFile, "en");
            SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
            simpleTaxonomy.readSimpleTaxonomyFromSkosFile(euroVocHierarchyFile);

            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseCountsFromKs("");
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases);
            //System.out.println("cntPredicates.size() = " + cntPredicates.size());

            ArrayList<String> tops = simpleTaxonomy.getTops();
            //System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("dbp:", tops, cnt);
            //System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "topic", "", tops, 1, cnt, cntPredicates, null);
            System.out.write(tree.toString(0).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   static public void getJsonHierarchyAuthorsKnowledgeStore () {
        try {
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseCountsFromKs("author");
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases);
            //System.out.println("cntPredicates.size() = " + cntPredicates.size());
            String sparqlTaxonomy = SparqlGenerator.makeSparqlQueryForTaxonomyFromKs(cntPredicates.keySet());
            SimpleTaxonomy simpleTaxonomy = GetTriplesFromKnowledgeStore.getTaxonomyFromKnowledgeStore(sparqlTaxonomy);
            ArrayList<String> tops = simpleTaxonomy.getTops();
            //System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            //System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "author", "", tops, 1, cnt, cntPredicates, null);
            System.out.write(tree.toString(0).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void getJsonHierarchyCiteKnowledgeStore () {
        try {
            String sparqlPhrases = SparqlGenerator.makeSparqlQueryForPhraseCountsFromKs("cite");
            HashMap<String, ArrayList<PhraseCount>> cntPredicates = GetTriplesFromKnowledgeStore.getCountsFromKnowledgeStore (sparqlPhrases);
            //System.out.println("cntPredicates.size() = " + cntPredicates.size());
            String sparqlTaxonomy = SparqlGenerator.makeSparqlQueryForTaxonomyFromKs(cntPredicates.keySet());
            SimpleTaxonomy simpleTaxonomy = GetTriplesFromKnowledgeStore.getTaxonomyFromKnowledgeStore(sparqlTaxonomy);
            ArrayList<String> tops = simpleTaxonomy.getTops();
            //System.out.println("tops.toString() = " + tops.toString());
            HashMap<String, Integer> cnt = cntPhrases(cntPredicates);
            simpleTaxonomy.cumulateScores("", tops, cnt);
            //System.out.println("building hierarchy");
            JSONObject tree = new JSONObject();
            simpleTaxonomy.jsonTree(tree, "cite", "", tops, 1, cnt, cntPredicates, null);
            System.out.write(tree.toString(0).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            countMap.put(key, sum);
        }
        return countMap;
    }


}
