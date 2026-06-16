# 모듈 5 — 통합 검증과 서비스 확인

> **목표**: "배포 성공 = Pod Running"이 아니라 "서비스 응답까지 확인"이라는
> 운영 관점의 점검 절차를 직접 수행한다.
> kubectl을 기본으로 상태를 확인하고, k9s가 가능하면 실시간 관찰에 활용한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd day2`로 이동한 `day2/` 디렉터리 기준입니다.

## 5-1. 전체 리소스 상태 한눈에 확인

```powershell
# todo-app 네임스페이스의 전체 리소스 조회
kubectl get all -n todo-app
```

**정상 출력 예시**

```
NAME                            READY   STATUS    RESTARTS
pod/postgres-xxx-xxx            1/1     Running   0
pod/todo-app-xxx-xxx            1/1     Running   0

NAME           TYPE        CLUSTER-IP   PORT(S)
service/postgres   ClusterIP   10.x.x.x    5432/TCP
service/todo-app   NodePort    10.x.x.x    8080:30080/TCP

NAME                       READY   UP-TO-DATE   AVAILABLE
deployment.apps/postgres   1/1     1            1
deployment.apps/todo-app   1/1     1            1
```

---

## 5-2. k9s로 실시간 모니터링 (권장)

```powershell
k9s
```

- `0` 키: 전체 네임스페이스 보기
- `:pod` → `todo-app` 네임스페이스 필터 → Pod 상태 확인
- todo-app Pod 선택 → `l`: 실시간 로그 확인
- postgres Pod 선택 → `l`: DB 기동 로그 확인
- Pod 선택 → `d`: describe 상세 정보

---

## 5-3. Spring ↔ PostgreSQL 연결 검증

```powershell
# 할 일 목록 조회 (빈 배열이 정상)
curl.exe http://localhost:30080/todos

# 할 일 추가
curl.exe -X POST http://localhost:30080/todos `
  -H "Content-Type: application/json" `
  -d '{"title":"K8s 배포 완료"}'

# 목록 재조회 (추가된 항목 확인)
curl.exe http://localhost:30080/todos

# 헬스체크
curl.exe http://localhost:30080/actuator/health
```

---

## 5-4. 포트 포워딩으로 직접 접근 (대안)

NodePort 대신 포트 포워딩을 사용할 수도 있다.

```powershell
# todo-app Pod를 로컬 8080으로 포워딩
kubectl port-forward deployment/todo-app 8080:8080 -n todo-app
```

다른 터미널에서:

```powershell
curl.exe http://localhost:8080/todos
```

`Ctrl+C`로 포워딩 종료.

---

## 5-5. 배포 이력 확인

```powershell
# Deployment 롤아웃 이력
kubectl rollout history deployment/todo-app -n todo-app

# 현재 롤아웃 상태
kubectl rollout status deployment/todo-app -n todo-app
```

---

## 5-6. 점검 순서 정리

운영 현장에서 배포 후 점검 순서:

```
1. kubectl get pods  → Pod가 Running인지 확인
2. kubectl logs      → 기동 로그에 오류 없는지 확인
3. kubectl describe  → 이벤트/환경변수 설정 확인
4. API 호출          → 실제 서비스 응답 확인
5. k9s               → 실시간 모니터링 유지
```

---

## ✅ 모듈 5 완료 기준

- [ ] `kubectl get all -n todo-app`에서 모든 리소스가 정상 상태다
- [ ] `http://localhost:30080/todos` API가 정상 응답한다
- [ ] 할 일 추가 후 조회 시 데이터가 PostgreSQL에 저장된다
- [ ] kubectl logs 또는 k9s로 두 Pod의 로그를 확인할 수 있다

---

[← 모듈 4](./module4.md) | [← 목차로 돌아가기](./README.md) | [모듈 6 →](./module6.md)
