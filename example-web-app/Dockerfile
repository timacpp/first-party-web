FROM maven:3.9.4-eclipse-temurin-17 AS builder
COPY . /web-app
WORKDIR web-app
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=builder /web-app/target/books.jar web-app.jar
ENTRYPOINT java -jar web-app.jar
