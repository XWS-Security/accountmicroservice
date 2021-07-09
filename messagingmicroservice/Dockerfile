FROM maven:3.8.1-jdk-11 AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=messagingmicroservice
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/messagingmicroservice/archive/refs/heads/certificates.tar.gz | tar -xz && \
    cd messagingmicroservice-certificates && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk:11 AS appWebServerRuntime
COPY --from=appServerBuild app/messagingmicroservice-certificates/target/messagingmicroservice-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar messagingmicroservice-0.0.1-SNAPSHOT.jar
