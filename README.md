This README provides information about the complete storyteller software stack. If you are looking for information on the Computational Linguistics Library alone, please refer to [StoryTeller_Library.md](StoryTeller_Library.md)

StoryTeller knowlegde store querying and visualization suite.
=============================================================
version 1.0  
Shared Copyright:  
VU University Amsterdam, Piek Vossen  
Netherlands eScience Center  

email: piek.vossen@vu.nl, m.vanmeersbergen@esciencecenter.nl  
website: cltl.nl, esciencecenter.nl  

## SOURCE CODE:
https://github.com/cltl/StoryTeller

## DESCRIPTION of the StoryTeller stack
The Storyteller system will allow you to generate general queries about topics on the [KnowledgeStore](https://knowledgestore.fbk.eu/) and visualize the results for analysis and exploration. It tries to accomplish this task by providing the user with web frontends that simplify the information needed for the complex tasks required and allowing the user to explore the knowledge with custom selections.

## Installation
### Automated installation of the complete query building and visualization suite
(please make sure to install docker and docker-compose https://www.docker.com/)  

After installation of docker, the following bash commands should suffice to install the entire system:

```bash
    git clone git@github.com:NLeSC-Storyteller/StoryTeller.git
    cd StoryTeller/
    docker volume create --name=data
    docker-compose up
```
This will create a set of docker containers that will run the following system
![diagram of the stack](https://cdn.rawgit.com/NLeSC-Storyteller/StoryTeller/master/doc/stack-diagram.svg "Stack diagram")  

## Useage

Please allow the installation process some time. After installation is complete, the web frontends can be found at [localhost:9000](http://localhost:9000) for the query builder component, and at [localhost:9001](http://localhost:9001) for the visualization component

Please be aware that we have targeted these web applications for Google Chrome. Results on other browsers may vary.

The install.sh will build the binary through apache-maven-2.2.1 and the pom.xml and move it to the "lib" folder.

# LICENSE
    StoryTeller is free software: you can redistribute it and/or modify
    it under the terms of the The Apache License, Version 2.0:
        http://www.apache.org/licenses/LICENSE-2.0.txt.

    StoryTeller is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    License for more details.
