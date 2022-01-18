FROM nginx:alpine

WORKDIR /bencloud-quasar/dist/spa

RUN mkdir -p /var/www
# RUN cp -R /builds/devsecops/laravel-reference-example /var/www

COPY ./docker/bencloudapp/nginx.conf /etc/nginx/

RUN apk update \
    && apk upgrade \
    && adduser -D -H -u 1000 -s /bin/bash www-data -G www-data

COPY ./docker/bencloudapp/sites/ /etc/nginx/sites-available/

CMD nginx

EXPOSE 80
