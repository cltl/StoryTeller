FROM nlescstoryteller/storyteller

WORKDIR /src/StoryTeller/scripts
RUN chmod +x getoverview.sh
RUN getoverview.sh

COPY token.index.gz /data
RUN mv *.json /data