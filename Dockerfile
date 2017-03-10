# Dockerfile to create a machine with 
# - basic linux functions (curl,wget,python, etc)
# - Lastest JDK installed

FROM ubuntu:latest
MAINTAINER m.vanmeersbergen@esciencecenter.nl

# Generic stuff
RUN locale-gen en_US.UTF-8
ENV LANG='en_US.UTF-8' LC_ALL='en_US.UTF-8'

# Update the APT cache
RUN apt-get update
RUN apt-get upgrade -y

# Install and setup project dependencies
RUN apt-get install -y \
    git \
    ssh \
    rsync \
    curl \ 
    wget \
    python3 \
    python3-pip \
    cmake \
    build-essential \
    libsqlite3-dev
    

# prepare for Java download
RUN apt-get install -y python-software-properties
RUN apt-get install -y software-properties-common

# grab oracle java (auto accept licence)
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer maven

# node 
RUN curl -sL https://deb.nodesource.com/setup_7.x | bash -
RUN apt-get install -y nodejs

# environment
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
ENV PYTHON_HOME /usr/lib/python3

# make barebones
RUN mkdir /src

# download and unzip git releases

# vua-resources (done by install script of cltl/StoryTeller)
# RUN git clone https://github.com/cltl/vua-resources.git /src/vua-resources/

# cltl/StoryTeller
RUN mkdir /src/StoryTeller
COPY pom.xml install.sh /src/StoryTeller/
COPY scripts /src/StoryTeller/scripts
COPY src /src/StoryTeller/src

WORKDIR /src/StoryTeller
RUN chmod +wrx install.sh
RUN ./install.sh

# Setup git to hadle line endings properly
RUN git config --global core.autocrlf input

# query-builder-preprocessing
RUN git clone https://github.com/NLeSC-StoryTeller/query-builder-preprocessing.git /src/query-builder-preprocessing/
# WORKDIR /src/query-builder-preprocessing

# query-builder-server
RUN git clone https://github.com/NLeSC-StoryTeller/query-builder-server.git /src/query-builder-server/
WORKDIR /src/query-builder-server
RUN npm install

# query-builder-daemon
RUN git clone https://github.com/NLeSC-StoryTeller/query-builder-daemon.git /src/query-builder-daemon/
WORKDIR /src/query-builder-daemon/
RUN ./gradlew installDist
