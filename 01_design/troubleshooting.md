# Troubleshooting — Docker/Kubernetes/GitOps 문제 해결 가이드

> 강의 중 자주 발생하는 문제와 1차 확인 명령을 정리한다.
> 모든 명령은 Windows PowerShell 기준이다.

---

## 1. Docker Desktop 실행 문제

### 증상

- `docker ps` 실패
- Docker Desktop이 Starting 상태에서 멈춤
- WSL 관련 오류 출력

### 확인

```powershell
docker version
wsl --status
wsl --list --verbose
```

### 조치

1. Docker Desktop 재실행
2. WSL2 상태 확인
3. Docker Desktop Settings에서 WSL2 backend 활성화 확인
4. 필요 시 PC 재부팅

---

## 2. 포트 충돌

### 증상

- `Bind for 0.0.0.0:8080 failed`
- `port is already allocated`

### 확인

```powershell
netstat -ano | findstr :8080
netstat -ano | findstr :5432
netstat -ano | findstr :30080
```

### 조치

- 기존 프로세스 종료
- 기존 컨테이너 종료

```powershell
docker ps
docker stop <container-id>
```

---

## 3. Docker build 실패

### 확인

```powershell
docker build -t todo-app:1.0 ./spring-app
```

### 주요 원인

- 경로 오류
- Dockerfile 위치 오류
- Gradle wrapper 실행 실패
- 네트워크 문제로 dependency 다운로드 실패

### 조치

- 현재 위치 확인
- Dockerfile 경로 확인
- 다시 빌드

---

## 4. Docker Compose 실행 실패

### 확인

```powershell
docker compose ps
docker compose logs
```

### 주요 원인

- DB 컨테이너 기동 지연
- 환경변수 누락
- 포트 충돌
- 이전 볼륨 데이터 충돌

### 조치

```powershell
docker compose down
docker compose up -d
docker compose logs -f
```

필요 시 볼륨까지 제거한다.

```powershell
docker compose down -v
```

---

## 5. Kubernetes node가 Ready가 아님

### 확인

```powershell
kubectl get nodes
kubectl get pods -A
```

### 조치

1. Docker Desktop Kubernetes 활성화 확인
2. Docker Desktop 재시작
3. Kubernetes reset 후 재활성화

---

## 6. ImagePullBackOff

### 증상

```text
STATUS: ImagePullBackOff
```

### 확인

```powershell
kubectl get pods -n todo-app
kubectl describe pod <pod-name> -n todo-app
```

### 주요 원인

- 이미지 태그 오타
- GHCR 이미지가 없음
- private image 권한 문제
- 로컬 이미지인데 `imagePullPolicy`가 맞지 않음

### 조치

- `day3/k8s/app-deployment.yml`의 이미지 태그 확인
- GHCR Packages에서 실제 태그 확인
- 정상 태그로 복구 후 commit/push
- Argo CD 수동 Sync 실행

---

## 7. CrashLoopBackOff

### 확인

```powershell
kubectl logs <pod-name> -n todo-app
kubectl describe pod <pod-name> -n todo-app
```

### 주요 원인

- DB 연결 실패
- 환경변수 누락
- ConfigMap/Secret 이름 불일치
- 애플리케이션 기동 오류

### 조치

```powershell
kubectl get configmap -n todo-app
kubectl get secret -n todo-app
kubectl describe deployment todo-app -n todo-app
```

---

## 8. Service 접속 실패

### 확인

```powershell
kubectl get svc -n todo-app
kubectl get endpoints -n todo-app
kubectl get pods -n todo-app --show-labels
```

### 주요 원인

- Service selector와 Pod label 불일치
- NodePort 포트 오류
- Pod가 Ready 상태가 아님

---

## 9. GitHub Actions 실패

### 확인 위치

- GitHub 저장소 → Actions 탭
- 실패한 job의 로그

### 주요 원인

- workflow YAML 들여쓰기 오류
- Docker build 실패
- GHCR push 권한 문제
- repository package 권한 문제

### 조치

- Actions 로그에서 첫 번째 실패 지점 확인
- `permissions: packages: write` 확인
- 이미지 이름/계정명 확인

---

## 10. Argo CD OutOfSync / Degraded

### 확인

```powershell
argocd app get todo-app
kubectl get pods -n todo-app
kubectl describe pod <pod-name> -n todo-app
```

### OutOfSync 의미

Git 상태와 클러스터 상태가 다르다는 뜻이다. 수동 Sync로 반영한다.

### Degraded 의미

클러스터에 반영은 됐지만 Pod나 리소스가 비정상이라는 뜻이다.

### 조치

1. Argo CD UI에서 diff 확인
2. 수동 Sync 실행
3. Pod 상태 확인
4. 문제 태그 또는 설정이면 Git revert 후 다시 Sync

---

## 11. 롤백 실습 표준 절차

```powershell
# 1. 잘못된 이미지 태그 commit/push
# 2. Argo CD Sync
# 3. ImagePullBackOff 확인
kubectl get pods -n todo-app

# 4. Git revert 또는 정상 태그로 수정
git revert HEAD
# 또는 app-deployment.yml 수정 후
git add day3/k8s/app-deployment.yml
git commit -m "fix: restore working image tag"
git push origin main

# 5. Argo CD 수동 Sync
# 6. 정상화 확인
kubectl rollout status deployment/todo-app -n todo-app
kubectl get pods -n todo-app
```
