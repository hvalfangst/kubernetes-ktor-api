#!/bin/sh

echo

kubectl delete -f k8s/secrets.yml > /dev/null 2>&1
kubectl delete -f k8s/database.yml > /dev/null 2>&1
kubectl delete -f k8s/nginx-config.yml > /dev/null 2>&1
kubectl delete -f k8s/nginx.yml > /dev/null 2>&1
kubectl delete -f k8s/api.yml  > /dev/null 2>&1

echo "Deleting resources defined in .yaml files"
./misc/progress_bar.sh 0.10