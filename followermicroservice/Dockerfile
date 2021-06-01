FROM maven AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=followermicroservice
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/followermicroservice/archive/refs/heads/develop.tar.gz | tar -xz && \
    cd followermicroservice-develop && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk AS appWebServerRuntime
COPY --from=appServerBuild app/followermicroservice-develop/target/followermicroservice-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar followermicroservice-0.0.1-SNAPSHOT.jar
