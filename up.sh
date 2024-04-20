#!/bin/sh

DB_SERVER="db"
DB_PORT=5432
DB_SCHEMA="DB"
DB_USER="MP77"
DB_PASSWORD="IDecreeAndDeclareWarOnShitePerformance"
DB_MIGRATION="classpath:db/migration"

JWT_ISSUER="me"
JWT_AUDIENCE="you"
JWT_SECRET="ALieIsSweetInTheBeginningButBitterInTheEnd"

LOG_PATH="logs"
LOG_PREFIX="api"

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <local or kubernetes>"
  exit 1
fi

# Run locally with docker-compose
if [ "$1" = "local" ]; then
  echo "Running locally..."

  docker-compose -f local/docker-compose.yml up -d
  ./gradlew build
  ./gradlew run
fi

# Run exclusively on Kubernetes
if [ "$1" = "kubernetes" ]; then
  echo "Deploying to Kubernetes..."

  echo "Build image [hvalbanken-api] from Dockerfile"
    if ! docker build -t hvalbanken-api:666 .; then
        echo
        echo "[Error building image 'hvalbanken-api' - Exiting script]"
        exit 1
    fi

  echo -e "\n\n"

  #-------------------------------------------------------------------------

  b64_db_server=$(echo -n $DB_SERVER | base64 | tr -d '\n')
  b64_db_port=$(echo -n $DB_PORT | base64 | tr -d '\n')
  b64_db_schema=$(echo -n $DB_SCHEMA | base64 | tr -d '\n')
  b64_db_user=$(echo -n $DB_USER | base64 | tr -d '\n')
  b64_db_password=$(echo -n $DB_PASSWORD | base64 | tr -d '\n')
  b64_db_migration=$(echo -n $DB_MIGRATION | base64 | tr -d '\n')

  sed -i "s|^\(.*server: \)\(.*\)|\1$b64_db_server|" k8s/db_secrets.yml
  sed -i "s|^\(.*port: \)\(.*\)|\1$b64_db_port|" k8s/db_secrets.yml
  sed -i "s|^\(.*schema: \)\(.*\)|\1$b64_db_schema|" k8s/db_secrets.yml
  sed -i "s|^\(.*user: \)\(.*\)|\1$b64_db_user|" k8s/db_secrets.yml
  sed -i "s|^\(.*password: \)\(.*\)|\1$b64_db_password|" k8s/db_secrets.yml
  sed -i "s|^\(.*migration: \)\(.*\)|\1$b64_db_migration|" k8s/db_secrets.yml

  # Create a k8s secret based on contents of manifest file "db_secrets.yml"
  kubectl apply -f k8s/db_secrets.yml > /dev/null 2>&1

  #-------------------------------------------------------------------------

  b64_jwt_issuer=$(echo -n $JWT_ISSUER | base64 | tr -d '\n')
  b64_jwt_audience=$(echo -n $JWT_AUDIENCE | base64 | tr -d '\n')
  b64_jwt_secret=$(echo -n $JWT_SECRET | base64 | tr -d '\n')

  sed -i "s|^\(.*issuer: \)\(.*\)|\1$b64_jwt_issuer|" k8s/jwt_secrets.yml
  sed -i "s|^\(.*audience: \)\(.*\)|\1$b64_jwt_audience|" k8s/jwt_secrets.yml
  sed -i "s|^\(.*secret: \)\(.*\)|\1$b64_jwt_secret|" k8s/jwt_secrets.yml

  # Create a k8s secret based on contents of manifest file "jwt_secrets.yml"
  kubectl apply -f k8s/jwt_secrets.yml > /dev/null 2>&1

  #-------------------------------------------------------------------------

  b64_log_path=$(echo -n $LOG_PATH | base64 | tr -d '\n')
  b64_log_prefix=$(echo -n $LOG_PREFIX | base64 | tr -d '\n')

  sed -i "s|^\(.*path: \)\(.*\)|\1$b64_log_path|" k8s/log_secrets.yml
  sed -i "s|^\(.*prefix: \)\(.*\)|\1$b64_log_prefix|" k8s/log_secrets.yml

  # Create a k8s secret based on contents of manifest file "log_secrets.yml"
  kubectl apply -f k8s/log_secrets.yml > /dev/null 2>&1

  #-------------------------------------------------------------------------

  kubectl apply -f k8s/database.yml > /dev/null 2>&1
  echo "Creating DB..."
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
  exit 1
fi

echo "Invalid argument. Use: $0 <local or kubernetes>"
exit 1