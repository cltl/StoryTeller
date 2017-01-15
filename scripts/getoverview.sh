#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git

SERVER="http://145.100.58.139:50053"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

fnPath="$RESOURCES/frAllRelation.xml"
esoPath = "$RESOURCES/ESO_Version2.owl"
euroVocLabelFile = "$RESOURCES/mapping_eurovoc_skos.csv.gz"
euroVocHierarchyFile = ""$RESOURCES/eurovoc_in_skos_core_concepts.rdf.gz"
entityTypeFile = "$RESOURCES/instance_types_en.ttl.gz"
entityHierarchyFile = "$RESOURCES/DBpediaHierarchy_parent_child.tsv"

# get KS stats in json for query generator for a specified type of data.
# types are:
# light-entities
# dark-entities
# concepts
# events
# topics
# authors
# cited
# perspectives


#OPTIONAL parameters --debug (writes JSON to file for debugging) --all-events (all events are collected, also the ones without FrameNet and ESO type)

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data light-entities


java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data dark-entities

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data concepts

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data events

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data topics

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data authors

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data cited

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --eso $esoPath --framenet $fnPath --eurovoc-label euroVocLabelFile --eurovoc-core euroVocHierarchyFile --entity-hiearchy $entityHierarchyFile --entity-type $entityTypeFile --data perspectives
