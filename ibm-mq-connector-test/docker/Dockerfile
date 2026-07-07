# Use the official IBM MQ Developer image as the base
# Pin to a specific, supported IBM MQ image version (avoid using `latest`)
FROM icr.io/ibm-messaging/mq:9.3.0.0-r1

# Automatically accept the IBM license agreement
ENV LICENSE=accept

# Define the default Queue Manager name
ENV MQ_QMGR_NAME=QM1

# Set app/admin passwords for dev image users
ENV MQ_APP_PASSWORD=123456
ENV MQ_ADMIN_PASSWORD=123456

# Apply dev channel auth settings automatically during queue manager startup
COPY mq-config/20-dev-channels.mqsc /etc/mqm/
COPY setup.mqsc /etc/mqm/
COPY load_messages.sh /etc/mqm/

# Set correct ownership and execution permissions for mqm user
USER root
RUN chown -R 1001:0 /etc/mqm && chmod +x /etc/mqm/load_messages.sh
USER 1001

# Expose MQ traffic port and Web Console port
EXPOSE 1414 9443
