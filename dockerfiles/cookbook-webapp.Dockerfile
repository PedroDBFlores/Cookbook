FROM node:current-alpine3.12 as install
RUN mkdir -p /usr/src/app/cookbook-webapp
COPY ./webapp/package.json /usr/src/app/cookbook-webapp
WORKDIR /usr/src/app/cookbook-webapp
RUN npm i

FROM node:current-alpine3.12 as build
WORKDIR /usr/src/app/cookbook-webapp
RUN npm build

FROM nginx:stable-alpine as server
WORKDIR /usr/src/app/cookbook-webapp
COPY --from=build /usr/src/app/cookbook-webapp/dist /usr/share/nginx/html
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]