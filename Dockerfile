FROM maven AS appServerBuild
ARG STAGE=dev
ARG APPLLICATION_REPOSITORY=Security-Backend
WORKDIR /app
RUN curl -L https://github.com/XWS-Security/Security-Backend/archive/refs/heads/develop.tar.gz | tar -xz && \
    cd Security-Backend-develop && \
    mvn package -P${STAGE} -DskipTests
    
FROM openjdk AS appWebServerRuntime
COPY --from=appServerBuild app/Security-Backend-develop/target/pki-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar pki-0.0.1-SNAPSHOT.jar
    
