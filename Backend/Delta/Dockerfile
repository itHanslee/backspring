# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos directamente desde la carpeta Backend/Delta
COPY pom.xml .
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]