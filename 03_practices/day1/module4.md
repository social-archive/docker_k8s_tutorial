# 모듈 4 — Docker Compose 기반 Spring + PostgreSQL 연동

> **목표**: Spring 애플리케이션과 PostgreSQL을 각각 컨테이너로 구성하고
> Compose를 통해 함께 기동한다. 환경변수 분리와 서비스 의존성 설정을 이해한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd workspace`로 이동한 `day1/` 디렉터리 기준입니다.

## 4-1. Antigravity IDE 또는 사용 중인 IDE에서 관련 파일 열기

1. `day1/compose.yml` 클릭 — 서비스 구성 확인
2. `day1/.env` 클릭 — 환경변수 확인

**파일 구조 요약**

```
day1/
├── compose.yml   ← Spring + PostgreSQL 서비스 정의
└── .env          ← DB 접속 정보 (환경변수)
```

---

## 4-2. .env 파일 내용 확인

```powershell
# day1 디렉토리로 이동
cd ..\day1

# .env 파일 내용 확인
type .env
```

**예시**

```
POSTGRES_USER=todo
POSTGRES_PASSWORD=todo1234
POSTGRES_DB=tododb
DB_HOST=postgres
DB_PORT=5432
DB_NAME=tododb
DB_USER=todo
DB_PASSWORD=todo1234
```

> 💡 `DB_HOST=postgres`는 같은 Compose 네트워크 안에서
> 서비스 이름(`postgres`)을 호스트명으로 사용하기 때문이다.

---

## 4-3. compose.yml 구조 이해

Antigravity IDE 또는 사용 중인 IDE에서 `day1/compose.yml`을 열어 구조를 확인한다.

핵심 포인트:

| 항목 | 설명 |
|---|---|
| `depends_on: condition: service_healthy` | PostgreSQL이 준비된 후에만 Spring 앱이 시작된다 |
| `env_file: .env` | `.env` 파일에서 환경변수를 자동으로 로드한다 |
| `volumes: postgres-data` | 컨테이너가 재시작되어도 DB 데이터가 유지된다 |
| `healthcheck` | PostgreSQL 준비 완료 여부를 주기적으로 확인한다 |

---

## 4-4. 전체 서비스 기동

```powershell
# day1 디렉토리에서 실행
docker compose up -d
```

**출력 예시**

```
[+] Running 3/3
 ✔ Network day1_default     Created
 ✔ Container todo-postgres  Started
 ✔ Container todo-app       Started
```

---

## 4-5. 기동 상태 확인

```powershell
# 서비스 상태 확인
docker compose ps
```

**정상 출력**

```
NAME            IMAGE        COMMAND                  SERVICE    STATUS
todo-app        day1-app     "java -jar app.jar"      app        running
todo-postgres   postgres:16  "docker-entrypoint.s…"  postgres   running (healthy)
```

---

## 4-6. 로그 확인

```powershell
# Spring 앱 로그 확인
docker compose logs app

# PostgreSQL 로그 확인
docker compose logs postgres

# 실시간 로그 팔로우 (Ctrl+C로 종료)
docker compose logs -f app
```

---

## 4-7. API 동작 확인

```powershell
# 할 일 목록 조회 (빈 배열 [] 이 정상)
curl.exe http://localhost:8080/todos

# 할 일 추가
curl.exe -X POST http://localhost:8080/todos `
  -H "Content-Type: application/json" `
  -d '{"title":"Docker 실습 완료"}'

# 다시 목록 조회 (추가된 항목 확인)
curl.exe http://localhost:8080/todos

# 헬스체크
curl.exe http://localhost:8080/actuator/health
```

**정상 응답 예시**

```json
[{"id":1,"title":"Docker 실습 완료","done":false}]
```

---

## 4-8. 서비스 정리

```powershell
# 컨테이너 중지 및 삭제
docker compose down

# 볼륨까지 삭제 (데이터 초기화할 때)
docker compose down -v
```

---

## ✅ 모듈 4 완료 기준

- [ ] `docker compose up -d` 후 두 컨테이너가 모두 `running` 상태다
- [ ] `http://localhost:8080/todos` API 응답이 정상이다
- [ ] 할 일 추가 후 목록 조회 시 추가된 항목이 보인다
- [ ] `docker compose down`으로 서비스를 정리할 수 있다

---

## 🔥 자주 발생하는 오류

| 증상 | 원인 | 해결 |
|---|---|---|
| 앱 로그에 `Connection refused` | DB 준비 전 앱 기동 | `depends_on: condition: service_healthy` 확인 |
| `port is already allocated` | 포트 충돌 | `netstat -ano \| findstr 8080` 으로 사용 프로세스 확인 |
| `image not found` | 이미지 빌드 미완료 | `docker images`로 이미지 존재 여부 확인 |

---

[← 모듈 3](./module3.md) | [← 목차로 돌아가기](./README.md) | [모듈 5 →](./module5.md)
