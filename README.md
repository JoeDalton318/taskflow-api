# TaskFlow API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)

API REST de gestion de tâches collaborative avec Spring Boot, PostgreSQL et authentification JWT.

## Fonctionnalités

- CRUD complet des tâches avec assignation multi-utilisateurs
- Authentification JWT (Spring Security + BCrypt)
- Pagination, filtres et recherche full-text
- Tests unitaires JUnit 5 (couverture 70%+)
- Documentation Swagger/OpenAPI 3.0
- Migrations Flyway versionnées
- Docker & Docker Compose
- CI/CD GitHub Actions

## Technologies

**Backend:** Java 21, Spring Boot 3.4.0, Spring Security, Spring Data JPA  
**Base de données:** PostgreSQL 15, Flyway  
**Sécurité:** JWT (JJWT 0.12.3), BCrypt  
**Tests:** JUnit 5, Mockito, H2, JaCoCo  
**Documentation:** Swagger/OpenAPI  
**DevOps:** Docker, Maven, GitHub Actions

## Démarrage rapide

### Avec Docker (recommandé)

```bash
docker-compose up -d
```

**Accès :**

- API : <http://localhost:8081>
- Swagger UI : <http://localhost:8081/swagger-ui.html>
- Base de données : localhost:5432

### Sans Docker

**Prérequis :** PostgreSQL 15+, Java 21, Maven

```bash
# Créer la base de données
createdb taskflow
createuser taskflow_user -P

# Variables d'environnement
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=taskflow
export DB_USER=taskflow_user
export DB_PASSWORD=taskflow_password
export JWT_SECRET=dGFza2Zsb3ctc2VjcmV0LWtleS1mb3ItcHJvZHVjdGlvbi11c2UtMjU2LWJpdHMtbWluaW11bQ==

# Démarrer l'application
mvn spring-boot:run
```

## API

### Authentification

**Inscription**

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","username":"user","password":"password123"}'
```

**Connexion**

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Tâches

**Créer**

```bash
curl -X POST http://localhost:8081/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Nouvelle tâche","status":"TODO","priority":"MEDIUM"}'
```

**Lister**

```bash
curl http://localhost:8081/api/tasks?page=0&size=10 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Rechercher**

```bash
curl "http://localhost:8081/api/tasks?search=mot-clé" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Filtrer**

```bash
curl "http://localhost:8081/api/tasks?status=IN_PROGRESS&priority=HIGH" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Tests

```bash
# Exécuter les tests
mvn test

# Générer le rapport de couverture
mvn jacoco:report

# Rapport disponible : target/site/jacoco/index.html
```

## Schéma de base de données

```sql
users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(20) NOT NULL,
  enabled BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
)

tasks (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  status VARCHAR(20) NOT NULL,
  priority VARCHAR(20) NOT NULL,
  due_date TIMESTAMP,
  creator_id BIGINT NOT NULL REFERENCES users(id),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
)

task_assignments (
  task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  PRIMARY KEY (task_id, user_id)
)
```

## Architecture

```text
taskflow-api/
├── src/
│   ├── main/
│   │   ├── java/com/taskflow/api/
│   │   │   ├── config/          # Configuration (Security, CORS, OpenAPI)
│   │   │   ├── controller/      # Contrôleurs REST
│   │   │   ├── dto/             # Objets de transfert
│   │   │   ├── entity/          # Entités JPA
│   │   │   ├── exception/       # Gestion des erreurs
│   │   │   ├── repository/      # Couche d'accès aux données
│   │   │   ├── security/        # JWT et authentification
│   │   │   └── service/         # Logique métier
│   │   └── resources/
│   │       ├── db/migration/    # Scripts Flyway
│   │       └── application.yml  # Configuration Spring
│   └── test/                    # Tests unitaires et d'intégration
├── .github/workflows/           # CI/CD GitHub Actions
├── docker-compose.yml           # Orchestration Docker
├── Dockerfile                   # Image Docker multi-stage
└── pom.xml                      # Dépendances Maven
```

## Variables d'environnement

| Variable         | Défaut        | Description              |
|------------------|---------------|--------------------------|
| `DB_HOST`        | localhost     | Hôte PostgreSQL          |
| `DB_PORT`        | 5432          | Port PostgreSQL          |
| `DB_NAME`        | taskflow      | Nom de la base           |
| `DB_USER`        | taskflow_user | Utilisateur DB           |
| `DB_PASSWORD`    | - - - - - - - | Mot de passe DB          |
| `JWT_SECRET`     | - - - - - - - | Clé secrète JWT (base64) |
| `JWT_EXPIRATION` | 86400000      | Durée du token (ms)      |
| `PORT`           | 8080          | Port de l'application    |

## Comptes de démonstration

L'application est livrée avec 3 utilisateurs de test :

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| admin@taskflow.com | admin123 | ADMIN |
| user@taskflow.com | user123 | USER |
| manager@taskflow.com | manager123 | USER |

## Déploiement

### Docker Hub

```bash
docker build -t votre-username/taskflow-api:latest .
docker push votre-username/taskflow-api:latest
```

### Production

1. Configurer les variables d'environnement
2. Utiliser un secret JWT fort (générer avec `openssl rand -base64 64`)
3. Limiter CORS aux domaines autorisés
4. Activer HTTPS
5. Configurer les sauvegardes PostgreSQL

## Licence

MIT
