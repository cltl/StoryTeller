#!/usr/bin/env bash
set -e

#Requires an installation of maven 2.x and Java 1.6 or higher

# define the location of the install scipt
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PARENT="$( cd $DIR && cd .. && pwd)"

echo "Compiling the library from source code and dependencies"
mvn clean
mvn install

echo "#Installing the vua-resources"
if [ ! -d "$PARENT/vua-resources" ]; then
    cd "$PARENT"
    git clone https://github.com/cltl/vua-resources.git
fi
