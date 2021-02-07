### MySQL config
Enter mysql  
`> CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'SECRET';`  
`> create database rental_fetches;`  
`> GRANT ALL PRIVILEGES ON rental_fetches.* TO 'appuser'@'localhost';`

To start Mysql:  
`sudo service mysqld start`

_In case of problems (i.e. running docker-compose fails in main-service (Spring boot app) with_ `Access denied for user 'appuser'@'172.24.0.4' `). The reason can be with MySql password - it works for sure when password contains alpha-numeric characters (Hello, security, my old friend :))  

### ActiveMQ config
- Install activemq  
- Go to installation folder (/your/path/apache-activemq-5.16.0/bin/) and run  
`./activemq start`
- ActiveMQ management url - http://localhost:8161/admin/  

**Set ActiveMQ Security**  
Add this config to conf file (location in Unix: /path/to/apache-activemq-X/conf/activemq.xml)  
<!-- Added -->
	<plugins>
	    <simpleAuthenticationPlugin anonymousAccessAllowed="false">
		<users>
		    <authenticationUser username="admin" password="<PASSWORD>" groups="admins, producers, consumers"/>
		</users>
	    </simpleAuthenticationPlugin>
	    <authorizationPlugin>
		<map>
		    <authorizationMap>
		        <authorizationEntries>
		            <authorizationEntry queue="rental" read="consumers" write="producers" admin="producers,consumers,admins" />    
		            <authorizationEntry topic="ActiveMQ.Advisory.>" read="consumers" write="producers" admin="producers,consumers,admins"/>
		            <authorizationEntry topic="rental" read="consumers" write="producers" admin="producers,consumers,admins"/>
		        </authorizationEntries>
		    </authorizationMap>
		</map>
	    </authorizationPlugin>
	</plugins>  
	
Keep in mind, \<PASSWORD> should be configured as env variable for Docker compose

### Wildfly
Download [wildfly](https://www.wildfly.org/downloads/)  
Once downloaded, unzip and browse to WILDFLY_HOME/bin and run the add-user (.sh/.bat) script  
http://127.0.0.1:9990/console/index.html  
Manager user should be created  
Once the user is added, run standalone (.sh/.bat) to start the server. If started successfully, browse to the following URL: http://127.0.0.1:9990/console/index.html

`mvn -e clean package -DskipTests`  
This command packages the application. Notice that we have configured the Wildfly plugin to execute in the maven install phase with the goal deploy.  

Once the build completes, browse to http://127.0.0.1:9990/console/index.html#deployments and we can see the deployed WAR file.

_Tip in case of java.lang.OutOfMemoryError: Metaspace:_  
Undeploy and restart wildfly container

_Tip in case of Failed to execute goal deploy: WFLYCTL0063: Composite operation was rolled back_  
Check running wildfly logs. Probably the issue can be with port 8080 being already in use. 
To check this and find id of a proces: `sudo netstat -nlp | grep 8080` 


### Telegram Bot   
Bot creation is pretty straight-forward. Details could be found here: https://core.telegram.org/bots#2-how-do-bots-work  
> There's a… bot for that. Just talk to BotFather (described below) and follow a few simple steps.   
> Once you've created a bot and received your authorization token, head down to the Bot API manual to see what you can teach your bot to do.


### Docker
For correct docker-compose execution with `docker-compose run` file with name ".env" should exist in current (app) folder.


### Tests
Tests use Testcontainer. They should be run in configured docker environment.   
Docker should be able to run from current user (without sudo)