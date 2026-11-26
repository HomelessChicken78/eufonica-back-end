FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar catalog-query-service.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","catalog-query-service.jar"]