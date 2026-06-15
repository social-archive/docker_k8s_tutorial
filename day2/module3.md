# 모듈 3 — Spring 애플리케이션 배포

> **목표**: 1일차에 만든 `todo-app:1.0` 이미지를 기반으로
> Spring Boot 애플리케이션을 Deployment로 배포하고 Service로 노출한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd day2`로 이동한 `day2/` 디렉터리 기준입니다.

## 3-1. 관련 파일 열기

Antigravity IDE 또는 사용 중인 IDE에서 `day2/k8s/` 디렉토리의 아래 파일들을 확인한다.

```
day2/k8s/
├── app-deployment.yml   ← Spring Boot 앱 배포 정의
└── app-service.yml      ← NodePort로 외부 접근 허용
```

---

## 3-2. 이미지 빌드 확인

```powershell
# 1일차에 빌드한 이미지가 있는지 확인
docker images | findstr todo-app
```

없으면 `spring-app/` 디렉토리에서 다시 빌드한다.

```powershell
cd ..\spring-app
docker build -t todo-app:1.0 .
cd ..\day2
```

---


## 3-3. 앱 ConfigMap 먼저 적용

Spring 앱 Deployment는 `app-config` ConfigMap에서 DB 호스트, 포트, DB명을 환경변수로 읽는다.
Deployment가 Pod를 기동할 때 ConfigMap이 존재하지 않으면 `CreateContainerConfigError`가 발생한다.
따라서 Deployment를 만들기 전에 ConfigMap을 반드시 먼저 생성해야 한다.

> 💡 ConfigMap의 내용과 동작 원리는 **모듈 4**에서 자세히 다룬다.
> 지금은 "Deployment가 읽어야 할 설정 파일을 미리 만들어 두는 것"으로 이해하면 된다.

```powershell
kubectl apply -f k8s/app-configmap.yml -n todo-app

# 생성 확인
kubectl get configmap app-config -n todo-app
```

---

## 3-4. Spring 앱 Deployment 배포

```powershell
kubectl apply -f k8s/app-deployment.yml -n todo-app

# Pod 기동 상태 확인
kubectl get pods -n todo-app -w
```

**정상 출력**

```
NAME                   READY   STATUS    RESTARTS
todo-app-xxx-xxx       1/1     Running   0
postgres-xxx-xxx       1/1     Running   0
```

> ❌ `CrashLoopBackOff` 가 보이면 다음 섹션에서 로그를 확인한다.

---

## 3-5. Spring 앱 로그 확인

```powershell
# 앱 로그 확인
kubectl logs -l app=todo-app -n todo-app

# 실시간 로그 팔로우
kubectl logs -l app=todo-app -n todo-app -f
```

**정상 기동 로그 예시**

```
Started TodoApplication in 3.x seconds
```

**k9s로 확인 (권장)**

`:pod` → `todo-app` Pod 선택 → `l`

---

## 3-6. Service 생성 및 접근 확인

```powershell
kubectl apply -f k8s/app-service.yml -n todo-app

# 서비스 확인
kubectl get services -n todo-app
```

**정상 출력**

```
NAME       TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)          AGE
postgres   ClusterIP   10.x.x.x      <none>        5432/TCP         5m
todo-app   NodePort    10.x.x.x      <none>        8080:30080/TCP   10s
```

`NodePort` 타입이므로 로컬에서 `http://localhost:30080`으로 접근할 수 있다.

---

## 3-7. API 동작 확인

```powershell
# 할 일 목록 조회
curl.exe http://localhost:30080/todos

# 헬스체크
curl.exe http://localhost:30080/actuator/health
```

---

## 3-8. Deployment 구조 이해

```powershell
# Deployment 상세 정보
kubectl describe deployment todo-app -n todo-app
```

핵심 확인 항목:

| 항목 | 의미 |
|---|---|
| `Replicas` | 몇 개의 Pod를 유지할지 |
| `Image` | 사용 중인 컨테이너 이미지 |
| `Environment` | 환경변수 주입 상태 |
| `Conditions` | 배포 성공/실패 이유 |

---

## ✅ 모듈 3 완료 기준

- [ ] `app-config` ConfigMap이 생성되었다
- [ ] Spring 앱 Pod가 `Running` 상태다
- [ ] `http://localhost:30080/todos` API 응답이 정상이다
- [ ] `kubectl logs`로 정상 기동 로그가 확인된다
- [ ] kubectl 또는 k9s로 todo-app Pod의 상태가 확인된다

---

[← 모듈 2](./module2.md) | [← 목차로 돌아가기](./README.md) | [모듈 4 →](./module4.md)
