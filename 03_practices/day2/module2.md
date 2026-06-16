# 모듈 2 — PostgreSQL 쿠버네티스 배포

> **목표**: PostgreSQL을 Deployment와 Service로 구성하고
> PersistentVolumeClaim으로 데이터 저장 구조를 이해한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd workspace`로 이동한 `day2/` 디렉터리 기준입니다.

## 2-1. 관련 파일 열기

Antigravity IDE 또는 사용 중인 IDE에서 `day2/k8s/` 디렉토리의 아래 파일들을 확인한다.

```
day2/k8s/
├── app-secret.yml            ← PostgreSQL/Spring 공통 DB 계정 Secret
├── postgres-pvc.yml          ← 데이터 볼륨 요청
├── postgres-deployment.yml   ← PostgreSQL Pod 배포
└── postgres-service.yml      ← 클러스터 내부 접근용 서비스
```

---


## 2-2. DB Secret 먼저 적용

PostgreSQL Deployment는 `db-secret` Secret에서 DB 계정과 비밀번호를 읽는다.
따라서 PostgreSQL Pod를 만들기 전에 Secret을 먼저 생성해야 한다.

```powershell
kubectl apply -f k8s-day2/app-secret.yml -n todo-app

# 생성 확인
kubectl get secret db-secret -n todo-app
```

---

## 2-3. PersistentVolumeClaim 적용

```powershell
kubectl apply -f k8s-day2/postgres-pvc.yml -n todo-app

# 생성 확인 (STATUS가 Bound이어야 정상)
kubectl get pvc -n todo-app
```

**정상 출력**

```
NAME           STATUS   VOLUME   CAPACITY   ACCESS MODES
postgres-pvc   Bound    ...      1Gi        RWO
```

---

## 2-4. PostgreSQL Deployment 배포

```powershell
kubectl apply -f k8s-day2/postgres-deployment.yml -n todo-app

# Pod 기동 상태 확인
kubectl get pods -n todo-app -w
```

**정상 출력 예시**

```
NAME                        READY   STATUS    RESTARTS
postgres-xxx-xxx            1/1     Running   0
```

> ⏳ 처음 실행 시 이미지 Pull로 1~2분 소요될 수 있다.

---

## 2-5. PostgreSQL Service 생성

```powershell
kubectl apply -f k8s-day2/postgres-service.yml -n todo-app

# 서비스 확인
kubectl get services -n todo-app
```

**정상 출력**

```
NAME       TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)    AGE
postgres   ClusterIP   10.x.x.x      <none>        5432/TCP   10s
```

> 💡 `ClusterIP` 타입이므로 클러스터 내부에서만 접근 가능하다.
> Spring 앱이 `postgres:5432`로 접근할 수 있는 것은 이 Service 덕분이다.

---

## 2-6. PostgreSQL Pod 상태 상세 확인

```powershell
# Pod 상세 정보
kubectl describe pod -l app=postgres -n todo-app

# PostgreSQL 로그 확인
kubectl logs -l app=postgres -n todo-app
```

**k9s로 확인 (권장)**

```
k9s
```
`:pod` → `todo-app` 네임스페이스 → `postgres` Pod 선택 → `l` (로그 보기)

---

## ✅ 모듈 2 완료 기준

- [ ] `db-secret` Secret이 생성되었다
- [ ] PVC가 `Bound` 상태다
- [ ] PostgreSQL Pod가 `Running` 상태다
- [ ] `postgres` Service가 생성되었다
- [ ] kubectl logs 또는 k9s로 PostgreSQL Pod의 로그가 확인된다

---

[← 모듈 1](./module1.md) | [← 목차로 돌아가기](./README.md) | [모듈 3 →](./module3.md)
