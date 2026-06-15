# 모듈 3 — Spring 애플리케이션 배포

> **목표**: 1일차에 만든 `todo-app:1.0` 이미지를 기반으로  
> Spring Boot 애플리케이션을 Deployment로 배포하고 Service로 노출한다.

---

## 3-1. 관련 파일 열기

Antigravity IDE에서 `day2/k8s/` 디렉토리의 아래 파일들을 확인한다.

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

## 3-3. Spring 앱 Deployment 배포

```powershell
kubectl apply -f k8s/app-deployment.yml -n todo

# Pod 기동 상태 확인
kubectl get pods -n todo -w
```

**정상 출력**

```
NAME                   READY   STATUS    RESTARTS
todo-app-xxx-xxx       1/1     Running   0
postgres-xxx-xxx       1/1     Running   0
```

> ❌ `CrashLoopBackOff` 가 보이면 다음 섹션에서 로그를 확인한다.

---

## 3-4. Spring 앱 로그 확인

```powershell
# 앱 로그 확인
kubectl logs -l app=todo-app -n todo

# 실시간 로그 팔로우
kubectl logs -l app=todo-app -n todo -f
```

**정상 기동 로그 예시**

```
Started TodoApplication in 3.x seconds
```

**k9s로 확인**

`:pod` → `todo-app` Pod 선택 → `l`

---

## 3-5. Service 생성 및 접근 확인

```powershell
kubectl apply -f k8s/app-service.yml -n todo

# 서비스 확인
kubectl get services -n todo
```

**정상 출력**

```
NAME       TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)          AGE
postgres   ClusterIP   10.x.x.x      <none>        5432/TCP         5m
todo-app   NodePort    10.x.x.x      <none>        8080:30080/TCP   10s
```

`NodePort` 타입이므로 로컬에서 `http://localhost:30080`으로 접근할 수 있다.

---

## 3-6. API 동작 확인

```powershell
# 할 일 목록 조회
curl http://localhost:30080/todos

# 헬스체크
curl http://localhost:30080/actuator/health
```

---

## 3-7. Deployment 구조 이해

```powershell
# Deployment 상세 정보
kubectl describe deployment todo-app -n todo
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

- [ ] Spring 앱 Pod가 `Running` 상태다
- [ ] `http://localhost:30080/todos` API 응답이 정상이다
- [ ] `kubectl logs`로 정상 기동 로그가 확인된다
- [ ] k9s에서 todo-app Pod의 상태가 확인된다

---

[← 모듈 2](./module2.md) | [← 목차로 돌아가기](./README.md) | [모듈 4 →](./module4.md)
