# Start with a base image containing Java runtime
FROM --platform=linux/amd64 openjdk:19-jdk-alpine

# Update and upgrade the Alpine packages
RUN apk update  && apk upgrade --no-cache

# The application's jar file
ARG JAR_FILE=target/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Copy the /images folder to the container
COPY images /images

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]