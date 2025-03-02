FROM ubuntu:latest

# Instalar dependências para download e instalação
RUN apt-get update && apt-get install -y \
    wget \
    ca-certificates \
    tar \
    && rm -rf /var/lib/apt/lists/*

# Baixar, extrair e renomear o diretório do OpenJDK 21
RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz -P /tmp \
    && tar -xzf /tmp/jdk-21_linux-x64_bin.tar.gz -C /opt \
    && rm /tmp/jdk-21_linux-x64_bin.tar.gz \
    && mv /opt/jdk* /opt/jdk-21

# Configurar variáveis de ambiente para o Java
ENV JAVA_HOME=/opt/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Verificar se o Java foi instalado corretamente
RUN /opt/jdk-21/bin/java -version

# Criar diretório do app e copiar o .jar
RUN mkdir /app
WORKDIR /app
COPY target/*.jar /app/app.jar

# Comando para rodar a aplicação
CMD ["java", "-jar", "/app/app.jar"]
