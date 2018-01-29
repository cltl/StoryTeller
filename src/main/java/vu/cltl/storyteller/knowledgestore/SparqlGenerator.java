package vu.cltl.storyteller.knowledgestore;

import vu.cltl.storyteller.objects.NameSpaces;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 11/12/2016.
 */
public class SparqlGenerator {
    public static String limit = "500";


    /**
     * Creates a SPARQL query to get a list of mentions for a list of event URIs
     * @param uris
     * @return
     */
    static public String makeMentionQuery(ArrayList<String> uris) {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT distinct ?mention WHERE { VALUES ?event {";
        for (int i = 0; i < uris.size(); i++) {
            String uri = uris.get(i);
            if (!uri.startsWith("<")) uri = "<"+uri+">";
            sparqlQuery +=  uri+" ";

        }
        sparqlQuery += "}\n" +
                "    ?event gaf:denotedBy ?mention\n" +
                "}";
        return sparqlQuery;
    }

    /**
     * Creates a SPARQL query for a list of event URIs to get a table with:
     * ?event ?mention ?attribution ?author ?cite ?label ?comment"
     *
     PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
     PREFIX owltime: <http://www.w3.org/TR/owl-time#>
     PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
     PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
     SELECT distinct ?event ?mention ?attribution ?author ?cite ?label ?comment WHERE { VALUES ?event {
     <http://www.cidrap.umn.edu/news-perspective/2016/10/news-scan-oct-10-2016#ev121>
     <https://web.archive.org/web/20161107200904/http://www.reuters.com/article/us-measles-disneyland-idUSKBN0MC1OJ20150316#ev51>
     <https://web.archive.org/web/20150818195231/http://www.nydailynews.com/life-style/health/disneyland-measles-outbreak-linked-vaccine-rate-article-1.2151859#ev58>
     <https://web.archive.org/web/20160729045546/http://www.pbs.org/newshour/updates/whos-risk-measles-maybe-think/#ev70>
     <https://web.archive.org/web/20150905085927/http://www.naturalnews.com/048383_measles_outbreak_Disneyland_vaccine_inserts.html#ev170>
     <https://web.archive.org/web/20150723165753/http://nymag.com/daily/intelligencer/2015/01/anti-vaccinators-react-to-measles-outbreak.html#ev159>
     <https://web.archive.org/web/20150810135938/http://www.wired.com/2015/01/vaccinated-people-get-measles-disneyland-blame-unvaccinated/#ev93>
     <https://web.archive.org/web/20161107195115/http://insideupmc.upmc.com/disney-measles-outbreak-a-consequence-of-low-vaccination-rates-easy-travel/#ev66>
     <https://web.archive.org/web/20161018172826/http://www.usnews.com/news/blogs/data-mine/2015/01/26/whats-really-behind-the-measles-outbreak#ev132>
     }
     ?event <http://groundedannotationframework.org/gaf#denotedBy> ?mention .
     ?attr <http://groundedannotationframework.org/grasp#isAttributionFor> ?mention .
     ?attr rdf:value  ?attribution .
     OPTIONAL { ?attr <http://groundedannotationframework.org/grasp#wasAttributedTo> ?cite }
     OPTIONAL { ?attr <http://www.w3.org/ns/prov#wasAttributedTo> ?doc . ?doc <http://www.w3.org/ns/prov#wasAttributedTo> ?author}
     }
     * @param uris
     * @return
     */
    static public String makeAttributionQuery(ArrayList<String> uris) {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT distinct ?event ?mention ?attribution ?author ?cite ?label ?comment WHERE { VALUES ?event {\n";
        for (int i = 0; i < uris.size(); i++) {
            String u = uris.get(i);
            if (!u.startsWith("<")) u = "<"+u+">";
            sparqlQuery += u+"\n";
        }
            sparqlQuery+= "}\n"+
                "    ?event <http://groundedannotationframework.org/gaf#denotedBy> ?mention .\n" +
                "     ?attr <http://groundedannotationframework.org/grasp#isAttributionFor> ?mention .\n" +
                "     ?attr rdf:value  ?attribution .\n" +
                "     OPTIONAL { ?attr <http://groundedannotationframework.org/grasp#wasAttributedTo> ?cite }\n" +
                //"     OPTIONAL { ?attr <http://www.w3.org/ns/prov#wasAttributedTo> ?doc . ?doc <http://www.w3.org/ns/prov#wasAttributedTo> ?author}\n" +
                "     OPTIONAL { ?attr <http://www.w3.org/ns/prov#isDerivedFrom> ?doc . ?doc <http://www.w3.org/ns/prov#wasAttributedTo> ?author}\n" +
                "}";
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    /**
     * @Deprecated
     * Creates a SPARQL query for a list of event URIs to get a table with:
     * ?event ?mention ?attribution ?author ?cite ?label ?comment"
     *
     PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
     PREFIX owltime: <http://www.w3.org/TR/owl-time#>
     PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
     PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
     SELECT distinct ?event ?mention ?attribution ?author ?cite ?label ?comment WHERE { VALUES ?event {<http://www.ft.com/thing/b0d8195c-eb8d-11e5-888e-2eadd5fbc4a4#ev101> <http://www.ft.com/thing/bb5a999a-c67b-11e5-808f-8231cd71622e#ev129> <http://www.ft.com/thing/e5c73d14-92cd-11e5-94e6-c5413829caa5#ev57>}
     ?event <http://groundedannotationframework.org/gaf#denotedBy> ?mention
     OPTIONAL { ?mention rdfs:label ?label}
     OPTIONAL { ?mention rdfs:comment ?comment}
     OPTIONAL { ?mention <http://groundedannotationframework.org/grasp#hasAttribution> [ rdf:value ?attribution]}
     OPTIONAL { ?mention <http://groundedannotationframework.org/grasp#hasAttribution> [ <http://groundedannotationframework.org/grasp#wasAttributedTo> ?cite ]}
     OPTIONAL { ?mention <http://groundedannotationframework.org/grasp#hasAttribution> [ <http://www.w3.org/ns/prov#wasAttributedTo> [<http://www.w3.org/ns/prov#wasAttributedTo> ?author] ]}
     }
     <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77#char=1207,1211>
     rdfs:comment          "said" ;
     rdfs:label            "loan" ;
     grasp:generatedBy     <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77#char=1097,1101> ;
     grasp:hasAttribution  <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77/source_attribution/Attr1> .

     <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77/source_attribution/Attr1>
     rdf:value              grasp:CERTAIN_FUTURE_POS , grasp:u_u_u , grasp:CERTAIN_u_POS ,
     grasp:CERTAIN_NON_FUTURE_POS , grasp:PROBABLE_u_POS ;
     grasp:wasAttributedTo  <http://dbpedia.org/resource/Institute_for_Fiscal_Studies> .

     <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77/doc_attribution/Attr0>
     rdf:value             grasp:u_NON_FUTURE_u ;
     prov:wasAttributedTo  <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77> .

     <http://www.ft.com/thing/00937ca4-cffd-11e5-92a1-c5e23ef99c77>
     prov:wasAttributedTo  <http://www.newsreader-project.eu/provenance/author/Gonzalo+Vi%C3%B1a%2C+Public+Policy+Reporter> .

     * @param uris
     * @return
     */
    static public String makeAttributionQueryOrg(ArrayList<String> uris) {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT distinct ?event ?mention ?attribution ?author ?cite ?label ?comment" +
                " WHERE { VALUES ?event {";
        for (int i = 0; i < uris.size(); i++) {
            sparqlQuery += uris.get(i);
        }
        sparqlQuery += "}\n" +
                "    ?event <http://groundedannotationframework.org/gaf#denotedBy> ?mention\n" +
                "     OPTIONAL { ?mention rdfs:label ?label}\n" +
                "     OPTIONAL { ?mention rdfs:comment ?comment}\n" +
                "     OPTIONAL { ?mention <http://groundedannotationframework.org/grasp#hasAttribution> [rdf:value  ?attribution]}\n" +
                "     OPTIONAL { ?mention <http://groundedannotationframework.org/grasp#hasAttribution> [<http://groundedannotationframework.org/grasp#wasAttributedTo> ?cite]}\n" +
                "     OPTIONAL { ?mention <http://groundedannotationframework.org/grasp#hasAttribution> [<http://www.w3.org/ns/prov#wasAttributedTo>  [<http://www.w3.org/ns/prov#wasAttributedTo> ?author]]}\n" +
                "}";
        System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    public static String makeSparqlQueryInit () {
        String sparqQueryInit = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX ili: <http://globalwordnet.org/ili/> \n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "PREFIX nwrcite: <http://www.newsreader-project.eu/data/non-entities/> \n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX dbpedianl: <http://nl.dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event ?relation ?object ?indatetime ?begintime ?endtime  \n" +
                //"SELECT ?event ?relation ?object \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                "?event rdf:type " + "sem:Event" + " .\n";
        return sparqQueryInit;
    }

    public static String makeSparqlQueryEnd () {
        String sparqQueryEnd =
                "\n} LIMIT "+limit+" }\n" +
                        "?event ?relation ?object . \n" +
                        "OPTIONAL { ?object rdf:type owltime:Instant ; owltime:inDateTime ?indatetime } \n" +
                        "OPTIONAL { ?object rdf:type owltime:Interval ; owltime:hasBeginning ?begintime } \n" +
                        "OPTIONAL { ?object rdf:type owltime:Interval ; owltime:hasEnd ?endtime } \n" +
                        "} ORDER BY ?event" ;
        return sparqQueryEnd;
    }

    static public String makeTripleQuery (String subjectUri) {
        String subject = subjectUri;
        if (!subjectUri.startsWith("<")) subject = "<"+subject+">";
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?subject ?predicate ?object WHERE { "+subject+" ?predicate ?object} LIMIT 10";
        return sparqlQuery;
    }




    static public String makeValidUriString (String label) {
        String valid = label;
        valid = valid.replace('^', ' ');
        valid = valid.replace('_', '.');
        valid = valid.replace('+', '.');
        valid = valid.replace('(', '.');
        valid = valid.replace('?', '.');
        valid = valid.replace(')', '.');
        return valid;
    }

    static public String makeLabelFilter(String variable, String query) {
        //FILTER ( regex(str(?entlabel), "Bank") || regex(str(?entlabel), "Dank")) .

        String filter = "FILTER (";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');;
            if (i>0)  filter +=" || ";
            filter += "regex(str("+variable+"), \"^"+field+"$\")";
        }
        filter += ") .\n" ;
        return filter;
    }

    static public String makeLabelSingleConstraint(String variable, String query) {
        //?ent rdfs:label "beleggen" .

        String filter = variable+" rdfs:label \""+query+"\" .\n" ;
        return filter;
    }

    static public String makeLabelConstraint(String variable, String query) {
        //{  { ?ent rdfs:label "Sanddbezorger" }  UNION  { ?ent rdfs:label "InPost" }  UNION  { ?ent rdfs:label "Postbedrijf" }

        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');;
            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" rdfs:label \""+field+"\" } ";
        }
        filter += " }\n" ;
        return filter;
    }

    static public String makeAuthorConstraint(String variable, String query) {
        //{  { ?doc prov:wasAttributedTo nwrauthor:Twitter }  UNION  { ?doc prov:wasAttributedTo nwrauthor:Postbodeforum }  }

        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');
            try {
                field = URLEncoder.encode(field, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
            }

            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" prov:wasAttributedTo nwrauthor:"+field+" } ";
        }
        filter += " }\n" ;
        return filter;
    }

    static public String makeCiteConstraint(String variable, String query) {

        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');
            try {
                field = URLEncoder.encode(field, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
            }
            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" grasp:wasAttributedTo nwrcite:"+field+" } ";
        }
        filter += " }\n" ;
        return filter;
    }

    static public String makeValueConstraint(String variable, String query) {
        //?value rdfs:value "negative" .
        String filter = variable+" rdfs:value \""+query+"\" .\n" ;
        return filter;
    }

    static public String makeYearFilter(String variable, String query) {

        String filter = "FILTER (";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');;
            if (i>0)  filter +=" || ";
            filter += "regex(str("+variable+"), \"^"+field+"$\")";
        }
        filter += ") .\n" ;
        return filter;
    }

    static public String makeSubStringLabelFilter(String variable, String query) {
        //FILTER ( regex(str(?entlabel), "Bank") || regex(str(?entlabel), "Dank")) .
        //http://www.newsreader-project.eu/provenance/author/Algemeen+Dagblad
        String filter = "FILTER (";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = makeValidUriString(fields[i]);
            if (i>0)  filter +=" || ";
            filter += "regex(str("+variable+"), \""+field+"\")";
        }
        filter += ") .\n" ;
        return filter;
    }

    static public String makeSubStringLabelUnionFilter(String variable, String query) {
        //FILTER ( regex(str(?entlabel), "Bank") || regex(str(?entlabel), "Dank")) .
        //http://www.newsreader-project.eu/provenance/author/Algemeen+Dagblad
        String filter = "FILTER (";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = makeValidUriString(fields[i]);
            if (i>0)  filter +=" || ";
            filter += "regex(str("+variable+"), \""+field+"\")";
        }
        filter += ") \n" ;
        return filter;
    }

    static public String makeTypeFilter(String variable, String query) {
        //FILTER ( regex(str(?entlabel), "Bank") || regex(str(?entlabel), "Dank")) .
        // "?event rdf:type " + eventType + " .\n" +
        // { ?event rdf:type eso:Buying } UNION {?event rdf:type eso:Selling }
        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');;
            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" rdf:type "+field+" } ";
        }
        filter += " }\n" ;
        return filter;
    }

