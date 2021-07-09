FROM maven AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=campaignmicroservice
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/campaignmicroservice/archive/refs/heads/certificates.tar.gz | tar -xz && \
    cd campaignmicroservice-certificates && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk AS appWebServerRuntime
COPY --from=appServerBuild app/campaignmicroservice-certificates/target/campaignmicroservice-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar campaignmicroservice-0.0.1-SNAPSHOT.jar
