FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar catalog-command-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","catalog-command-service.jar"]