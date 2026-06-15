# 모듈 2 — PostgreSQL 쿠버네티스 배포

> **목표**: PostgreSQL을 Deployment와 Service로 구성하고  
> PersistentVolumeClaim으로 데이터 저장 구조를 이해한다.

---

## 2-1. 관련 파일 열기

Antigravity IDE에서 `day2/k8s/` 디렉토리의 아래 파일들을 확인한다.

```
day2/k8s/
├── postgres-pvc.yml          ← 데이터 볼륨 요청
├── postgres-deployment.yml   ← PostgreSQL Pod 배포
└── postgres-service.yml      ← 클러스터 내부 접근용 서비스
```

---

## 2-2. PersistentVolumeClaim 적용

```powershell
kubectl apply -f k8s/postgres-pvc.yml -n todo

# 생성 확인 (STATUS가 Bound이어야 정상)
kubectl get pvc -n todo
```

**정상 출력**

```
NAME            STATUS   VOLUME   CAPACITY   ACCESS MODES
postgres-data   Bound    ...      1Gi        RWO
```

---

## 2-3. PostgreSQL Deployment 배포

```powershell
kubectl apply -f k8s/postgres-deployment.yml -n todo

# Pod 기동 상태 확인
kubectl get pods -n todo -w
```

**정상 출력 예시**

```
NAME                        READY   STATUS    RESTARTS
postgres-xxx-xxx            1/1     Running   0
```

> ⏳ 처음 실행 시 이미지 Pull로 1~2분 소요될 수 있다.

---

## 2-4. PostgreSQL Service 생성

```powershell
kubectl apply -f k8s/postgres-service.yml -n todo

# 서비스 확인
kubectl get services -n todo
```

**정상 출력**

```
NAME       TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)    AGE
postgres   ClusterIP   10.x.x.x      <none>        5432/TCP   10s
```

> 💡 `ClusterIP` 타입이므로 클러스터 내부에서만 접근 가능하다.  
> Spring 앱이 `postgres:5432`로 접근할 수 있는 것은 이 Service 덕분이다.

---

## 2-5. PostgreSQL Pod 상태 상세 확인

```powershell
# Pod 상세 정보
kubectl describe pod -l app=postgres -n todo

# PostgreSQL 로그 확인
kubectl logs -l app=postgres -n todo
```

**k9s로 확인**

```
k9s
```
`:pod` → `todo` 네임스페이스 → `postgres` Pod 선택 → `l` (로그 보기)

---

## ✅ 모듈 2 완료 기준

- [ ] PVC가 `Bound` 상태다
- [ ] PostgreSQL Pod가 `Running` 상태다
- [ ] `postgres` Service가 생성되었다
- [ ] k9s에서 PostgreSQL Pod의 로그가 확인된다

---

[← 모듈 1](./module1.md) | [← 목차로 돌아가기](./README.md) | [모듈 3 →](./module3.md)
