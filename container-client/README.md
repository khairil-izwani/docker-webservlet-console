Client Container Servlet via Embedded Jetty
============================================

This is a maven project setup as a JAR packaging, with a Launcher class in
the provided scope that can be started as a java application.

Quick Start
-----------

    $ mvn clean install

Open your maven project target folder

    $ java -jar container-client-1-SNAPSHOT.jar

Servlet can only be accessed via POST request to

    http://localhost:8070/container

Credit to https://github.com/joakime
