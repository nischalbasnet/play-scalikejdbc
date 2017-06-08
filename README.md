### Sample playframework app with scalikejdbc with component interface binding
=================================

#### Steps to run:
 1. Make copy of application.conf.example, remove .example and update with proper db info
 2. Go to project root and run using "sbt run"
 3. Application with run at "localhost:9000"
 
#### Components:
=================================

##### 1. Controller 
  - Handles users request and passes the request to Service component to perform work
##### 2. Service
  - Takes request from Controller and uses different components DAOs and other services to full fill the request
##### 3. DAO
  - Performs action to retreive and save data from different sources
  - Contains different contracts/interfaces for Read and Write so that sources can adhere to one that make sense to them

##### Components talk to each other using interface and the concrete implementation is bind using module DIBindingModule class
  
