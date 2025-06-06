FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-glibc AS builder
WORKDIR /builder
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM bellsoft/liberica-runtime-container:jre-21-cds-glibc AS runtime
WORKDIR /application
ARG UID=10001
RUN apk add --no-cache g++
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/compiler" \
    --shell "/sbin/nologin" \
    --uid "${UID}" \
    appuser
USER appuser
EXPOSE 8080
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
ENTRYPOINT ["java", "-jar", "application.jar"]
