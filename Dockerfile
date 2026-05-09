# Use a simple JDK image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/*.jar", "--spring.profiles.active=render"]