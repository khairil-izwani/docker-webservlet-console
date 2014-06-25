docker-webservlet-console
=========================

The Idea
--------
It acts as a desktop / mobile web console to connect, read, write and execute stuffs remotely inside the running containers hosted in the local machine. Additionally, by using tunnel service such as pagekite or ngrok, these sandboxed containers can be controlled easily via the internet.

How It Is Done
--------------
Both host and container are talking to each other via inter servlet communication. It does not use the REST Docker Remote API but instead forwards the docker native command to the Java Runtime API. 

Why It Is Done This Way
-----------------------
I found it's quite hard to find a good example of attaching input to the STDIN in the running container via Docker remote API for host-container interaction. Besides, the 'docker execute' command is not yet available in the Docker release 1.0.0. This might change in the future once there is more resources of doing this.

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

UI / UX
-------
1. For the time being the web console is developed with a simple style in mind. No fancy html,css or js stuffs.
2. It uses what is available from the web browser such as loading status, autocomplete etc.
3. The console's html file consists of 2 fields. One is for displaying output and one is for sending input.
4. For creating a new file via 'save-as' command, output field will be used to transport the file content to the server side.

Command List
------------
1. Almost all of the commands which are permitted in the docker container can be executed via web console except those which need user input interactivity such as cd, vi etc.

2. Additional commands

     1. `start <container name>` to start existing container.
        
     2. `stop <container name>` to stop existing container.
        
     3. `save-as <file name with extension>` create or update a file.
     
Change Servlet Port
-------------------

    $ java -jar host.jar <host_port> <container_port> e.g.
    $ docker run -d <myimage> java -jar container.jar <container_port> e.g.

By default both host.jar and container.jar run on port 8070. To change the port e.g. host to 8075 and container to 8074, run

    $ java -jar host.jar 8075 8074
    $ docker run -d <myimage> java -jar container.jar 8074
    
Getting 'Connection refused' error after starting the container is likely due to the port which is
1. already in use or
2. the host servlet which cannot find the port defined in the container servlet. 

Make sure they'are all set correctly as example above.
    
Create Hello World in Java
--------------------------
This to demonstrate how to add a new script/program/process and execute it inside an already running container via the client web console.

1. type this in the ouput field of the web console

        public class Welcome {
           public static void main(String[] args) {
             System.out.println("Welcome to container!");
           }
        }

2. save the file by typing in the input field

        [connected@mycontainer]save-as Welcome.java
        
3. compile the java file

        [connected@mycontainer]javac Welcome.java
        
4. run the java class

         [connected@mycontainer]java Welcome
         
5. notice "Welcome to container!" is displayed in the output field.
