FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY target/be-dms-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9000
CMD ["java", "-jar", "app.jar"]
