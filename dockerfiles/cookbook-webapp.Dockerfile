FROM node:current-alpine3.12 as build
WORKDIR /usr/src/app/webapp
RUN npm i
RUN npm build

FROM nginx:stable-alpine as server
COPY --from=build /usr/src/app/webapp/build /usr/share/nginx/html
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]