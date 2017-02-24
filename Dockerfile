FROM dockergarten/payara-micro
RUN mkdir -p /opt/payara/.moodini
COPY build/libs/moodini.war ${DEPLOYMENT_DIR}
