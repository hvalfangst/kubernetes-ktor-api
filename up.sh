#!/bin/sh

  echo "Build image [hvalbanken] from Dockerfile"
  if ! docker build -t hardokkerdocker/hvalfangst:hvalbanken-api .; then
      echo
      echo "[Error building image 'hvalbanken' - Exiting script]"
      exit 1
  fi

echo -e "\n\n"

# Prompt user for basic auth name
read -p "Enter desired username for basic auth: " username

echo -e "\n\n"

# Base64 encode the username
encoded_username=$(echo -n "$username" | base64 | tr -d '\n')

# Set field username in secrets.yaml to point to the recently encoded username
sed -i "s|^\(.*username: \)\(.*\)|\1$encoded_username|" k8s/secrets.yaml

# Prompt user for basic auth password
read -p "Enter desired password for basic auth: " password

echo -e "\n\n"

# Base64 encode the password
encoded_password=$(echo -n "$password" | base64 | tr -d '\n')

# Set field password in secrets.yaml to point to the recently encoded password
sed -i "s|^\(.*password: \)\(.*\)|\1$encoded_password|" k8s/secrets.yaml

# Create our deployment and service resources
kubectl apply -f k8s/nginx.yaml > /dev/null 2>&1
kubectl apply -f k8s/manifest.yaml > /dev/null 2>&1

echo "Creating resources defined in manifest.yaml"
./misc/progress_bar.sh 0.25

echo -e "\n\n"

cluster_ip=$(kubectl get svc entrypoint -n default -o jsonpath='{.spec.clusterIP}')

# Get the output of the "kubectl describe svc entrypoint" command
service_definition=$(kubectl describe svc entrypoint)

# Extract the first occurrence of line associated with port definition for the db deployment 
db_port_line=$(echo "$service_definition" | grep "db" | head -n 1)

# Extract the port number for db
db_port=$(echo "$db_port_line" | awk '{print $3}' | cut -d "/" -f1)

# Assign service name for DB and Encryption deployments
db_url="$cluster_ip:$db_port/postgres?user=postgres&password=admin"

# Base64 encode URLs
b64_db_url=$(echo -n "$db_url" | base64 | tr -d '\n')

# Use 'sed' to overwrite value of the field "database-url" contained in yaml file "secrets"
sed -i "s|^\(.*database-url: \)\(.*\)|\1$b64_db_url|" k8s/secrets.yaml

# Create a k8s secret based on contents of manifest file "secrets.yaml"
kubectl apply -f k8s/secrets.yaml > /dev/null 2>&1


# Restart our api deployment in order to provision secrets
kubectl scale deployment hvalbanken-api --replicas=0 > /dev/null 2>&1 && kubectl scale deployment hvalbanken-api --replicas=3  > /dev/null 2>&1

echo "Applying secrets..."
./misc/progress_bar.sh 0.25

echo

echo "Preparing pods..."
./misc/progress_bar.sh 0.69

echo -e "\n\n"

# List pods
kubectl get pods