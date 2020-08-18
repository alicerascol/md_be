FROM openjdk:8-jdk-alpine
MAINTAINER alicerphi@gmail.com
EXPOSE 8080
ADD /build/libs/be-1.0-SNAPSHOT.jar be.jar
ENTRYPOINT ["java", "-jar", "be.jar"]
