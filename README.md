# Skeleton API and UI Project
A skeleton project containing a fully working Java API and Angular UI allowing anyone to start developing quickly

This is fully working end to end sample including:

* Keycloak integration for both the API and the Angular frontend.  
* Dropwizard Metrics for all API calls  
* Swagger integration for documentation of API calls  
* Persistence set up to allow access to DB entities using Hibernate/JPA  
* Connects to the Config database to retrieve DB connection strings and Keycloak config

## API Information  

The project is setup as follows.

#### database  
Contains all the Java DB entities for the tables in the models folder and PersistenceManager which handles the connections to the DB.
 
#### endpoints  
Contains all the endpoints for the API where most of the code is held

#### framework
Contains standard exception classes and swagger bootstrap code.  This is also where the Metrics are initialised on loading

#### json
JSON representations of the DB entities to allow communication between frontend and backend

#### Metrics 
Contains the standard implementations for the metrics code

#### Running API
create a run configuration in intelliJ using Tomcat -> Local.  
In deployment tab, click + choose Artifact and select API:war exploded
In the startup tab, click on debug and add these options into the environment variables. 

CONFIG_JDBC_USERNAME=postgres
CONFIG_JDBC_PASSWORD=YOURPOSTGRESPASSWORD
CONFIG_JDBC_URL=jdbc:postgresql://localhost:5432/config

Click run and it should be up and running.

## Frontend Information
This is using Angular CLI so use this for creating any new components.  https://cli.angular.io/ for more information.

service.ts files are used to communicate with the API.

#### Running Frontend.  
Create a run configuration in intelliJ using npm
in the script input type start
Click run and it should start running on [http://localhost:4200](http://localhost:4200/) 


**Please keep this project updated with any additions/modifications when starting or contributing to a new project to allow others to get up to speed as quickly as possible.**

