#!/bin/sh

echo

kubectl delete -f k8s/nginx.yaml > /dev/null 2>&1
kubectl delete -f k8s/secrets.yaml > /dev/null 2>&1
kubectl delete -f k8s/manifest.yaml  > /dev/null 2>&1

echo "Deleting resources defined in .yaml files"
./misc/progress_bar.sh 0.333