#!/bin/sh

DB_URL="5432/postgres?user=postgres&password=cookiecutter"

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <local|k8s>"
  exit 1
fi

if [ "$1" = "local" ]; then
  echo "Running locally..."

  docker-compose -f local/docker-compose.yml up -d
  export DATABASE_URL="localhost:$DB_URL"
  export POD_NAME="LOCAL"
  ./gradlew build
  ./gradlew run
  exit $?
fi

if [ "$1" = "kubernetes" ]; then
  echo "Deploying to Kubernetes..."

  echo "Build image [hvalbanken-api] from Dockerfile"
    if ! docker build -t hvalbanken-api:666 .; then
        echo
        echo "[Error building image 'hvalbanken-api' - Exiting script]"
        exit 1
    fi

  echo -e "\n\n"

  kubectl apply -f k8s/database.yml > /dev/null 2>&1
  echo "Creating DB..."
  ./misc/progress_bar.sh 0.10

  # Base64 encode URL associated with our Postgres container
  b64_db_url=$(echo -n "db:$DB_URL" | base64 | tr -d '\n')

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
fi

echo "Invalid argument. Use: $0 <local|k8s>"
exit 1