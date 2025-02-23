FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

WORKDIR /app/image-service
RUN --mount=type=cache,target=/root/.m2 mvn verify clean --fail-never
COPY image-service/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app
COPY --from=build /app/image-service/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]