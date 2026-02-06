# Product Catalog Service
## Overview
- The Product Catalog Service is responsible for managing product listings, categories, product details, search, and filtering capabilities across the Mazadak platform.

- It exposes a REST API, persists product data, orchestrates Temporal saga workflows for listing creation and deletion, and emits domain events to other services.

- The Product Catalog Service is the owner of product and listing state within the platform.

## API Endpoints
- See [Product Catalog Service Wiki Page](https://github.com/Mazaadak/.github/wiki/Product-Catalog-Service) for a detailed breakdown of the service's API endpoints
- Swagger UI available at `http://localhost:18086/swagger-ui/index.html` when running locally

## How to Run
You can run it via [Docker Compose](https://github.com/Mazaadak/mazadak-infrastructure) or <!-- [Kubernetes](https://github.com/Mazaadak/mazadak-k8s/) -->

## Tech Stack
- **Spring Boot 3.5.6** (Java 21) 
- **PostgreSQL**
- **Apache Kafka**
- **Temporal** - Workflow Orchestration
- **Netflix Eureka** - Service Discovery
- **Docker & Kubernetes** - Deployment & Containerization
- **Micrometer, OpenTelemetry, Alloy, Loki, Prometheus, Tempo, Grafana** - Observability
- **OpenAPI/Swagger** - API Documentation

## For Further Information
Refer to [Product Catalog Service Wiki Page](https://github.com/Mazaadak/.github/wiki/Product-Catalog-Service).
