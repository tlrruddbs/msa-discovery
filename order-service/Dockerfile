FROM openjdk:17-ea-jdk-slim
VOLUME /tmp
COPY build/libs/order-service-1.0.jar OrderService.jar
ENTRYPOINT ["java","-jar","OrderService.jar"]