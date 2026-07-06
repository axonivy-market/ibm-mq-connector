#!/bin/bash
(
  echo "=== Start load_messages.sh ===" > /etc/mqm/load.log
  
  # 1. Wait for Queue Manager to be Running
  until dspmq -m QM1 | grep -q "Running"; do
    echo "Waiting for QM1 to start..." >> /etc/mqm/load.log
    sleep 2
  done
  echo "QM1 is Running!" >> /etc/mqm/load.log

  # 2. Wait for the queue DEV.QUEUE.1 to be created
  until echo "DISPLAY QLOCAL(DEV.QUEUE.1)" | runmqsc QM1 | grep -q "QUEUE(DEV.QUEUE.1)"; do
    echo "Waiting for DEV.QUEUE.1 to be created..." >> /etc/mqm/load.log
    sleep 2
  done
  echo "DEV.QUEUE.1 created!" >> /etc/mqm/load.log

  # 3. Clear existing messages from the queue to prevent accumulation
  echo "CLEAR QLOCAL(DEV.QUEUE.1)" | runmqsc QM1 >> /etc/mqm/load.log 2>&1
  echo "DEV.QUEUE.1 cleared!" >> /etc/mqm/load.log

  # 4. Put 3 preloaded messages
  echo 'Preloaded Message from Docker 1' | /opt/mqm/samp/bin/amqsput DEV.QUEUE.1 QM1 >> /etc/mqm/load.log 2>&1
  echo 'Preloaded Message from Docker 2' | /opt/mqm/samp/bin/amqsput DEV.QUEUE.1 QM1 >> /etc/mqm/load.log 2>&1
  echo 'Preloaded Message from Docker 3' | /opt/mqm/samp/bin/amqsput DEV.QUEUE.1 QM1 >> /etc/mqm/load.log 2>&1
  echo "Messages preloaded! Exit code: $?" >> /etc/mqm/load.log
) &
