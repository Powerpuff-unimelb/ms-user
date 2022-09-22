FROM fabric8/java-alpine-openjdk11-jre
ADD target/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar","app/app.jar"]