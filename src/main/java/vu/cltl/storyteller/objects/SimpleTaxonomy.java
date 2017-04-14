package vu.cltl.storyteller.objects;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.json.JSONException;
import org.json.JSONObject;
import vu.cltl.storyteller.input.EuroVoc;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by piek on 29/05/16.
 */
public class SimpleTaxonomy {
    public HashMap<String, ArrayList<String>> conceptToLabels = new HashMap<String, ArrayList<String>>();
    public HashMap<String, String> labelToConcept = new HashMap<String, String>();
    public HashMap<String, String> conceptToPrefLabel = new HashMap<String, String>();
    public HashMap<String, String> subToSuper = new HashMap<String, String>();
    public HashMap<String, ArrayList<String>> superToSub = new HashMap<String, ArrayList<String>>();

    public SimpleTaxonomy () {
        subToSuper = new HashMap<String, String>();
        superToSub = new HashMap<String, ArrayList<String>>();
    }

    public void readSimpleTaxonomyFromSkosFile (String filePath) {
        //<rdf:Description rdf:about="http://eurovoc.europa.eu/8404">
        // <skos:broader rdf:resource="http://eurovoc.europa.eu/2467"/>
        try {
            InputStreamReader isr = null;
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                String subClass = "";
                String superClass= "";
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {

                        //<rdf:Description rdf:about="http://eurovoc.europa.eu/8404">
                        // <skos:broader rdf:resource="http://eurovoc.europa.eu/2467"/>
                        int idx_s = inputLine.indexOf("<rdf:Description rdf:about=");
                        int idx_e = -1;
                        if (idx_s>-1) {
                            idx_s = inputLine.indexOf("\"");
                            idx_e = inputLine.lastIndexOf("\"");
                            subClass = inputLine.substring(idx_s+1, idx_e);
                           // System.out.println("subClass = " + subClass);
                        }
                        else {
                            idx_s = inputLine.indexOf("<skos:broader rdf:resource=");
                            idx_e = -1;
                            if (idx_s>-1) {
                                idx_s = inputLine.indexOf("\"");
                                idx_e = inputLine.lastIndexOf("\"");
                                superClass = inputLine.substring(idx_s+1, idx_e);
                                //System.out.println("parent = " + superClass);
                                if (!subClass.equals(superClass)) {
                                    subToSuper.put(subClass, superClass);
                                    if (superToSub.containsKey(superClass)) {
                                        ArrayList<String> subs = superToSub.get(superClass);
                                        if (!subs.contains(subClass)) {
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    }
                                    else {
                                        ArrayList<String> subs = new ArrayList<String>();
                                        subs.add(subClass);
                                        superToSub.put(superClass, subs);
                                    }
                                }
                            }
                            else {
                                //<skos:prefLabel xml:lang="en">
                                //<skos:altLabel xml:lang="en">resolution of the European Parliament</skos:altLabel>
                                idx_s = inputLine.indexOf("skos:prefLabel xml:lang=\"en\">");
                                idx_e = -1;
                                if (idx_s>-1) {
                                    idx_s = inputLine.indexOf(">");
                                    idx_e = inputLine.lastIndexOf("</");
                                    String label = inputLine.substring(idx_s+1, idx_e);
                                    labelToConcept.put(label, subClass);
                                    conceptToPrefLabel.put(subClass, label);
                                    if (conceptToLabels.containsKey(subClass)) {
                                        ArrayList<String> labels = conceptToLabels.get(subClass);
                                        labels.add(label);
                                        conceptToLabels.put(subClass, labels);
                                    }
                                    else {
                                        ArrayList<String> labels = new ArrayList<String>();
                                        labels.add(label);
                                        conceptToLabels.put(subClass, labels);
                                    }
                                }
                                else {
                                    idx_s = inputLine.indexOf("skos:altLabel xml:lang=\"en\">");
                                    idx_e = -1;
                                    if (idx_s > -1) {
                                        idx_s = inputLine.indexOf(">");
                                        idx_e = inputLine.lastIndexOf("</");
                                        String label = inputLine.substring(idx_s + 1, idx_e);
                                        labelToConcept.put(label, subClass);
                                        if (conceptToLabels.containsKey(subClass)) {
                                            ArrayList<String> labels = conceptToLabels.get(subClass);
                                            labels.add(label);
                                            conceptToLabels.put(subClass, labels);
                                        }
                                        else {
                                            ArrayList<String> labels = new ArrayList<String>();
                                            labels.add(label);
                                            conceptToLabels.put(subClass, labels);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    /**
     * Assume the following structure TAB separated structure with Parent TAB Child per line
     * Parent   Child
     * Parent   Child
     * etc...
     * @param filePath
     */
    public void readSimpleTaxonomyFromFile (String filePath) {
        try {
            InputStreamReader isr = null;
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {
                        String[] fields = inputLine.split("\t");
                        if (fields.length == 2) {
                            for (int i = 0; i < fields.length-1; i++) {
                                String superClass = fields[0];
                                String subClass = fields[1];
                                if (!subClass.equals(superClass)) {
                                    subToSuper.put(subClass, superClass);
                                    if (superToSub.containsKey(superClass)) {
                                        ArrayList<String> subs = superToSub.get(superClass);
                                        if (!subs.contains(subClass)) {
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    }
                                    else {
                                        ArrayList<String> subs = new ArrayList<String>();
                                        subs.add(subClass);
                                        superToSub.put(superClass, subs);
                                    }
                                }
                            }
                        }
                        else {
                            System.out.println("Skipping line:"+inputLine);
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    public void readSimpleTaxonomyFromDbpFile (String filePath) {
        try {
            InputStreamReader isr = null;
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {
                        System.out.println("inputLine = " + inputLine);
                        String[] fields = inputLine.split("\t");
                        if (fields.length > 1) {
                            for (int i = 0; i < fields.length-1; i++) {
                                String subClass = "dbp:"+fields[i+1];
                                Integer cnt = -1;
                                try {
                                    cnt = Integer.parseInt(subClass);
                                } catch (NumberFormatException e) {
                                   // e.printStackTrace();
                                    //So only if fields[i+1] is not a count!
                                    //System.out.println("subClass = " + subClass);
                                    String superClass = "dbp:"+fields[i];
                                    //System.out.println("subClass = " + subClass);
                                    //System.out.println("superClass = " + superClass);
                                    if (!subClass.equals(superClass)) {
                                        subToSuper.put(subClass, superClass);
                                        if (superToSub.containsKey(superClass)) {
                                            ArrayList<String> subs = superToSub.get(superClass);
                                            if (!subs.contains(subClass)) {
                                                subs.add(subClass);
                                                superToSub.put(superClass, subs);
                                            }
                                        }
                                        else {
                                            ArrayList<String> subs = new ArrayList<String>();
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            System.out.println("Skipping line:"+inputLine);
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    public void readSimpleTaxonomyFromDbpFile (String filePath, Set<String> keySet) {
        try {
            InputStreamReader isr = null;
            //System.out.println("filePath = " + filePath);
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {
                             /*
<http://dbpedia.org/resource/Abraham_Lincoln__1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/TimePeriod> .
<http://dbpedia.org/resource/Abraham_Lincoln__2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/TimePeriod> .
<http://dbpedia.org/resource/Abraham_Lincoln__3> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/TimePeriod> .
<http://dbpedia.org/resource/Austroasiatic_languages> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> .
<http://dbpedia.org/resource/Afroasiatic_languages> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> .

inputLine = <http://dbpedia.org/resource/Cabot_Tower_(St._John's)> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Building> .
     */
                       // if (inputLine.indexOf("/resource/")>-1)  System.out.println("inputLine = " + inputLine);
                        String[] fields = inputLine.split(" ");
                        if (fields.length >= 2) {
                            String subClass = fields[0];
                            if (subClass.startsWith("<")) subClass = subClass.substring(1);
                            if (subClass.endsWith(">")) subClass = subClass.substring(0, subClass.length()-1);
                           // System.out.println("className = " + className);
                            if (keySet.contains(subClass)) {
                                //System.out.println("subClass = " + subClass);
                                String superClass = fields[2];
                                if (superClass.startsWith("<")) superClass = superClass.substring(1);
                                if (superClass.endsWith(">")) superClass = superClass.substring(0, superClass.length()-1);
                               // superClass = superClass.substring(superClass.lastIndexOf("/"));
                                if (!subClass.equals(superClass)) {
                                    subToSuper.put(subClass, superClass);
                                    if (superToSub.containsKey(superClass)) {
                                        ArrayList<String> subs = superToSub.get(superClass);
                                        if (!subs.contains(subClass)) {
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    } else {
                                        ArrayList<String> subs = new ArrayList<String>();
                                        subs.add(subClass);
                                        superToSub.put(superClass, subs);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    public ArrayList<String> getTops () {
        ArrayList<String> tops = new ArrayList<String>();
        Set keySet = superToSub.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals("eso:SituationRuleAssertion")) {
                if (!subToSuper.containsKey(key)) {
                    if (!tops.contains(key)) tops.add(key);
                }
            }
        }
        return tops;
    }

    public void getParentChain (String c, ArrayList<String> parents) {
        if (subToSuper.containsKey(c)) {
            String p = subToSuper.get(c);
            if (!parents.contains(p)) {
                parents.add(p);
                getParentChain(p, parents);
            }
        }
    }

    public void getDescendants (String c, ArrayList<String> decendants) {
        if (superToSub.containsKey(c)) {
            ArrayList<String> subs = superToSub.get(c);
            for (int i = 0; i < subs.size(); i++) {
                String sub = subs.get(i);
                if (!decendants.contains(sub)) {
                    decendants.add(sub);
                    getDescendants(sub, decendants);
                }
            }
        }
    }

    public String getMostSpecificChild (ArrayList<String> types) {
        String child = "";
        if (types.size()==1) {
            child = types.get(0);
        }
        else {
            ArrayList<String> parents = new ArrayList<String>();
            for (int i = 0; i < types.size(); i++) {
                String t = types.get(i);
                if (subToSuper.containsKey(t)) {
                    for (int j = 0; j < types.size(); j++) {
                        if (j!=i) {
                            String t2 = types.get(j);
                            if (subToSuper.get(t).equals(t2)) {
                                parents.add(t2);
                                if (!parents.contains(t)) {
                                    child = t;
                                }
                            }
                        }
                    }
                }
            }
        }
        return child;
    }

    public void addTypesToTops (Iterator<String> keys, ArrayList<String> tops) {
        while (keys.hasNext()) {
            String key = keys.next();
            if (!subToSuper.containsKey(key)) {
                if (!tops.contains(key)) tops.add(key);
            }
        }
    }

    public void addToTaxonymy (HashMap<String, ArrayList<String>> subToSuper)  {
        Set keySet = subToSuper.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String subClass = keys.next();
            ArrayList<String> supers = subToSuper.get(subClass);
            for (int i = 0; i < supers.size(); i++) {
                String superClass = supers.get(i);
                if (!subClass.equals(superClass)) {
                    this.subToSuper.put(subClass, superClass);
                    if (this.superToSub.containsKey(superClass)) {
                        ArrayList<String> subs = this.superToSub.get(superClass);
                        if (!subs.contains(subClass)) {
                            subs.add(subClass);
                            this.superToSub.put(superClass, subs);
                        }
                    } else {
                        ArrayList<String> subs = new ArrayList<String>();
                        subs.add(subClass);
                        this.superToSub.put(superClass, subs);
                    }
                }
            }
        }
    }

    public void printTree (OutputStream stream) throws IOException {
        ArrayList<String> tops = this.getTops();
        ArrayList<String> covered = new ArrayList<String>();
        printTree(stream, tops, 0, covered);
    }

    public void printTree (OutputStream stream, ArrayList<String> tops, int level, ArrayList<String> covered) throws IOException {
        level++;
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            if (!covered.contains(top)) {
                covered.add(top);
                String str = "";
                for (int j = 0; j < level; j++) {
                    str += "  ";

                }
                if (superToSub.containsKey(top)) {
                    ArrayList<String> children = superToSub.get(top);
                    str += top + ":" + children.size() + "\n";
                    stream.write(str.getBytes());
                    printTree(stream, children, level, covered);
                } else {
                    str += top + "\n";
                    stream.write(str.getBytes());
                }
            }
        }
    }

    public void printTree (ArrayList<String> tops, int level, HashMap<String, Integer> eventCounts) {
        level++;
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            Integer cnt = 0;
            if (eventCounts.containsKey(top)) {
                cnt = eventCounts.get(top);
            }
            String str = "";
            for (int j = 0; j < level; j++) {
                str += "  ";

            }
            if (superToSub.containsKey(top)) {
                ArrayList<String> children = superToSub.get(top);
                str += top + ":" + cnt;
                System.out.println(str);
                printTree(children, level, eventCounts);
            }
            else {
                str += top;
                System.out.println(str);
            }
        }
    }

    public void  jsonTree (JSONObject tree, String gType, String iType, String ns, ArrayList<String> tops,
                           int mCount,
                                 int level,
                                 HashMap<String, Integer> typeCounts,
                                 HashMap<String, ArrayList<PhraseCount>> phrases,
                                 HashMap<String, TypedPhraseCount> typedPredicates) throws IOException, JSONException {
        ArrayList<String> covered = new ArrayList<String>();
        jsonTree ( tree,  gType,  iType, ns,  tops, covered, mCount,
         level,
         typeCounts,
         phrases,
         typedPredicates);
    }

    String cleanName (String name) {
        String clName = name.replaceAll("unknown", "");
        clName = clName.replaceAll("%2C", ":");
        clName = clName.replaceAll("%2F", "/");
        clName = clName.replaceAll("%27", "'");
        clName = clName.replace("+", "_");
       // System.out.println("clName = " + clName);
        return clName;
    }

    public void  jsonTree (JSONObject tree, String gType, String iType, String ns, ArrayList<String> tops,
                           ArrayList<String> covered,
                           int mCount,
                           int level,
                           HashMap<String, Integer> typeCounts,
                           HashMap<String, ArrayList<PhraseCount>> phrases,
                           HashMap<String, TypedPhraseCount> typedPredicates) throws IOException, JSONException {



        level++;
        ArrayList<PhraseCount> countedTops = new ArrayList<PhraseCount>();
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            if (!covered.contains(top)) {
                covered.add(top);
                Integer cnt = 0;
                if (typeCounts.containsKey(top)) {
                    cnt = typeCounts.get(top);
                }
                PhraseCount phraseCount = new PhraseCount(top, cnt);
                countedTops.add(phraseCount);
            }
        }
        Collections.sort(countedTops, new Comparator<PhraseCount>() {
            @Override
            public int compare(PhraseCount p1, PhraseCount p2) {
                return p2.getCount().compareTo(p1.getCount());
            }
        });
        for (int i = 0; i < countedTops.size(); i++) {
            PhraseCount topCount = countedTops.get(i);
            if (topCount.getPhrase().startsWith(ns) || ns.isEmpty()) {
                ArrayList<String> children = new ArrayList<String>();
                //// This check is needed since children can belong to multiple types (count several times) and some hiearchies have cycles
                if (superToSub.containsKey(topCount.getPhrase())) {
                     ArrayList<String> oChildren = superToSub.get(topCount.getPhrase());
                    for (int j = 0; j < oChildren.size(); j++) {
                        String oChild = oChildren.get(j);
                        if (!covered.contains(oChild) && !children.contains(oChild)) {
                            children.add(oChild);
                        }
                    }
                }
                int instances = 0;
                if (phrases.containsKey(topCount.getPhrase())) {
                    ArrayList<PhraseCount> phraseCounts = phrases.get(topCount.getPhrase());
                    instances = phraseCounts.size();
                }
                if (topCount.getCount()>0 && (instances>0 || children.size()>0)) {
                    String ref = topCount.getPhrase();
                    String type = getType(gType, topCount.getPhrase());

                    String name = topCount.getPhrase();
                    int idx = topCount.getPhrase().lastIndexOf("/");
                    //if (idx==-1) idx = topCount.getPhrase().indexOf(":");  /// this is to remove ns but it is actually useful to keep these
                    if (idx > -1) {
                        name = topCount.getPhrase().substring(idx + 1);
                    }

                    JSONObject node = new JSONObject();
                   // node.put("level", new Integer(level).toString());
                    if (!name.isEmpty()) node.put("name", cleanName(name));
                    node.put("query", topCount.getPhrase());
                    if (!type.isEmpty()) node.put("type", type);
                    if (!ref.isEmpty()) node.put("url", ref);
                    node.put("child_count", children.size());
                    node.put("instance_count", instances);
                    node.put("mention_count", topCount.getCount());
                    if (phrases.containsKey(topCount.getPhrase())) {

                        ArrayList<PhraseCount> phraseCounts = phrases.get(topCount.getPhrase());
                        Collections.sort(phraseCounts, new Comparator<PhraseCount>() {
                            @Override
                            public int compare(PhraseCount p1, PhraseCount p2) {
                                return p2.getCount().compareTo(p1.getCount());
                            }
                        });
                        for (int j = 0; j < phraseCounts.size(); j++) {
                            PhraseCount phraseCount = phraseCounts.get(j);
                            if (phraseCount.getCount()>=mCount) {
                                JSONObject phraseCountJsonObject = new JSONObject();
                                name = phraseCount.getPhrase().trim();
                                idx = phraseCount.getPhrase().lastIndexOf("/");
                                if (idx > -1) {
                                    name = phraseCount.getPhrase().substring(idx + 1);
                                }
                                if (!name.isEmpty()) name =  cleanName(name);

                                phraseCountJsonObject.put("name", name);
                                //phraseCountJsonObject.put("name", name.replace("+","_"));
                                phraseCountJsonObject.put("query", phraseCount.getPhrase());
                                if (phraseCount.getPhrase().indexOf("http")> -1) {
                                    ///what about eso and eurovoc
                                    phraseCountJsonObject.put("url", phraseCount.getPhrase());
                                    phraseCountJsonObject.put("type", iType+"Instance");
                                }
                                else {
                                    phraseCountJsonObject.put("type", iType+"Phrase");
                                }
                                phraseCountJsonObject.put("mention_count", phraseCount.getCount());
                                //@TODO provide an additional structure for ILI mappings to support cross-lingual search
                                /*if (iliMap!=null) {
                                    if (iliMap.containsKey(name)) {
                                        ArrayList<String> ilis = iliMap.get(name);
                                        for (int k = 0; k < ilis.size(); k++) {
                                            String ili = ilis.get(k);
                                            phraseCountJsonObject.append("ili", ili);
                                        }
                                    } else {
                                        //System.out.println("could not find iliString = " + iliString);
                                    }
                                }*/
                                if (!ref.isEmpty()) phraseCountJsonObject.put("parent", ref);

                                if (typedPredicates!=null && typedPredicates.containsKey(phraseCount.getPhrase())) {
                                    TypedPhraseCount typedPhraseCount = typedPredicates.get(phraseCount.getPhrase());
                                    for (int k = 0; k < typedPhraseCount.getLabels().size(); k++) {
                                        String label = typedPhraseCount.getLabels().get(k);
                                        phraseCountJsonObject.append("labels", label);
                                    }

                                }
                                node.append("instances", phraseCountJsonObject);
                            }
                        }
                    };
                    if (children.size()>0) {
                        jsonTree(node, gType, iType, ns, children, covered, mCount, level, typeCounts, phrases, typedPredicates);
                    }
                    else {
                        //  System.out.println("has no children top = " + top);
                    }
                    tree.append("children", node);
                }
                else {
                    //// no use for this class
                   /* System.out.println("ignoring topCount = " + topCount.getPhraseCount());
                    System.out.println("instances. = " + instances);
                    System.out.println("children = "+children.size());*/
                }
            }
            else {
                   System.out.println("ns = " + ns);
                   System.out.println("top = " + topCount.getPhraseCount());
            }
        }
    }
    public void  jsonPerspectiveTree (JSONObject tree, String gType, String iType, String ns, ArrayList<String> tops,
                           ArrayList<String> covered,
                           int mCount,
                           int level,
                           HashMap<String, Integer> typeCounts,
                           HashMap<String, ArrayList<PhraseCount>> phrases,
                           HashMap<String, TypedPhraseCount> typedPredicates) throws IOException, JSONException {



        level++;
        ArrayList<PhraseCount> countedTops = new ArrayList<PhraseCount>();
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            if (!covered.contains(top)) {
                covered.add(top);
                Integer cnt = 0;
                if (typeCounts.containsKey(top)) {
                    cnt = typeCounts.get(top);
                }
                PhraseCount phraseCount = new PhraseCount(top, cnt);
                countedTops.add(phraseCount);
            }
        }
        Collections.sort(countedTops, new Comparator<PhraseCount>() {
            @Override
            public int compare(PhraseCount p1, PhraseCount p2) {
                return p2.getCount().compareTo(p1.getCount());
            }
        });
        for (int i = 0; i < countedTops.size(); i++) {
            PhraseCount topCount = countedTops.get(i);
            if (topCount.getPhrase().startsWith(ns) || ns.isEmpty()) {
                ArrayList<String> children = new ArrayList<String>();
                //// This check is needed since children can belong to multiple types (count several times) and some hiearchies have cycles
                if (superToSub.containsKey(topCount.getPhrase())) {
                     ArrayList<String> oChildren = superToSub.get(topCount.getPhrase());
                    for (int j = 0; j < oChildren.size(); j++) {
                        String oChild = oChildren.get(j);
                        if (!covered.contains(oChild) && !children.contains(oChild)) {
                            children.add(oChild);
                        }
                    }
                }
                int instances = 0;
                if (phrases.containsKey(topCount.getPhrase())) {
                    ArrayList<PhraseCount> phraseCounts = phrases.get(topCount.getPhrase());
                    instances = phraseCounts.size();
                }
                if (topCount.getCount()>0 && (instances>0 || children.size()>0)) {
                    String ref = topCount.getPhrase();
                    String type = getType(gType, topCount.getPhrase());

                    String name = topCount.getPhrase();
                    int idx = topCount.getPhrase().lastIndexOf("/");
                    //if (idx==-1) idx = topCount.getPhrase().indexOf(":");  /// this is to remove ns but it is actually useful to keep these
                    if (idx > -1) {
                        name = topCount.getPhrase().substring(idx + 1);
                    }

                    JSONObject node = new JSONObject();
                   // node.put("level", new Integer(level).toString());
                    if (!name.isEmpty()) node.put("name", cleanName(name));
                    node.put("query", topCount.getPhrase());
                    if (!type.isEmpty()) node.put("type", type);
                    if (!ref.isEmpty()) node.put("url", ref);
                    node.put("child_count", children.size());
                    node.put("instance_count", instances);
                    node.put("mention_count", topCount.getCount());

                    tree.append("children", node);
                }
                else {
                    //// no use for this class
                   /* System.out.println("ignoring topCount = " + topCount.getPhraseCount());
                    System.out.println("instances. = " + instances);
                    System.out.println("children = "+children.size());*/
                }
            }
            else {
                   System.out.println("ns = " + ns);
                   System.out.println("top = " + topCount.getPhraseCount());
            }
        }
    }

    public void  jsonTopicTree (JSONObject tree, String gType, String ns, ArrayList<String> tops,
                                 int level,
                                 HashMap<String, String> uriLabelMap,
                                 HashMap<String, Integer> typeCounts) throws IOException, JSONException {
        ArrayList<String> covered = new ArrayList<String>();
        jsonTopicTree ( tree,  gType,  ns,  tops, covered,
                level,
                uriLabelMap,
                typeCounts);
    }



    public void  jsonTopicTree (JSONObject tree, String gType, String ns, ArrayList<String> tops,
                                ArrayList<String> covered,
                                int level,
                                HashMap<String, String> uriLabelMap,
                                HashMap<String, Integer> typeCounts ) throws IOException, JSONException {



        level++;
        ArrayList<PhraseCount> countedTops = new ArrayList<PhraseCount>();
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            if (!covered.contains(top)) {
                covered.add(top);
                Integer cnt = 0;
                if (typeCounts.containsKey(top)) {
                    cnt = typeCounts.get(top);
                }
                PhraseCount phraseCount = new PhraseCount(top, cnt);
                countedTops.add(phraseCount);
            }
        }
        Collections.sort(countedTops, new Comparator<PhraseCount>() {
            @Override
            public int compare(PhraseCount p1, PhraseCount p2) {
                return p2.getCount().compareTo(p1.getCount());
            }
        });
        for (int i = 0; i < countedTops.size(); i++) {
            PhraseCount topCount = countedTops.get(i);
            if (topCount.getPhrase().startsWith(ns) || ns.isEmpty()) {
                ArrayList<String> children = new ArrayList<String>();
                //// This check is needed since children can belong to multiple types (count several times) and some hiearchies have cycles
                if (superToSub.containsKey(topCount.getPhrase())) {
                     ArrayList<String> oChildren = superToSub.get(topCount.getPhrase());
                    for (int j = 0; j < oChildren.size(); j++) {
                        String oChild = oChildren.get(j);
                        if (!covered.contains(oChild) && !children.contains(oChild)) {
                            children.add(oChild);
                        }
                    }
                }
                if (topCount.getCount()>0) {
                //if (topCount.getCount()>0 || children.size()>0) {
                    String ref = topCount.getPhrase();
                    String type = getType(gType, topCount.getPhrase());

                    String name = topCount.getPhrase();
                    int idx = topCount.getPhrase().lastIndexOf("/");
                    //if (idx==-1) idx = topCount.getPhrase().indexOf(":");  /// this is to remove ns but it is actually useful to keep these
                    if (idx > -1) {
                        name = topCount.getPhrase().substring(idx + 1);
                    }
                    if (uriLabelMap.containsKey(topCount.getPhrase())) {
                        name = uriLabelMap.get(topCount.getPhrase());
                    }
                    else {
                       // System.out.println("no name for topCount = " + topCount.getPhrase());
                    }
                    JSONObject node = new JSONObject();
                   // node.put("level", new Integer(level).toString());
                    if (!name.isEmpty()) node.put("name", name);
                    node.put("query", topCount.getPhrase());
                    if (!type.isEmpty()) node.put("type", type);
                    if (!ref.isEmpty()) node.put("url", ref);
                    node.put("child_count", children.size());
                    node.put("mention_count", topCount.getCount());

                    if (children.size()>0) {
                        jsonTopicTree(node, gType, ns, children, covered, level,
                                uriLabelMap,typeCounts);
                    }
                    else {
                        //  System.out.println("has no children top = " + top);
                    }
                    tree.append("children", node);
                }
                else {
                    //// no use for this class
                   /* System.out.println("ignoring topCount = " + topCount.getPhraseCount());
                    System.out.println("instances. = " + instances);
                    System.out.println("children = "+children.size());*/
                }
            }
            else {
                   System.out.println("ns = " + ns);
                   System.out.println("top = " + topCount.getPhraseCount());
            }
        }
    }

    public String getType (String gType, String reference) {
        String type = gType;

        if (reference.indexOf("dbpedia")>-1 && reference.indexOf("resource")>-1) {
            type =gType+"Instance";
        }
        else if (reference.startsWith("http://dbpedia.org/ontology") ||
                reference.startsWith("http://www.w3.org/2002/07/owl#Thing") ||
                reference.startsWith("https://www.w3.org/2002/07/owl#Thing")) {
            type = gType+"Type";
        }
        else if (reference.indexOf("ontology")>-1) {
            type = gType+"Type";
        }
        else if (reference.indexOf("ontologies")>-1) {
            type = gType+"Type";
        }
        else if (reference.indexOf("eso:")>-1) {
            type = gType+"Type";
        }
        else if (reference.indexOf("fn:")>-1) {
            type = gType+"Type";
        }
        else if (reference.indexOf("sem:")>-1) {
            type = gType+"Type";
        }
        else if (reference.indexOf("Event")>-1) {
            type = gType+"Type";
        }
        else if (reference.indexOf("eurovoc")>-1) {
            type = gType+"Type";
        }
        else {
          //  System.out.println("reference = " + reference);
        }
        return type;
    }

    public void cumulateScores (String ns, ArrayList<String> tops,
                                HashMap<String, Integer> eventCounts, EuroVoc euroVoc) {
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i).trim();
            if (top.startsWith(ns) || ns.isEmpty()) {
                if (superToSub.containsKey(top)) {
                    ArrayList<String> children = superToSub.get(top);
                    cumulateScores(ns, children, eventCounts, euroVoc);
                    int cCount = 0;
                    for (int j = 0; j < children.size(); j++) {
                        String child =  children.get(j);
                        if (conceptToLabels.containsKey(child)) {
                            ArrayList<String> labels = conceptToLabels.get(child);
                            for (int k = 0; k < labels.size(); k++) {
                                String label =  labels.get(k);
                                if (euroVoc.labelUriMap.containsKey(label)) {
                                    String id = euroVoc.labelUriMap.get(label);
                                    if (eventCounts.containsKey(id)) {
                                        cCount += eventCounts.get(id);
                                    }
                                }
                            }
                        }
                    }
                    if (conceptToLabels.containsKey(top)) {
                        ArrayList<String> labels = conceptToLabels.get(top);
                        for (int k = 0; k < labels.size(); k++) {
                            String label =  labels.get(k);
                            if (euroVoc.labelUriMap.containsKey(label)) {
                                String id = euroVoc.labelUriMap.get(label);
                                if (eventCounts.containsKey(id)) {
                                    cCount += eventCounts.get(id);
                                }
                            }
                        }
                    }
                    if (eventCounts.containsKey(top)) {
                        Integer cnt = eventCounts.get(top);
                        cnt+= cCount;
                        eventCounts.put(top, cnt);
                    }
                    else {
                        eventCounts.put(top, cCount);
                    }
                    System.out.println("top count = " + cCount);
                }
                else {
                    //  System.out.println("top = " + top);
                }
            }
            else {
                // System.out.println("ns = " + ns);
                // System.out.println("top = " + top);
            }
        }
    }

    public void cumulateScores (String ns, ArrayList<String> tops,
                                HashMap<String, Integer> eventCounts
                                ) {
        ArrayList<String> covered = new ArrayList<String>();
        cumulateScores(ns, tops, eventCounts, covered);
    }

    public void cumulateScores (String ns, ArrayList<String> tops,
                                HashMap<String, Integer> eventCounts,
                                ArrayList<String> covered) {
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i).trim();
            if (!covered.contains(top)) {
                covered.add(top);
                if (top.startsWith(ns) || ns.isEmpty()) {
                    if (superToSub.containsKey(top)) {
                        ArrayList<String> oChildren = superToSub.get(top);
                        ArrayList<String> children = new ArrayList<String>();
                        //// This check is needed since children can belong to multiple types (count several times) and some hierarchies have cycles
                        for (int j = 0; j < oChildren.size(); j++) {
                            String oChild = oChildren.get(j);
                            if (!covered.contains(oChild) && !children.contains(oChild)) {
                                children.add(oChild);
                            }
                        }
                        /// we first recurse over the children depth first to obtain their counts
                        /// initial counts are based on the direct mentions
                        cumulateScores(ns, children, eventCounts, covered);

                        /// we now cumulate the mention counts of the children to their parent
                        int cCount = 0;
                        for (int j = 0; j < children.size(); j++) {
                            String child = children.get(j);
                            if (eventCounts.containsKey(child)) {
                                cCount += eventCounts.get(child);
                               // System.out.println("child = " + child+" :"+cCount);
                            } else {
                                // System.out.println("no counts for child = " + child);
                            }
                        }
                        if (eventCounts.containsKey(top)) {
                            Integer cnt = eventCounts.get(top);
                            cnt += cCount;
                            eventCounts.put(top, cnt);
                        } else {
                            eventCounts.put(top, cCount);
                        }
                    } else {
                        //  System.out.println("top = " + top);
                    }
                } else {
                    // System.out.println("ns = " + ns);
                    // System.out.println("top = " + top);
                }
            }
        }
    }


    public void cumulateScores (String ns, ArrayList<String> tops,
                                HashMap<String, Integer> eventCounts,
                                HashMap<String, TypedPhraseCount> labelCounts
    ) {
        ArrayList<String> covered = new ArrayList<String>();
        cumulateScores(ns, tops, eventCounts, labelCounts, covered);
    }

    public void cumulateScores (String ns, ArrayList<String> tops,
                                HashMap<String, Integer> eventCounts,
                                HashMap<String, TypedPhraseCount> labelCounts,
                                ArrayList<String> covered) {
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i).trim();
            if (!covered.contains(top)) {
                covered.add(top);
                if (top.startsWith(ns) || ns.isEmpty()) {
                    if (superToSub.containsKey(top)) {
                        ArrayList<String> children = superToSub.get(top);
                        cumulateScores(ns, children, eventCounts,labelCounts, covered);
                        int cCount = 0;
                        for (int j = 0; j < children.size(); j++) {
                            String child = children.get(j);
                            if (labelCounts.containsKey(child)) {
                                cCount += labelCounts.get(child).getCount();
                            } else {
                                // System.out.println("no counts for child = " + child);
                            }
                        }
                        if (labelCounts.containsKey(top)) {
                            Integer cnt = labelCounts.get(top).getCount();
                            cnt += cCount;
                            eventCounts.put(top, cnt);
                        } else {
                            eventCounts.put(top, cCount);
                        }
                    } else {
                        //  System.out.println("top = " + top);
                    }
                } else {
                    // System.out.println("ns = " + ns);
                    // System.out.println("top = " + top);
                }
            }
        }
    }

    public int getMaxDepth (ArrayList<String> tops, int level) {
        int maxDepth = 0;
        level++;
        maxDepth = level;
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            if (superToSub.containsKey(top)) {
                ArrayList<String> children = superToSub.get(top);
                int depth = getMaxDepth(children, level);
                if (depth>maxDepth) {
                    maxDepth = depth;
                }
            }
        }
        return maxDepth;
    }

    public void topEvents (Iterator<String> keys) {
        ArrayList<String> tops = getTops();
        addTypesToTops(keys, tops);
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            if (top.indexOf("http://globalwordnet.org/ili/")>-1 || top.indexOf("ili:")>-1) {
                subToSuper.put(top, "ili");
                if (superToSub.containsKey("ili")) {
                    ArrayList<String> ilis = superToSub.get("ili");
                    ilis.add(top);
                    superToSub.put("ili", ilis);
                }
                else {
                    ArrayList<String> ilis = new ArrayList<String>();
                    ilis.add(top);
                    superToSub.put("ili", ilis);
                }
            }
            else if (top.indexOf("eso:")>-1) {
                subToSuper.put(top, "eso");
                if (superToSub.containsKey("eso")) {
                    ArrayList<String> ilis = superToSub.get("eso");
                    ilis.add(top);
                    superToSub.put("eso", ilis);
                }
                else {
                    ArrayList<String> ilis = new ArrayList<String>();
                    ilis.add(top);
                    superToSub.put("eso", ilis);
                }

            }
            else if (top.indexOf("fn:")>-1) {
                subToSuper.put(top, "framenet");
                if (superToSub.containsKey("framenet")) {
                    ArrayList<String> ilis = superToSub.get("framenet");
                    ilis.add(top);
                    superToSub.put("framenet", ilis);
                }
                else {
                    ArrayList<String> ilis = new ArrayList<String>();
                    ilis.add(top);
                    superToSub.put("framenet", ilis);
                }

            }
            else {
                subToSuper.put(top, "other");
                if (superToSub.containsKey("other")) {
                    ArrayList<String> ilis = superToSub.get("other");
                    ilis.add(top);
                    superToSub.put("other", ilis);
                }
                else {
                    ArrayList<String> ilis = new ArrayList<String>();
                    ilis.add(top);
                    superToSub.put("other", ilis);
                }

            }
        }
    }
}
