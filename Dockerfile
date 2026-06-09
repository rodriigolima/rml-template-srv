# syntax=docker/dockerfile:1
# Multi-stage build — imagem final enxuta (~200MB vs ~600MB)
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

ARG GITHUB_ACTOR

# Cria settings.xml usando BuildKit secret — token nunca fica em camada da imagem
RUN --mount=type=secret,id=packages_token \
    mkdir -p /root/.m2 && \
    printf '<settings>\n  <servers>\n    <server>\n      <id>github</id>\n      <username>%s</username>\n      <password>%s</password>\n    </server>\n  </servers>\n</settings>\n' \
    "$GITHUB_ACTOR" "$(cat /run/secrets/packages_token)" > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B
RUN rm -f /root/.m2/settings.xml

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuário não-root por segurança
RUN addgroup -S rml && adduser -S rml -G rml
USER rml

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
