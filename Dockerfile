# Usa uma imagem do Maven (baseada no Java 21) para construir o projeto
FROM maven:3.9-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o pom.xml primeiro para otimizar o cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o resto do código-fonte (o 'src')
COPY src ./src

# Roda o comando de build (mvn clean package)
# Isso vai criar a pasta /app/target/API-conectaAtleta...jar
RUN mvn package -DskipTests


# --- ESTÁGIO 2: O "RODADOR" (Run) ---
# Começa com uma imagem Java 21 limpa e leve
FROM eclipse-temurin:21-jdk-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia o .jar que foi criado no Estágio 1 (o 'builder') para cá
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que o Render vai usar
# (O Render na verdade usa a variável PORT, mas é bom ter isso)
EXPOSE 8080

# O comando final para ligar o app
ENTRYPOINT ["java","-jar","/app.jar"]