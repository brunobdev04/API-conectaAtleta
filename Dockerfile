FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/appConecta-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]