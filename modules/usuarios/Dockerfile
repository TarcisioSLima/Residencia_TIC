FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn package -DskipTests -B

FROM registry.access.redhat.com/ubi9/openjdk-17:1.21

WORKDIR /app

COPY --from=build /app/target/quarkus-app/ /app/
EXPOSE 8002

CMD ["java", "-jar", "/app/quarkus-run.jar"]
