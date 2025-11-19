# Multi-stage build for smaller image size

# Stage 1: Build with Gradle
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

# Copy Gradle configuration files
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/users/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]