FROM openjdk:8-jdk-alpine
VOLUME /tmp

RUN mkdir collabtrack
WORKDIR collabtrack

COPY target/collabtrack-0.0.1-SNAPSHOT.jar collabtrack.jar
COPY src/main/resources/docker-application.properties collabtrack.properties

RUN mkdir _uploads \
    && mkdir _uploads/monitor \
    && mkdir _uploads/audio \
    && chmod -R 755 _uploads

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","collabtrack.jar", "--spring.config.location=collabtrack.properties"]