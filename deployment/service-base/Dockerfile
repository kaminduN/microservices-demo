FROM openjdk:8u151-alpine
MAINTAINER Kamindu.nanayakkara

ENV APP_VERSION=0.0.1-SNAPSHOT \
    APP_NAME=app-api \
    STORAGE_REPO=service-repo.dist.io \
    APP_PORT=8080

EXPOSE ${APP_PORT}

# logs are accessible from docker logs
#default user is root
ADD docker-entrypoint.sh /root
RUN chmod +x /root/docker-entrypoint.sh

ENTRYPOINT ["/root/docker-entrypoint.sh"] 
