FROM openjdk:21
VOLUME /tmp
EXPOSE 8080
COPY target/demo-0.0.1-SNAPSHOT.jar demo.jar
ENTRYPOINT ["java","-jar","/demo.jar"]
