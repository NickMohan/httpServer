# httpServer
This project is very simple to operate. Keep all of the java files
and log files in a base directory, and included in this is a root
file. This root file should be populated with the html files, notably
index.html and 404.html. You can replace these file and put any project
you want to have the server host for you here.

|-Server.java
|-HttpServer.java
|-error.log
|-activity.log
|-root
| |-index.html
| |-404.html

The index.html file is for user authentication testing and can be 
accessed at localhost:8080

To run the server just compile the Server.java file and then run it.
The server is set to run on port 8080 and go to localhost:8080 on any
browser to see your hosted page. We compiled, ran, and tested with these
versions of java below:
 Java Compiler : 1.8.0_191
 Java Virtual Machine : 1.8.0_191

To test the server we used the command line curl command. Below is
the linux bash commands for testing each of the difference HTTP 
request methods

GET
curl -v http://localhost:8080

POST
curl -d "data=test" http://localhost:8080

HEAD
curl -v -I HEAD http://localhost:8080 

