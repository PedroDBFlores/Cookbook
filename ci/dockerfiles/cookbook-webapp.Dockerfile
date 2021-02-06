FROM node:lts-alpine3.12 as webapp_build
RUN mkdir -p /usr/src/app/webapp
COPY ./webapp/package.json /usr/src/app/webapp
COPY ./webapp/package-lock.json* /usr/src/app/webapp
WORKDIR /usr/src/app/webapp
RUN npm i
COPY ./webapp ./
RUN npm run build

FROM nginx:stable-alpine as server
COPY ./ci/config/nginx_template.conf /etc/nginx/templates/default.conf.template
WORKDIR /usr/src/app/webapp
COPY --from=webapp_build /usr/src/app/webapp/dist /usr/share/nginx/html
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]