#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git

SERVER="http://130.37.53.35:50053"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

fnPath="$RESOURCES/frAllRelation.xml"
esoPath="$RESOURCES/ESO_Version2.owl"
euroVocLabelFile="$RESOURCES/mapping_eurovoc_skos.label.concept.gz"
euroVocHierarchyFile="$RESOURCES/eurovoc_in_skos_core_concepts.rdf.gz"
entityTypeFile="$RESOURCES/instance_types_en.ttl.gz"
entityHierarchyFile="$RESOURCES/DBpediaHierarchy_parent_child.tsv"
MCOUNT=-1;

# get KS stats in json for query generator for a specified type of data.
# Supported types are:
# light-entities
# dark-entities
# concepts
# events
# topics
# authors
# cited
# perspectives


#OPTIONAL parameter --debug (writes JSON to file for debugging to a file and prints debug information toi standard out)
#OPTIONAL parameter --mention (takes a counter as argument that restricts the phrases and concepts to those that are mentioned more than this threshold

# generate a JSON output stream for ligh-entities using the DBPedia ontology, --entity-hiearchy parameter expects a text file with parent<TAB>child on separate lines.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --entity-hierarchy $entityHierarchyFile --data "light-entities" --mention $MCOUNT

# generate a JSON output stream for dark-entities using the ENTITY classes assigned to entities in the NAF file.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --data "dark-entities" --mention $MCOUNT

# generate a JSON output stream for concepts. Concepts are anything that is not an entity but plays an important role in an event. It uses the DBPedia ontology, --entity-hiearchy parameter expects a text file with parent<TAB>child on separate lines, --entity-type expects a gz file with  <dbpedia resource uri><TAB><dbpedia ontology uri>
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER  --entity-hierarchy $entityHierarchyFile --entity-type $entityTypeFile --data "concepts" --mention $MCOUNT

# generate a JSON output stream for events. It uses two ontologies for building up the hiearchy --eso $esoPath --framenet $fnPath. Standard mode only outputs events mapped to ESO or FrameNet. Use the optional parameter --all-events to also get other events not mapped to ESO or FrameNet
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER  --eso $esoPath --framenet $fnPath --data "events" --mention $MCOUNT

# generate a JSON output stream for topics.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER  --eurovoc-label $euroVocLabelFile --eurovoc-core $euroVocHierarchyFile --data "topics"

# generate a JSON output stream for authors of the sources (documents). Flat list with countings.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER  --data "authors"

# generate a JSON output stream for cited sources within the documents. Flat list with countings.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER  --data "cited"

# generate a JSON output stream for perspective values. Flat list with countings.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER  --data "perspectives"
