# 모듈 6 — 운영 관점 점검과 2일차 연결

> **목표**: 1일차에 배운 것을 운영 관점으로 한 번 더 확인하고
> Docker의 동작 원리(레이어·볼륨)를 실험을 통해 체감한다.
> Compose 구조가 내일 Kubernetes 리소스와 어떻게 연결되는지 미리 파악한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd day1`로 이동한 `day1/` 디렉터리 기준입니다.

## 6-1. 1일차 전체 체크리스트

아래 항목을 모두 확인한다.

| 모듈 | 확인 항목 | 상태 |
|---|---|:---:|
| 모듈 1 | `docker version` 명령이 정상 출력된다 | ☐ |
| 모듈 1 | `docker compose version` 명령이 정상 출력된다 | ☐ |
| 모듈 2 | nginx 컨테이너 실행 및 브라우저 접속이 성공한다 | ☐ |
| 모듈 3 | `docker build -t todo-app:1.0 .` 빌드가 성공한다 | ☐ |
| 모듈 4 | `docker compose up -d` 후 API 응답이 정상이다 | ☐ |
| 모듈 5 | `kubectl get nodes`에서 `docker-desktop`이 Ready다 | ☐ |

---

## 6-2. Docker 이미지 레이어 확인

Docker 이미지는 여러 **레이어**로 쌓여 있다.
Dockerfile의 각 명령(FROM, COPY, RUN 등)이 레이어 하나를 만든다.
레이어 캐시 덕분에 두 번째 빌드부터 빠르게 완료된다.

```powershell
# 이미지 레이어 목록 확인
docker history todo-app:1.0
```

**출력 예시**

```
IMAGE          CREATED        CREATED BY                                SIZE
xxxxxxxxxxxx   2 hours ago    ENTRYPOINT ["java", "-jar", "app.jar"]    0B
<missing>      2 hours ago    COPY --from=builder /workspace/build/…    54MB
<missing>      2 hours ago    FROM eclipse-temurin:17-jre-alpine        184MB
```

> 💡 멀티스테이지 빌드 덕분에 JDK가 아닌 JRE만 포함된다.
> "빌드 도구(JDK)는 최종 이미지에 없어도 된다"는 원칙을 레이어로 확인할 수 있다.

---

## 6-3. 볼륨 — 컨테이너가 종료되면 데이터는?

컨테이너는 기본적으로 **일회성**이다. 컨테이너를 삭제하면 내부 데이터도 사라진다.
볼륨(Volume)은 컨테이너 바깥에 데이터를 저장해 이 문제를 해결한다.

**실험 1: 볼륨 없이 데이터 저장**

```powershell
# 이미 실행 중인 Compose 서비스 정리
docker compose down

# 볼륨까지 삭제 후 재기동
docker compose down -v
docker compose up -d

# 할 일 추가
curl.exe -X POST http://localhost:8080/todos `
  -H "Content-Type: application/json" `
  -d '{"title":"볼륨 실험"}'

# 목록 확인 (데이터 있음)
curl.exe http://localhost:8080/todos
```

**실험 2: 컨테이너 재시작 후 데이터 유지 확인**

```powershell
# compose down은 볼륨 유지
docker compose down

# 다시 기동
docker compose up -d

# 목록 재확인 (볼륨이 있으면 데이터가 살아 있음)
curl.exe http://localhost:8080/todos
```

**실험 3: 볼륨까지 삭제 후 데이터 소실 확인**

```powershell
# -v 옵션으로 볼륨도 삭제
docker compose down -v

# 다시 기동
docker compose up -d

# 목록 재확인 (데이터 없음 - 빈 배열)
curl.exe http://localhost:8080/todos
```

| 명령 | 볼륨 유지 여부 | 데이터 |
|---|---|---|
| `docker compose down` | ✅ 유지 | 살아 있음 |
| `docker compose down -v` | ❌ 삭제 | 사라짐 |

> 💡 2일차 Kubernetes에서는 **PersistentVolumeClaim(PVC)**이 이 볼륨 역할을 한다.

---

## 6-4. 자주 발생하는 오류 점검

### Docker 관련

| 증상 | 원인 | 해결 |
|---|---|---|
| `Cannot connect to the Docker daemon` | Docker Desktop 미실행 | 트레이에서 Docker Desktop 실행 |
| `port is already allocated` | 포트 충돌 (8080 / 5432) | `netstat -ano \| findstr 8080` 으로 PID 확인 후 종료 |
| `image not found` | 이미지 빌드 미완료 | `docker images`로 이미지 존재 여부 확인 |
| 앱 로그에 `Connection refused` | DB 준비 전 앱 기동 | `depends_on: condition: service_healthy` 확인 |

### Kubernetes 관련

| 증상 | 원인 | 해결 |
|---|---|---|
| `kubectl`을 찾을 수 없음 | Kubernetes 비활성화 상태 | Docker Desktop → Settings → Kubernetes 활성화 |
| 노드가 `NotReady` | Kubernetes 시작 중 | 1~2분 기다린 후 재확인 |
| `The connection was refused` | 컨텍스트 오류 | `kubectl config current-context` 확인 |

---

## 6-5. 포트 충돌 해결 방법

```powershell
# 8080 포트를 사용 중인 프로세스 확인
netstat -ano | findstr 8080

# PID로 프로세스 종료
taskkill /PID <PID번호> /F
```

---

## 6-6. 남은 컨테이너 정리

```powershell
# 실행 중인 모든 컨테이너 확인
docker ps

# day1 Compose 서비스 정리 (day1 디렉토리에서)
docker compose down

# 불필요한 이미지 정리 (선택)
docker image prune -f
```

---

## 6-7. Docker Compose → Kubernetes 매핑

오늘 만든 Compose 구조가 내일 Kubernetes에서는 어떤 리소스로 바뀌는지 미리 파악한다.

| Docker Compose | Kubernetes 대응 | 내일 실습 |
|---|---|---|
| `services.app` (Spring) | `Deployment` | `app-deployment.yml` |
| `services.postgres` | `Deployment` | `postgres-deployment.yml` |
| `ports: 8080:8080` | `Service (NodePort 30080)` | `app-service.yml` |
| `env_file: .env` → `DB_HOST` | `ConfigMap` → `DB_HOST` | `app-configmap.yml` |
| `env_file: .env` → `DB_PASSWORD` | `Secret` → `db-password` | `app-secret.yml` |
| `volumes: postgres-data` | `PersistentVolumeClaim` | `postgres-pvc.yml` |

> 💡 이 표를 내일 Kubernetes 실습을 시작할 때 다시 보면 이해가 빠르다.

---

## 6-8. 2일차 사전 확인

```powershell
# 내일 실습 전 아래 명령이 모두 정상 동작해야 한다
docker images | findstr todo-app
kubectl get nodes
kubectl config current-context
```

---

## ✅ 1일차 최종 완료 기준

- [ ] `docker version` 명령이 정상 출력된다
- [ ] Spring 앱 이미지(`todo-app:1.0`) 빌드가 성공했다
- [ ] `docker compose up` 후 `http://localhost:8080/todos` 응답이 온다
- [ ] 볼륨 실험: `docker compose down -v` 후 데이터가 사라짐을 확인했다
- [ ] `kubectl get nodes`에서 `docker-desktop` 노드가 `Ready` 상태다

---

[← 모듈 5](./module5.md) | [← 목차로 돌아가기](./README.md)
