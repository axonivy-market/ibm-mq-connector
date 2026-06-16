docker build -t quachson-mq:dev .
docker run -d --name my-mq-container -p 1414:1414 -p 9443:9443 quachson-mq:dev