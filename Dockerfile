# Mirrors the real container — Eclipse Temurin 17 JRE + OTEL Java agent.
FROM eclipse-temurin:17.0.18_8-jre

ARG OTEL_AGENT_VERSION=2.1.0
WORKDIR /app

# Download the OpenTelemetry Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar /app/otel.jar

COPY build/libs/travel-card-api-standalone-*-all.jar /app/app.jar

ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317 \
    OTEL_SERVICE_NAME=travel-card-api-standalone \
    OTEL_RESOURCE_ATTRIBUTES=service.namespace=travelcard,deployment.environment=AP

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/otel.jar", "-jar", "/app/app.jar"]
