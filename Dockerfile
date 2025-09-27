# Build webapp
FROM node:24.9-alpine3.22 AS build-js
RUN mkdir -p /opt/picklunch
COPY webapp /opt/picklunch/webapp

WORKDIR /opt/picklunch/webapp
RUN pwd && ls -lrt
RUN npm install
RUN npm run build

# Build spring boot 
FROM eclipse-temurin:25_36-jdk-alpine-3.22 AS build-java
RUN mkdir -p /opt/picklunch
COPY . /opt/picklunch/

WORKDIR /opt/picklunch
RUN pwd && ls -lrt
RUN ./mvnw clean package

FROM eclipse-temurin:25_36-jre-alpine-3.22 
RUN mkdir -p /opt/picklunch
COPY --from=build-js /opt/picklunch/webapp/dist/webapp/browser /opt/picklunch/webapp
COPY --from=build-java /opt/picklunch/target/picklunch-*.jar /opt/picklunch/picklunch.jar
COPY users.csv /opt/picklunch/

WORKDIR /opt/picklunch
RUN pwd && ls -lrt
ENV PL_WEBAPP_STATIC_DIR=/opt/picklunch/webapp
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "picklunch.jar" ]
