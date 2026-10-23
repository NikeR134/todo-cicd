# TODO Service with CI/CD

Spring Boot REST API for a task list. Tasks are stored in memory. The API supports creating, listing, updating, and deleting tasks; request validation rejects empty titles.

## Run locally

Requires Java 21 and Maven.

```shell
mvn spring-boot:run
```

The API listens on port 8080. Health information is available at `/actuator/health`.

## API

| Method | Path | Result |
| --- | --- | --- |
| POST | `/tasks` | Creates a task |
| GET | `/tasks` | Lists tasks |
| PUT | `/tasks/{id}` | Updates a task |
| DELETE | `/tasks/{id}` | Deletes a task |

Example request:

```json
{"title":"Buy milk","description":"2 liters","completed":false}
```

## CI/CD

GitHub Actions runs Maven verification, enforces at least 70% line coverage, caches Maven dependencies, stores the JAR as a workflow artifact, builds and publishes a Docker image to GHCR, and reports each job in the workflow summary. Pull requests run the build and tests without publishing the image.

To enable deployment, configure repository variable `DEPLOY_ENABLED` as `true` and add these repository secrets:

- `SERVER_HOST`
- `SERVER_USER`
- `SERVER_SSH_KEY`
- `GHCR_READ_TOKEN` with read access to the container package

The test server must have Docker Engine and the Compose plugin installed. The workflow connects over SSH, pulls the new image, and recreates the service. Secrets are read from GitHub Actions and are not stored in the repository.

## Docker

```shell
docker build -t todo:local .
docker run --rm -p 8080:8080 todo:local
```
