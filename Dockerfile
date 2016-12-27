FROM payara/micro
MAINTAINER Marcus Fihlon, fihlon.ch
ENV DEPLOYMENT_DIR /opt/payara/deployments
COPY build/libs/moodini.war ${DEPLOYMENT_DIR}
ENTRYPOINT java -jar /opt/payara/payara-micro.jar --deploymentDir ${DEPLOYMENT_DIR}
