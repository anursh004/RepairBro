FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew :insight-engine:bootJar --no-daemon -x test
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/insight-engine/build/libs/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
