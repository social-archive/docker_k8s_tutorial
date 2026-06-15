# 모듈 6 — 롤백과 운영 체크리스트

> **목표**: 배포가 실패하거나 오동작할 때
> Argo CD 롤백 또는 Git revert로 이전 안정화 버전을 즉각 복구한다.
> 3일간의 실습을 마무리하며 운영 체크리스트를 정리한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다. (`git add day3/k8s/...` 경로가 루트 기준입니다.)

## 6-1. 롤백 실습 전제 확인

롤백 실습은 정상 버전이 한 번 이상 Argo CD로 Sync되어 있고, 2일차 기반 리소스가 유지되어 있다는 전제로 진행한다.

```powershell
argocd app get todo-app
kubectl get svc todo-app -n todo-app
kubectl get configmap app-config -n todo-app
kubectl get secret db-secret -n todo-app
kubectl get svc postgres -n todo-app
```

`todo-app` Service, `app-config`, `db-secret`, `postgres` Service가 없으면 이미지 롤백 이전에 앱이 정상 기동할 수 없다.

---

## 6-2. 잘못된 배포 시뮬레이션

Antigravity IDE 또는 사용 중인 IDE에서 `day3/k8s/app-deployment.yml`을 열어
이미지 태그를 존재하지 않는 버전으로 수정한다.

```yaml
image: ghcr.io/<username>/todo-app:broken-version
```

```powershell
git add day3/k8s/app-deployment.yml
git commit -m "bug: wrong image tag (intentional)"
git push origin main
```

---

## 6-3. 오류 감지

Argo CD UI에서 `todo-app`이 `Degraded` 또는 `OutOfSync` 상태로 변경됨.

```powershell
# Pod 상태 확인 → ImagePullBackOff
kubectl get pods -n todo-app

# k9s로 실시간 확인 (권장)
k9s
```

---

## 6-4. 방법 1 — Argo CD 롤백

**UI 방식**

1. `todo-app` → **History and Rollback** 탭 클릭
2. 이전 정상 버전(revision) 선택
3. **Rollback** 클릭

**CLI 방식**

```powershell
# 배포 이력 확인
argocd app history todo-app

# 특정 revision으로 롤백 (번호는 이력에서 확인)
argocd app rollback todo-app <revision-number>

# 상태 확인
argocd app get todo-app
kubectl get pods -n todo-app
```

---

## 6-5. 방법 2 — Git revert

```powershell
# 마지막 커밋 되돌리기
git revert HEAD --no-edit
git push origin main
```

기본 실습에서는 Git revert 후 Argo CD에서 수동 Sync를 실행해 이전 버전으로 복구한다. 자동 Sync를 선택적으로 활성화한 경우에는 Argo CD가 자동으로 감지해 복구한다.

---

## 6-6. 복구 확인

```powershell
# Pod 상태 확인
kubectl get pods -n todo-app

# API 응답 확인
curl.exe http://localhost:30080/todos
curl.exe http://localhost:30080/actuator/health
```

---

## 6-7. 3일차 완료 체크리스트

| 항목 | 확인 |
|---|:---:|
| GitHub Actions CI가 push 시 자동 실행된다 | ☐ |
| 이미지 태그가 커밋 SHA로 추적 가능하다 | ☐ |
| Argo CD에 `todo-app`이 등록되었다 | ☐ |
| Git 변경 → Argo CD Sync → 클러스터 반영이 확인되었다 | ☐ |
| 잘못된 배포에서 롤백으로 복구했다 | ☐ |

---

## 6-8. 3일간 핵심 흐름 복습

```
1일차: Spring 앱 컨테이너화
  → docker build → docker compose up → kubectl 준비

2일차: Kubernetes 배포
  → Deployment/Service/ConfigMap/Secret → kubectl + k9s 점검

3일차: GitOps 배포
  → git push → CI 빌드 → GHCR 이미지 생성
  → 이미지 태그 수동 반영
  → Argo CD 감지 → 클러스터 반영 → 롤백
```

---

## 6-9. 운영 관점 핵심 원칙

| 원칙 | 내용 |
|---|---|
| **불변 이미지** | 배포된 이미지는 수정하지 않는다. 새 태그로 재배포한다 |
| **Git이 진실** | 클러스터 상태는 항상 Git 매니페스트를 기준으로 판단한다 |
| **관찰 가능성** | `kubectl logs` + Argo CD UI로 상태를 확인하고, k9s가 가능하면 실시간 관찰에 활용한다 |
| **롤백 우선** | 오류 발생 시 수정보다 먼저 롤백으로 서비스를 안정화한다 |

---

## ✅ 모듈 6 완료 기준

- [ ] 잘못된 이미지 배포 → Argo CD 롤백 또는 Git revert로 복구했다
- [ ] 복구 후 `todo-app` Service와 2일차 ConfigMap/Secret/DB 의존성이 유지되어 앱 API가 정상 응답한다
- [ ] 3일간의 빌드-배포-롤백 흐름을 한 사이클로 경험했다
- [ ] 운영 관점 핵심 원칙 4가지를 이해했다

---

[← 모듈 5](./module5.md) | [← 목차로 돌아가기](./README.md)
