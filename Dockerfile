# Stage 1: Build with Gradle
FROM gradle:8.4-jdk21 AS builder
WORKDIR /app

# Copy build files
COPY build.gradle settings.gradle ./

# Copy source code
COPY src ./src

# Build using gradle command
RUN gradle build -x test --no-daemon

# Stage 2: Slim runtime
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/authservice-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 9898
CMD ["java", "-jar", "app.jar"]