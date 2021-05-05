FROM maven AS appWebServerBuild
ARG STAGE=dev
WORKDIR /usr/src/pki
COPY . .
RUN mvn package -P${STAGE} -DskipTests

FROM openjdk AS appWebServerRuntime
WORKDIR /app
COPY --from=appWebServerBuild /usr/src/pki/target/pki-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
CMD java -jar pki-0.0.1-SNAPSHOT.jar
