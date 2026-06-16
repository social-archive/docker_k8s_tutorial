# 모듈 6 — Sync, 롤백, 운영 체크리스트

> **목표**: Helm values 변경이 Argo CD를 거쳐 클러스터에 반영되는 과정을 확인하고,
> 잘못된 이미지 태그 배포를 Git revert 또는 Argo CD rollback으로 복구한다.
> 3일간의 실습을 마무리하며 운영 체크리스트를 정리한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다.

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

## 6-2. Helm values 변경 → Argo CD OutOfSync 확인

Antigravity IDE 또는 사용 중인 IDE에서 `day3/k8s/helm/values.yaml`을 열어 replica 수를 변경한다.

```yaml
replicaCount: 3
```

```powershell
git add day3/k8s/helm/values.yaml
git commit -m "scale: increase replicas to 3"
git push origin main
```

Argo CD에서 `todo-app` Application이 `OutOfSync`로 바뀌는지 확인한다.

```powershell
argocd app get todo-app
```

---

## 6-3. 수동 Sync와 클러스터 반영 확인

```powershell
argocd app sync todo-app

# Pod 개수와 상태 확인
kubectl get pods -n todo-app

# API 응답 확인
curl.exe http://localhost:30080/todos
```

k9s를 사용할 수 있으면 Pod가 늘어나는 과정을 실시간으로 확인한다.

```powershell
k9s
```

---

## 6-4. 잘못된 배포 시뮬레이션

이번에는 `values.yaml`의 이미지 태그를 존재하지 않는 값으로 바꾼다.

```yaml
image:
  repository: ghcr.io/<username>/todo-app
  tag: "broken-version"
```

```powershell
git add day3/k8s/helm/values.yaml
git commit -m "bug: wrong helm image tag (intentional)"
git push origin main
```

Argo CD에서 Sync한다.

```powershell
argocd app sync todo-app
```

---

## 6-5. 오류 감지

Argo CD UI에서 `todo-app`이 `Degraded` 또는 `Progressing` 상태로 남을 수 있다.
Pod 상태는 `ImagePullBackOff`가 된다.

```powershell
# Pod 상태 확인 → ImagePullBackOff
kubectl get pods -n todo-app

# 상세 원인 확인
kubectl describe pod -n todo-app -l app=todo-app

# k9s로 실시간 확인 (권장)
k9s
```

---

## 6-6. 방법 1 — Git revert로 복구

GitOps 관점에서 가장 설명하기 쉬운 복구 방식은 잘못된 commit을 되돌리는 것이다.

```powershell
# 마지막 커밋 되돌리기
git revert HEAD --no-edit
git push origin main

# Argo CD 수동 Sync
argocd app sync todo-app
```

복구 확인:

```powershell
kubectl get pods -n todo-app
curl.exe http://localhost:30080/todos
curl.exe http://localhost:30080/actuator/health
```

---

## 6-7. 방법 2 — Argo CD 롤백

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

> 강의에서는 Git revert를 기본 복구 방식으로 권장한다.
> Argo CD rollback은 UI/운영 도구 관점의 대안으로 비교한다.

---

## 6-8. 자동 Sync 활성화 (선택)

수동 Sync 흐름을 이해한 뒤, 시간이 남으면 자동 동기화를 선택적으로 활성화한다.

```powershell
argocd app set todo-app --sync-policy automated
```

이후 `git push`만 하면 Argo CD가 자동으로 감지하고 클러스터에 반영한다.
단, 본 과정의 기본 완료 기준은 수동 Sync와 수동 복구 성공이다.

---

## 6-9. 3일차 완료 체크리스트

| 항목 | 확인 |
|---|:---:|
| GitHub Actions CI가 push 시 자동 실행된다 | ☐ |
| 이미지 태그가 커밋 SHA로 추적 가능하다 | ☐ |
| Helm chart와 values 구조를 설명할 수 있다 | ☐ |
| Argo CD에 Helm chart 기반 `todo-app`이 등록되었다 | ☐ |
| Git 변경 → Argo CD Sync → 클러스터 반영이 확인되었다 | ☐ |
| 잘못된 배포에서 Git revert 또는 Argo CD rollback으로 복구했다 | ☐ |

---

## 6-10. 3일간 핵심 흐름 복습

```text
1일차: Spring 앱 컨테이너화
  → docker build → docker compose up → kubectl 준비

2일차: Kubernetes 배포
  → Deployment/Service/ConfigMap/Secret → kubectl + k9s 점검

3일차: GitOps + Helm 배포
  → git push → CI 빌드 → GHCR 이미지 생성
  → 이미지 태그 수동 반영
  → Helm chart/values로 배포 소스 패키징
  → Argo CD 감지 → 클러스터 반영 → 롤백
```

---

## 6-11. 운영 관점 핵심 원칙

| 원칙 | 내용 |
|---|---|
| **불변 이미지** | 배포된 이미지는 수정하지 않는다. 새 태그로 재배포한다 |
| **Git이 진실** | 클러스터 상태는 항상 Git의 manifest 또는 Helm values를 기준으로 판단한다 |
| **변경점 분리** | Helm values로 환경별/버전별 변경값을 분리한다 |
| **관찰 가능성** | `kubectl logs`, `describe`, Argo CD UI로 상태를 확인하고, k9s가 가능하면 실시간 관찰에 활용한다 |
| **롤백 우선** | 오류 발생 시 수정보다 먼저 롤백으로 서비스를 안정화한다 |

---

## ✅ 모듈 6 완료 기준

- [ ] Helm values 변경 → Argo CD OutOfSync → 수동 Sync 흐름을 확인했다
- [ ] 잘못된 이미지 배포 → Git revert 또는 Argo CD rollback으로 복구했다
- [ ] 복구 후 `todo-app` Service와 2일차 ConfigMap/Secret/DB 의존성이 유지되어 앱 API가 정상 응답한다
- [ ] 3일간의 빌드-패키징-배포-롤백 흐름을 한 사이클로 경험했다
- [ ] 운영 관점 핵심 원칙을 이해했다

---

[← 모듈 5](./module5.md) | [← 목차로 돌아가기](./README.md)
