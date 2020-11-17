### MySQL config
Enter mysql  
`> CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'SECRET';`  
`> create database rental_fetches;`  
`> GRANT ALL PRIVILEGES ON rental_fetches.* TO 'appuser'@'localhost';`

To start Mysql:  
`sudo service mysqld start`

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

Once the build completes, berowse to http://127.0.0.1:9990/console/index.html#deployments and we can see the deployed WAR file.

_Tip in case of java.lang.OutOfMemoryError: Metaspace:_  
Undeploy and restart wildfly container

_Tip in case of Failed to execute goal deploy: WFLYCTL0063: Composite operation was rolled back_  
Check running wildfly logs. Probably the issue can be with port 8080 being already in use. 
To check this and find id of a proces: `sudo netstat -nlp | grep 8080` 
