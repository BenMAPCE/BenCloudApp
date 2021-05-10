FROM amazoncorretto:8-alpine

WORKDIR /bencloud

COPY build/libs/BenCloudServer.jar /bencloud/BenCloudServer.jar
COPY bencloud-server.properties /bencloud/bencloud-server.properties
ADD config /bencloud/config
ADD www /bencloud/www

ENTRYPOINT java -jar /bencloud/BenCloudServer.jar





