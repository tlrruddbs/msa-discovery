FROM openjdk:17-ea-jdk-slim
VOLUME /tmp
COPY build/libs/config-service-1.0.jar ConfigServer.jar
ENTRYPOINT ["java","-jar","ConfigServer.jar"]
