# 모듈 6 — 운영 관점 점검과 2일차 연결

> **목표**: 오늘 실습에서 발생할 수 있는 오류들을 점검하고  
> 2일차 Kubernetes 배포 실습으로의 연결 고리를 확인한다.

---

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

## 6-2. 자주 발생하는 오류 점검

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

## 6-3. 포트 충돌 해결 방법

```powershell
# 8080 포트를 사용 중인 프로세스 확인
netstat -ano | findstr 8080

# PID로 프로세스 종료
taskkill /PID <PID번호> /F
```

---

## 6-4. 남은 컨테이너 정리

```powershell
# 실행 중인 모든 컨테이너 확인
docker ps

# day1 Compose 서비스 정리 (day1 디렉토리에서)
docker compose down

# 불필요한 이미지 정리 (선택)
docker image prune -f
```

---

## 6-5. 2일차 연결 포인트

1일차에서 만든 것들이 2일차에 어떻게 연결되는지 확인한다.

| 1일차 산출물 | 2일차 활용 |
|---|---|
| `todo-app:1.0` 이미지 | Kubernetes Deployment에서 이 이미지를 사용 |
| `.env`의 DB 환경변수 구조 | Kubernetes Secret/ConfigMap으로 변환 |
| `compose.yml`의 서비스 구조 | Deployment + Service 매니페스트로 변환 |
| 활성화된 Kubernetes 클러스터 | 2일차 전체 실습의 기반 |

---

## 6-6. 2일차 사전 확인

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
- [ ] `kubectl get nodes`에서 `docker-desktop` 노드가 `Ready` 상태다

---

[← 모듈 5](./module5.md) | [← 목차로 돌아가기](./README.md)
