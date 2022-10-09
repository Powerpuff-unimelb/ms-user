FROM amazoncorretto:17
ADD target/user-service.jar /app/app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar","app/app.jar"]