#!/usr/bin/env bash
#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

INPUT="../naf-examples"
EXTENSION=".naf"
PROJECT="wikinews"

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.input.NafTokenLayerIndex --folder $INPUT --extension $EXTENSION --project $PROJECT
