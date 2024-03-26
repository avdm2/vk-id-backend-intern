FROM maven:3.9.6-eclipse-temurin-17 as build

VOLUME /tmp

COPY src ./src
COPY pom.xml .

RUN mvn clean package -DskipTests

ENTRYPOINT ["java","-jar","target/vk-id-backend-intern-1.0.0.jar"]