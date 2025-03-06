# Usar uma imagem base do OpenJDK 21
FROM openjdk:21-jdk-slim

# Copiar o arquivo .jar gerado para o contêiner
COPY target/baralho-0.0.1-SNAPSHOT.jar app.jar

# Expor a porta em que a aplicação Spring Boot vai rodar (padrão 8080)
EXPOSE 8080

# Rodar a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]
