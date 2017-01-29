#!/usr/bin/env bash

#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

#KnowledgeStore server address and credentials if needed
SERVER="http://145.100.58.139:50053"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"
MCOUNT="2"

PROJECTS="<http://www.newsreader-project.eu/project/London>;<http://www.newsreader-project.eu/project/AI>"

#Get the light entities from the KS
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonRobotHierarchy --ks-service $SERVER --projects $PROJECTS --mention $MCOUNT --data "light-entities" --out light-entities.json
#Get the dark entities from the KS
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonRobotHierarchy --ks-service $SERVER --projects $PROJECTS --mention $MCOUNT --data "dark-entities" --out dark-entities.json
#Get the non entities from the KS
MCOUNT="10"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonRobotHierarchy --ks-service $SERVER --projects $PROJECTS --mention $MCOUNT --data "non-entities" --out non-entities.json
