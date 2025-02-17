FROM azul/zulu-openjdk:21-latest
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=k8s
ENTRYPOINT ["java","-jar","app.jar"] 