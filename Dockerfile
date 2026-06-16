# Use the official IBM MQ Developer image as the base
FROM icr.io/ibm-messaging/mq:latest

# Automatically accept the IBM license agreement
ENV LICENSE=accept

# Define the default Queue Manager name
ENV MQ_QMGR_NAME=QM1

# Set app/admin passwords for dev image users
ENV MQ_APP_PASSWORD=123456
ENV MQ_ADMIN_PASSWORD=123456

# Apply dev channel auth settings automatically during queue manager startup
COPY mq-config/20-dev-channels.mqsc /etc/mqm/

# Expose MQ traffic port and Web Console port
EXPOSE 1414 9443
