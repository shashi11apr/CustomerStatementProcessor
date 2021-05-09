# Customer Statement Validation Processor #

### What is this project for? ###

* Quick summary

  Rabobank receives monthly deliveries of customer statement records. This information is delivered in JSON Format. These records need to be validated based on below criteria:
  
     1 All transaction references should be unique
     2 The end balance needs to be validated ( Start Balance +/- Mutation = End Balance ) 


* Version 0.0.1


**1. Clone the repository** 

```bash
https://github.com/MohanMugi/customer-statement-processor.git
```

**2. Run the app using maven**

```bash
cd customerstatementprocessor
mvn spring-boot:run
```

The application can be accessed at `http://localhost:8081/statementprocesssor/validatestatements`


You may also package the application in the form of a jar and then run the jar file like so -

```bash
mvn clean package
java -jar target/customerstatementprocessor-0.0.1-SNAPSHOT.jar
```