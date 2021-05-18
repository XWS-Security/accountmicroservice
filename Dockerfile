FROM maven AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=accountmicroservice
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/accountmicroservice/archive/refs/heads/develop.tar.gz | tar -xz && \
    cd accountmicroservice-develop && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk AS appWebServerRuntime
COPY --from=appServerBuild app/accountmicroservice-develop/target/pki-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar pki-0.0.1-SNAPSHOT.jar
