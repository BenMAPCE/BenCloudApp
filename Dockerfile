FROM amazoncorretto:8-alpine

ENV postgresql_host 192.168.0.152
ENV postgresql_port 5432
ENV postgresql_database docker
ENV postgresql_user docker
ENV postgresql_password docker

WORKDIR /bencloud

COPY build/libs/BenCloudServer.jar /bencloud/BenCloudServer.jar
COPY bencloud-server.properties /bencloud/bencloud-server.properties
ADD config /bencloud/config
ADD www /bencloud/www

ENTRYPOINT java -jar /bencloud/BenCloudServer.jar





