# Use a base image with Maven and JDK pre-installed
FROM maven:3.9.2-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better layer caching)
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage with smaller JRE image
FROM openjdk:17-jdk-slim

# Install curl and mysql-client for health checks and debugging
RUN apt-get update && apt-get install -y \
    curl \
    default-mysql-client \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create wait-for-it script for database readiness
RUN echo '#!/bin/bash\n\
set -e\n\
\n\
host="$1"\n\
port="$2"\n\
shift 2\n\
cmd="$@"\n\
\n\
until mysqladmin ping -h "$host" -P "$port" --silent; do\n\
  >&2 echo "MySQL is unavailable - sleeping"\n\
  sleep 2\n\
done\n\
\n\
>&2 echo "MySQL is up - executing command"\n\
exec $cmd' > /wait-for-mysql.sh && chmod +x /wait-for-mysql.sh

# Change ownership to spring user
RUN chown -R spring:spring /app && chown spring:spring /wait-for-mysql.sh

# Switch to non-root user
USER spring:spring

# Expose port (Railway will assign PORT dynamically)
EXPOSE 8080

# Health check that works with Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with database wait
ENTRYPOINT ["/wait-for-mysql.sh", "mysql.railway.internal", "3306", "java", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]