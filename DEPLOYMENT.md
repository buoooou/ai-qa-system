# Deployment Guide

This document describes how to build, package, and deploy the AI QA microservice platform onto the provided EC2 server (`3.113.113.164`) using Docker Compose and the existing GitHub Actions workflow.

## Prerequisites

- EC2 instance (Amazon Linux) with Docker installed (already provided).
- GitHub repository with this codebase and GitHub Actions enabled.
- Docker Hub account for publishing images.
- Nacos server, Gemini API key, and other configuration secrets (see `.env` section).
- SSH key-pair for accessing the EC2 instance.

## Repository Structure

```
backend-services/
  api-gateway/
  qa-service/
  user-service/
frontend-nextjs/
  frontend/
docker-compose.yml            # Local build/development
docker-compose.prod.yml       # Production/EC2 deployment
.github/workflows/ci.yml      # CI/CD pipeline
deploy/deploy.sh              # Remote deployment script
```

## Environment Variables

Services rely on environment variables for database connections, Nacos, JWT secrets, etc. Maintain them via GitHub secrets for CI/CD and a `.env` (not committed) on EC2.

| Variable | Description |
| --- | --- |
| `MYSQL_ROOT_PASSWORD` | Root password for MySQL |
| `MYSQL_DATABASE` | Schema name (default `ai_qa_system`) |
| `MYSQL_USER`, `MYSQL_PASSWORD` | Application DB user credentials |
| `NACOS_SERVER_ADDR` | Nacos server address (e.g., `3.113.113.164:8848`) |
| `JWT_SECRET` | Shared JWT signing secret |
| `GEMINI_API_KEY` | Google Gemini API key |
| `FRONTEND_API_BASE_URL` | Used by frontend (default `http://api-gateway:8083`) |
| `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN` | Docker Hub credentials |
| `EC2_HOST`, `EC2_USERNAME`, `SSH_PRIVATE_KEY` | Needed for SSH deploy step |

The sample `deploy/env.sample` illustrates required settings.

## GitHub Actions CI/CD Workflow

The workflow `.github/workflows/ci.yml` executes on pushes/PRs to `main`:

1. **Build & Test**
   - Runs Maven build (`mvn clean package`) for backend modules.

2. **Docker Build & Push**
   - Logs into Docker Hub using repository secrets.
   - Builds multi-stage images for each backend and the frontend.
   - Tags them as `${DOCKERHUB_USERNAME}/ai-qa-[service]:latest` and pushes to Docker Hub.

3. **Deploy to EC2**
   - Uses `appleboy/ssh-action` with secrets (`EC2_HOST`, `EC2_USERNAME`, `SSH_PRIVATE_KEY`).
   - Exports environment variables from GitHub Secrets.
   - Clones/pulls the repository on the EC2 host.
   - Runs `docker compose -f docker-compose.prod.yml pull` and `... up -d --remove-orphans`.
   - Prunes old images.

Ensure all required secrets are set in GitHub before running the workflow.

## Docker Compose Stacks

### Development (`docker-compose.yml`)
- Builds images locally using multi-stage Dockerfiles.
- Brings up MySQL, user-service, qa-service, api-gateway, and frontend.
- Usage:
  ```bash
  docker compose up --build
  ```
- Accessible URLs:
  - Frontend: http://localhost:3000
  - Gateway: http://localhost:8083
  - User Service: http://localhost:8081
  - QA Service: http://localhost:8082

### Production (`docker-compose.prod.yml`)
- Pulls published images from Docker Hub instead of building locally.
- Use `docker compose -f docker-compose.prod.yml up -d` on EC2.
- References the same environment variables.

## Manual Deployment (Optional)

1. SSH into EC2:
   ```bash
   ssh -i /path/to/key.pem ec2-user@3.113.113.164
   ```
2. Clone repo (first time):
   ```bash
   git clone https://github.com/<your-org>/ai-qa-system.git
   cd ai-qa-system
   cp deploy/env.sample .env  # update values as needed
   chmod +x deploy/deploy.sh
   ```
3. Deploy:
   ```bash
   ./deploy/deploy.sh
   ```

## Troubleshooting

- Ensure Docker service is running on EC2 (`sudo service docker start`).
- Validate environment variables; missing `JWT_SECRET` or `GEMINI_API_KEY` will cause runtime failures.
- Use `docker compose logs [service-name]` to inspect container logs.
- Verify that Nacos and MySQL endpoints are reachable from the EC2 instance.

With the above setup, CI/CD should automatically build and redeploy the entire stack whenever changes land on `main`. Update this document if new services or environment variables are introduced.
