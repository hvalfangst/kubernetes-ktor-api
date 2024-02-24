#!/bin/sh

#  echo "Build image [hvalbanken-api] from Dockerfile"
#  if ! docker build -t hvalbanken-api:666 .; then
#      echo
#      echo "[Error building image 'hvalbanken-api' - Exiting script]"
#      exit 1
#  fi

echo -e "\n\n"


kubectl apply -f k8s/database.yml > /dev/null 2>&1
echo "Creating DB..."
./misc/progress_bar.sh 0.10


# Prompt user for desired encryption key, which is used for crypto operations on tokens
read -p "Enter desired encryption key: " encryption_key

echo -e "\n\n"

# Base64 encode the provided encryption key
encoded_encryption_key=$(echo -n "$encryption_key" | base64 | tr -d '\n')

# Set field encryption-key in secrets.yml to point to the recently encoded encryption key
sed -i "s|^\(.*encryption-key: \)\(.*\)|\1$encoded_encryption_key|" k8s/secrets.yml

# Create database URL based on service name associated with Postgres deployment
db_url="db:5432/postgres?user=postgres&password=admin"

# Base64 encode URLs
b64_db_url=$(echo -n "$db_url" | base64 | tr -d '\n')

# Use 'sed' to overwrite value of the field "database-url" contained in yaml file "secrets"
sed -i "s|^\(.*database-url: \)\(.*\)|\1$b64_db_url|" k8s/secrets.yml

# Create a k8s secret based on contents of manifest file "secrets.yml"
kubectl apply -f k8s/secrets.yml > /dev/null 2>&1

echo "Creating secrets..."
./misc/progress_bar.sh 0.10

kubectl apply -f k8s/nginx-config.yml > /dev/null 2>&1
echo "Creating Nginx config..."
./misc/progress_bar.sh 0.10

kubectl apply -f k8s/nginx.yml > /dev/null 2>&1
echo "Creating Nginx..."
./misc/progress_bar.sh 0.10

kubectl apply -f k8s/api.yml > /dev/null 2>&1
echo "Creating API..."
./misc/progress_bar.sh 0.10

echo "Preparing pods..."
./misc/progress_bar.sh 0.10

# List pods
kubectl get pods