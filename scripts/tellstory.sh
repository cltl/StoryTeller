#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git

SERVER="http://130.37.53.35:50053/"
LIMIT="100"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

# clean-up old json files
rm *.json
# Search for two words used as labels for entities or events
# Now takes INTERSECT but needs to be changed to UNION
QUERY="--word virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > word.virusORdisease.json

# '*' marks substring search
QUERY="--word *virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > word.virusORdiseaseSubstring.json

# events in which entities with UNION of two labels participates
QUERY="--entityPhrase virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityPhrase.virusOrdisease.json

# '*' marks substring search
QUERY="--entityPhrase *virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityPhrase.virusOrdiseaseSubstring.json

# events in which an entity instance of the type dbp:Company participates
#QUERY="--entityType dbp:Person;dbp:Company"
QUERY="--entityType http://dbpedia.org/ontology/Scientist"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityType.Scientist.json

# events in which an entity instance participates
#QUERY="--entityInstance dbpedia:Pakistan;dbpedia:India"
#QUERY="--entityInstance http://dbpedia.org/resource/Mikhail_Chumakov"
QUERY="--entityInstance http://dbpedia.org/resource/Edward_Jenner"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityInstance.Edward_Jenner.json

# events in which a dark entity participates
QUERY="--darkEntityInstance http://www.newsreader-project.eu/data/vaccinations/entities/Quinvaxem"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > darkEntityInstance.Quinvaxem.json

# events in which a concept participates
QUERY="--conceptInstance http://www.newsreader-project.eu/data/vaccinations/non-entities/influenza"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > conceptInstance.json

# events in which a concept participates
QUERY="--conceptInstance http://dbpedia.org/resource/Measles"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > conceptInstanceResource.json

# events in which a concept Type  participates
QUERY="--conceptType http://dbpedia.org/ontology/Species"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityConceptInstance.json


# UNION of  various entities: phrases, instances and types
QUERY="--entityPhrase virus;disease --entityType dbp:Person;dbp:Company --entityInstance dbpedia:Pakistan;dbpedia:India"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityVarious.OR.json

# events with the UNION of the two labels
QUERY="--eventPhrase kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventPhrase.killOrdie.json

# events with the UNION of the two labels as a substring
QUERY="--eventPhrase *kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventPhrase.killOrdieSubstring.json

# events that belong to the UNION of he two types
QUERY="--eventType eso:Decreasing;eso:Increasing"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventType.DecreasingOrIncreasing.json

# UNION of various events: phrases and types
QUERY="--eventType eso:Decreasing;eso:Increasing --eventPhrase kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventVarious.OR.json

# MULTIPLE CONSTRAINTS of events and participants apply simultaneously, each constraints is a UNION
QUERY="--entityType dbp:Person;dbp:Company --entityInstance dbpedia:Pakistan;dbpedia:India --eventType eso:Decreasing;eso:Increasing --entityPhrase virus;disease --eventPhrase kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventType.mixedEntitiesEvents.json

# Events from documents with UNION of topics
#QUERY="--topic http://eurovoc.europa.eu/1854;http://eurovoc.europa.eu/2560"
QUERY="--topicType http://eurovoc.europa.eu/1854;http://eurovoc.europa.eu/2560"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > topic.json

# Events attributed to UNION of authors
QUERY="--authorPhrase us-measles-disneyland;Jen_Kirby"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > authorPhrase.OR.json

# Events attributed to UNION of cited sources
QUERY="--citePhrase vanPanhuis;EricHandler"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > citePhrase.OR.json

# Events attributed to UNION of authorInstances
QUERY="--agentInstance http://www.newsreader-project.eu/provenance/author/PBS%2CJenny_Marder;http://www.newsreader-project.eu/provenance/author/Reuters%2CLisa_Rapaport;"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > authorInstance.OR.json

# Events attributed to UNION of citeInstances
QUERY=" --citeInstance http://dbpedia.org/resource/Michael_Kirby_(judge);http://dbpedia.org/resource/Edward_Jenner;"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > citeInstance.json

# Events attributed to type of author sources
QUERY="--authorType http://dbpedia.org/ontology/Agent;"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > authorType.json

# Events attributed to type of cited sources
QUERY="--citeType http://dbpedia.org/ontology/Scientist;"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > citeType.json

# Events on which someone has a positive or negative sentiment
QUERY="--grasp negative;positive"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.negativeORpositive.json

# Events with a FUTURE time perspective of the source
QUERY="--grasp FUTURE"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.time.future.json

# Events with a PAST time perspective of the source
QUERY="--grasp PAST"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.time.past.json

# Events with an uncertain perspective from the source
QUERY="--grasp UNCERTAIN"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.uncertain.json

# Events with an denied perspective from the source
QUERY="--grasp NEG"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.denial.json

# Events with any non-default perspective: UNION of values
QUERY="--grasp negative;positive;UNCERTAIN;FUTURE;RECENT;PAST;NEG"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.non-default.json

# Events with non-default perspective with certain participants and of certain event types
#QUERY="--grasp negative;positive;UNCERTAIN;FUTURE;PAST;NEG --entityPhrase health --eventType eso:Decreasing;eso:Increasing"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.entity.event.perspective.json
