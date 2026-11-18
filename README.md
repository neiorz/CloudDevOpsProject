# CloudDevOpsProject

A microservices web application used as the final project of the Cloud DevOps Accelerator Program (NTI x iVolve). The application is built, containerized, scanned, deployed to Kubernetes and delivered with a GitOps workflow.

> This README is the first version. It is extended as each part of the project is finished.

## Application

The application lets a user sign up, log in and see a DevOps roadmap page. The source code comes from the course repository `iVolveFinalProject`; the code is kept unchanged, except for one line in the `auth-service` Dockerfile (see Notes).

| Service | Technology | Port | What it does |
|---------|------------|------|--------------|
| `frontend` | Node.js 22, Express, EJS | 3000 | Web UI, talks to the two backend services |
| `auth-service` | Python 3.12, Flask | 5000 | Sign up and login, stores users in MySQL (bcrypt hashes); `/health` endpoint |
| `roadmap-service` | Java 21, Spring Boot | 8080 | Returns the roadmap data on `/api/roadmap` |
| `mysql` | MySQL 8.0 | 3306 | Database `ivolve`, used only by `auth-service` |

## Repository layout

| Path | Content | Status |
|------|---------|--------|
| `app/` | The three microservices with their Dockerfiles | done |
| `docker-compose.yml` | Local run of the application with the database | done |
| `terraform/` | AWS infrastructure modules | planned |
| `ansible/` | Jenkins server configuration | planned |
| `k8s/`, `secrets-example/` | Kubernetes manifests and an example Secret | done |
| `jenkins/`, `vars/` | Pipelines and Shared Library | planned |
| `argocd/` | Argo CD Application | planned |

## Run it locally

Requirements: Docker and Docker Compose.

```bash
git clone https://github.com/neiorz/CloudDevOpsProject.git
cd CloudDevOpsProject
cp .env.example .env
# edit .env and replace every "change-me-..." value with your own secret
docker compose up -d --build
docker compose ps
```

Open `http://localhost:3000`, create an account, log in and the roadmap page opens.

Useful checks:

```bash
curl http://127.0.0.1:5000/health          # {"database":"connected","status":"UP"}
curl http://127.0.0.1:8080/api/roadmap     # roadmap data
```

Stop the application (the database volume is kept):

```bash
docker compose down
```

The `.env` file holds the passwords and is ignored by Git. Only `.env.example`, with placeholder values, is committed.

## Run it on Kubernetes (minikube)

minikube is used as a local test cluster. The target cluster of the project is EKS on AWS, so only the storage provisioner changes between the two.

```bash
minikube start
minikube addons enable ingress

# load the images built by docker compose into minikube
docker tag ivolve-frontend:latest nourz1/ivolve-frontend:local
docker tag ivolve-auth-service:latest nourz1/ivolve-auth-service:local
docker tag ivolve-roadmap-service:latest nourz1/ivolve-roadmap-service:local
minikube image load nourz1/ivolve-frontend:local
minikube image load nourz1/ivolve-auth-service:local
minikube image load nourz1/ivolve-roadmap-service:local

# the Secret is created from the local .env file and is never committed
kubectl apply -f k8s/00-namespace.yaml
set -a; . ./.env; set +a
kubectl -n ivolve create secret generic ivolve-secret \
  --from-literal=MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD" \
  --from-literal=DB_PASSWORD="$DB_PASSWORD" \
  --from-literal=SESSION_SECRET="$SESSION_SECRET"
unset MYSQL_ROOT_PASSWORD DB_PASSWORD SESSION_SECRET

kubectl apply -f k8s/
kubectl -n ivolve get pods

echo "$(minikube ip) ivolve.local" | sudo tee -a /etc/hosts
```

Open `http://ivolve.local`.

| Manifest | What it creates |
|----------|-----------------|
| `00-namespace.yaml` | Namespace `ivolve` |
| `01-configmap.yaml` | Non-secret settings: database host, name and user, service URLs |
| `02-storageclass.yaml` | StorageClass `ivolve-storage` |
| `03-mysql-headless-service.yaml` | Headless Service `mysql-headless` for the database |
| `04-mysql-statefulset.yaml` | MySQL StatefulSet with a 1Gi persistent volume |
| `05-auth-service.yaml` | Deployment (2 replicas) and Service |
| `06-roadmap-service.yaml` | Deployment (2 replicas) and Service |
| `07-frontend.yaml` | Deployment (2 replicas) and Service |
| `08-ingress.yaml` | Ingress `ivolve.local` to the frontend |

Design notes:

- The namespace is `ivolve` in lowercase, because Kubernetes does not accept capital letters in names.
- The real Secret is not in Git. `secrets-example/ivolve-secret.example.yaml` only shows its shape. A production setup would use Sealed Secrets or an external secret manager.
- The frontend keeps sessions in the memory of each pod, so the Ingress uses cookie affinity. A shared session store such as Redis would remove this limit.
- The `auth-service` readiness probe calls `/health`, which checks the database, so no traffic reaches a pod that cannot reach MySQL.
- On EKS the StorageClass provisioner becomes `ebs.csi.aws.com`; the other manifests stay the same.
- To query MySQL inside the pod, use `mysql -h 127.0.0.1 ...`; without `-h` the client looks for a socket file and fails.

## Notes

- The course README lists `auth-service` as Java and `roadmap-service` as Python. The code is the other way around, as the table above shows.
- `PIP_DEFAULT_TIMEOUT` and `PIP_RETRIES` are set in `app/auth-service/Dockerfile` because `pip install` timed out on a slow internet connection.
