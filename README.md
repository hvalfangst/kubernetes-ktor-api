# Ktor banking API with Flyway & Exposed deployed on k8s



## Requirements

* x86-64
* Linux
* Docker
* k8s

## Creating resources
The shell script "up.sh" is responsible for building the local Docker image and creating requested resources, which are defined in our k8s manifest.

```
sh up.sh
```

## Destroying resources
The shell script "down.sh" frees up allocated resources.

```
sh down.sh
```

## Routes

### Customers 
GET/POST http://localhost:8082/customers

GET/PUT/DELETE http://localhost:8082/customers/{id}

GET http://localhost:8082/customers/{id}/accounts-and-loans

DELETE http://localhost:8082/customers/all/{customerId}


### Accounts 
GET/POST http://localhost:8082/accounts

GET/PUT/DELETE http://localhost:8082/accounts/{id}

DELETE http://localhost:8082/accounts/all/{customerId}
### Loans 
GET/POST http://localhost:8082/loans

GET/PUT/DELETE http://localhost:8082/loans/{id}

DELETE http://localhost:8082/loans/all/{customerId}

