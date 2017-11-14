package vu.cltl.storyteller.trig;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import vu.cltl.storyteller.objects.PhraseCount;
import vu.cltl.storyteller.objects.TrigTripleData;
import vu.cltl.storyteller.util.EventTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static vu.cltl.storyteller.util.Util.makeRecursiveFileList;

/**
 * Created by piek on 23/06/15.
 */
@Deprecated
public class TrigUtil {

    static final public String provenanceGraph = "http://www.newsreader-project.eu/provenance";
    static final public String instanceGraph = "http://www.newsreader-project.eu/instances";


    /** KS util
     * Obtains type statistics from KG
     * @param statementMap
     */
    public static void getTypeStatistics(HashMap<String, ArrayList<Statement>> statementMap) {
        Set keySet = statementMap.keySet();
        HashMap<String, PhraseCount> typeCountMap = new HashMap<String, PhraseCount>();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String tripleKey = keys.next();
            ArrayList<Statement> statements = statementMap.get(tripleKey);
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                if (statement.getPredicate().getLocalName().equals("type")) {
                    String type = getPrettyNSValue(statement.getObject().toString());
                    if (typeCountMap.containsKey(type)) {
                        PhraseCount phraseCount = typeCountMap.get(type);
                        phraseCount.incrementCount();
                        typeCountMap.put(type, phraseCount);
                    } else {
                        PhraseCount phraseCount = new PhraseCount(type, 1);
                        typeCountMap.put(type, phraseCount);
                    }
                }
            }
        }
        SortedSet<PhraseCount> treeSet = new TreeSet<PhraseCount>(new PhraseCount.Compare());
        Set keySetP = typeCountMap.keySet();
        Iterator<String> keysP = keySetP.iterator();
        while(keysP.hasNext()) {
            String label = keysP.next();
            PhraseCount phraseCount = typeCountMap.get(label);
            if (phraseCount != null) {
                treeSet.add(phraseCount);
            }
        }
        for( PhraseCount pcount :treeSet) {
            PhraseCount phraseCount = typeCountMap.get(pcount.getPhrase());
            System.out.println(phraseCount.getPhraseCount());
        }
    }


    /**
     * KS util
     * @param trigTripleData
     * @return
     */
    public static HashMap<String, ArrayList<Statement>> getPrimaryKnowledgeGraphHashMap (ArrayList<String> subjectUriArrayList, TrigTripleData trigTripleData) {
        HashMap<String, ArrayList<Statement>>  eckgMap = new HashMap<String, ArrayList<Statement>>();
        for (int k = 0; k < subjectUriArrayList.size(); k++) {
            String tripleKey =  subjectUriArrayList.get(k);
            ArrayList<Statement> statements = trigTripleData.tripleMapInstances.get(tripleKey);
            ArrayList<String> objectKeys = new ArrayList<String>();
            if (trigTripleData.tripleMapOthers.containsKey(tripleKey)) {
                ArrayList<Statement> semStatements = trigTripleData.tripleMapOthers.get(tripleKey);
                addNewStatements(statements, semStatements);
            }
            eckgMap.put(tripleKey, statements);
        }
        return eckgMap;
    }

    static public void  addNewStatements(ArrayList<Statement> statements1, ArrayList<Statement> statements2) {
        ArrayList<Statement> statementsMerge = statements1;
        for (int i = 0; i < statements2.size(); i++) {
            Statement statement2 = statements2.get(i);
            addNewStatement(statements1, statement2);
        }
    }

    static public void addNewStatement(ArrayList<Statement> statements1, Statement statement2) {
        boolean match = false;
        for (int j = 0; j < statements1.size(); j++) {
            Statement statement1 = statements1.get(j);
            if (statement1.toString().equals(statement2.toString())) {
                match = true;
                break;
            }
        }
        if (!match) statements1.add(statement2);
    }

    /**
     * KS util
     * @param trigTripleData
     * @return
     */
    public static HashMap<String, ArrayList<Statement>> getExtendedKnowledgeGraphHashMap (ArrayList<String> subjectUriArrayList, TrigTripleData trigTripleData) {
        HashMap<String, ArrayList<Statement>>  eckgMap = new HashMap<String, ArrayList<Statement>>();
        for (int k = 0; k < subjectUriArrayList.size(); k++) {
            String tripleKey =  subjectUriArrayList.get(k);
            ArrayList<Statement> statements = trigTripleData.tripleMapInstances.get(tripleKey);
            ArrayList<String> objectKeys = new ArrayList<String>();
            if (trigTripleData.tripleMapOthers.containsKey(tripleKey)) {
                ArrayList<Statement> semStatements = trigTripleData.tripleMapOthers.get(tripleKey);
                addNewStatements(statements, semStatements);
                for (int j = 0; j < semStatements.size(); j++) {
                    Statement semStatement = semStatements.get(j);
                    //String objectUri = semStatement.getObject().asLiteral().getString();
                    String objectUri = getObjectUriValueAsString(semStatement);
                    //System.out.println("objectUri = " + objectUri);
                    if (!objectKeys.contains(objectUri)) objectKeys.add(objectUri);
                }
            }
            /// Next we get all the properties of the objects

            for (int j = 0; j < objectKeys.size(); j++) {
                String s = objectKeys.get(j);
                if (trigTripleData.tripleMapInstances.containsKey(s))  {
                    ArrayList<Statement> objStatements = trigTripleData.tripleMapInstances.get(s);
                    addNewStatements(statements, objStatements);
                }

            }
            eckgMap.put(tripleKey, statements);
        }
        return eckgMap;
    }
    /**
     * KS util
     * @param trigTripleData
     * @return
     */
    public static HashMap<String, ArrayList<Statement>> getSecondaryKnowledgeGraphHashMap (ArrayList<String> subjectUriArrayList, TrigTripleData trigTripleData) {
        HashMap<String, ArrayList<Statement>>  eckgMap = new HashMap<String, ArrayList<Statement>>();
        for (int k = 0; k < subjectUriArrayList.size(); k++) {
            String tripleKey =  subjectUriArrayList.get(k);
            ArrayList<Statement> statements = new ArrayList<Statement>();
            ArrayList<String> objectKeys = new ArrayList<String>();
            if (trigTripleData.tripleMapOthers.containsKey(tripleKey)) {
                ArrayList<Statement> semStatements = trigTripleData.tripleMapOthers.get(tripleKey);
                for (int j = 0; j < semStatements.size(); j++) {
                    Statement semStatement = semStatements.get(j);
                    //String objectUri = semStatement.getObject().asLiteral().getString();
                    String objectUri = getObjectUriValueAsString(semStatement);
                    //System.out.println("objectUri = " + objectUri);
                    if (!objectKeys.contains(objectUri)) objectKeys.add(objectUri);
                }
            }
            /// Next we get all the properties of the objects
            for (int j = 0; j < objectKeys.size(); j++) {
                String s = objectKeys.get(j);
                if (trigTripleData.tripleMapInstances.containsKey(s))  {
                    ArrayList<Statement> objStatements = trigTripleData.tripleMapInstances.get(s);
                    statements.addAll(objStatements);
                }

            }
            eckgMap.put(tripleKey, statements);
        }
        return eckgMap;
    }

    static public class ComparePredicate implements Comparator {
        public int compare (Object aa, Object bb) {
            Statement a = (Statement) aa;
            Statement b = (Statement) bb;
            return a.getPredicate().getLocalName().compareTo(b.getPredicate().getLocalName());
        }
    }

    static public class CompareStatement implements Comparator {
        public int compare (Object aa, Object bb) {
            Statement a = (Statement) aa;
            Statement b = (Statement) bb;
            return a.toString().compareTo(b.toString());
        }
    }

    /** KS util
     * prints KG
     * @param statementMap
     */
    public static void printKnowledgeGraph(OutputStream fos,
                                           HashMap<String, ArrayList<Statement>> statementMap,
                                           HashMap<String, ArrayList<Statement>> secondaryStatementMap
    ) throws IOException {
        Set keySet = statementMap.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String tripleKey = keys.next();
            String str = tripleKey+"\n";
            /// we first print the primary triples
            ArrayList<Statement> statements = statementMap.get(tripleKey);
            SortedSet<Statement> treeSet = new TreeSet<Statement>(new CompareStatement());
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                treeSet.add(statement);
            }
            for (Statement statement : treeSet) {
                //str += "\t"+statement.getString()+"\n";
                str +=  "\t" + getPrettyNSValue(statement.getPredicate().toString());
                if (!isGafTriple(statement)) {
                  str+=  "\t" + getPrettyNSValue(statement.getObject().toString()) + "\n";
                }
                else {
                    str+=  "\t" + getPrettyNSValueFile(statement.getObject().toString()) + "\n";
                }
            }
             /// now print the secondary triples
            treeSet = new TreeSet<Statement>(new CompareStatement());
            statements = secondaryStatementMap.get(tripleKey);
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                treeSet.add(statement);
            }
            for (Statement statement : treeSet) {
                //str += "\t\t"+statement.getString()+"\n";
                str += "\t\t" + getPrettyNSValue(statement.getSubject().toString())+
                        "\t"+ getPrettyNSValue(statement.getPredicate().toString());
                if (!isGafTriple(statement)) {
                    str+=  "\t" + getPrettyNSValue(statement.getObject().toString()) + "\n";
                }
                else {
                    str+=  "\t" + getPrettyNSValueFile(statement.getObject().toString()) + "\n";
                }
            }
            str +="\n";
            fos.write(str.getBytes());
        }
    }

    /** KS util
     * prints KG
     * @param statementMap
     */
    public static void printKnowledgeGraph(OutputStream fos,
                                           HashMap<String, ArrayList<Statement>> statementMap

    ) throws IOException {
        Set keySet = statementMap.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String tripleKey = keys.next();
            String str = tripleKey+"\n";
            /// we first print the primary triples
            ArrayList<Statement> statements = statementMap.get(tripleKey);
            SortedSet<Statement> treeSet = new TreeSet<Statement>(new CompareStatement());
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                treeSet.add(statement);
            }
            for (Statement statement : treeSet) {
                //str += "\t"+statement.getString()+"\n";
                str +=  "\t" + getPrettyNSValue(statement.getPredicate().toString());
                if (!isGafTriple(statement)) {
                  str+=  "\t" + getPrettyNSValue(statement.getObject().toString()) + "\n";
                }
                else {
                    str+=  "\t" + getPrettyNSValueFile(statement.getObject().toString()) + "\n";
                }
            }
            str +="\n";
            fos.write(str.getBytes());
        }
    }

    /** KS util
     * prints KG
     * @param statementMap
     */
    public static void printKnowledgeGraph(OutputStream fos,
                                           ArrayList<String> eventKeys,
                                           HashMap<String, ArrayList<Statement>> statementMap,
                                           HashMap<String, ArrayList<Statement>> secondaryStatementMap
    ) throws IOException {
        for (int k = 0; k < eventKeys.size(); k++) {
            String tripleKey =  eventKeys.get(k);
            String str = tripleKey+"\n";
            /// we first print the primary triples
            ArrayList<Statement> statements = statementMap.get(tripleKey);
            SortedSet<Statement> treeSet = new TreeSet<Statement>(new CompareStatement());
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                treeSet.add(statement);
            }
            for (Statement statement : treeSet) {
                //str += "\t"+statement.getString()+"\n";
                str +=  "\t" + getPrettyNSValue(statement.getPredicate().toString());
                if (!isGafTriple(statement)) {
                    str+=  "\t" + getPrettyNSValue(statement.getObject().toString()) + "\n";
                }
                else {
                    str+=  "\t" + getPrettyNSValueFile(statement.getObject().toString()) + "\n";
                }            }
             /// now print the secondary triples
            treeSet = new TreeSet<Statement>(new CompareStatement());
            statements = secondaryStatementMap.get(tripleKey);
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                treeSet.add(statement);
            }
            for (Statement statement : treeSet) {
                //str += "\t\t"+statement.getString()+"\n";
                str += "\t\t" + getPrettyNSValue(statement.getSubject().toString())+
                        "\t"+ getPrettyNSValue(statement.getPredicate().toString()) ;
                if (!isGafTriple(statement)) {
                    str+=  "\t" + getPrettyNSValue(statement.getObject().toString()) + "\n";
                }
                else {
                    str+=  "\t" + getPrettyNSValueFile(statement.getObject().toString()) + "\n";
                }            }
            str +="\n";
            fos.write(str.getBytes());
        }
    }



    /**
     * KS util
     * @param triples
     * @param statement
     * @return
     */
    public static ArrayList<Statement> getObjectStatement (HashMap<String, ArrayList<Statement>> triples, Statement statement) {
        ArrayList<Statement> statements = new ArrayList<Statement>();
        return statements;
    }



    static public ArrayList<String> getAllEntityEvents (Dataset dataset, String entity) {
        ArrayList<String> events = new ArrayList<String>();
        Iterator<String> it = dataset.listNames();
        while (it.hasNext()) {
            String name = it.next();
            if (!name.equals(instanceGraph) && (!name.equals(provenanceGraph))) {
                Model namedModel = dataset.getNamedModel(name);
                StmtIterator siter = namedModel.listStatements();
                while (siter.hasNext()) {
                    Statement s = siter.nextStatement();
                    String object = getPrettyNSValue(s.getObject().toString()).toLowerCase();
                    if (object.indexOf(entity.toLowerCase()) > -1) {
                        String subject = s.getSubject().getURI();
                        if (!events.contains(subject)) {
                            events.add(subject);
                        }
                    }
                }
            }
        }
        return events;
    }

    static public void getAllEntityEventTriples (Dataset dataset,
                                          ArrayList<String> events,
                                          HashMap<String, ArrayList<Statement>> eventMap) {
        HashMap<String, ArrayList<Statement>> triples = new HashMap<String, ArrayList<Statement>>();
        Iterator<String> it = dataset.listNames();
        while (it.hasNext()) {
            String name = it.next();
            if (!name.equals(instanceGraph) && (!name.equals(provenanceGraph))) {
                Model namedModel = dataset.getNamedModel(name);
                StmtIterator siter = namedModel.listStatements();
                while (siter.hasNext()) {
                    Statement s = siter.nextStatement();
                    if (validTriple (s)) {
                        String subject = s.getSubject().getURI();
                        if (events.contains(subject)) {
                            if (triples.containsKey(subject)) {
                                ArrayList<Statement> statements = triples.get(subject);
                                if (!hasStatement(statements, s)) {
                                    statements.add(s);
                                    eventMap.put(subject, statements);
                                }
                            } else {
                                ArrayList<Statement> statements = new ArrayList<Statement>();
                                statements.add(s);
                                eventMap.put(subject, statements);
                            }
                        }
                    }
                }
            }
        }
    }


    static public boolean isEventInstance (Statement statement) {
        String predicate = statement.getPredicate().getURI();
        if (predicate.endsWith("#type")) {
            String object = "";
            if (statement.getObject().isLiteral()) {
                object = statement.getObject().asLiteral().toString();
            } else if (statement.getObject().isURIResource()) {
                object = statement.getObject().asResource().getURI();
            }
            String[] values = object.split(",");
            for (int j = 0; j < values.length; j++) {
                String value = values[j];
                String property = getNameSpaceString(value);
                //  System.out.println("value = " + value);
                //    System.out.println("property = " + property);
                if (value.endsWith("Event") && property.equalsIgnoreCase("sem")) {
                    return true;
                }
            }
        }
        return false;
    }

    static public boolean isEventTripe (Statement statement) {
        String subject = statement.getSubject().toString();
        int idx = subject.lastIndexOf("/");
        if (idx>-1) {
            subject = subject.substring(idx);
        }
        if (subject.toLowerCase().startsWith("ev#")) {
            return true;
        }
        return false;
    }

    static public int mentionCounts (Statement statement) {
        int cnt = 0;
        String predicate = statement.getPredicate().getURI();
        if (predicate.endsWith("#denotedBy")) {
            String object = "";
            if (statement.getObject().isLiteral()) {
                object = statement.getObject().asLiteral().toString();
            } else if (statement.getObject().isURIResource()) {
                object = statement.getObject().asResource().getURI();
            }
            String[] values = object.split(",");
            cnt = values.length;

        }
        return cnt;
    }



    static public String getValue (String predicate) {
        int idx = predicate.lastIndexOf("#");
        if (idx>-1) {
            return predicate.substring(idx + 1);
        }
        else {
            idx = predicate.lastIndexOf("/");
            if (idx>-1) {
                return predicate.substring(idx + 1);
            }
            else {
                return predicate;
            }
        }
    }

    static public String getValueFile (String predicate) {
            int idx = predicate.lastIndexOf("/");
            if (idx>-1) {
                return predicate.substring(idx + 1);
            }
            else {
                return predicate;
            }
    }


    static public boolean hasStatement (ArrayList<Statement> statements, Statement s) {
        for (int i = 0; i < statements.size(); i++) {
            Statement statement = statements.get(i);
            if (statement.getSubject().equals(s.getSubject()) &&
                    statement.getPredicate().equals(s.getPredicate()) &&
                    statement.getObject().equals(s.getObject())) {
                return true;
            }
        }

        return false;
    }

    static public boolean validTriple (Statement s) {
        if (s.getPredicate().toString().toLowerCase().contains("propbank")) {
            return true;
        }/*
        else if (s.getPredicate().toString().toLowerCase().contains("hastime")) {
            return true;
        }*/
        else if (s.getPredicate().toString().toLowerCase().contains("#label")) {
            return true;
        }
        else if (s.getPredicate().toString().toLowerCase().contains("#denotedby")) {
            return true;
        }/*
        else if (s.getPredicate().toString().toLowerCase().contains("#type") &&
                 s.getObject().toString().toLowerCase().contains("framenet")) {
            return true;
        }*/
        /*else if (s.getPredicate().toString().toLowerCase().contains("#type") &&
                 s.getObject().toString().toLowerCase().contains("domain-ontology")) {
            return true;
        }*/
        else {
          //  System.out.println("s.getPredicate() = " + s.getPredicate());

            return false;
        }
    }

    static public boolean validLabelTriple (Statement s) {
       if (s.getPredicate().toString().toLowerCase().contains("#label")) {
            return true;
        }
        else if (s.getPredicate().toString().toLowerCase().contains("#denotedby")) {
            return true;
        }
        else {
          //  System.out.println("s.getPredicate() = " + s.getPredicate());

            return false;
        }
    }

    static public boolean validRoleTriple (Statement s) {
        if (s.getPredicate().toString().toLowerCase().contains("propbank")) {
            return true;
        }
        else {
          //  System.out.println("s.getPredicate() = " + s.getPredicate());

            return false;
        }
    }



    static public boolean isGafTriple(Statement s) {
        if (s.getPredicate().toString().toLowerCase().contains("#denotedby")) {
            return true;
        }
        else {
            return false;
        }
    }

    static public String getPrettyNSValue (String element) {
        String object = "";
        String value = getValue(element);
        String nameSpace =  getNameSpaceString(element);
        if (nameSpace.isEmpty()) {
            object = value;
        }
        else {
            object = nameSpace+":" + value;
        }
        return object;
    }
    static public String getPrettyNSValueFile (String element) {
        String object = "";
        String value = getValueFile(element);
        String nameSpace =  getNameSpaceString(element);
        if (nameSpace.isEmpty()) {
            object = value;
        }
        else {
            object = nameSpace+":" + value;
        }
        return object;
    }




    public static String getObjectUriValueAsString(Statement statement) {
        String var2 = "";
        if(statement.getObject().isLiteral()) {
            var2 = statement.getObject().asLiteral().toString();
        } else if(statement.getObject().isURIResource()) {
            var2 = statement.getObject().asResource().getLocalName();
        }

        var2 = statement.getObject().toString();
        return var2;
    }


    static public String triplesToString (ArrayList<Statement> statements) {
        String str = "";
        String eventLabels = "";
        String roles = "";
        String gaf = "";
        for (int i = 0; i < statements.size(); i++) {
            Statement statement = statements.get(i);
            if (statement.getPredicate().toString().toLowerCase().contains("#label")) {
                if (!eventLabels.isEmpty()) {
                    eventLabels += ",";
                }
                eventLabels += getPrettyNSValue(statement.getObject().toString());
            } else {
                if (isGafTriple(statement)) {
                    if (!gaf.isEmpty()) {
                        gaf+= "; ";
                    }
                    gaf += getMention(statement.getObject().toString());
                }
                else {
                    roles += "\t" + getValue(statement.getPredicate().toString())
                            + ":" + getPrettyNSValue(statement.getObject().toString());
                }
            }
        }
        if (!roles.isEmpty()) {
            str = eventLabels + roles + "\t" + gaf + "\n";
        }
        return str;
    }

    static public String triplesToString (ArrayList<Statement> statements, String entity) {
        String str = "";
        String eventLabels = "";
        String roles = "";
        String gaf = "";
        boolean hasAnotherEntity = false;
        boolean hasEntity = false;
        for (int i = 0; i < statements.size(); i++) {
            Statement statement = statements.get(i);
            if (statement.getPredicate().toString().toLowerCase().contains("#label")) {
                if (!eventLabels.isEmpty()) {
                    eventLabels += ",";
                }
                eventLabels += getPrettyNSValue(statement.getObject().toString());
            }
            else {
                if (isGafTriple(statement)) {
                    if (!gaf.isEmpty()) {
                        gaf+= "; ";
                    }
                    gaf += getMention(statement.getObject().toString());
                }
                else {
                    String objectValue = getPrettyNSValue(statement.getObject().toString());
                    if (objectValue.toLowerCase().contains(entity.toLowerCase())) {
                        hasEntity = true;
                      //  System.out.println("entity = "+objectValue);
                    }
                    else {
                        hasAnotherEntity = true;
                     //   System.out.println("other entity = "+objectValue);
                    }
                    roles += "\t" + getValue(statement.getPredicate().toString())
                            + ":" + objectValue;
                }
            }
        }

        if (hasAnotherEntity && hasEntity) {
            str = eventLabels + roles + "\t" + gaf + "\n";
        }
        else {
          //  System.out.println("not valid = " + roles);
        }
        return str;
    }




    static public String getNameSpaceString (String value) {
        String property = "";
        if (value.indexOf("/framenet/") > -1) {
            property = "fn";
        }
        else if (value.indexOf("/propbank/") > -1) {
            property = "pb";
        }
        else if (value.indexOf("/sem/") > -1) {
            property = "sem";
        }
        else if (value.indexOf("/cornetto") > -1) {
            property = "cornetto";
        }
        else if (value.indexOf("/sumo/") > -1) {
            property = "sumo";
        }
        else if (value.indexOf("/eso/") > -1) {
            property = "eso";
        }
        else if (value.indexOf("/domain-ontology") > -1) {
            property = "eso";
        }
        else if (value.indexOf("/dbpedia") > -1) {
            property = "dbp";
        }
        else if (value.indexOf("http://www.newsreader-project.eu/data/") > -1) {
            property = "nwr";
        }
        else if (value.indexOf("/non-entities") > -1) {
            property = "nwr-non-entity";
        }
        else if (value.indexOf("/entities/") > -1) {
            property = "nwr-entity";
        }
        else if (value.indexOf("ili-30") > -1) {
            property = "wn";
        }
        return property;
    }

    static public String getMention(String mention) {
        String mentionValue = getNameSpaceString(mention);
        String value = mention;
        int idx = mention.lastIndexOf("/");
        if (idx>-1) {
            value = mention.substring(idx + 1);
        }
        mentionValue += ":"+value;
        return mentionValue;
    }

    static public void removeObjectFiles(File inputFile) {
        File[] theFileList = null;
        if ((inputFile.canRead())) {
            theFileList = inputFile.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                File newFile = theFileList[i];
                if (newFile.isDirectory()) {
                     removeObjectFiles(newFile);
                } else {
                    if (newFile.getName().endsWith(".obj")) {
                        newFile.delete();
                    }
                }
            }
        } else {
            System.out.println("Cannot access file:" + inputFile + "#");
            if (!inputFile.exists()) {
                System.out.println("File/folder does not exist!");
            }
        }
    }

    static public void main (String[] args) {
        String pathToFolder = "";
        //removeObjectFiles(new File(pathToFolder));
        String pathToTrigFiles = "/Users/piek/Desktop/SemEval2018/trial_data/nwr/data";
        ArrayList<File> trigFiles = makeRecursiveFileList(new File(pathToTrigFiles), ".trig");

        TrigTripleData trigTripleData = TrigTripleReader.readTripleFromTrigFiles(trigFiles);
        ArrayList<String> domainEvents = EventTypes.getEventSubjectUris(trigTripleData.tripleMapInstances);
        HashMap<String, ArrayList<Statement>> eckgMap = getPrimaryKnowledgeGraphHashMap(domainEvents,trigTripleData);
        HashMap<String, ArrayList<Statement>> seckgMap = getSecondaryKnowledgeGraphHashMap(domainEvents,trigTripleData);
        System.out.println("eckgMap = " + eckgMap.size());
        System.out.println("domainEvents = " + domainEvents.size());
        System.out.println("eckgMap after merge = " + eckgMap.size());
        try {
            OutputStream fos = new FileOutputStream("/Users/piek/Desktop/SemEval2018/trial_data/nwr/"+"test.eckg");
            printKnowledgeGraph(fos, eckgMap, seckgMap);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
