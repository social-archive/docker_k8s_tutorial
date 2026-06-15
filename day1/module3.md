# 모듈 3 — Spring 애플리케이션 컨테이너화

> **목표**: Spring Boot 예제 앱을 Docker 이미지로 빌드하고 실행하는 흐름을 경험한다.  
> "애플리케이션이 이미지가 되고 컨테이너로 실행된다"는 흐름을 확실히 잡는다.

---

## 3-1. Antigravity IDE에서 Dockerfile 열기

1. Antigravity IDE 왼쪽 탐색기에서 `spring-app/Dockerfile` 클릭
2. 파일 구조를 살펴본다

**Dockerfile 구조 요약**

```dockerfile
# --- 빌드 스테이지 ---
FROM eclipse-temurin:17-jdk-alpine AS builder
# Gradle로 JAR 빌드

# --- 실행 스테이지 ---
FROM eclipse-temurin:17-jre-alpine
# 빌드된 JAR만 복사해서 실행
```

> 💡 **멀티스테이지 빌드**: JDK로 빌드하고 JRE만 담아 실행하므로  
> 최종 이미지 크기를 최소화할 수 있다.

---

## 3-2. Spring 앱 이미지 빌드

`spring-app/` 디렉토리에서 진행한다.

```powershell
# spring-app 디렉토리로 이동
cd ..\spring-app

# Docker 이미지 빌드 (처음에는 의존성 다운로드로 수 분 소요)
docker build -t todo-app:1.0 .
```

> ⏳ 첫 빌드는 Gradle 의존성을 다운로드하므로 3~5분 정도 소요된다.  
> 두 번째 빌드부터는 캐시를 활용해 빠르게 완료된다.

---

## 3-3. 빌드 결과 확인

```powershell
# 빌드된 이미지 확인
docker images | findstr todo-app
```

**정상 출력 예시**

```
todo-app   1.0   xxxxxxxxxxxx   1 minute ago   xxx MB
```

---

## 3-4. 단독 실행 (DB 없이 — 오류 확인용)

```powershell
# PostgreSQL 없이 단독 실행
docker run -d -p 8080:8080 --name todo-test todo-app:1.0

# 로그 확인 → DB 연결 오류 발생 (정상)
docker logs todo-test
```

**예상 로그**

```
...
com.example.todo: Connection refused: localhost/127.0.0.1:5432
```

> ✅ 이 오류는 **정상**이다.  
> PostgreSQL 없이 Spring 앱만 단독 실행하면 DB 연결을 못 해 오류가 발생한다.  
> 다음 모듈에서 Compose로 함께 실행하면 해결된다.

```powershell
# 테스트 컨테이너 정리
docker rm -f todo-test
```

---

## 3-5. Antigravity IDE에서 Dockerfile 분석 (선택)

Antigravity IDE 에서 `spring-app/Dockerfile`을 열고 AI 채팅에 아래와 같이 질문해볼 수 있다.

```
이 Dockerfile의 멀티스테이지 빌드 구조를 설명해줘
```

---

## ✅ 모듈 3 완료 기준

- [ ] `docker build -t todo-app:1.0 .` 빌드가 성공한다
- [ ] `docker images`에서 `todo-app` 이미지가 확인된다
- [ ] 단독 실행 시 DB 연결 오류가 발생하는 이유를 이해한다

---

[← 모듈 2](./module2.md) | [← 목차로 돌아가기](./README.md) | [모듈 4 →](./module4.md)
