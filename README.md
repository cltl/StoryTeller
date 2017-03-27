StoryTeller
===========
version 1.0  
Copyright: VU University Amsterdam, Piek Vossen  
email: piek.vossen@vu.nl  
website: cltl.nl  

## SOURCE CODE:
https://github.com/cltl/StoryTeller

## AUTOMATED INSTALLATION of the complete query building and visualization suite
(please make sure to install docker and docker-compose https://www.docker.com/)

1. docker volume create --name=data
2. docker-compose up

## AUTOMATED INSTALLATION of this component only
*Please Note that this will also install all necessary dependencies in the docker container*  
```bash
    docker build -t nlescstoryteller/storyteller
```

## MANUAL INSTALLATION of this component only
*If the steps below do not suffice, please refer to the Dockerfile for more information on dependencies etc.*  
1. git clone https://github.com/cltl/StoryTeller
2. cd StoryTeller
3. chmod +wrx install.sh
4. run the install.sh script

The install.sh will build the binary through apache-maven-2.2.1 and the pom.xml and move it to the "lib" folder.

### REQUIREMENTS
StoryTeller is developed in Java 1.6 and can run on any platform that supports Java 1.6
The code includes a JENA library for excecuting SPARQL and reading the results. Running JENA requires
the jena-log4j.properties file to be present from the location that the code is excecuted.
This file is included in the scripts folder of this distribution.

Some of the functions require resources that need to be cloned from

    git clone https://github.com/cltl/vua-resources.git

The download of these resources is included in the install.sh script. The scripts assume these resources to be present
next to the installation of the StoryTeller. If not, you need to adapt the paths in the scripts.

## Connect to the docker container for troubleshooting
The user may wish to connect to the running docker components for troubleshooting purposes. The following is a listing of possible commands to connect to the various components.
**linux**
```bash
    sudo docker exec -v data:/data -it nlescstoryteller/storyteller /bin/bash
    sudo docker exec -v data:/data -it nlescstoryteller/query-builder-preprocessing /bin/bash
    sudo docker exec -v data:/data -it nlescstoryteller/query-builder-server /bin/bash
    sudo docker exec -v data:/data -it nlescstoryteller/query-builder-daemon /bin/bash    
    sudo docker exec -it nlescstoryteller/query-builder-client /bin/bash
    sudo docker exec -it nlescstoryteller/uncertainty-visualization /bin/bash
```

**windows**
```bash
    winpty docker exec -v data:/data -it nlescstoryteller/storyteller //bin/bash
    winpty docker exec -v data:/data -it nlescstoryteller/query-builder-preprocessing //bin/bash
    winpty docker exec -v data:/data -it nlescstoryteller/query-builder-server //bin/bash
    winpty docker exec -v data:/data -it nlescstoryteller/query-builder-daemon //bin/bash    
    winpty docker exec -it nlescstoryteller/query-builder-client //bin/bash
    winpty docker exec -it nlescstoryteller/uncertainty-visualization //bin/bash    
```

# LICENSE
    StoryTeller is free software: you can redistribute it and/or modify
    it under the terms of the The Apache License, Version 2.0:
        http://www.apache.org/licenses/LICENSE-2.0.txt.

    StoryTeller is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    License for more details.


# DESCRIPTION

Toolkit to query the NewsReader KnowledgeStore with SPARQL and create a JSON story.
The NewsReader KnowledgeStore contains event-centric knowledge graphs (ECKGs) according to the
Simple Event Model (SEM, Verhage et al 2011) and the Grounded Annotation and Source Perspective model
(GRaSP, Vossen et al 2016). ECKGs represent instances of events with their participants and their anchoring in time.
Instance representations of events and participants is done using URIs and a whole range of properties is expressed for these
instances, among which: the labels used to make reference, semantic types of which they are instances,
the offsets where they are mentioned in the text, the semantic roles between participants and events,
the topic of the document in which the event is mentioned, etc.
The event data can be queried through the instance level (participants), the phrase level and the type level,
where you can combine constraints on the events and the participants.

For each event mention, there is a representation of the source to which the mentioning of the event is attributed and
the perspective of the source to the event. The source can be the author or somebody cited. The perspective reflects
the sentiment, the denial/confirmation, the certainty, the time perspective. Likewise you can query for events
mentioned by specific sources and/or on which a certain perspective is expressed.

The JSON story that is returned for a query consists of a list of ECKGs, where each ECKG has the following structure (see also Zwaan et al 2016):
```javascript
 {  "actors": {"actor:": [
        "co:virus",
        "co:expert"
    ]},
    "climax": 93,
    "group": "100:[\"expose\"]",
    "groupName": "[\"expose\"]",
    "groupScore": "100",
    "instance": "http://web.archive.org/web/20160511215309/http://www.cdc.gov:80/flu/protect/keyfacts.htm#ev166",
    "labels": ["pick"],
    "mentions": [{
        "char": [
            "6377",
            "6381"
        ],
        "perspective": [
            {
                "attribution": {
                    "belief": "confirm",
                    "certainty": "certain",
                    "possibility": "likely",
                    "sentiment": "neutral",
                    "when": "recent"
                },
                "source": "author:Centers_for_Disease_Control"
            },
            {
                "attribution": {
                    "belief": "confirm",
                    "certainty": "certain",
                    "possibility": "likely",
                    "sentiment": "neutral",
                    "when": "recent"
                },
                "source": "author:Prevention?CDC"
            }
        ],
        "snippet": ["Experts must pick which viruses to include in the vaccine many months in advance in order for vaccine to be produced and delivered on time. ( For more information about the vaccine virus selection process visit Selecting the Viruses in the Influenza ( Flu ) Vaccine. )"],
        "snippet_char": [
            13,
            17
        ],
        "uri": "http://web.archive.org/web/20160511215309/http://www.cdc.gov:80/flu/protect/keyfacts.htm"
    }],
    "prefLabel": ["pick"],
    "sentence": "6377",
    "time": "20160504"
},
```

The stories consists of groups of ECKGs identified by the groupName, where each ECKG has a climax score that indicates how prominent the
event is in the group. Groups have scores based on the highest climax score in the group.

The Toolkit supports the following global functions:

1. Creating SPARQL queries for the NewsReader KnowledgeStore
2. Querying the NewsReader KnowledgeStore to obtain the event data
3. Creating a JSON structure with events from the trigTripleData with storyline groupings
4. Adding perspective values to the event data
5. Adding text snippets to the event data
6. Outputting
7. Creating a JSON tree structure for generating queries

We describe each function in more detail below.

1. Creating SPARQL queries for the NewsReader KnowledgeStore

Function:
    vu.cltl.storyteller.knowledgestore.KnowledgeStoreQueryApi.createSparqlQuery(String [] args);
Arguments:
    String [] with the search parameters. The following query arguments are accepted:
     --entityPhrase virus;disease
       #entity has the label virus OR disease
     --entityPhrase *virus;disease
        #entity has the substring virus OR disease
     --entityInstance dbpedia:Pakistan;dbpedia:India
        #entity is of the instance dbpedia.resource/Pakistan OR dbped.resource/India
     --entityType dbp:Company;dbp:Person
        #entity is of the the type dbpedia.ontology/Compnay OR dbpedia.ontology/Person
     --eventPhrase kill;die
        #event has the label kill OR die
     --eventPhrase *kill;die
        #event has the substring kill OR die
     --eventType eso:Increasing;eso:Decreasing
        #event is of the type eso:Increasing OR eso:Decreasing
     --topic http://eurovoc.europa.eu/219382;http://eurovoc.europa.eu/215505
        #events are mentioned in document wit the topic http://eurovoc.europa.eu/219382 OR http://eurovoc.europa.eu/215505
     --authorPhrase us-measles-disneyland;Jen_Kirby
        #events are mentioned by the us-measles-disneyland OR Jen_Kirby
     --citePhrase vanPanhuis;EricHandler
        #events are mentioned by cited sources vanPanhuis OR EricHandler
     --grasp negative;positive"
        #events on which the source has a negative OR positive opinion
     --grasp FUTURE;RECENT;PAST"
        #events that the source places in the PAST, RECENT OR FUTURE
     --grasp UNCERTAIN"
        #events about which the source is UNCERTAIN
     --grasp DENIAL"
        #events that a source denies

    The OR options results in the UNION of the results. Combining more than one constraint results in the events that satisfy all the constraints.
    Search for --eventType eso:Increasing --entityInstance dbpedia:Pakistan
    results in events of the type eso:Increasing in which the entity dbpedia:Pakistan is involved.
Result:
    String: Sparql query for event structures

    In addition to the query parameters there are a number of general parameters that need or can be set:

     --ks-service     http address of the KnowledgeStore service
     --ks-user        (optional) if the KnowledgeStore is user protected a username is required
     --ks-passw       (optional) if the KnowledgeStore is user protected a psswords is required
     --ks-limit       (optional) limits the number of events returned by the KnowledgeStore.
                      The default value is set to 500 events
     --token-index    (optional) path to the NafTokenIndex file (gzipped) that is needed to create text snippets for the results
                      Without the token-index, the KnowledgeStore is queried, which is much slower
     --eurovoc        (optional) Path to the EuroVoc topic label file. This is needed to provide readable labels for topic identifiers
     --eurovoc-blacklist (optinal) Path to a text file that provides topics that should be ignored to make storyline groupings
     --log            (optional) switch to turn on logging. If ommitted there is no logging of the queries.
                      If specified some logging of the querying is done

     The main function carries out the complete search and conversion and returs a JSON stream as a result.
     The usage of this class is demonstrated in the shell script tellstory.sh with a variety of queries.


2. Querying the NewsReader KnowledgeStore to obtain the event data

Function:
    vu.cltl.storyteller.knowledgestore.GetTriplesFromKnowledgeStore.readTriplesFromKs(sparqlQuery,trigTripleData);
Arguments:
    String Sparql query for event structures
    TrigTripleData trigTripleData
        # Data object in memory to store the triple data
Result:
    trigTripleData is filled with the event data returned from the KnowledgeStore


3. Creating a JSON structure with events from the trigTripleData with storyline groupings

     Given the RDF triples in trigTripleData it creates an ArrayList with JSON objects in which each object
     represents an EventCentricKnowledgeGraph and stories consists of events with the same group name.
     Groupings are based on sharing of topics and participants across events. The topicThreshold determines
     the proportion of overlap the coarseness of the groupings. High thresholds results in many different small
     groupings with high topic sharing, whereas a low threshold results in few groups with low overlap.

     The climaxThreshold excludes events that are not salient or prominent enough.
     The euroVoc data is used to label the stories with topic labels.
     The euroVocBlacklist is used to exclude topics from making groupings

Function
     vu.cltl.storyteller.json.JsonMakeStoryFromTripleData.makeUpStory
Arguments
     trigTripleData             triples obtained from querying the KnowledgeStore
     climaxThreshold            climax threhold to exclude events with less prominance
     topicThreshold             topic threshold to determine granularity of groups
     euroVoc                    topic label map to rename groupings
     euroVocBlackList           topics excluded from groupings
Result
    ArrayList<JSONObject>      List of JSONObjects representing event data


4. Adding perspective values to the mentions of the event data

Function
    vu.cltl.storyteller.json.JsonMakeStoryFromTripleData.addPerspectiveToStory
Arguments
   ArrayList<JSONObject> jsonObjects   list of the event data structures
Return
    void                JSONObjects have been enriched with a perspective layer on the mentions


5. Adding text snippets to the event data

The text snippets include the sentence with the event, a preceding sentence (if any)
and a following sentence (if any). The offsets of the event token are given as well for highlighting the event. Note that the data structure also
includes the document URL with an offset. This is the offset of the event in the raw text of the document. Due to white spaces, the latter offset may
not work properly.

There are two functions to add text snippets to the event data. The first function calls the KnowledgeStore to obtain a compressed NAF file
for each set of results from the same document. It then takes the token layer from the NAF file to reconstruct the snippet. The second method
reads a NAF token index file in memory with every query and reconstructs the snippet from it. The latter method is much faster but less dynamic.
If new document are added to the KnowledgeStore, the NAF token index file needs to be extended as well.

Function
    vu.cltl.storyteller.json.JsonMakeStoryFromTripleData.addSnippetsToStoryFromKnowledgeStore
Parameters:
    ArrayList<JSONObject>       json Objects that represent the event datat to which the snippets are added
     --ks-service               ip address with the port of the KnowledgeStore service
     --ks-user                  (optional) if the KnowledgeStore is user protected a username is required
     --ks-passw                 (optional) if the KnowledgeStore is user protected a psswords is required

Return
    void                        JSONObjects have been enriched

Function
    vu.cltl.storyteller.json.JsonMakeStoryFromTripleData.addSnippetsToStoryFromIndexFile
Parameters
    ArrayList<JSONObject>       json Objects that represent the event datat to which the snippets are added
    String                      pathToTokenIndex from which the snippets are obtained
Return
    void                        JSONObjects have been enriched


To build a NafTokenLayIndex for a collection of NAF files you should call the following function:

Function
    vu.cltl.storyteller.input.NafTokenLayerIndex
Parameters
    --folder                    path to the folder with the NAF files
    --extension                 extension of the NAF files e.g. ".naf"
    --project                   project name that was used to create the TRiG files to guarantee that     
Return
    gzip file with the token index in which the URI of a source is mapped to the token layer from NAF:
    
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <index>
    <text>
    <url><![CDATA[https://web.archive.org/web/20161107201934/https://www.yahoo.com/news/disney-measles-outbreak-sparked-vaccination-debate-ends-204440487.html?ref=gs]]></url>
    <wf id="w1" sent="1" length="6" offset="0"><![CDATA[Disney]]></wf>
    <wf id="w2" sent="1" length="7" offset="7"><![CDATA[measles]]></wf>
    <wf id="w3" sent="1" length="8" offset="15"><![CDATA[outbreak]]></wf>
    <wf id="w4" sent="1" length="4" offset="24"><![CDATA[that]]></wf>
    <wf id="w5" sent="1" length="7" offset="29"><![CDATA[sparked]]></wf>
    <wf id="w6" sent="1" length="11" offset="37"><![CDATA[vaccination]]></wf>
    </text>


6. Outputting

Writes the story structure as a stream in JSON format.

Function
    vu.cltl.storyteller.json.JsonMakeStoryFromTripleData.writeStory
Parameter
    ArrayList<JSONObject>       json Objects that represent the event data that are to be saved
Return
    JSON text stream representing the story data structure to be written as output stream

The tellstory.sh script shows the different query options that are supported through the API and the complete steps
from a query to a JSON result structure. You may need to set the proper address, user, and password for accessing a KnowledgStore installation.


7. Creating JSON tree data from the KnowledgeStore for generating queries to the KnowledgeStore.
The tree data show the statistics for different data types and the child-parent relations if relevant.
Supported types are:
 light-entities
 dark-entities
 concepts
 events
 topics
 authors
 cited
 perspectives

The function requires different resources for building up the hiearchies and labeling the nodes. These resources need to be installed separately from:

git clone https://github.com/cltl/vua-resources.git

Function
    vu.cltl.storyteller.json.JsonQueryHierarchy

The API is illustrated in the script getoverview.sh

Any update of the KnowledgeStore requires running the script again to update the statistics.

References:

P. Vossen, R. Agerri, I. Aldabe, A. Cybulska, M. van Erp, A. Fokkens, E. Laparra, A. Minard, A. P. Aprosio, G. Rigau, M. Rospocher, and R. Segers, “Newsreader: how semantic web helps natural language processing helps semantic web,” Special issue knowledge-based systems, elsevier, 2016. doi:http://dx.doi.org/10.1016/j.knosys.2016.07.013

M. Rospocher, M. van Erp, P. Vossen, A. Fokkens, I. Aldabe, G. Rigau, A. Soroa, T. Ploeger, and T. Bogaard, “Building event-centric knowledge graphs from news,” Journal of web semantics, 2016.

Zwaan van der J., M. van Meersbergen, A. Fokkens, S. ter Braake, I. Leemans, E. Kuijpers, P. Vossen, I. Maks, “Storyteller: visualizing perspectives in digital humanities projects,” in Proceedings of the 2nd ifip international workshop on computational history and data-driven humanities, Dublin, Ireland, Ireland, May 25, 2016.

Van Hage, W.R., Malaisé, V., Segers, R., Hollink, L., Schreiber, G.: Design and use of the simple event model (sem). Web Semant. Sci. Serv. Agent. World Wide Web 9(2), 128–136 (2011)


