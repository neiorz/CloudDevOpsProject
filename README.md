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
| `k8s/` | Kubernetes manifests | planned |
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

## Notes

- The course README lists `auth-service` as Java and `roadmap-service` as Python. The code is the other way around, as the table above shows.
- `PIP_DEFAULT_TIMEOUT` and `PIP_RETRIES` are set in `app/auth-service/Dockerfile` because `pip install` timed out on a slow internet connection.
