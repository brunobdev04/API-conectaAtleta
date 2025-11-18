FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# --- ESTÁGIO 2: O "RODADOR" (Run) ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# ---- A MUDANÇA ESTÁ AQUI ----
# Nós copiamos o JAR com o nome do projeto CORRETO
COPY --from=builder /app/target/API-conectaAtleta-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]