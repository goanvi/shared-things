# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /app

ARG MODULE

# Copy pom.xml and all module poms first for dependency resolution
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY gateway/pom.xml gateway/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY accounts/pom.xml accounts/pom.xml
COPY bookings/pom.xml bookings/pom.xml
COPY wishlists/pom.xml wishlists/pom.xml

# Download dependencies
RUN mvn -B dependency:go-offline -pl ":$MODULE" -am

# Copy source code
COPY . .

# Build specific module
RUN mvn package -pl ":$MODULE" -am -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jdk
ARG MODULE
COPY --from=builder /app/$MODULE/target/*.jar /app/app.jar
ARG APP_PORT=8080
EXPOSE ${APP_PORT}
ENTRYPOINT ["java", "-jar", "/app/app.jar"]