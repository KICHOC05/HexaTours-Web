# ═══════════════════════════════════════════════════════
# HexaTours — Build & Runtime
# Java 21 · Spring Boot 3.3 · MariaDB driver (external DB)
# ═══════════════════════════════════════════════════════

# ── Etapa 1: compilación ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cachea dependencias: si pom.xml no cambia, no se descargan de nuevo
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ── Etapa 2: runtime ───────────────────────────────────
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Usuario sin privilegios
RUN groupadd --system spring \
 && useradd --system --gid spring --home /app --shell /bin/false spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring
EXPOSE 8080

# Config 100% por variables de entorno:
#   DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD,
#   JPA_DDL_AUTO, APP_REMEMBER_ME_KEY, CLOUDINARY_URL, ...
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
