# Build webapp
FROM node:24.9-alpine3.22 AS build-js
RUN mkdir -p /opt/picklunch
COPY webapp /opt/picklunch/webapp

WORKDIR /opt/picklunch/webapp
RUN pwd && ls -la
RUN npm install
RUN npm run build

# Build spring boot 
FROM eclipse-temurin:25_36-jdk-alpine-3.22 AS build-java
RUN mkdir -p /opt/picklunch
WORKDIR /opt/picklunch
COPY .mvn /opt/picklunch/.mvn
COPY mvnw /opt/picklunch/
COPY pom.xml /opt/picklunch/
RUN pwd && ls -la
# Cache maven dependencies in docker layer
RUN ./mvnw verify --fail-never
COPY . /opt/picklunch/
RUN pwd && ls -la
RUN ./mvnw clean package

# Runtime
FROM eclipse-temurin:25_36-jre-alpine-3.22 
RUN mkdir -p /opt/picklunch
COPY --from=build-js /opt/picklunch/webapp/dist/webapp/browser /opt/picklunch/webapp
COPY --from=build-java /opt/picklunch/target/picklunch-*.jar /opt/picklunch/picklunch.jar
COPY users.csv /opt/picklunch/

WORKDIR /opt/picklunch
RUN pwd && ls -lrt
ENV PICKLUNCH_WEBAPP_STATIC_DIR=/opt/picklunch/webapp
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "picklunch.jar" ]
