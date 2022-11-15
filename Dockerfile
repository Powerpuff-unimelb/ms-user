FROM amazoncorretto:17
ADD target/user-service.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar","app/app.jar"]