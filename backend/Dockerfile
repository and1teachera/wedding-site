FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make Gradle executable
RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build -x test --no-daemon

# Use a minimal JRE for runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR
COPY --from=0 /app/build/libs/*.jar app.jar

# Add debugging tools
RUN apk add --no-cache curl

# Add a basic health check
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
