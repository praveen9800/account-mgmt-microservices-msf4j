# Microservices communication using synchronous protocol like HTTP/HTTPS - MSF4J

MSF4J - Microservices Framework for Java is a WSO2 framework for developing microservices. It is compatible with other spring based dependencies
and work together.


## Dependency

```
<dependency>
	<groupId>org.wso2.msf4j</groupId>
	<artifactId>msf4j-spring</artifactId>
	<version>2.0.0</version>
</dependency>
```

## Configuring MSF4J through Spring

### 1. Changing HTTP port

```
@Configuration
public class TransportConfiguration {

    @Bean
    public HTTPTransportConfig http(){
         return new HTTPTransportConfig(8090);
     }

}
```

## Overview

There are four microservices developed to demonstrate the Account Management use case. Typically the basic operations done on a banking customer account.

### 1. Account Inquiry Microservice
### 2. Credit Microservice
### 3. Debit Microservice
### 4. Fund Transfer Microservice

![Image of Workflow](https://github.com/praveen9800/microservice-sample-msf4j/blob/master/accountinquiry_msf4j.png)


## Account Inquiry Microservice

This microservice provides the account details of a banking customer. It fetches the account related data from MySQL database where all the customer data are stored.

### URI to test the GET service

```
http://localhost:8090/bank/accounts/1011
```

## Sample Response

```
{
  "accNo" : 101,
  "accName" : "AAAAA",
  "amount" : 10000,
  "currency" : "INR"
}
```

## Credit Microservice

This microservice deals with the amount credit of particular customer's account. It updates the customer's account stored in MySQL database.

### URI to test the POST service

```
http://localhost:8100/bank/credit
```

## Sample Request

```
{
  "accNo" : 101,
  "accName" : "AAAAA",
  "amount" : 2000,
  "currency" : "INR"
}
```
## Sample Response

```
Transaction Success!!! INR 2000.0 successfully credited to the account 101
```

## Debit Microservice

This microservice deals with the amount debit of particular customer's account. It updates the customer's account stored in MySQL database.

### URI to test the POST service

```
http://localhost:8090/bank/accounts/1011
```

## Sample Request

```
{
  "accNo" : 101,
  "accName" : "AAAAA",
  "amount" : 2000,
  "currency" : "INR"
}

```
## Sample Response

```
Transaction Success!!! INR 2000.0 successfully debited from the account 101
```

## Fund Transfer Microservice

This microservice deals with the transfer of funds from one customer's accout to other customer. Once this microservice is invoked, it will first communicate with debit microservice. If the debit
operation success, then it will call the credit microservice and inturn debit/credit status will be updated in MySQL database by Debit/Credit Microservices. Once the transaction is completed,
the transaction history will be updated in Mongo database by Fund Transfer Microservice.

If for any reason debit operation is success and credit is failed, then the transaction will be reversed and the customer account will be credited with the appropriate amount.


### URI to test the POST service

```
http://localhost:8120/bank/fundTransfer
```

## Sample Request

```
{
  "fromAccount" : 101,
  "toAccount" : 102,
  "amount" : 2000,
  "currency" : "INR"
}

```
## Sample Response

```
Transaction Success!!! INR 2000.0 debited from the account 101 and credited to the account 102
```

## Service to view the transaction history

Apart from the four major microservices, an additional service was developed which is to view the transaction history stored in the Mongo database. It will list all the transactions done
on a particular customer account.


### URI to test the GET service

```
http://localhost:8120/bank/fundTransfer/transactions/101
```

## Sample Response

```
[
    {
        "fromAccount": 101,
        "toAccount": 102,
        "amount": 2000,
        "currency": "INR",
        "startTime": "Feb 26, 2019 4:51:23 PM",
        "endTime": "Feb 26, 2019 4:51:24 PM",
        "status": "Success"
    },
    {
        "fromAccount": 101,
        "toAccount": 102,
        "amount": 2000,
        "currency": "INR",
        "startTime": "Feb 27, 2019 2:02:56 PM",
        "endTime": "Feb 27, 2019 2:02:57 PM",
        "status": "Success"
    }
]
```