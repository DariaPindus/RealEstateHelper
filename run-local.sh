docker run -d --name my-redis -p 6379:6379 -v ~/redis-pers:/data redis:rc-alpine
docker run -d --name my-activemq -p 61616:61616 -p 8161:8161 -d rmohr/activemq:5.15.9
