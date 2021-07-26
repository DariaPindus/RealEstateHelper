export CONFIG_SERVER_PATH=/home/daria/Documents/MyPrograms/config-server
export CONFIG_SERVER_JAR=target/config-server-0.0.1-SNAPSHOT.jar
export DISCOVERY_SERVER_PATH=/home/daria/Documents/MyPrograms/config-server
export DISCOVERY_SERVER_JAR=target/config-server-0.0.1-SNAPSHOT.jar

# build config-server
cd $CONFIG_SERVER_PATH
mvn clean package -DksipTests
# start config-server
if cspid=$(pgrep -f $CONFIG_SERVER_JAR)
then
    echo "Running config-server, pid is $cspid"
else
    echo "Stopped"
    java -jar $CONFIG_SERVER_JAR &
fi

# build service-discovery
cd $DISCOVERY_SERVER_PATH
mvn clean package -DksipTests
# start config-server
if cspid=$(pgrep -f $DISCOVERY_SERVER_JAR)
then
    echo "Running config-server, pid is $cspid"
else
    echo "Stopped"
    java -jar $DISCOVERY_SERVER_JAR &
fi

#docker run -d --name my-redis -p 6379:6379 -v ~/redis-pers:/data redis:rc-alpine
#docker run -d --name my-activemq -p 61616:61616 -p 8161:8161 -d rmohr/activemq:5.15.9

docker container start my-redis
docker container start my-activemq

# check running jars:
# pgrep -fa .*.jar