FROM gradle:6.8.1-jdk11 as api_build
RUN mkdir -p /usr/src/app/api
COPY ./api/build.gradle.kts /usr/src/app/api
COPY ./api/gradle.properties /usr/src/app/api
COPY ./api/settings.gradle.kts /usr/src/app/api
COPY ./api/.dockerignore /usr/src/app/api
WORKDIR /usr/src/app/api
RUN gradle build -i
COPY ./api/src ./src
RUN gradle shadowJar -i

# Deploy project step
FROM openjdk:11-jre-slim as run
WORKDIR  /usr/src/app/api
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8999
COPY --from=api_build /usr/src/app/api/build/libs ./
CMD ["java", "-jar", "cookbook-api-all.jar"]