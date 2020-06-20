FROM gradle:6.5.0-jdk11 as cache
RUN mkdir -p /home/gradle/cache_home
RUN mkdir -p /usr/src/app/cookbook-api
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY ./api/build.gradle.kts /usr/src/app/cookbook-api
COPY ./api/gradle.properties /usr/src/app/cookbook-api
WORKDIR /usr/src/app/cookbook-api
RUN gradle clean build -i --stacktrace

FROM gradle:6.5.0-jdk11 as build
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
WORKDIR  /usr/src/app/cookbook-api
COPY ./api ./
RUN gradle clean shadowJar -i --stacktrace
RUN rm -rf /tmp/*

FROM openjdk:11-jre-slim as run
WORKDIR  /usr/src/app/cookbook-api
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9000
COPY --from=build /usr/src/app/cookbook-api/build/libs ./
CMD ["java", "-jar", "cookbook-api-all.jar"]
