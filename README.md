docker-webservlet-console
=========================

What is the idea?
-----------------
It acts as a web or mobile console to remotely connect, access and execute stuffs inside the running containers hosted in the local machine. Additionally, by using local tunnel service such as pagekite or ngrok, these sandboxed containers can be controlled easily via the internet.

my development stack
--------------------
* Fedora 19 64 bit
* Java 1.6 u38
* Docker 1.0.0

pre-requisite
------------
* Docker version  >= 1.0.0
* Fair knowledge of using docker to create, start and stop containers.
* Java (jdk or jre version > 1.6) is installed in both host and container and accessible via console.

how to run
----------
1. Start the docker service.

2. Copy host.jar (~2mb) from https://dl.dropboxusercontent.com/u/43630710/docker-webservlet-console/bin/host.jar

3. Copy container.jar (~2mb) from https://dl.dropboxusercontent.com/u/43630710/docker-webservlet-console/bin/container.jar

4. Start the host.jar

'''
	$ java -jar host.jar
'''	

5. Run an image that executes container.jar as a foreground (or background process).

	$ docker run -d **{myimage}** java -jar container.jar

6. Get the name of newly created container which is running, e.g. 'mycontainer' is returned.

	$ docker ps

7. Stop the newly created container.

	$ docker stop mycontainer

8. Open a web browser and go to

	http://localhost:8070/host

9. To start the container, type

	start mycontainer

10. If successful [connected@mycontainer] is returned in the command input field.

11. Type ls after [connected@mycontainer] to list content inside the 'mycontainer' container.

12. To stop container

	stop mycontainer


Remarks
-------
TODO


