FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/*.jar engagement-service.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","engagement-service.jar"]