#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git
#QUERY="--entityPhrase bank --entityType dbp:Bank --entityInstance dbpedia:Rabo --eventPhrase kill --eventType eso:Killing --topic eurovoc:16789 --grasp POSITIVE"
QUERY="--entityType dbp:Measles"
SERVER="http://145.100.58.139:50053"
LIMIT="2000"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index > /Users/piek/Desktop/NWR-INC/query/UncertaintyVisualization/app/data/brexit2/contextual.timeline.json

# We first create a query

# We get the data from the KnowledgeStore

# We turn the data into a story

