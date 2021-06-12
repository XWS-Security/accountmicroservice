FROM node:lts-alpine as frontBuild
ARG APPLLICATION_REPOSITORY=XWS-Security-front
WORKDIR /src
RUN apk --update --no-cache add curl tar && \
    curl -L https://github.com/XWS-Security/XWS-Security-front/archive/refs/heads/certificates.tar.gz | tar -xz && \
    mv $(ls -d */|head -n 1) app && \
    cd app && \
    npm install && \
    npm run build && \
    cd dist

FROM nginx:1.19.8-alpine as gatewayWithFront
COPY --from=frontBuild src/app/dist /usr/share/nginx/html/app
COPY ./config/nginx.conf /etc/nginx/nginx.conf
COPY ./config/api_gateway.conf /etc/nginx/api_gateway.conf
COPY ./config/ssl/intermediate-cert.pem /etc/ngnix/intermediate-cert.pem
COPY ./config/ssl/intermediate-key.pem /etc/ngnix/intermediate-key.pem
