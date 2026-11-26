FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/*.jar auth-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","auth-service.jar"]