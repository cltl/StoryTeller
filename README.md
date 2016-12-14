# StoryTeller
================
version 1.0
Copyright: VU University Amsterdam, Piek Vossen
email: piek.vossen@vu.nl
website: cltl.nl

SOURCE CODE:
https://github.com/cltl/StoryTeller

INSTALLATION
1. git clone https://github.com/cltl/StoryTeller
2. cd StoryTeller
3. chmod +wrx install.sh
4. run the install.sh script

The install.sh will build the binary through apache-maven-2.2.1 and the pom.xml and move it to the "lib" folder.

REQUIREMENTS
StoryTeller is developed in Java 1.6 and can run on any platform that supports Java 1.6

LICENSE
    StoryTeller is free software: you can redistribute it and/or modify
    it under the terms of the The Apache License, Version 2.0:
        http://www.apache.org/licenses/LICENSE-2.0.txt.

    EventCoreference is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.


DESCRIPTION

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

The stories consists of groups of ECKGs identified by the groupName, where each ECKG has a climax score that indicates how prominent the
event is in the group. Groups have scores based on the highest climax score in the group.

The Toolkit supports the following global functions:

1. Creating SPARQL queries for the NewsReader KnowledgeStore
2. Querying the NewsReader KnowledgeStore to obtain the event data
3. Adding perspective values to the event data
4. Adding text snippets to the event data
5. Outputting
6. Creating a JSON tree structure for generating queries

We describe each function in more detail below.

1. Creating SPARQL queries for the NewsReader KnowledgeStore

Function:
    KnowledgeStoreQueryApi.createSparqlQuery(String [] args);
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


     * The main function needs a number of parameters to run:
     * --ks-service     http address of the KnowledgeStore service
     * --ks-user        (optional) if the KnowledgeStore is user protected a username is required
     * --ks-passw       (optional) if the KnowledgeStore is user protected a psswords is required
     * --ks-limit       (optional) limits the number of events returned by the KnowledgeStore.
     *                  The default value is set to 500 events
     * --token-index    (optional) path to the NafTokenIndex file (gzipped) that is needed to create text snippets for the results
     *                  Without the token-index, the KnowledgeStore is queried, which is much slower
     * --eurovoc        (optional) Path to the EuroVoc topic label file. This is needed to provide readable labels for topic identifiers
     * --eurovoc-blacklist (optinal) Path to a text file that provides topics that should be ignored to make storyline groupings
     * --log            (optional) switch to turn on logging. If ommitted there is no logging of the queries.
     *                  If specified some logging of the querying is done
     *
     * A number of query types that can be combined
     * --entityPhrase
     * --entityInstance
     * --entityType
     * --eventPhrase
     * --eventType
     * --topic
     * --grasp
     *
     * @param args
     *
     * The main function carries out the complete search and conversion and returs a JSON stream as a result.
     * The usage of this class is demonstrated in the shell script tellstory.sh with a variety of queries.
     */


2. Querying the NewsReader KnowledgeStore to obtain the event data

Function:
    GetTriplesFromKnowledgeStore.readTriplesFromKs(sparqlQuery,trigTripleData);
Arguments:
    String Sparql query for event structures
    TrigTripleData trigTripleData
        # Data object in memory to store the triple data
Result:
    trigTripleData is filled with the event data returned from the KnowledgeStore

4. Adding perspective values to the event data
5. Adding text snippets to the event data
6. Outputting

The tellstory.sh script shows the different query options that are supported through the API.
Additional parameters to run the tellstory.sh:


References:

P. Vossen, R. Agerri, I. Aldabe, A. Cybulska, M. van Erp, A. Fokkens, E. Laparra, A. Minard, A. P. Aprosio, G. Rigau, M. Rospocher, and R. Segers, “Newsreader: how semantic web helps natural language processing helps semantic web,” Special issue knowledge-based systems, elsevier, 2016. doi:http://dx.doi.org/10.1016/j.knosys.2016.07.013

M. Rospocher, M. van Erp, P. Vossen, A. Fokkens, I. Aldabe, G. Rigau, A. Soroa, T. Ploeger, and T. Bogaard, “Building event-centric knowledge graphs from news,” Journal of web semantics, 2016.

Zwaan van der J., M. van Meersbergen, A. Fokkens, S. ter Braake, I. Leemans, E. Kuijpers, P. Vossen, I. Maks, “Storyteller: visualizing perspectives in digital humanities projects,” in Proceedings of the 2nd ifip international workshop on computational history and data-driven humanities, Dublin, Ireland, Ireland, May 25, 2016.

Van Hage, W.R., Malaisé, V., Segers, R., Hollink, L., Schreiber, G.: Design and use of the simple event model (sem). Web Semant. Sci. Serv. Agent. World Wide Web 9(2), 128–136 (2011)
