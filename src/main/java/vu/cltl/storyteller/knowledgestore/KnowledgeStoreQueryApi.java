package vu.cltl.storyteller.knowledgestore;

/**
 * Created by piek on 11/12/2016.
 */
public class KnowledgeStoreQueryApi {

    static String KSLIMIT = "2000";
    static public String log = "";

    static public void main (String[] args) {
        String query = "";
        if (args.length==0) {
            args = new String[]{"--entityPhrase", "bank;money", "--entityType", "dbp:Bank", "--entityInstance", "dbpedia:Rabo",
                                "--eventPhrase", "kill", "--eventType", "eso:Killing", "--topic", "eurovoc:16789", "--grasp", "POSITIVE"};
        }
        query = createSparqlQuery(args);
        //System.out.print(query);
        //System.out.println("query = " + query);
        //System.out.println("log = " + log);
    }

    static public String createSparqlQuery (String [] args) {
        String eventPhrase = "";
        String eventType = "";
        String entityPhrase = "";
        String entityType = "";
        String entityInstance = "";
        String word = "";
        String topicQuery = "";
        String graspQuery = "";
        String authorPhrase = "";
        String citePhrase = "";
        String authorType = "";
        String citeType = "";
        String yearBegin = "";
        String yearEnd = "";
        String locationPhrase = "";
        String locationRegion = "";
        log +="\n\n";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--word") && args.length>(i+1)) {
                word = args[i+1];
                log += " -- querying for word = " + word;
            }
            else if (arg.equalsIgnoreCase("--eventPhrase") && args.length>(i+1)) {
                eventPhrase = args[i+1];
                log += " -- querying for eventPhrase = " + eventPhrase;
            }
            else if (arg.equalsIgnoreCase("--eventType") && args.length>(i+1)) {
                eventType = args[i+1];
                log += " -- querying for eventType = " + eventType;
            }
            else if (arg.equalsIgnoreCase("--entityPhrase") && args.length>(i+1)) {
                entityPhrase = args[i+1];
                log += " -- querying for entityPhrase = " + entityPhrase;
            }
            else if (arg.equalsIgnoreCase("--entityType") && args.length>(i+1)) {
                entityType = args[i+1];
                log += " -- querying for entityType = " + entityType;
            }
            else if (arg.equalsIgnoreCase("--entityInstance") && args.length>(i+1)) {
                entityInstance = args[i+1];
                log += " -- querying for entityInstance = " + entityInstance;
            }
            else if (arg.equalsIgnoreCase("--authorPhrase") && args.length>(i+1)) {
                authorPhrase = args[i+1];
                log += " -- querying for authorPhrase = " + authorPhrase;
            }
            else if (arg.equalsIgnoreCase("--authorType") && args.length>(i+1)) {
                authorType = args[i+1];
                log += " -- querying for authorType = " + authorType;
            }
            else if (arg.equalsIgnoreCase("--citePhrase") && args.length>(i+1)) {
                citePhrase = args[i+1];
                log += " -- querying for citedPhrase = " + citePhrase;
            }
            else if (arg.equalsIgnoreCase("--citeType") && args.length>(i+1)) {
                citeType = args[i+1];
                log += " -- querying for citeType = " + citeType;
            }
            else if (arg.equalsIgnoreCase("--grasp") && args.length>(i+1)) {
                graspQuery = args[i+1];
                log += " -- querying for grasp = " + graspQuery;
            }
            else if (arg.equalsIgnoreCase("--topic") && args.length>(i+1)) {
                topicQuery = args[i+1];
                log += " -- querying for topic = " + topicQuery;
            }
            else if (arg.equalsIgnoreCase("--ks-limit") && args.length>(i+1)) {
                KSLIMIT = args[i+1];
                SparqlGenerator.limit = KSLIMIT;
                log += " -- limit = " +KSLIMIT;
            }
        }
        log += "\n";

        String sparql = SparqlGenerator.makeSparqlQueryInit();

        //@TODO replace INTERSECT by UNION search
        if (!word.isEmpty()) {
            /// we convert a word search into the INTERSECT of  an event and entity phrase search
            eventPhrase = word;
            entityPhrase = word;
        }

        // Events
        if (!eventPhrase.isEmpty() || !eventType.isEmpty()) {
            sparql += "{\n";

            if (!eventPhrase.isEmpty()) {
                String labels = SparqlGenerator.getLabelQueryforEvent(eventPhrase);
                if (labels.indexOf("*")>-1)  {
                    labels = labels.replace("*", "");
                    sparql += SparqlGenerator.makeSubStringLabelFilter("?event", labels);
                }
                else { sparql += SparqlGenerator.makeLabelConstraint("?event", labels); }
            }
            if (!eventType.isEmpty()) {
                String types = SparqlGenerator.getTypeQueryforEvent(eventType);
                if (!eventPhrase.isEmpty()) {
                    sparql += " UNION \n";
                }
                sparql += SparqlGenerator.makeTypeFilter("?event", types);
            }
            sparql += "}\n";
        }


        //Entities
        if (!entityPhrase.isEmpty() || !entityType.isEmpty() || !entityInstance.isEmpty()) {
            //split query into types, instances and labels
            //
            String labels = SparqlGenerator.getLabelQueryforEntity(entityPhrase);
            String types = SparqlGenerator.getTypeQueryforEntity(entityType);
            String instances = SparqlGenerator.getInstanceQueryforEntity(entityInstance);
            sparql += "?event sem:hasActor ?ent .\n";
            sparql += "{\n";
            if (!labels.isEmpty()) {
                //makeLabelFilter("?entlabel",entityLabel) +
                if (labels.indexOf("*")>-1)  {
                    labels = labels.replace("*", "");
                    sparql += "?ent rdfs:label ?entlabel .\n" ;
                    sparql += SparqlGenerator.makeSubStringLabelFilter("?entlabel", labels);
                }
                else {
                    sparql += SparqlGenerator.makeLabelConstraint("?ent", labels);
                }
            }
            if (!instances.isEmpty()) {
                if (!labels.isEmpty()) {
                    sparql += " UNION \n";
                }
                sparql += SparqlGenerator.makeInstanceFilter("?event", instances);
                // "?event sem:hasActor ?ent .";

            }
            if (!types.isEmpty()) {
                if (!labels.isEmpty() || !instances.isEmpty()) {
                    sparql += " UNION \n";
                }
                sparql += SparqlGenerator.makeTypeFilter("?ent", types) ;
            }
            sparql += "}\n";
        }

        if (!topicQuery.isEmpty()) {
            sparql += SparqlGenerator.makeTopicFilter("?event", topicQuery);
        }

         /*
          @TODO implement period filter for events
         */
        if (!yearBegin.isEmpty()) {
/*
            sparql += SparqlGenerator.makeYearFilter("?time", yearBegin) +
                    "?ent rdfs:label ?entlabel .\n" +
                    "?event sem:hasTime ?time .\n";
*/
        }

        /*

        nwr:grasp {
    <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html>
            prov:wasAttributedTo  <http://www.newsreader-project.eu/provenance/author/The+Huffington+Post___Jennifer+Raff> .

    <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html/source_attribution/attr1_1>
            rdf:value               grasp:CERTAIN , grasp:PAST , grasp:POS ;
            grasp:isAttributionFor  <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5563,5570> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4014,4022> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5542,5550> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3873,3878> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3902,3912> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3916,3923> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3939,3943> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3974,3981> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5491,5502> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4813,4820> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4832,4840> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4873,4878> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4940,4946> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5330,5335> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5609,5614> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5365,5370> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5374,5378> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5403,5414> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5379,5385> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5419,5428> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5449,5452> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5468,5473> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5630,5631> ;
            grasp:wasAttributedTo   <http://dbpedia.org/resource/American_Academy_of_Pediatrics> .
     <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html/source_attribution/attr17_1>
            rdf:value               grasp:CERTAIN , grasp:PAST , grasp:POS ;
            grasp:isAttributionFor  <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2317,2324> ;
            grasp:wasAttributedTo   <http://www.newsreader-project.eu/data/vaccinations/non-entities/researcher> .

    <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html/doc_attribution/attr1_29>
            rdf:value               grasp:CERTAIN , grasp:POS , grasp:NONFUTURE ;
            grasp:isAttributionFor  <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=163,170> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1716,1723> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=49,54> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=89,96> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4206,4213> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=97,105> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=282,290> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=382,390> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3150,3158> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4214,4222> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=137,141> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2984,2989> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=228,235> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1643,1650> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2080,2087> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2363,2370> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=403,416> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=458,464> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2543,2547> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=496,500> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=485,489> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=513,519> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5275,5281> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=520,525> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=538,548> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4066,4076> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5196,5205> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=722,726> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2116,2120> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2417,2421> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=570,577> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=674,681> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1004,1010> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1195,1201> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=631,638> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=640,647> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=651,657> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=691,698> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=740,750> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=772,781> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1996,2005> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=797,807> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=823,835> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=890,895> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1416,1423> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2056,2061> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2308,2313> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2809,2814> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3285,3292> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3412,3417> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=848,854> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1202,1208> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3421,3425> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=858,863> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=932,940> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1687,1692> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1102,1109> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1238,1242> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1268,1274> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1308,1317> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1486,1489> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1666,1669> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1344,1349> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1350,1358> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1368,1374> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3265,3271> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1378,1385> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1412,1415> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1458,1462> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1466,1473> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1496,1502> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1574,1580> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1629,1637> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4501,4509> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1581,1587> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3777,3783> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1676,1683> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1764,1771> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1826,1834> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1785,1792> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1808,1818> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1861,1867> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=1868,1877> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2024,2031> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2162,2171> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2192,2196> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2210,2220> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2258,2265> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2435,2443> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2391,2403> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2493,2501> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2506,2512> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2532,2536> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5111,5115> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2940,2948> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4299,4307> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2707,2710> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3384,3387> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2741,2746> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2716,2727> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2772,2778> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2803,2808> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2833,2841> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=2912,2921> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3047,3057> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3208,3213> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3162,3173> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3233,3237> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3250,3260> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3314,3323> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3337,3345> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3356,3361> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3453,3462> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4600,4609> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3463,3467> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3616,3621> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3517,3529> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3849,3855> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5129,5134> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=3799,3808> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4737,4746> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4097,4106> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4040,4047> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4110,4117> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4028,4036> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4078,4080> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4136,4143> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4186,4191> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4165,4176> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4343,4352> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4276,4281> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4377,4385> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4463,4471> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4567,4577> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4525,4532> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4579,4588> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4621,4624> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4633,4642> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4661,4668> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4783,4794> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=4730,4736> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5095,5099> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5022,5030> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5048,5059> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5182,5186> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5187,5192> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5283,5290> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5231,5235> , <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html#char=5247,5252> ;
            prov:wasAttributedTo    <https://web.archive.org/web/20151010194304/http://www.huffingtonpost.com/jennifer-raff/dear-parents-you-are-being-lied-to-about-vaccines_b_5112620.html> .
         */
        if (!authorPhrase.isEmpty()) {
            String sources = SparqlGenerator.getSource(authorPhrase);
            if (!sources.isEmpty()) {
                sparql +=
                        "?event gaf:denotedBy ?mention.\n" +
                        "?attribution grasp:isAttributionFor ?mention .\n" +
                        //"?attribution prov:wasAttributedTo ?doc .\n" +
                        "?attribution prov:wasDerivedFrom ?doc .\n" +
                        "?doc prov:wasAttributedTo ?author .\n";
                sparql += SparqlGenerator.makeSubStringLabelFilter("?author", sources);
                //@TODO URIs with wird characters tend to fail with SPARQL
/*                if (sources.indexOf("*")>-1)  {
                    sources = sources.replace("*", "");
                    sparql += SparqlGenerator.makeSubStringLabelFilter("?doc", sources);
                }
                else {
                    sparql += SparqlGenerator.makeAuthorConstraint("?doc", sources);
                }*/
            }
        }

        if (!citePhrase.isEmpty()) {
            String sources = SparqlGenerator.getSource(citePhrase);
            if (!sources.isEmpty()) {
                sparql +=
                        "?event gaf:denotedBy ?mention.\n" +
                        "?attribution grasp:isAttributionFor ?mention .\n" +
                        "?attribution grasp:wasAttributedTo ?cite.\n";
/*
                if (STRICTSTRING) sparql += TrigKSTripleReader.makeLabelConstraint("?cite", sources);
                else {sparql += TrigKSTripleReader.makeSubStringLabelFilter("?cite", sources); }
*/
                sparql += SparqlGenerator.makeSubStringLabelFilter("?cite", sources);

                //@TODO   URIs for cites needs to be adapted, now has project name in URI
                ///grasp:wasAttributedTo  <http://www.newsreader-project.eu/data/wikinews/non-entities/actual+science> .

/*                if (sources.indexOf("*")>-1)  {
                    sources = sources.replace("*", "");
                    sparql += SparqlGenerator.makeSubStringLabelFilter("?cite", sources);
                }
                else {
                    sparql += SparqlGenerator.makeLabelConstraint("?cite", sources);
                }*/

            }
        }

            /// rdf:value grasp:CERTAIN_NON_FUTURE_POS , grasp:positive ;
            ///graspQuery = NEG;UNCERTAIN;positive;
        if (!graspQuery.isEmpty()) {
            boolean UNION = false;
            sparql += "?event gaf:denotedBy ?mention.\n" +
                    "?attribution grasp:isAttributionFor ?mention .\n" +
                    "?attribution rdf:value ?value .\n" ;
            sparql += "{\n";
            if (graspQuery.indexOf("negative") >-1) {
                sparql +=  "{ ?attribution rdf:value grasp:negative } \n";
                UNION = true;
            }
            if (graspQuery.indexOf("positive") >-1) {
                if (UNION) sparql += " UNION \n";
                sparql +=  "{ ?attribution rdf:value grasp:positive }\n";
                UNION = true;
            }
            String [] fields = graspQuery.split(";");
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                if (!field.toLowerCase().equals(field)) {
                    ///upper case field
                    if (UNION) sparql += " UNION \n";
                    //sparql +=  "{ "+ SparqlGenerator.makeSubStringLabelUnionFilter("?value", field) +" }"+ "\n";
                    sparql +=  "{ "+"?attribution rdf:value" + " grasp:"+field +" }"+ "\n";
                    UNION = true;
                }
            }
            sparql += " }\n";
            /*if (graspQuery.indexOf("FUTURE") > -1) {
                sparql += "FILTER(!CONTAINS(STR(?value), \"NON\"))\n";
            }*/
        }

        sparql += SparqlGenerator.makeSparqlQueryEnd();

        return sparql;
    }


}
