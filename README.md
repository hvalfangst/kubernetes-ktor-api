# Ktor banking API with Flyway & Exposed deployed on k8s


## Requirements

* x86-64
* Linux/WSL
* Docker
* Kubernetes

## Creating resources
The shell script "up.sh" is responsible for allocating Kubernetes resources.

```
./up.sh
```

## Destroying resources
The shell script "down.sh" is responsible for deallocating Kubernetes resources.

```
./down.sh
```