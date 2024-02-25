#!/bin/sh

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <local|k8s>"
  exit 1
fi

if [ "$1" = "local" ]; then
  docker-compose -f local/docker-compose.yml down
  exit $?
fi

if [ "$1" = "kubernetes" ]; then
  kubectl delete -f k8s/secrets.yml > /dev/null 2>&1
  kubectl delete -f k8s/database.yml > /dev/null 2>&1
  kubectl delete -f k8s/nginx-config.yml > /dev/null 2>&1
  kubectl delete -f k8s/nginx.yml > /dev/null 2>&1
  kubectl delete -f k8s/api.yml  > /dev/null 2>&1

  echo "Deleting resources defined in .yaml files"
  ./misc/progress_bar.sh 0.10
fi

echo "Invalid argument. Use: $0 <local|k8s>"
exit 1
