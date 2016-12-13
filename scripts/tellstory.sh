#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git

SERVER="http://145.100.58.139:50053"
LIMIT="100"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

# Search for two words used as labels for entities or events
# Now takes INTERSECT but needs to be changed to UNION
QUERY="--word virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > word.virusORdisease.json

# '*' marks substring search
QUERY="--word *virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > word.virusORdiseaseSubstring.json

# events in which entities with UNION of two labels participates
QUERY="--entityPhrase virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityPhrase.virusOrdisease.json

# '*' marks substring search
QUERY="--entityPhrase *virus;disease"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityPhrase.virusOrdiseaseSubstring.json

# events in which an entity instance of the type dbp:Company participates
QUERY="--entityType dbp:Person;dbp:Company"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityType.PersonOrCompany.json

# events in which an entity instance participates
QUERY="--entityInstance dbpedia:Pakistan;dbpedia:India"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityInstance.PakistanOrIndia.json

# UNION of  various entities: phrases, instances and types
QUERY="--entityPhrase virus;disease --entityType dbp:Person;dbp:Company --entityInstance dbpedia:Pakistan;dbpedia:India"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityVarious.OR.json

# events with the UNION of the two labels
QUERY="--eventPhrase kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventPhrase.killOrdie.json

# events with the UNION of the two labels as a substring
QUERY="--eventPhrase *kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventPhrase.killOrdieSubstring.json

# events that belong to the UNION of he two types
QUERY="--eventType eso:Decreasing;eso:Increasing"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventType.DecreasingOrIncreasing.json

# UNION of various events: phrases and types
QUERY="--eventType eso:Decreasing;eso:Increasing --eventPhrase kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventVarious.OR.json

# MULTIPLE CONSTRAINTS of events and participants apply simultaneously, each constraints is a UNION
QUERY="--entityType dbp:Person;dbp:Company --entityInstance dbpedia:Pakistan;dbpedia:India --eventType eso:Decreasing;eso:Increasing --entityPhrase virus;disease --eventPhrase kill;die"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventType.mixedEntitiesEvents.json

# Events from documents with UNION of topics
QUERY="--topic http://eurovoc.europa.eu/219382;http://eurovoc.europa.eu/215505"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > topic.json

# Events attributed to UNION of authors
QUERY="--authorPhrase us-measles-disneyland;Jen_Kirby"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > author.OR.json

# Events attributed to UNION of cited sources
QUERY="--citePhrase vanPanhuis;EricHandler"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > cite.OR.json

# Events on which someone has a positive or negative sentiment
QUERY="--grasp negative;positive"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.negativeORpositive.json

# Events with a time perspective of the source
QUERY="--grasp FUTURE;RECENT;PAST"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.time.json

# Events with an uncertain perspective from the source
QUERY="--grasp UNCERTAIN"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.uncertain.json

# Events with an denied perspective from the source
QUERY="--grasp DENIAL"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.denial.json

# Events with any non-default perspective: UNION of values
QUERY="--grasp negative;positive;UNCERTAIN;FUTURE;RECENT;PAST;DENIAL"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.json

# Events with non-default perspective with certain participants and of certain event types
QUERY="--grasp negative;positive;UNCERTAIN;FUTURE;RECENT;PAST;DENIAL --entityPhrase disease --eventType eso:Decreasing;eso:Increasing"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.entity.event.json
