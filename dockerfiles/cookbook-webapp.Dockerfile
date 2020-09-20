FROM node:current as build
RUN mkdir -p /usr/src/app/cookbook-webapp
COPY ./webapp/package.json /usr/src/app/cookbook-webapp
WORKDIR /usr/src/app/cookbook-webapp
RUN npm i
COPY ./webapp ./
RUN npm run build

FROM nginx:stable as server
COPY ./webapp/nginx.conf /etc/nginx/conf.d/default.conf
WORKDIR /usr/src/app/cookbook-webapp
COPY --from=build /usr/src/app/cookbook-webapp/dist /usr/share/nginx/html
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]