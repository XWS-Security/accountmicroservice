FROM maven AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=followermicroservice
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/followermicroservice/archive/refs/heads/certificates.tar.gz | tar -xz && \
    cd followermicroservice-certificates && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk AS appWebServerRuntime
COPY --from=appServerBuild app/followermicroservice-certificates/target/followermicroservice-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar followermicroservice-0.0.1-SNAPSHOT.jar
