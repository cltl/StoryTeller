#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources
# assumes vua-resources is installed next to this installation
# git clone https://github.com/cltl/vua-resources.git

#KnowledgeStore server address and credentials if needed
SERVER="http://130.37.53.35:50053"
KS="nwr/wikinews-new"
KSUSER="wikinews"
KSPASS="wikinews"

#Resources needed to build hierarchies
fnPath="$RESOURCES/frAllRelation.xml"
esoPath="$RESOURCES/ESO_Version2.owl"
euroVocLabelFile="$RESOURCES/mapping_eurovoc_skos.label.concept.gz"
euroVocHierarchyFile="$RESOURCES/eurovoc_in_skos_core_concepts.rdf.gz"
entityTypeFile="$RESOURCES/instance_types_en.ttl.gz"
entityHierarchyFile="$RESOURCES/DBpediaHierarchy_parent_child.tsv"
entityCustomFile="$RESOURCES/NERC_DBpediaHierarchy_mapping.tsv"
MCOUNT=0;

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


#OPTIONAL parameter --debug (writes JSON to file for debugging to a file and prints debug information to standard out)
#OPTIONAL parameter --out to specify the output file
#OPTIONAL parameter --mention (takes a counter as argument that restricts the phrases and concepts to those that are mentioned equal or more than this threshold

# generate a JSON output file or stream for ligh-entities using the DBPedia ontology, --entity-hierarchy parameter expects a text file with parent<TAB>child on separate lines.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --entity-hierarchy $entityHierarchyFile --data "light-entities" --mention $MCOUNT --out light.json

# generate a JSON output file or stream for dark-entities using the ENTITY classes assigned to entities in the NAF file.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --data "dark-entities"   --entity-hierarchy $entityHierarchyFile --entity-custom $entityCustomFile --mention $MCOUNT --out dark.json

# generate a JSON output file or stream for concepts. Concepts are anything that is not an entity but plays an important role in an event. It uses the DBPedia ontology, --entity-hierarchy parameter expects a text file with parent<TAB>child on separate lines, --entity-type expects a gz file with  <dbpedia resource uri><TAB><dbpedia ontology uri>
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --entity-hierarchy $entityHierarchyFile --entity-type $entityTypeFile --data "concepts" --mention $MCOUNT --out concept.json

# generate a JSON output file or stream for events. It uses two ontologies for building up the hierarchy --eso $esoPath --framenet $fnPath. Standard mode only outputs events mapped to ESO or FrameNet. Use the optional parameter --all-events to also get other events not mapped to ESO or FrameNet
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --eso $esoPath --framenet $fnPath --data "events" --out events.json

# generate a JSON output file or stream for topics.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --eurovoc-label $euroVocLabelFile --eurovoc-core $euroVocHierarchyFile --data "topics" --mention $MCOUNT --out topics.json

# generate a JSON output file or stream for authors of the sources (documents). Flat list with countings.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --data "authors" --out authors.json

# generate a JSON output file or stream for cited sources within the documents. Flat list with countings.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --data "cited" --out cited.json

# generate a JSON output file or stream for perspective values. Flat list with countings.
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonQueryHierarchy --ks-service $SERVER --data "perspectives" --out perspectives.json