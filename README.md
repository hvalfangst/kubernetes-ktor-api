# Ktor API deployed on Kubernetes

API programmed in Kotlin with the Ktor micro-framework utilizing Exposed for ORM and Redis for cache deployed on Kubernetes. Routes are protected by JWT.


## Requirements

* [x86-64](https://en.wikipedia.org/wiki/X86-64)
* [Linux/WSL](https://learn.microsoft.com/en-us/windows/wsl/install)
* [JDK 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
* [Gradle](https://gradle.org/)
* [Docker](https://www.docker.com/products/docker-desktop/)
* [Kubernetes](https://www.docker.com/products/docker-desktop/)

## Running the project

A startup script named 'up' has been provided for building and running the API. This script takes an argument with two permutations: local or kubernetes.
Executing this script with the local flag will first spin up Docker containers for PostgresSQL and Redis and then invoke gradle build & run.
The Kubernetes option will containerize our codebase based on a supplied Dockerfile and deploy a fully-fledged Kubernetes cluster based on the included manifest files.

After having deployed to Kubernetes it is paramount that one utilizes the 'port-forward' command from Kubectl in order to -actually- be able to reach cluster resources.
It is recommended to develop in 'local' mode as build times for JVM docker images are pretty bad. 

## Allocating resources
The shell script "up.sh" is responsible for allocating resources.

```
./up.sh local -> Creates DB and Redis containers, which are bridged on the same network to emulate Kubernetes.
./up.sh kubernetes -> Containerizes our API and provisions Kubernetes resources.
```

## Deallocating resources
The shell script "down.sh" is responsible for deallocating resources. 

```
./down.sh local -> Stops containers, removes images and network.
./down.sh kubernetes -> Deletes secrets, services and deployments.
```

## Postman
A postman collection has been provided under the 'postman' folder.