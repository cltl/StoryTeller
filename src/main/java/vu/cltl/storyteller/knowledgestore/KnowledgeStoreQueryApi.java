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
        System.out.print(query);
        //System.out.println("query = " + query);
        //System.out.println("log = " + log);
    }
    /*
 --entityType http://dbpedia.org/ontology/Bacteria;
 --lightEntityInstance http://dbpedia.org/resource/Human_papillomavirus;
 --darkEntityInstance http://www.newsreader-project.eu/data/vaccinations/entities/Quinvaxem;
 --darkEntityInstance http://www.newsreader-project.eu/data/vaccinations/entities/VAERS;
 --conceptType http://dbpedia.org/ontology/Disease;
 --nonEntityInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/immunization;
 --nonEntityInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/flu+vaccine;


  --esoType fn:Lose_possession_scenario; --eventPhrase reflect;

   --esoType fn:Change_of_state_scenario; --eventPhrase reflect; --eventPhrase reflection;


    --agentInstance http://www.newsreader-project.eu/provenance/author/unknown%2CDepartment_of_Health%2Cunknown;
     --agentInstance http://www.newsreader-project.eu/provenance/author/unknown%2CPatient%2CDr_Mary_Lowth;
     --citeType http://dbpedia.org/ontology/Organisation;
     --agentInstance http://dbpedia.org/resource/Edward_Jenner;
     --agentInstance http://dbpedia.org/resource/Alfred_Russel_Wallace;


  --topicType http://eurovoc.europa.eu/6011;
  --topicType http://eurovoc.europa.eu/1370;
  --grasp

   202 authors.json:"type": "authorInstance",
   2 authors.json:"type": "authorType",
  11 cited.json:"type": "citeInstance",
  17 cited.json:"type": "citeType",
 101 light.json:"type": "entityType",
 529 light.json:"type": "lightEntityInstance",
 513 dark.json:"type": "darkEntityInstance",
   6 dark.json:"type": "entityType",
 120 concept.json:"type": "conceptType",
4921 concept.json:"type": "nonEntityInstance",
1239 concept.json:"type": "conceptInstance",
1864 events.json:"type": "eventPhrase"
 396 events.json:"type": "eventType",
  12 perspectives.json:"type": "grasp"
 259 topics.json:"type": "topicType",


  --conceptInstance http://dbpedia.org/resource/Infection;
   --nonEntityInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/influenza;
    --nonEntityInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/flu+vaccination;

     --conceptInstance http://dbpedia.org/resource/Infection;
      --nonEntityInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/influenza;
       --nonEntityInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/flu+vaccination;
        --eventPhrase start;
         --esoType eso:Meeting;
          --esoType fn:Lose_possession_scenario;
     */
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
        String authorInstance = "";
        String citeInstance = "";
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
            else if (arg.equalsIgnoreCase("--darkEntityPhrase") && args.length>(i+1)) {
                entityPhrase = args[i+1];
                log += " -- querying for entityPhrase = " + entityPhrase;
            }
            else if (arg.equalsIgnoreCase("--lightEntityPhrase") && args.length>(i+1)) {
                entityPhrase = args[i+1];
                log += " -- querying for entityPhrase = " + entityPhrase;
            }
            else if (arg.equalsIgnoreCase("--entityType") && args.length>(i+1)) {
                entityType = args[i+1];
                log += " -- querying for entityType = " + entityType;
            }
            else if (arg.equalsIgnoreCase("--conceptType") && args.length>(i+1)) {
                entityType = args[i+1];
                log += " -- querying for entityType = " + entityType;
            }
            else if (arg.equalsIgnoreCase("--lightEntityInstance") && args.length>(i+1)) {
                entityInstance = args[i+1];
                log += " -- querying for entityInstance = " + entityInstance;
            }
            else if (arg.equalsIgnoreCase("--darkEntityInstance") && args.length>(i+1)) {
                entityInstance = args[i+1];
                log += " -- querying for entityInstance = " + entityInstance;
            }
            else if (arg.equalsIgnoreCase("--entityInstance") && args.length>(i+1)) {
                entityInstance = args[i+1];
                log += " -- querying for entityInstance = " + entityInstance;
            }
            else if (arg.equalsIgnoreCase("--conceptInstance") && args.length>(i+1)) {
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
            else if (arg.equalsIgnoreCase("--authorInstance") && args.length>(i+1)) {
                authorInstance = args[i+1];
                log += " -- querying for authorInstance = " + authorInstance;
            }
            else if (arg.equalsIgnoreCase("--citePhrase") && args.length>(i+1)) {
                citePhrase = args[i+1];
                log += " -- querying for citePhrase = " + citePhrase;
            }
            else if (arg.equalsIgnoreCase("--citeInstance") && args.length>(i+1)) {
                citeInstance = args[i+1];
                log += " -- querying for citeInstance = " + citeInstance;
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
            else if (arg.equalsIgnoreCase("--topicType") && args.length>(i+1)) {
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
      /*  if (!entityPhrase.isEmpty()) {
            //split query into types, instances and labels
            //
            String labels = SparqlGenerator.getLabelQueryforEntity(entityPhrase);
            if (!labels.isEmpty()) {
                sparql += "?event sem:hasActor ?ent .\n";
                if (labels.indexOf("*")>-1)  {
                    labels = labels.replace("*", "");
                    sparql += "?ent rdfs:label ?entlabel .\n" ;
                    sparql += SparqlGenerator.makeSubStringLabelFilter("?entlabel", labels);
                }
                else {
                    sparql += SparqlGenerator.makeLabelConstraint("?ent", labels);
                }
            }
        }

        if (!entityInstance.isEmpty()) {
            //split query into types, instances and labels
            //
            String instances = SparqlGenerator.getInstanceQueryforEntity(entityInstance);
            if (!instances.isEmpty()) {
                //sparql += "?event sem:hasActor ?ent .\n";
                sparql += SparqlGenerator.makeInstanceFilter("?event", instances);

            }
        }

        if (!entityType.isEmpty()) {
            //split query into types, instances and labels
            //
            String types = SparqlGenerator.getTypeQueryforEntity(entityType);
            if (!types.isEmpty()) {
                sparql += "?event sem:hasActor ?ent .\n";
                sparql += SparqlGenerator.makeTypeFilter("?ent", types) ;
            }
        }*/



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

        if (!authorPhrase.isEmpty()) {
            String sources = SparqlGenerator.getSourcePhrase(authorPhrase);
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

        if (!authorInstance.isEmpty()) {
            String sourceInstances = SparqlGenerator.getSource(authorInstance);
            if (!sourceInstances.isEmpty()) {
                sparql +=
                        "?event gaf:denotedBy ?mention.\n" +
                        "?attribution grasp:isAttributionFor ?mention .\n" +
                        //"?attribution prov:wasAttributedTo ?doc .\n" +
                        "?attribution prov:wasDerivedFrom ?doc .\n" ;
                sparql += SparqlGenerator.makeInstanceFilter("?doc","prov:wasAttributedTo", sourceInstances);
            }
        }

        if (!authorType.isEmpty()) {
            String sourceTypes = SparqlGenerator.getSource(authorType);
            if (!sourceTypes.isEmpty()) {
                sparql +=
                        "?event gaf:denotedBy ?mention.\n" +
                        "?attribution grasp:isAttributionFor ?mention .\n" +
                        //"?attribution prov:wasAttributedTo ?doc .\n" +
                        "?attribution prov:wasDerivedFrom ?doc .\n" +
                        "?doc prov:wasAttributedTo ?author .\n";
                sparql += SparqlGenerator.makeTypeFilter("?cite", sourceTypes) ;
            }
        }

        if (!citePhrase.isEmpty()) {
            String sources = SparqlGenerator.getSourcePhrase(citePhrase);
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
        if (!citeType.isEmpty()) {
            String sourceTypes = SparqlGenerator.getSource(citeType);
            if (!sourceTypes.isEmpty()) {
                sparql +=
                        "?event gaf:denotedBy ?mention.\n" +
                        "?attribution grasp:isAttributionFor ?mention .\n" +
                        "?attribution grasp:wasAttributedTo ?cite.\n";
                sparql += SparqlGenerator.makeTypeFilter("?cite", sourceTypes) ;
            }
        }
        if (!citeInstance.isEmpty()) {
            String sourceInstances = SparqlGenerator.getSource(citeInstance);
            if (!sourceInstances.isEmpty()) {
                sparql +=
                        "?event gaf:denotedBy ?mention.\n" +
                        "?attribution grasp:isAttributionFor ?mention .\n";
                sparql += SparqlGenerator.makeInstanceFilter("?attribution","grasp:wasAttributedTo", sourceInstances) ;
            }
        }

        ////cite
        /*if (!citePhrase.isEmpty() || !citeType.isEmpty() || !citeInstance.isEmpty()) {
            //split query into types, instances and labels
            //
            String labels = SparqlGenerator.getLabelQueryforEntity(citePhrase);
            String types = SparqlGenerator.getTypeQueryforEntity(citeType);
            String instances = SparqlGenerator.getInstanceQueryforEntity(citeInstance);
            sparql +=
                    "?event gaf:denotedBy ?mention.\n" +
                            "?attribution grasp:isAttributionFor ?mention .\n" +
                            "?attribution grasp:wasAttributedTo ?cite.\n";
            sparql += "{\n";
            if (!labels.isEmpty()) {
                //makeLabelFilter("?entlabel",entityLabel) +
                if (labels.indexOf("*")>-1)  {
                    labels = labels.replace("*", "");
                    sparql += "?cite rdfs:label ?entlabel .\n" ;
                    sparql += SparqlGenerator.makeSubStringLabelFilter("?entlabel", labels);
                }
                else {
                    sparql += SparqlGenerator.makeLabelConstraint("?cite", labels);
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
                sparql += SparqlGenerator.makeTypeFilter("?cite", types) ;
            }
            sparql += "}\n";
        }*/

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
