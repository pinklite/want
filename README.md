![want logo](war/images/readme-logo.png) 

The Web App Network Tester (WANT) is a web application that tests for network issues including latency and packet loss issues between client browsers and back-end web service engines (e.g., Apache Tomcat). This code is distinguished from other web browser network analysis tools by continuously polling the back-end using user-defined intervals and payload sizes. In addition, this tester includes a file-upload feature for testing of large file uploads. This web application has been used to detect network issues caused by browser cache, network proxy, and network load issues.


![Network-tester](war/images/screenshot.png)


## Requirements

* JDK 1.8
* Apache Tomcat 8.x (or other web service engine)

## Installation

Download the (latest release)[https://github.com/dhs-gov/want/releases] of the `want.war` file and place into your Apache Tomcat `/webapps` directory. Start Tomcat and open a browser to `http://<host:port>/want`.

## Build

Download the want repository to path `$WANT_HOME` on your local machine. Navigate to `$WANT_HOME` and run `ant` to build the `want` source code and generate the `$WANT_HOME/dist/want.war` file. 