    static public String makeInstanceFilter(String variable, String query) {
        // "?event sem:hasActor ?ent .\n" +
        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
          //  String field = makeValidUriString(fields[i]);
            String field = fields[i];
            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" sem:hasActor "+field+" } ";
        }
        filter += " }\n" ;
        return filter;
    }

    static public String makeInstanceFilter(String variable, String property, String query) {
        // "?event sem:hasActor ?ent .\n" +
        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = makeValidUriString(fields[i]);
            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" "+property +" "+field+" } ";
        }
        filter += " }\n" ;
        return filter;
    }


    static public String makeTopicFilter(String variable, String query) {
        String filter = "{ ";
        String[] fields = query.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].replace('^', ' ');;
            if (i>0)  filter +=" UNION ";
            filter += " { "+variable+" skos:relatedMatch <"+field+"> } ";
        }
        filter += " }\n" ;
        return filter;
    }

    static public String getSource (String sourceQuery) {
        String [] fields = sourceQuery.split(";");
        String sources = "";
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            if (!sources.isEmpty()) sources += ";";
            sources += "<"+field+">";
        }
        return sources;
    }

    static public String getSourcePhrase (String sourceQuery) {
        String [] fields = sourceQuery.split(";");
        String sources = "";
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            if (!sources.isEmpty()) sources += ";";
            sources += field;
        }
        return sources;
    }

    static public String getLabelQueryforEvent(String entityQuery) {
        String labels = "";
        String [] fields = entityQuery.split(";");
        // System.out.println("entityQuery = " + entityQuery);
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if (field.indexOf(":")==-1) {
                if (!labels.isEmpty()) labels += ";";
                labels += field;
            }
        }
        return labels;
    }

    static public String getTypeQueryforEvent(String entityEvent) {
        String labels = "";
        String [] fields = entityEvent.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if (!labels.isEmpty()) labels += ";";
            labels += field;
        }
        return labels;
    }

    static public String getTypeQueryforEventFiltered(String entityEvent) {
        String labels = "";
        String [] fields = entityEvent.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if (field.indexOf(":")>-1) {
                if (!labels.isEmpty()) labels += ";";
                labels += field;
            }
        }
        return labels;
    }

    static public String getLabelQueryforEntity(String entityQuery) {
        String labels = "";
        String [] fields = entityQuery.split(";");
        // System.out.println("entityQuery = " + entityQuery);
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if (!labels.isEmpty()) labels += ";";
            labels += field;
        }
        return labels;
    }

    static public String getLabelQueryforEntityFiltered(String entityQuery) {
        String labels = "";
        String [] fields = entityQuery.split(";");
        // System.out.println("entityQuery = " + entityQuery);
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if (field.indexOf("dbp:")==-1 && field.indexOf("//cltl.nl/")==-1 && field.indexOf("dbpedia:")==-1 && field.indexOf("dbpedianl:")==-1) {
                if (!labels.isEmpty()) labels += ";";
                labels += field;
            }
        }
        return labels;
    }

    static public String getTypeQueryforEntity(String entityQuery) {
        String labels = "";
        if (!entityQuery.isEmpty()) {
            String[] fields = entityQuery.split(";");
            // System.out.println("entityQuery = " + entityQuery);
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].trim().replace('^', ' ');
                // field = multiwordFix(field);
                if (!labels.isEmpty()) labels += ";";
                labels += "<" + field + ">";
            }
        }
        return labels;
    }

    static public String getTypeQueryforEntityFiltered(String entityQuery) {
        String labels = "";
        String [] fields = entityQuery.split(";");
        // System.out.println("entityQuery = " + entityQuery);
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if (field.indexOf("dbp:")>-1 || field.indexOf("//cltl.nl/")>-1) {
                if (!labels.isEmpty()) labels += ";";
                labels += field;
            }
        }
        return labels;
    }

    static public String getInstanceQueryforEntityFiltered(String entityQuery) {
        String labels = "";
        String[] fields = entityQuery.split(";");
        // System.out.println("entityQuery = " + entityQuery);
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim().replace('^', ' ');
            // field = multiwordFix(field);
            if ((field.indexOf("dbpedia:") > -1) || (field.indexOf("dbpedianl:") > -1)) {
                if (!labels.isEmpty()) labels += ";";
                labels += field;
            }
        }
        return labels;
    }

    static public String getInstanceQueryforEntity(String entityQuery) {
        String labels = "";
        if (!entityQuery.isEmpty()) {
            String[] fields = entityQuery.split(";");
            // System.out.println("entityQuery = " + entityQuery);
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].trim().replace('^', ' ');
                // field = multiwordFix(field);
                if (!labels.isEmpty()) labels += ";";
                labels += "<" + field + ">";
            }
        }
        return labels;
    }

    static public String makeQueryforEntityLabel(String entityLabel, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event  \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                //makeLabelFilter("?entlabel",entityLabel) +
                makeSubStringLabelFilter("?entlabel",entityLabel) +
                "?ent rdfs:label ?entlabel .\n" +
                "?event sem:hasActor ?ent .\n";
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery+= "} LIMIT "+limit+" }}\n";
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }


    static public String makeQueryforEntityType(String entityType, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                "?event sem:hasActor ?ent .\n" +
                makeTypeFilter("?ent", entityType);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery +="} LIMIT "+limit+" }}\n" ;
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    static public String makeQueryforEntityInstance(String entityType, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                makeInstanceFilter("?event", entityType);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    static public String makeQueryforEventType(String eventType, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX ili: <http://globalwordnet.org/ili/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                makeTypeFilter("?event", eventType);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    static public String makeQueryforEventLabel(String eventLabel, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX ili: <http://globalwordnet.org/ili/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                makeLabelFilter("?eventlabel",eventLabel);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        // System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    //@TODO make years filter
    static public String makeQueryforYears(String year, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX ili: <http://globalwordnet.org/ili/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                makeYearFilter("?eventlabel",year);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    @Deprecated
    static public String makeQueryforCitedSurfaceForm(String citedLabel, ArrayList<String> eventIds)throws Exception {

        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "SELECT ?event ?relation ?object ?indatetime ?begintime ?endtime \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                "?event gaf:denotedBy ?mention.\n" +
                "?mention grasp:hasAttribution ?attribution.\n" +
                "?attribution grasp:wasAttributedTo ?cite.\n" +
                makeSubStringLabelFilter("?cite", citedLabel);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        return sparqlQuery;
    }

    @Deprecated
    static public String makeQueryforAuthorSurfaceForm(String authorLabel, ArrayList<String> eventIds)throws Exception {
        //http://www.newsreader-project.eu/provenance/author/Algemeen+Dagblad
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "SELECT ?event ?relation ?object ?indatetime ?begintime ?endtime \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                "?event gaf:denotedBy ?mention.\n" +
                "?mention grasp:hasAttribution ?attribution.\n" +
                //"?attribution prov:wasAttributedTo ?doc .\n" +
                "?attribution prov:wasDerivedFrom ?doc .\n" +
                "?doc prov:wasAttributedTo ?author .\n"  +
                makeSubStringLabelFilter("?author", authorLabel);
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        // System.out.println("sparqlQuery = " + sparqlQuery);
        return  sparqlQuery;
    }

    static public String makeQueryforGraspValue(String graspValue, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "SELECT ?event \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                "?event gaf:denotedBy ?mention.\n" +
                "?mention grasp:hasAttribution ?attribution.\n" +
                "?attribution rdf:value ?value .\n" +
                makeSubStringLabelFilter("?value", graspValue) +"\n";
        if (graspValue.indexOf("FUTURE")>-1) {
            sparqlQuery += "FILTER(!CONTAINS(STR(?value), \"NON_FUTURE\"))\n";
        }
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }}\n" ;
        //System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    static public String makeQueryforTopic(String topic, ArrayList<String> eventIds)throws Exception {
        String sparqlQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" +
                "PREFIX eurovoc: <http://eurovoc.europa.eu/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "SELECT ?event ?relation ?object ?indatetime ?begintime ?endtime \n" +
                "WHERE {\n" +
                "{SELECT distinct ?event WHERE { \n" +
                "?event skos:relatedMatch eurovoc:" + topic + " .\n";
        if (eventIds.size()>0) {
            sparqlQuery += "\t\t\tVALUES ?event\n" + "\t\t\t\t{";
            for (int i = 0; i < eventIds.size(); i++) {
                String evenId = eventIds.get(i);
                sparqlQuery += "<" + evenId + ">\n";
            }
            sparqlQuery+= "}";
        }
        sparqlQuery += "} LIMIT "+limit+" }\n";
        // System.out.println("sparqlQuery = " + sparqlQuery);
        return sparqlQuery;
    }

    /*
PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#>
PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/>
PREFIX ili: <http://globalwordnet.org/ili/>
PREFIX dbp: <http://dbpedia.org/ontology/>
PREFIX dbpedia: <http://dbpedia.org/resource/>
PREFIX owltime: <http://www.w3.org/TR/owl-time#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?event ?relation ?object ?indatetime ?begintime ?endtime
WHERE {
{SELECT distinct ?event
 WHERE {
        VALUES ?event
             {<http://digikrant-archief.fd.nl/vw/txt.do?id=FD-20140305-01018019#ev50>
                 <http://digikrant-archief.fd.nl/vw/txt.do?id=FD-20140305-01018019#ev51>}
        }
}
?event ?relation ?object .
OPTIONAL { ?object rdf:type owltime:Instant ; owltime:inDateTime ?indatetime }
OPTIONAL { ?object rdf:type owltime:Interval ; owltime:hasBeginning ?begintime }
OPTIONAL { ?object rdf:type owltime:Interval ; owltime:hasEnd ?endtime }
} ORDER BY ?event
 */
    public static String makeSparqlQueryForEventArrayDataFromKs (ArrayList<String> eventIds) {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX ili: <http://globalwordnet.org/ili/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?event ?relation ?object ?indatetime ?begintime ?endtime \n" +
                "WHERE {\n" +
                "\t{SELECT distinct ?event\n" +
                "\t WHERE {\n" +
                "\t\t\tVALUES ?event\n" +
                "\t\t\t \t{";
        for (int i = 0; i < eventIds.size(); i++) {
            String evenId =  eventIds.get(i);
            sparqQuery += "<"+evenId+">\n";
        }
/*                <http://www.ft.com/thing/b0d8195c-eb8d-11e5-888e-2eadd5fbc4a4#ev101>\n" +
                "\t \t\t\t\t<http://www.ft.com/thing/bb5a999a-c67b-11e5-808f-8231cd71622e#ev129>\n" +
                "\t  \t\t\t\t<http://www.ft.com/thing/e5c73d14-92cd-11e5-94e6-c5413829caa5#ev57>"*/
        sparqQuery += "\t\t\t}}}\n" +
                "\t?event ?relation ?object .\n" +
                "\tOPTIONAL { ?object rdf:type owltime:Instant ; owltime:inDateTime ?indatetime }\n" +
                "\tOPTIONAL { ?object rdf:type owltime:Interval ; owltime:hasBeginning ?begintime }\n" +
                "\tOPTIONAL { ?object rdf:type owltime:Interval ; owltime:hasEnd ?endtime }\n" +
                "} ORDER BY ?event";
        //System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    ///////////////////////////////
    //////// Sparql queries to get hierarchical query interface data
    ///////////////////////////////

    public static String makeSparqlQueryForTaxonomyFromKs (Set children) {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <http://www.newsreader-project.eu/domain-ontology#> \n" +
                "PREFIX fn: <http://www.newsreader-project.eu/ontologies/framenet/> \n" +
                "PREFIX ili: <http://globalwordnet.org/ili/> \n" +
                "PREFIX dbp: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia: <http://dbpedia.org/resource/> \n" +
                "PREFIX owltime: <http://www.w3.org/TR/owl-time#> \n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?child ?parent\n" +
                "WHERE {\n";
                Iterator<String> keys = children.iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                }
                sparqQuery += "} ";
        //System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForLightEntityFromKs() {
        String sparqQuery = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n"+
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?label ?a (COUNT (DISTINCT ?m) as ?count) ?type \n" +
                "WHERE {\n" +
                "?a gaf:denotedBy ?m .\n" +
                "?a rdfs:label ?label .\n" +
                "OPTIONAL {?a a ?type . }\n" +
                "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
                "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n"+
                "}\n" +
                "group by ?label ?a ?type\n" +
                "order by DESC(?count)";
        //System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


    /**
     * "?a nwr:phrasecount ?phrasecount .\n" +
       "?phrasecount rdfs:label ?label .\n" +
       "?phrasecount nwr:count ?count .\n" +
     * @return
     */
    public static String makeSparqlQueryForDarkEntitiesFromKsCountFilter() {
        String sparqQuery = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n"+
                "PREFIX nwr: <http://www.newsreader-project.eu/> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT  ?label ?a (COUNT (DISTINCT ?m) as ?count) ?type \n" +
                "WHERE {\n" +
                "?a gaf:denotedBy ?m .\n" +
                "?a nwr:phrasecount ?phrasecount .\n" +
                "?phrasecount rdfs:label ?label .\n" +
                "?a a ?type . \n" +
                "FILTER(STRSTARTS(STR(?etype), \"http://www.newsreader-project.eu/domain-ontology\"))\n" +
                "}\n" +
                "group by  ?label ?a ?type\n" +
                "order by DESC(?count)";
        System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


    public static String makeSparqlQueryForDarkEntitiesFromKs() {
        String sparqQuery = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n"+
                "PREFIX nwr: <http://www.newsreader-project.eu/> \n" +
                "PREFIX nwrontology: <http://www.newsreader-project.eu/ontologies/>\n"+
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT  ?label ?a (COUNT (DISTINCT ?m) as ?count) ?type \n" +
                "WHERE {\n" +
                "?a gaf:denotedBy ?m .\n" +
                "?a rdfs:label ?label .\n" +
                "?a rdf:type ?type .\n" +
                "{?ent rdf:type nwrontology:PER}\n" +
                "UNION\n" +
                "{?ent rdf:type nwrontology:LOC}\n" +
                "UNION\n" +
                "{?ent rdf:type nwrontology:ORG}\n" +
                "UNION\n" +
                "{?ent rdf:type nwrontology:MISC}\n"+
                "}\n" +
                "group by ?label ?a ?type\n" +
                "order by DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    /**
     * "?a nwr:phrasecount ?phrasecount .\n" +
       "?phrasecount rdfs:label ?label .\n" +
       "?phrasecount nwr:count ?count .\n" +
     * @return
     */
    public static String makeSparqlQueryForDarkEntitiesFromKsCount() {
        String sparqQuery = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n"+
                "PREFIX nwr: <http://www.newsreader-project.eu/> \n" +
                "PREFIX nwrontology: <http://www.newsreader-project.eu/ontologies/>\n"+
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT  ?label ?a (COUNT (DISTINCT ?m) as ?count) ?type \n" +
                "WHERE {\n" +
                "?a gaf:denotedBy ?m .\n" +
                "?a nwr:phrasecount ?phrasecount .\n" +
                "?phrasecount rdfs:label ?label .\n" +
                "{?ent rdf:type nwrontology:PER}\n" +
                "UNION\n" +
                "{?ent rdf:type nwrontology:LOC}\n" +
                "UNION\n" +
                "{?ent rdf:type nwrontology:ORG}\n" +
                "UNION\n" +
                "{?ent rdf:type nwrontology:MISC}\n"+
                "}\n" +
                "group by  ?label ?a ?type\n" +
                "order by DESC(?count)";
        System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForPhraseSkosRelatedTypeCountsFromKs () {
        String sparqQuery = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n"+
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "SELECT ?a (COUNT (DISTINCT ?m) as ?count) ?type \n" +
                "WHERE {\n" +
                "?a gaf:denotedBy ?m .\n" +
                "OPTIONAL {?a skos:relatedMatch ?type . }\n" +
                "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n"+
                "FILTER (CONTAINS(STR(?a), \"/non-entities/\"))\n" +
                "}\n" +
                "group by ?a ?type\n" +
                "order by DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForTopicCountsFromKs () {
        String sparqQuery = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n"+
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "SELECT ?topic (COUNT (DISTINCT ?m) as ?count) \n" +
                "WHERE {\n" +
                "?a a sem:Event .\n" +
                "?a gaf:denotedBy ?m .\n" +
                "?a skos:relatedMatch ?topic . }\n" +
                "group by ?topic\n" +
                "order by DESC(?count)";
        //// topic + count
        //System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForEventEsoFramenetTypeCountsFromKs () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <"+ NameSpaces.eso+"> \n" +
                "PREFIX framenet: <"+NameSpaces.fn+"> \n" +
                "PREFIX nwr: <"+NameSpaces.nwrclass+"> \n" +
                "PREFIX rdf: <"+NameSpaces.rdf+"> \n" +
                "PREFIX rdfs: <"+NameSpaces.rdfs+"> \n" +
                "SELECT ?label (COUNT(?type) as ?count) ?type \n" +
                "WHERE {\n" +
                "       ?e a sem:Event .\n" +
                "       ?e rdf:type ?type .\n" +
                "       ?e rdfs:label ?label .\n" +
                "      { ?type nwr:isClassDefinedBy framenet: }\n" +
                "UNION\n" +
                "{?type nwr:isClassDefinedBy eso:} \n" +
                "}\n" +
                "GROUP BY ?label ?type\n" +
                "ORDER BY DESC(?count)";
      //  System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForEventAnyTypeCountsFromKs () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX eso: <"+ NameSpaces.eso+"> \n" +
                "PREFIX framenet: <"+NameSpaces.fn+"> \n" +
                "PREFIX nwr: <"+NameSpaces.nwrclass+"> \n" +
                "PREFIX rdf: <"+NameSpaces.rdf+"> \n" +
                "PREFIX rdfs: <"+NameSpaces.rdfs+"> \n" +
                "SELECT ?label (COUNT(?type) as ?count) ?type \n" +
                "WHERE {\n" +
                "       ?e a sem:Event .\n" +
                "       ?e rdf:type ?type .\n" +
                "       ?e rdfs:label ?label .\n" +
                "}\n" +
                "GROUP BY ?label ?type\n" +
                "ORDER BY DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }
    /*
    PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
     PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
     PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
     PREFIX grasp: <http://groundedannotationframework.org/grasp#>
     PREFIX gaf:   <http://groundedannotationframework.org/gaf#>
     SELECT ?cite (COUNT (DISTINCT ?mention) as ?count)
WHERE {
    ?attribution grasp:attributedTo ?mention .
    ?cite grasp:hasAttribution ?attribution .
    FILTER NOT EXISTS {?cite prov:wasAttributedTo ?author}

     }
group by ?cite
order by DESC(?count)
     */
    public static String makeSparqlQueryForCitedSourcesFromKs () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
                "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
                "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "    SELECT ?a ?type (COUNT (DISTINCT ?mention) as ?count)\n" +
                "WHERE {\n" +
                "\n" +

                "OPTIONAL {?a a ?type . }\n" +
                "    ?attribution grasp:isAttributionFor ?mention .\n" +
                "    ?attribution grasp:wasAttributedTo ?a \n" +
                "    }\n" +
                "group by ?a ?type \n" +
                "order by DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForCitedSourcesFromKsWithLabels () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
                "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
                "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "    SELECT ?a ?label ?type (COUNT (DISTINCT ?mention) as ?count)\n" +
                "WHERE {\n" +
                "\n" +

                "OPTIONAL {?a rdfs:label ?label .}\n" +
                "OPTIONAL {?a a ?type . }\n" +
                "    ?attribution grasp:isAttributionFor ?mention .\n" +
                "    ?attribution grasp:wasAttributedTo ?a \n" +
                "    }\n" +
                "group by ?a ?label ?type \n" +
                "order by DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    public static String makeSparqlQueryForCitedSourcesFromKsOrg () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
                "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
                "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "    SELECT ?cite (COUNT (DISTINCT ?mention) as ?count)\n" +
                "WHERE {\n" +
                "\n" +
                "    ?attribution grasp:isAttributionFor ?mention .\n" +
                "    ?attribution grasp:wasAttributedTo ?cited \n" +
                "    }\n" +
                "group by ?cite \n" +
                "order by DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    /**
     * @Deprecated
     * PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
     PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
     PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
     PREFIX grasp: <http://groundedannotationframework.org/grasp#>
     PREFIX gaf:   <http://groundedannotationframework.org/gaf#>
     SELECT ?author (COUNT (DISTINCT ?mention) as ?count)
     WHERE {
     ?attribution grasp:attributedTo ?mention .
     ?doc grasp:hasAttribution ?attribution .
     ?doc prov:wasAttributedTo ?author

     }
     group by ?author
     order by DESC(?count)
     *
     *
     * @return
     */
    public static String makeSparqlQueryForAuthorsFromKs_old () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
                "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
                "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "    SELECT ?a (COUNT (DISTINCT ?mention) as ?count)\n" +
                "WHERE {\n" +
                "\n" +
                "     ?attribution grasp:attributedTo ?mention .\n" +
                "     ?doc grasp:hasAttribution ?attribution .\n" +
                "     ?doc prov:wasAttributedTo ?a\n" +
                "\n" +
                "    }\n" +
                "group by ?a \n" +
                "order by DESC(?count)";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


   /* PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX grasp: <http://groundedannotationframework.org/grasp#>
    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>
    SELECT ?author (COUNT (DISTINCT ?mention) as ?count)
    WHERE {
    ?attribution grasp:isAttributionFor ?mention .
                ?attribution prov:wasDerivedFrom ?doc .
                ?doc prov:wasAttributedTo ?author

    }
    group by ?author
    order by DESC(?count)*/
   public static String makeSparqlQueryForAuthorsFromKsType () {
       String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
               "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
               "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
               "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
               "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
               "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
               "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
               "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
               "    SELECT ?a ?type (COUNT (DISTINCT ?mention) as ?count)\n" +
               "WHERE {\n" +
               "\n" +
               "OPTIONAL {?a a ?type . }\n" +

               "                 ?attribution grasp:isAttributionFor ?mention .\n" +
              // "                ?attribution prov:wasAttributedTo ?doc .\n" +
               "                ?attribution prov:wasDerivedFrom ?doc .\n" +
               "                ?doc prov:wasAttributedTo ?a\n" +
               "\n" +
               "    }\n" +
               "group by ?a ?type \n" +
               "order by DESC(?count)";
       //System.out.println("sparqQuery = " + sparqQuery);
       return sparqQuery;
   }

   public static String makeSparqlQueryForAuthorsFromKs () {
       String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
               "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
               "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
               "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
               "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
               "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
               "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
               "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
               "    SELECT ?a (COUNT (DISTINCT ?mention) as ?count)\n" +
               "WHERE {\n" +
               "\n" +
              "                 ?attribution grasp:isAttributionFor ?mention .\n" +
              // "                ?attribution prov:wasAttributedTo ?doc .\n" +
               "                ?attribution prov:wasDerivedFrom ?doc .\n" +
               "                ?doc prov:wasAttributedTo ?a\n" +
               "\n" +
               "    }\n" +
               "group by ?a \n" +
               "order by DESC(?count)";
       //System.out.println("sparqQuery = " + sparqQuery);
       return sparqQuery;
   }



    /*
    PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
     PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
     PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
     PREFIX grasp: <http://groundedannotationframework.org/grasp#>
     PREFIX gaf:   <http://groundedannotationframework.org/gaf#>
     SELECT ?value (COUNT (DISTINCT ?mention) as ?count)
     WHERE {
     ?attribution grasp:isAttributionFor ?mention .
     ?attribution rdf:value ?value

     }
     group by ?value
     order by DESC(?count)
     */

    public static String makeSparqlQueryForAttributionValuesFromKs () {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n" +
                "    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "    PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "    PREFIX nwrcite: <http://www.newsreader-project.eu/provenance/cited/> \n" +
                "    PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "    PREFIX gaf:   <http://groundedannotationframework.org/gaf#>\n" +
                "    SELECT ?value (COUNT (DISTINCT ?mention) as ?count)\n" +
                "     WHERE {\n" +
                "     ?attribution grasp:isAttributionFor ?mention .\n" +
                "     ?attribution rdf:value ?value\n" +
                "     }\n" +
                "     group by ?value\n"+
                "order by DESC(?count)";
       //  System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


    ////// Next queries are used to get the concepts and counts from the KS for the VUbot
    /**
     * Gives light entity data for Robot
     * @return
     */
    public static String makeSparqlQueryForLightEntityProjectFromKs(String project, String mcount) {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
        "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?a ?label ?type (COUNT (DISTINCT ?m) as ?mcount) (COUNT (DISTINCT ?doc) as ?dcount)\n" +
                "WHERE {\n" +
                "?e sem:hasActor ?a.\n" +
                "?e gaf:denotedBy ?m .\n" +
                "?a rdfs:label ?label .\n" +
                "?attribution grasp:isAttributionFor ?m .\n" +
                "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
                "?doc rdfs:comment "+project+" .\n" +
                "OPTIONAL {?a a ?type . }\n" +
                "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
                "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
                "}\n" +
                "group by ?a ?label ?type\n" +
                "HAVING (COUNT (DISTINCT ?m) > "+mcount+")\n" +
                "order by DESC(?mcount)";
        //System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


    public static String makeSparqlQueryForDarkEntityProjectFromKs(String project, String mcount) {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?a ?label ?type (COUNT (DISTINCT ?m) as ?mcount) (COUNT (DISTINCT ?doc) as ?dcount)\n" +
                "WHERE {\n" +
                "?e sem:hasActor ?a.\n" +
                "?e gaf:denotedBy ?m .\n" +
                "?a rdfs:label ?label .\n" +
                "?attribution grasp:isAttributionFor ?m .\n" +
                "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
                "?doc rdfs:comment "+project+" .\n" +
                "OPTIONAL {?a a ?type . }\n" +
                "FILTER (CONTAINS(STR(?a), \"/entities/\"))\n" +
                "}\n" +
                "group by ?a ?label ?type\n" +
                "HAVING (COUNT (DISTINCT ?m) > "+mcount+")\n" +
                "order by DESC(?mcount)";
        // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


    public static String makeSparqlQueryForNonEntityProjectFromKs(String project, String mcount) {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?a ?label ?type (COUNT (DISTINCT ?m) as ?mcount) (COUNT (DISTINCT ?doc) as ?dcount)\n" +
                "WHERE {\n" +
                "?e sem:hasActor ?a.\n" +
                "?e gaf:denotedBy ?m .\n" +
                "?a rdfs:label ?label .\n" +
                "?attribution grasp:isAttributionFor ?m .\n" +
                "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
                "?doc rdfs:comment "+project+" .\n" +
                "OPTIONAL {?a skos:relatedMatch ?type . }\n" +
                "FILTER (CONTAINS(STR(?a), \"/non-entities/\"))\n" +
                "}\n" +
                "group by ?a ?label ?type\n" +
                "HAVING (COUNT (DISTINCT ?m) > "+mcount+")\n" +
                "order by DESC(?mcount)";
        // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }


    public static String makeSparqlQueryForLightEntityProjectFromKsOrg(String project) {
        String sparqQuery = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
                "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
                "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
                "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
                "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?label ?project ?doc ?a ?m ?type \n" +
                "WHERE {\n" +

                "?e sem:hasActor ?a.\n" +
                "?e gaf:denotedBy ?m ." +
                //"?a gaf:denotedBy ?m .\n" +
                "?attribution grasp:isAttributionFor ?m .\n" +
                "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
                "bind( URI(strafter( str(?m), \"#\" )) as ?offset ).\n" +
                "?doc rdfs:comment "+project+" .\n" +
                "?a rdfs:label ?label .\n" +
                "OPTIONAL {?a a ?type . }\n" +
                "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
                "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
                "}";
       // System.out.println("sparqQuery = " + sparqQuery);
        return sparqQuery;
    }

    static String s0 = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?label ?project ?doc ?a ?m ?type \n" +
            "WHERE {\n" +
            "?a gaf:denotedBy ?m .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "bind( URI(strafter( str(?m), \"#\" )) as ?offset ).\n" +
            "?doc rdfs:comment ?project .\n" +
            "?a rdfs:label ?label .\n" +
            "OPTIONAL {?a a ?type . }\n" +
            "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
            "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
            "}\n" +
            "group by ?label ?doc ?a ?type ?project ?m";

    static String s1 = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?label ?project ?doc ?a (COUNT (DISTINCT ?m) as ?count) ?type \n" +
            "WHERE {\n" +
            "?a gaf:denotedBy ?m .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            " bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "?doc rdfs:comment ?project .\n" +
            "?a rdfs:label ?label .\n" +
            "OPTIONAL {?a a ?type . }\n" +
            "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
            "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
            "}\n" +
            "group by ?label ?doc ?a ?type ?project\n" +
            "order by DESC(?count)";

    static String s2 = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?label ?project ?doc ?a ?m ?type \n" +
            "WHERE {\n" +
            "?a gaf:denotedBy ?m .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "bind( URI(strafter( str(?m), \"#\" )) as ?offset ).\n" +
            "?doc rdfs:comment ?project .\n" +
            "?a rdfs:label ?label .\n" +
            "OPTIONAL {?a a ?type . }\n" +
            "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
            "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
            "}";

    static String s3 = "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?label ?project ?doc ?a ?m ?type \n" +
            "WHERE {\n" +
            "?a gaf:denotedBy ?m .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "bind( URI(strafter( str(?m), \"#\" )) as ?offset ).\n" +
            "?doc rdfs:comment ?project .\n" +
            "?a rdfs:label ?label .\n" +
            "OPTIONAL {?a a ?type . }\n" +
            "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
            "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
            "}";

    static String s4 = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
            "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?a ?label ?type (COUNT (DISTINCT ?m) as ?mcount) (COUNT (DISTINCT ?doc) as ?dcount)\n" +
            "WHERE {\n" +
            "?e sem:hasActor ?a.\n" +
            "?a gaf:denotedBy ?m .\n" +
            "?a rdfs:label ?label .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "?doc rdfs:comment <http://www.newsreader-project.eu/project/London> .\n" +
            "OPTIONAL {?a a ?type . }\n" +
            "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
            "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
            "}\n" +
            "group by ?a ?label ?type\n" +
            "order by DESC(?mcount)";

    static String s5 = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
            "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?project ?a ?type (COUNT (DISTINCT ?m) as ?mcount) (COUNT (DISTINCT ?doc) as ?dcount)\n" +
            "WHERE {\n" +
            "?a gaf:denotedBy ?m .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "?doc rdfs:comment ?project .\n" +
            "OPTIONAL {?a a ?type . }\n" +
            "FILTER (CONTAINS(STR(?a), \"dbpedia\"))\n" +
            "FILTER (CONTAINS(STR(?type), \"dbpedia\"))\n" +
            "}\n" +
            "group by ?a ?type ?project\n" +
            "order by DESC(?mcount)";

    static String s6 = "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> \n" +
            "PREFIX gaf: <http://groundedannotationframework.org/gaf#>\n" +
            "PREFIX prov:  <http://www.w3.org/ns/prov#>\n" +
            "PREFIX nwrauthor: <http://www.newsreader-project.eu/provenance/author/> \n" +
            "PREFIX grasp: <http://groundedannotationframework.org/grasp#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?a ?project (COUNT (DISTINCT ?m) as ?mcount) (COUNT (DISTINCT ?doc) as ?dcount)\n" +
            "WHERE {\n" +
            "?e sem:hasActor ?a. \n" +
            "?a gaf:denotedBy ?m .\n" +
            "?attribution grasp:isAttributionFor ?m .\n" +
            "bind( URI(strbefore( str(?m), \"#\" )) as ?doc ).\n" +
            "?doc rdfs:comment ?project .\n" +
            "}\n" +
            "group by ?a ?project\n" +
            "order by DESC(?mcount)\n";

    static public void main (String [] args) {
        //System.out.println(s6);
        System.out.println(makeSparqlQueryForLightEntityProjectFromKs("test", "nr"));
        //System.out.println(SparqlGenerator.makeSparqlQueryForLightEntityProjectFromKs("<http://www.newsreader-project.eu/project/London>", "5"));
        //System.out.println(SparqlGenerator.makeSparqlQueryForCitedSourcesFromKs());
        //System.out.println(SparqlGenerator.makeSparqlQueryForAuthorsFromKs());
        //System.out.println( SparqlGenerator.makeSparqlQueryForLightEntityProjectFromKs("<http://www.newsreader-project.eu/project/London>"));
       // System.out.println(SparqlGenerator.s6);
    }

}
