FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar engagement-service.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","engagement-service.jar"]