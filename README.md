# rml-template-srv

Template para criação de serviços **SRV (microserviço de domínio)** no padrão `rml`.  
Marcar como **Template Repository** no GitHub para uso em novos projetos.

---

## Visão Geral

| Item | Valor |
|------|-------|
| **Tipo** | SRV (microserviço de domínio) |
| **Padrão** | Arquitetura Hexagonal (Ports and Adapters) |
| **Porta** | `8080` |
| **Java** | 21 (Eclipse Temurin) |
| **Framework** | Spring Boot 3.4.5 |
| **Banco** | PostgreSQL + Liquibase |
| **Imagem Docker** | `ghcr.io/org-rml/rml-srv-br.com.{projeto}:latest` |

> O SRV contém a lógica de negócio do domínio e persiste dados no banco.  
> Nunca é chamado diretamente pelo FED — sempre via BFF.

---

## Estrutura

```
src/main/java/br/com/rml/SERVICE_NAME/
│
├── ServiceApplication.java              ← @SpringBootApplication
│
├── domain/
│   ├── model/
│   │   └── Sample.java                  ← modelo de domínio puro (sem anotações JPA)
│   ├── port/
│   │   ├── in/
│   │   │   └── ManageSampleUseCase.java ← interface de entrada (use case)
│   │   └── out/
│   │       └── SampleRepositoryPort.java ← interface de saída (repositório)
│   └── service/
│       └── SampleService.java           ← implementa o use case, usa porta de saída
│
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── SampleController.java    ← REST controller, chama use case
│   │       ├── dto/
│   │       │   ├── SampleRequest.java
│   │       │   └── SampleResponse.java
│   │       └── mapper/
│   │           └── SampleWebMapper.java ← MapStruct: DTO ↔ domínio
│   └── out/
│       └── persistence/
│           ├── SampleEntity.java        ← entidade JPA (extends BaseLongEntity)
│           ├── SampleJpaRepository.java ← extends BaseLongRepository
│           ├── SamplePersistenceAdapter.java ← implementa SampleRepositoryPort
│           └── SamplePersistenceMapper.java  ← MapStruct: Entity ↔ domínio
│
└── infrastructure/
    └── config/
        ├── SecurityConfig.java          ← stateless, valida JWT do srv-auth
        └── SpringDocConfig.java         ← Swagger com Bearer auth
```

---

## Como usar este template

### 1. Criar o repositório
GitHub → **New repository** → **Template: `org-rml/rml-template-srv`**  
Nome: `rml-srv-{dominio}-{projeto}` → ex: `rml-srv-client-sovarais`

### 2. Substituir os placeholders

| Placeholder | Substituir por | Exemplo |
|-------------|---------------|---------|
| `SERVICE_NAME` | nome do serviço | `client-sovarais` |
| `rml-srv-SERVICE_NAME` | nome do artefato | `rml-srv-client-sovarais` |
| `br.com.rml.SERVICE_NAME` | package base | `br.com.rml.clientsovarais` |
| `SampleController` | nome do controller | `ClientController` |
| `Sample` | nome da entidade/domínio | `Client` |
| `samples` | nome da tabela SQL | `clients` |

### 3. Criar o config repo
GitHub → **New repository** → **Template: `org-rml/rml-template-srv-config`**  
Nome: `rml-srv-{dominio}-{projeto}-config`

---

## Fluxo de Dados

```
BFF (rml-bff-*)
    │ Feign (REST interno K8s)
    ▼
SRV Controller
    │
    ▼
Use Case (port/in)
    │
    ▼
Service (domain)
    │ port/out
    ▼
Persistence Adapter
    │
    ▼
PostgreSQL
```

---

## Configuração

### Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | `8080` | Porta da aplicação |
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `rml_SERVICE_NAME` | Nome do banco |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | _(secret)_ | Senha do banco |
| `JWT_SECRET` | _(secret)_ | Mesma chave do `rml-srv-auth` |

---

## Executando Localmente

```bash
mvn clean spring-boot:run -s "$env:USERPROFILE\.m2\settings-personal.xml"
```

Swagger: `http://localhost:8080/swagger-ui.html`

### Banco local

```bash
docker run -d --name postgres-dev \
  -e POSTGRES_DB=rml_SERVICE_NAME \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:15-alpine
```

---

## CI/CD

Pipeline `.github/workflows/ci.yml`:

| Job | Trigger | Ação |
|-----|---------|------|
| `build` | push/PR | Maven build + testes |
| `docker` | push | Build + push GHCR (BuildKit secret) |
| `deploy-sandbox` | push develop | Bump `values-version.yaml` branch `sandbox` (DEV) |
| `deploy-staging` | após sandbox + aprovação | Bump branch `staging` (HOM) |
| `deploy-production` | push main + aprovação | Bump branch `production` (PRD) |

### Secrets necessários no repositório

| Secret | Descrição |
|--------|-----------|
| `PACKAGES_TOKEN` | PAT com `read:packages` — baixar `rml-common` do GitHub Packages |
| `CONFIG_REPO_TOKEN` | PAT com `repo` — fazer push no config repo |

---

## Dependências Principais

| Dependência | Versão |
|-------------|--------|
| Spring Boot | 3.4.5 |
| rml-common | 1.0.0-SNAPSHOT |
| PostgreSQL driver | (gerenciado pelo Boot) |
| Liquibase | (gerenciado pelo Boot) |
| MapStruct | 1.5.5 |
| Lombok | 1.18.38 |
| jjwt | 0.11.5 |
