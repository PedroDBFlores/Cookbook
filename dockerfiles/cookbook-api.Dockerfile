FROM gradle:6.5.0-jdk11 as cache
RUN mkdir -p /usr/src/app/cookbook-api
COPY ./api/build.gradle.kts /usr/src/app/cookbook-api
COPY ./api/gradle.properties /usr/src/app/cookbook-api
WORKDIR /usr/src/app/cookbook-api
RUN gradle clean build -i --stacktrace
RUN rm -rf /tmp/*

FROM gradle:6.5.0-jdk11 as build
WORKDIR /usr/src/app/cookbook-api
COPY ./api ./
RUN gradle shadowJar -i --stacktrace
RUN rm -rf /tmp/*

# Deploy project step
# FROM openjdk:11-jre-slim as run
# WORKDIR  /usr/src/app/cookbook-api
# ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8999
# COPY --from=build /usr/src/app/cookbook-api/build/libs ./
# CMD ["java", "-jar", "cookbook-api-all.jar"]
