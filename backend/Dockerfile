# Builder stage
FROM maven:3.8-openjdk-18-slim AS builder
WORKDIR /app
VOLUME /tmp
ENV DOCKER_BUILDKIT=0
ENV COMPOSE_DOCKER_CLI_BUILD=0
COPY pom.xml /app
RUN mvn dependency:go-offline
COPY /src /app/src
RUN mvn package -DskipTests

# Runner stage
FROM openjdk:21-slim-buster
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]