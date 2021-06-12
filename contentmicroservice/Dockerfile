FROM maven AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=contentmicroservice
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/contentmicroservice/archive/refs/heads/certificates.tar.gz | tar -xz && \
    cd contentmicroservice-certificates && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk AS appWebServerRuntime
COPY --from=appServerBuild app/contentmicroservice-certificates/target/contentmicroservice-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar contentmicroservice-0.0.1-SNAPSHOT.jar
