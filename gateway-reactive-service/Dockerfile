FROM eclipse-temurin:17-jre-focal
MAINTAINER Grigoriy

ADD ./target/gateway-reactive-service-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/gateway-reactive-service-0.0.1-SNAPSHOT.jar"]