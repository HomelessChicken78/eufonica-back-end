FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/*.jar catalog-command-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","catalog-command-service.jar"]