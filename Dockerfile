# syntax=docker/dockerfile:1

# ---- build ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# resolve dependencies first so they are cached until pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -ntp dependency:go-offline

COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -ntp package -DskipTests \
    && cp target/*.jar app.jar \
    && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# ---- runtime ----
FROM eclipse-temurin:25-jre
RUN groupadd --system spring && useradd --system --gid spring spring
WORKDIR /app

# least to most frequently changing layers
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]
