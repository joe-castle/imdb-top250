#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
COPY frontend /home/app/frontend
COPY package.json /home/app
COPY vite.config.js /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM eclipse-temurin:20-jdk-alpine
COPY --from=build /home/app/target/imdb-top250-0.0.1.jar /usr/local/lib/imdb-top250.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/imdb-top250.jar"]