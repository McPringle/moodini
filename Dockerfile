FROM dockergarten/payara-micro
RUN mkdir -p /opt/payara/.moodini
COPY build/libs/moodini.war ${DEPLOYMENT_DIR}
HEALTHCHECK --interval=5s --timeout=3s --retries=3 CMD curl --fail http://localhost:8080/api/healthcheck || exit 1

