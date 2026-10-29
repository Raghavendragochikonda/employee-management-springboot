HEAD
# Employee Management — Java Spring Boot + EC2 CI/CD

This is a small Java/Spring Boot employee management app for practising the complete deployment workflow: local run → GitHub → GitHub Actions CI → SSH authentication → EC2 Docker deployment → testing → logs.

## Project structure

```text
employee-management-springboot/
├── src/main/java/com/example/employeemanager/
│   ├── EmployeeManagementApplication.java
│   ├── controller/EmployeeController.java
│   ├── model/Employee.java
│   └── service/EmployeeService.java
├── src/main/resources/
│   ├── application.properties
│   └── static/index.html
├── src/test/java/com/example/employeemanager/controller/
│   └── EmployeeControllerTest.java
├── .github/workflows/ci-cd.yml
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Requirements

- JDK 21
- Maven 3.9+
- Git
- Docker Desktop (for local Docker testing)
- GitHub account and AWS account

## Step 1: Run locally

From the folder containing `pom.xml`:

```bash
mvn test
mvn spring-boot:run
```

Open http://localhost:8080

## API endpoints

- `GET /api/health` — app health
- `GET /api/employees` — list employees
- `GET /api/employees/{id}` — get one employee
- `POST /api/employees` — create employee
- `DELETE /api/employees/{id}` — delete employee

Example create request:
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Sam Lee","email":"sam@example.com","department":"Engineering","role":"Developer"}'
```

Data is stored in memory, not a database. It resets whenever the application restarts. This is intentional to keep the first deployment simple.

## Step 2: Docker

```bash
docker build -t employee-management-springboot .
docker run --rm -p 8080:8080 employee-management-springboot
```

Or:
```bash
docker compose up -d --build
docker compose logs -f app
```

## Step 3: Push to GitHub

Create an empty repository (e.g. `employee-management-springboot`) and run from the project folder:

```bash
git init
git add .
git commit -m "Add Spring Boot employee management app"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPO.git
git push -u origin main
```

## Step 4: EC2

1. Launch Ubuntu EC2. Check current AWS pricing and Free Tier eligibility.
2. Allow SSH TCP 22 from your IP only. Allow TCP 8080 from your IP only for testing. For production, use HTTPS through a reverse proxy or load balancer.
3. Connect with the instance's SSH key.
4. Install Git, Docker Engine and Docker Compose using official Docker instructions for your Ubuntu version.
5. Confirm `git --version`, `docker --version`, and `docker compose version`.

## Step 5: GitHub Actions secrets

Repository → Settings → Secrets and variables → Actions. Add:
- `EC2_HOST`: EC2 public IPv4/DNS
- `EC2_USER`: usually `ubuntu`
- `EC2_SSH_PRIVATE_KEY`: full private key text for the EC2 key pair

Never commit private keys. The workflow clones the repository on EC2, so this starter assumes a public repository. Private repositories need a secure deploy key or other repository authentication mechanism.

## Step 6: CI/CD

Push to `main`. GitHub Actions:
1. Checks out code.
2. Sets up Java 21 and Maven cache.
3. Runs unit tests and packages the app.
4. Builds a Docker image.
5. Connects to EC2 using the SSH secrets.
6. EC2 fetches `main` and rebuilds/restarts the app with Docker Compose.
7. Calls `/api/health` and fails the deployment if the endpoint is unhealthy.

## Step 7: Make a small change

Edit the heading in `src/main/resources/static/index.html`, then:
```bash
git add .
git commit -m "Update employee management heading"
git push origin main
```
Watch the GitHub Actions run and refresh the EC2 page after success.

## Step 8: Test on EC2

Open:
- `http://EC2-PUBLIC-IP:8080/`
- `http://EC2-PUBLIC-IP:8080/api/health`
- `http://EC2-PUBLIC-IP:8080/api/employees`

## Logs

On EC2:
```bash
docker ps
docker logs --tail 100 employee-management-springboot
docker logs -f employee-management-springboot
```

## Important limitations

- Employee records are in-memory and reset on restart.
- No login/authentication is included in this employee CRUD demo.
- Do not expose a real employee-management system publicly without authentication, authorization, HTTPS, persistent storage, validation and other production safeguards.
- Terminate the EC2 instance when finished practising to avoid ongoing charges.
# employee-management-springboot
 294022976d3ca200a61f1153c32f142ba8178680
