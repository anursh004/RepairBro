FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew :franchise-hub:bootJar --no-daemon -x test
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/franchise-hub/build/libs/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
