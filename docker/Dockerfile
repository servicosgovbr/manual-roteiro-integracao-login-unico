FROM maven:3-openjdk-8-slim

COPY pom.xml pom.xml
COPY src src

RUN mvn install

CMD java -jar target/logingovbr-pkg.jar  

