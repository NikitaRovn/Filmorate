FROM amazoncorretto:21
LABEL authors="Nikita Rovnin"

COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=dev"]