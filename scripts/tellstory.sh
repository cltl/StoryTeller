#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
RESOURCES="/Code/vu/newsreader/vua-resources"
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git
#QUERY="--entityPhrase bank --entityType dbp:Bank --entityInstance dbpedia:Rabo --eventPhrase kill --eventType eso:Killing --topic eurovoc:16789 --grasp POSITIVE --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz"
SERVER="http://145.100.58.139:50053"
LIMIT="100"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

QUERY="--word virus;disease"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > word.virusORdisease.json

# '*' marks substring search
QUERY="--word *virus;disease"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > word.virusORdiseaseSubstring.json

QUERY="--entityPhrase virus;disease"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityPhrase.virusOrdisease.json

# '*' marks substring search
QUERY="--entityPhrase *virus;disease"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityPhrase.virusOrdiseaseSubstring.json

QUERY="--entityType dbp:Person;dbp:Company"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityType.PersonOrCompany.json

QUERY="--entityInstance dbpedia:Pakistan;dbpedia:India"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityInstance.PakistanOrIndia.json

# UNION of  various entities: phrases, instances and types
QUERY="--entityPhrase virus;disease --entityType dbp:Person;dbp:Company --entityInstance dbpedia:Pakistan;dbpedia:India"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > entityVarious.OR.json

QUERY="--eventPhrase kill;die"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventPhrase.killOrdie.json

QUERY="--eventPhrase *kill;die"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventPhrase.killOrdieSubstring.json

QUERY="--eventType eso:Decreasing;eso:Increasing"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventType.DecreasingOrIncreasing.json

# UNION of various events: phrases and types
QUERY="--eventType eso:Decreasing;eso:Increasing --eventPhrase kill;die"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventVarious.OR.json

QUERY="--entityType dbp:Person;dbp:Company --entityInstance dbpedia:Pakistan;dbpedia:India --eventType eso:Decreasing;eso:Increasing --entityPhrase virus;disease --eventPhrase kill;die"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > eventType.mixedEntitiesEvents.json

QUERY="--topic http://eurovoc.europa.eu/219382;http://eurovoc.europa.eu/215505"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > topic.json

QUERY="--authorPhrase us-measles-disneyland;Jen_Kirby"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > author.OR.json

QUERY="--citePhrase vanPanhuis;EricHandler"
#java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > cite.OR.json

QUERY="--grasp NEGATIVE;POSITIVE"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.negativeORpositive.json

QUERY="--grasp FUTURE;RECENT;PAST"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.time.json

QUERY="--grasp UNCERTAIN"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.uncertain.json

QUERY="--grasp DENIAL"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.denial.json

QUERY="--grasp NEGATIVE;UNCERTAIN;POSITIVE;FUTURE;RECENT;PAST;DENIAL"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.json

QUERY="--grasp NEGATIVE;UNCERTAIN;POSITIVE;FUTURE;RECENT;PAST;DENIAL --entityPhrase disease --eventType eso:Decreasing;eso:Increasing"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > grasp.entity.event.json

# We first create a query

# We get the data from the KnowledgeStore

# We turn the data into a story

