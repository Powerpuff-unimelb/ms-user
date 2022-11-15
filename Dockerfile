FROM maven:3.8.1-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
EXPOSE 8082
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

FROM amazoncorretto:17
COPY --from=build /home/app/target/user-service.jar /usr/local/lib/demo.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]