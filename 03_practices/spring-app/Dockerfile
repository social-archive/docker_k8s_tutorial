# =======================================================================
# 1단계: Gradle 빌드 스테이지
# =======================================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

# Gradle 래퍼 및 의존성 캐시 레이어 (소스 변경 시 재다운로드 방지)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon > /dev/null 2>&1 || true

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# =======================================================================
# 2단계: 실행 스테이지 (JRE만 포함 - 이미지 크기 최소화)
# =======================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 빌더 스테이지에서 JAR만 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 컨테이너 실행 포트
EXPOSE 8080

# 헬스체크 (Docker Compose / Kubernetes 모두 활용)
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
