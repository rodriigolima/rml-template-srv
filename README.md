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
| **Java** | 21 |
| **Framework** | Spring Boot 3.4.5 |
| **Banco** | Nenhum por padrão — adaptador in-memory incluso |
| **Cobertura** | 100% (JaCoCo enforced no `mvn verify`) |

> O SRV contém a lógica de negócio do domínio.  
> Nunca é chamado diretamente pelo FED — sempre via BFF.

---

## Estrutura

```
src/main/java/br/com/rml/SERVICE_NAME/
│
├── Application.java                          ← @SpringBootApplication
│
├── domain/
│   ├── model/
│   │   └── Product.java                      ← modelo de domínio puro (record, sem JPA)
│   ├── port/
│   │   ├── in/
│   │   │   └── ManageProductUseCase.java     ← interface de entrada (use case)
│   │   └── out/
│   │       └── ProductRepositoryPort.java    ← interface de saída (repositório)
│   └── service/
│       └── ProductService.java               ← regras de negócio, implementa o use case
│
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── ProductController.java        ← REST controller, chama use case
│   │       ├── dto/
│   │       │   ├── ProductRequestDto.java    ← record com validações (@NotBlank, @NotNull)
│   │       │   └── ProductResponseDto.java   ← record de resposta
│   │       └── mapper/
│   │           └── ProductWebMapper.java     ← MapStruct: DTO ↔ domínio
│   └── out/
│       └── inmemory/
│           └── InMemoryProductAdapter.java   ← implementa ProductRepositoryPort (ConcurrentHashMap)
│
└── infrastructure/
    └── config/
        ├── SecurityConfig.java               ← stateless, todos os endpoints públicos (dev)
        └── SpringDocConfig.java              ← Swagger UI
```

---

## Como usar este template

### 1. Criar o repositório
GitHub → **New repository** → **Template: `org-rml/rml-template-srv`**  
Nome: `rml-srv-{dominio}-{projeto}` → ex: `rml-srv-product-catalog`

### 2. Substituir os placeholders

| Placeholder | Substituir por | Exemplo |
|-------------|---------------|---------|
| `SERVICE_NAME` | nome do serviço | `product-catalog` |
| `rml-srv-SERVICE_NAME` | nome do artefato | `rml-srv-product-catalog` |
| `br.com.rml.SERVICE_NAME` | package base | `br.com.rml.productcatalog` |
| `Product` | nome do seu domínio | `Order`, `Client`, etc. |

### 3. Trocar o adaptador de persistência
O template vem com `InMemoryProductAdapter` para rodar sem banco.  
Para usar banco de dados:
1. Adicione a dependência desejada no `pom.xml` (JPA, MongoDB, Redis, etc.)
2. Crie um novo adapter implementando `ProductRepositoryPort` (ex: `JpaProductAdapter`)
3. Remova o `@Component` do `InMemoryProductAdapter`
4. O `ProductService` não muda uma linha

---

## Arquitetura Hexagonal — Fluxo

```
HTTP Request
    │
    ▼
ProductController (adapter/in/web)
    │  chama port IN
    ▼
ManageProductUseCase (domain/port/in)
    │  implementado por
    ▼
ProductService (domain/service)
    │  chama port OUT
    ▼
ProductRepositoryPort (domain/port/out)
    │  implementado por
    ▼
InMemoryProductAdapter  ←→  (substitua por JPA/MongoDB/etc.)
```

**Regra de ouro:** o domínio nunca conhece os adapters. Os adapters conhecem o domínio.

---

## Executando Localmente

```bash
# Compilar (necessário para gerar os mappers do MapStruct)
mvn compile -s "$env:USERPROFILE\.m2\settings-personal.xml"

# Rodar
mvn spring-boot:run -s "$env:USERPROFILE\.m2\settings-personal.xml"
```

Swagger: `http://localhost:8080/swagger-ui.html`

---

## Testes

```bash
# Testes unitários
mvn test -s "$env:USERPROFILE\.m2\settings-personal.xml"

# Testes + relatório de cobertura JaCoCo (target/site/jacoco/index.html)
mvn verify -s "$env:USERPROFILE\.m2\settings-personal.xml"
```

| Suite | Testes | Cobertura |
|-------|--------|-----------|
| `ProductServiceTest` | 11 | domain/service — 100% |
| `InMemoryProductAdapterTest` | 7 | adapter/out — 100% |
| `ProductControllerTest` | 9 | adapter/in/web — 100% |

> JaCoCo quebra o build se a cobertura cair abaixo de 100%.  
> Excluídos do check: mappers gerados (MapStruct), `Application`, configs de infra.

---

## Dependências Principais

| Dependência | Versão |
|-------------|--------|
| Spring Boot | 3.4.5 |
| rml-common-core | 1.0.0-SNAPSHOT |
| MapStruct | 1.5.5.Final |
| Lombok | 1.18.38 |
| SpringDoc OpenAPI | 2.5.0 |
| JaCoCo | 0.8.13 |

---

## CI/CD

Pipeline `.github/workflows/ci.yml`:

| Job | Trigger | Ação |
|-----|---------|------|
| `build` | push/PR | `mvn verify` (testes + cobertura) |
| `docker` | push main | Build + push GHCR |
| `deploy-sandbox` | push develop | Bump `values-version.yaml` branch `sandbox` |
| `deploy-staging` | após sandbox + aprovação | Bump branch `staging` |
| `deploy-production` | push main + aprovação | Bump branch `production` |

### Secrets necessários

| Secret | Descrição |
|--------|-----------|
| `PACKAGES_TOKEN` | PAT com `read:packages` — baixar `rml-common` do GitHub Packages |
| `CONFIG_REPO_TOKEN` | PAT com `repo` — fazer push no config repo |


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
