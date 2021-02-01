FROM node:lts-alpine3.12 as webapp_build
RUN mkdir -p /usr/src/app/webapp
COPY ./webapp/package.json /usr/src/app/webapp
COPY ./webapp/package-lock.json* /usr/src/app/webapp
WORKDIR /usr/src/app/webapp
RUN npm i
COPY ./webapp ./
RUN npm run build

FROM gradle:6.8.1-jdk11 as api_build
RUN mkdir -p /usr/src/app/api
COPY ./api/build.gradle.kts /usr/src/app/api
COPY ./api/gradle.properties /usr/src/app/api
WORKDIR /usr/src/app/api
RUN gradle build -i
COPY ./api ./
COPY --from=webapp_build /usr/src/app/webapp/dist /usr/src/app/api/src/main/resources/static
RUN gradle shadowJar -i

# Deploy project step
FROM openjdk:11-jre-slim as run
WORKDIR  /usr/src/app/api
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8999
COPY ./heroku_jdbc.sh ./
COPY --from=api_build /usr/src/app/api/build/libs ./
CMD ["chmod +x heroku_jdbc.sh && ./heroku_jdbc.sh" ,"&&", "java", "-jar", "cookbook-api-all.jar"]