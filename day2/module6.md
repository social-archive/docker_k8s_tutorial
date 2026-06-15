# 모듈 6 — 운영 관점 문제 진단 입문

> **목표**: 자주 발생하는 배포 오류를 간단히 재현하고
> `describe`, `logs`, `rollout` 명령으로 원인을 찾는 흐름을 익힌다.
> 3일차 GitOps 롤백 실습으로의 연결 고리를 확인한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd day2`로 이동한 `day2/` 디렉터리 기준입니다.

## 6-1. 대표적인 배포 오류 유형

| 증상 | 원인 | 진단 명령 |
|---|---|---|
| Pod가 `Pending` | 리소스 부족, PVC 미생성 | `kubectl describe pod` |
| Pod가 `CrashLoopBackOff` | 앱 기동 실패, 환경변수 오류 | `kubectl logs` |
| Pod가 `ImagePullBackOff`/`ErrImageNeverPull` | 이미지 없음, 태그 오류, 로컬 이미지 정책 | `kubectl describe pod` |
| 앱은 뜨는데 DB 연결 실패 | 서비스 셀렉터 불일치, Secret 오류 | `kubectl describe svc` |

---

## 6-2. 오류 재현 실습 — 잘못된 이미지 태그

```powershell
# 존재하지 않는 이미지 태그로 배포 (오류 유도)
kubectl set image deployment/todo-app todo-app=todo-app:9.9 -n todo-app

# Pod 상태 확인 → ImagePullBackOff 또는 ErrImageNeverPull 발생
kubectl get pods -n todo-app -w
```

**원인 진단**

```powershell
kubectl describe pod -l app=todo-app -n todo-app
```

이벤트 섹션에서 이미지 태그 오류 또는 로컬 이미지 없음 메시지를 확인한다.

**복구**

```powershell
# 정상 태그로 롤백
kubectl rollout undo deployment/todo-app -n todo-app

# 롤백 상태 확인
kubectl rollout status deployment/todo-app -n todo-app
```

---

## 6-3. 오류 재현 실습 — 환경변수 누락

```powershell
# ConfigMap 삭제 (환경변수 누락 시뮬레이션)
kubectl delete configmap app-config -n todo-app

# Deployment 재시작
kubectl rollout restart deployment/todo-app -n todo-app

# Pod 상태/이벤트 확인 → ConfigMap not found 계열 오류 확인
kubectl get pods -n todo-app
kubectl describe pod -l app=todo-app -n todo-app
```

**복구**

```powershell
kubectl apply -f k8s/app-configmap.yml -n todo-app
kubectl rollout restart deployment/todo-app -n todo-app
```

---

## 6-4. k9s로 오류 진단

```powershell
k9s
```

- `CrashLoopBackOff` Pod 선택 → `l`: 오류 로그 확인
- `ImagePullBackOff` Pod 선택 → `d`: 이벤트에서 원인 확인
- `:event`: 전체 클러스터 이벤트 스트림 확인

---

## 6-5. 롤아웃 이력과 롤백

```powershell
# 배포 이력 확인
kubectl rollout history deployment/todo-app -n todo-app

# 특정 버전으로 롤백
kubectl rollout undo deployment/todo-app --to-revision=1 -n todo-app

# 현재 상태 확인
kubectl rollout status deployment/todo-app -n todo-app
```

---

## 6-6. 2일차 완료 체크리스트

| 항목 | 확인 |
|---|:---:|
| `todo-app` 네임스페이스가 생성되었다 | ☐ |
| PostgreSQL Pod가 `Running` 상태다 | ☐ |
| Spring 앱 Pod가 `Running` 상태다 | ☐ |
| ConfigMap과 Secret이 적용되었다 | ☐ |
| API 호출 시 DB 연동 응답이 정상이다 | ☐ |
| `kubectl rollout undo`로 롤백을 수행했다 | ☐ |

---

## 6-7. 3일차 연결 포인트

| 2일차 산출물 | 3일차 활용 |
|---|---|
| `day3/k8s/` 매니페스트 구조 | Argo CD가 Git에서 앱 Deployment를 동기화 |
| `kubectl rollout undo` 경험 | Argo CD 롤백으로 확장 |
| ConfigMap/Secret 분리 구조 | 3일차 Deployment가 기존 설정 리소스를 참조 |

---

## ✅ 모듈 6 완료 기준

- [ ] `ImagePullBackOff` 또는 `ErrImageNeverPull` 오류를 재현하고 `rollout undo`로 복구했다
- [ ] 배포 이력(`rollout history`)을 확인했다
- [ ] 3일차 실습에 필요한 매니페스트 구조가 확보되었다

---

[← 모듈 5](./module5.md) | [← 목차로 돌아가기](./README.md)
