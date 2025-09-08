FROM eclipse-temurin:21-jre
WORKDIR /app
COPY ../jars/authservice.jar app.jar
EXPOSE 9898
CMD ["java", "-jar", "app.jar"]