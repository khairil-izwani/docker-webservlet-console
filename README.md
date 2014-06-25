docker-webservlet-console
=========================

The Idea
--------
It acts as a desktop / mobile web console to remotely connect, access and execute stuffs inside the running containers hosted in the local machine. Additionally, by using local tunnel service such as pagekite or ngrok, these sandboxed containers can be controlled easily via the internet.

Development Stack
--------------------
This project is developed and tested on :-
* Fedora 19 64 bit
* Java 1.6 u38
* Docker 1.0.0

Pre-requisite
------------
* Docker version  >= 1.0.0
* Fair knowledge of using docker to create, start and stop containers.
* Java (jdk or jre version > 1.6) is installed in both host and container and accessible via console.


Quick Start
-----------
1. Start the docker service.

2. Copy host.jar (~2mb) from https://dl.dropboxusercontent.com/u/43630710/docker-webservlet-console/bin/host.jar

3. Copy container.jar (~2mb) from https://dl.dropboxusercontent.com/u/43630710/docker-webservlet-console/bin/container.jar

4. Start the host.jar

        $ java -jar host.jar

5. Run an image that executes container.jar as a foreground (or background) process.

        $ docker run -d <myimage> java -jar container.jar

6. Get the name of newly created container which is running, e.g. 'mycontainer' is returned.

        $ docker ps

7. Stop the newly created container.

        $ docker stop mycontainer

8. Open a web browser and go to

        http://localhost:8070/host
        
9. To re-start the container we just stopped, in the command input type

        start mycontainer       
        
10. If it's successful, [connected@mycontainer] will be returned in the command input field as a prefix.

11. To list out the content inside 'mycontainer'.

        [connected@mycontainer]ls -ltr

12. To stop container

        stop mycontainer


Command List
------------
1. Almost all commands which are permitted in the docker container can be executed via web console except those which need user input interactivity such as cd, vi etc.

2. Additional commands

     1. `start <container name>` to start existing container.
        
     2. `stop <container name>` to stop existing container.
        
     3. `save-as <file name with extension>` save content as a file.


UI/ UX
------
1. For the time being it has been decided to use a very simple console look. No fancy html,css or js stuffs.
2. Use the available UX functionality of the web browser such as loading status, autocomplete etc.
