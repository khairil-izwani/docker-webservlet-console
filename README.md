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
I found it's quite hard to find a good example of attaching input to the STDIN in the running container via Docker remote API for host-container interaction. Besides, the 'docker execute' command is not yet available in the Docker release 1.0.0. This might change in the future once there is more resources of doing it.

Tech Stack
----------
* Fedora 19 64 bit
* Docker 1.0.0
* Java 1.6 u38
* Maven 3.x (for development)
* Eclipse IDE (for development - optional)

Pre-requisite to Run This Project
---------------------------------
* Docker version  >= 1.0.0 is installed in the host.
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

    $ java -jar host.jar <host_port> <container_port>
    $ docker run -d <myimage> java -jar container.jar <container_port>

By default both host.jar and container.jar run on port 8070. To change the port e.g. host to 8075 and container to 8074, run

    $ java -jar host.jar 8075 8074
    $ docker run -d <myimage> java -jar container.jar 8074
    
Getting 'Connection refused' error after starting the container is likely due to the port which is

1. already in use or
2. the host servlet which cannot find the port defined in the container servlet. 

Make sure they'are all set correctly as example above.
    
Create Hello Container in Java
------------------------------
This to demonstrate how to add a new script/program/process and execute it inside an already running container via the client web console. 

*This example is not limited to java language only. We should be able to do this using other languages as well.*

1. type this in the **ouput** field of the web console

   ```java
        public class Welcome {
           public static void main(String[] args) {
             System.out.println("Hello container!");
           }
        }
   ```

2. save the file by typing in the **input** field

        [connected@mycontainer]save-as Welcome.java
        
3. compile the java file

        [connected@mycontainer]javac Welcome.java
        
4. run the java class

         [connected@mycontainer]java Welcome
         
5. notice "Hello container!" is displayed in the output field.

Modifying the Source Code and Rebuild It
----------------------------------------
This project is developed and tested on Fedora 19 only, but it should work on other linux distros as well. However some distros might use different commands. Currently there is no plan to have command factory for other distros but replacing command in the code is trivial. 

This project uses Maven and Eclipse IDE for development.

Two projects are available in the master branch. One is called **container-client** and the other is **container-host**. Container-client is used to develop container.jar and container-host is for host.jar. 

container-client contains the code to forward the command to the container.

container-host contains the main code to delegate the commands either to host or container. Usually this is the interesting part to look at.


Let's assume that we use terminal, navigating to container-host root folder (which contains pom.xml).

1. Import project dependencies

         mvn clean install
         
2. Create as Eclipse project

         mvn eclipse:eclipse

Create Executable Jar
---------------------
`mvn clean install` creates a runnable jar in the '**target**' folder but it is postfixed with a version string e.g. container-host-1-SNAPSHOT.jar and a folder called '**libs**' that contains all the dependencies. Rename the jar if we like. 

If we copy this jar to somewhere else or to the container, we will need to copy the **libs** folder as well.

*I have not (yet) successful executing this jar as a single jar together with its dependencies via Maven. But this can be done easily via Eclipse*

To create a single jar together with it dependencies via Eclipse,
   1. Right click a project
   2. Choose Export 
   3. Choose Java 
   4. Choose Runnable Jar file
   5. Select Launcher class from Launch configuration dropdown
   6. Choose 'Package required libraries into generated JAR'

Misc and General Knowledge
--------------------------
1. By default docker daemon and cli will run as root. It means that host.jar will need to be executed as root as well, else we will get permission denied when try to execute some commands especially docker commands. But this will raise a security concern. However docker gives option to run docker cli as non-root user, which means host.jar can be run as non-root user. There are many references of it :

   1. https://docs.docker.com/installation/binaries/#giving-non-root-access
   2. http://askubuntu.com/questions/477551/how-can-i-use-docker-without-sudo
   3. http://xmodulo.com/2014/05/manage-linux-containers-docker-ubuntu.html

2. To run container.jar as a background process is easy. We just need to append & before execute the script/program of the foreground process. it is easier to create an sh file and run in via command line or via Dockerfile. *If we expect to run container.jar as a background process without attaching a foreground process in docker run command, then it will not work because docker container is running per fg process and as long as the process is still running (basic concept of VE vs VM). We can do that if docker image is run using /bin/bash, but it will not start the bg process automatically when the container is started again.*

   Assume that host.jar has been added to the image.
   
   1. via command line 
        1. the .sh file. Let say name it start.sh

           ```
                #!/bin/bash
                java -jar /opt/servlet/host.jar & <our script here>
           ```

        2. run the sh file `docker run -i -t <myimage> ./start.sh`
        
   2. via Dockerfile
        1. as example

           ```
                FROM <myimage>:1.4
                MAINTAINER khairil <khaiz83@gmail.com>
                CMD ["/bin/bash", "-c","java -jar /opt/servlet/host.jar & <our script here>"]
           ```

        2. navigate to the folder run the Dockerfile `docker build -t <myimage>:1.5 .`
        
3. If we want to directly copy the container.jar file from the host to a container, we will need to use volume to mount the host directory inside the container (assume container.jar is added in /usr folder in the host). e.g. 

   `docker run -i -t -v /usr:/root mattdm/fedora /bin/bash`

   Then inside the container, we navigate to /root folder and copy the container.jar

   After we do this, usually we will commit this container and create a new image. **Then the new container created from    this new image will still have the directory to host unmounted**. This might raise a security concern. To solve this     issue, we can recreate a new image from this container.
   
4. To use Dockerfile is an easier way to copy container.jar to container and/or the jdk/jre if we prefer to copy them directly instead of using packager such as yum or apt.

                FROM <myimage>:1.4
                MAINTAINER khairil <khaiz83@gmail.com>
                CMD ["/bin/bash", "-c","java -jar /opt/servlet/host.jar & <our script here>"]
