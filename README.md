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