FROM amazoncorretto:17
ADD target/user-service.jar /app/app.jar
ENTRYPOINT ["java", "-jar","app/app.jar"]