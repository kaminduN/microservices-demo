#!/bin/sh


cd $HOME #/home/root
wget ${STORAGE_REPO}/${APP_NAME}/${APP_NAME}-${APP_VERSION}.jar
# now convert the jar to a generic name to be used
mv ${APP_NAME}-${APP_VERSION}.jar app-api.jar

java -jar /root/app-api.jar
