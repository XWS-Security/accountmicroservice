FROM node:lts-alpine as build-stage
ARG APPLLICATION_REPOSITORY=Security-front
WORKDIR /app
RUN apk --update --no-cache add curl tar && \
    curl -L https://github.com/XWS-Security/Security-front/archive/refs/heads/develop.tar.gz | tar -xz && \
    cd ${APPLLICATION_REPOSITORY}* && \
    npm install && \
    npm run build
    
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage app/Security-front-develop/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
