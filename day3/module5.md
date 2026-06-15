# 모듈 5 — 선언형 CD와 동기화 실습

> **목표**: Git의 매니페스트 수정이 Argo CD를 거쳐
> 로컬 Kubernetes 클러스터로 배포되는 과정을 눈으로 확인한다.
> 기본 실습은 수동 Sync로 진행하고, 자동 Sync는 선택적으로 확인한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다. (`git add day3/k8s/...` 경로가 루트 기준입니다.)

## 5-1. Sync 전 전제 확인

이 모듈은 2일차 기반 리소스 위에 `day3/k8s/app-deployment.yml`만 Argo CD로 동기화하는 실습이다. 먼저 앱이 의존하는 리소스가 존재하는지 확인한다.

```powershell
kubectl get svc todo-app -n todo-app
kubectl get svc postgres -n todo-app
kubectl get configmap app-config -n todo-app
kubectl get secret db-secret -n todo-app
kubectl get deployment postgres -n todo-app
```

위 리소스가 없으면 `day2/k8s` 매니페스트를 먼저 적용한 뒤 진행한다.

---

## 5-2. 초기 수동 Sync

Argo CD에 등록만 했을 때는 `OutOfSync` 상태다.
먼저 수동으로 동기화해 현재 상태를 클러스터에 반영한다.

**UI 방식**

1. `https://localhost:8443` → `todo-app` 클릭
2. **Sync** 버튼 클릭 → **Synchronize** 클릭

**CLI 방식**

```powershell
argocd app sync todo-app
```

**동기화 확인**

```powershell
# Argo CD 앱 상태 확인
argocd app get todo-app

# 클러스터 실제 상태 확인
kubectl get pods -n todo-app
curl.exe http://localhost:30080/todos
```

---

## 5-3. Git 변경 → 클러스터 반영 흐름 체험

매니페스트를 수정하고 push하면 Argo CD가 감지해 반영하는 흐름을 확인한다.

```powershell
# day3/k8s/app-deployment.yml 에서 replica 수 변경
# Antigravity IDE 또는 사용 중인 IDE에서 파일 열기
```

`app-deployment.yml`에서 `replicas: 2` → `replicas: 3`으로 수정한 뒤:

```powershell
git add day3/k8s/app-deployment.yml
git commit -m "scale: increase replicas to 3"
git push origin main
```

---

## 5-4. Argo CD 변경 감지 확인

**UI에서 확인**

- `todo-app`이 `OutOfSync` 상태로 변경됨
- **Sync** 버튼 클릭 → Pod가 3개로 늘어남

**CLI에서 확인**

```powershell
# OutOfSync 상태 확인
argocd app get todo-app

# Sync 실행
argocd app sync todo-app

# Pod 개수 확인
kubectl get pods -n todo-app
```

---

## 5-5. 자동 Sync 활성화 (선택)

수동 Sync 흐름을 이해한 뒤, 시간이 남으면 자동 동기화를 선택적으로 활성화한다.

**UI 방식**

1. `todo-app` → **App Details** → **Sync Policy**
2. **Enable Auto-Sync** 활성화

**CLI 방식**

```powershell
# 선택 실습: 자동 Sync 활성화
argocd app set todo-app --sync-policy automated
```

이후 `git push` 만 하면 Argo CD가 자동으로 감지하고 클러스터에 반영한다. 단, 본 과정의 기본 완료 기준은 수동 Sync 성공이다.

---

## 5-6. k9s로 배포 실시간 확인 (권장)

```powershell
k9s
```

- `:pod` → `todo-app` 네임스페이스 → Pod 개수가 늘어나는 과정 확인
- Pod 선택 → `l`: 새 Pod의 기동 로그 확인

---

## 5-7. Sync 상태 의미

| 상태 | 의미 |
|---|---|
| `Synced` | Git과 클러스터 상태가 일치 |
| `OutOfSync` | Git에 변경이 있으나 아직 클러스터에 미반영 |
| `Healthy` | 앱이 정상 동작 중 |
| `Degraded` | 앱에 문제 발생 (Pod 오류 등) |
| `Progressing` | 배포 진행 중 |

---

## ✅ 모듈 5 완료 기준

- [ ] 2일차 기반 리소스가 유지되어 있음을 확인했다
- [ ] 수동 Sync로 `todo-app`이 `Synced` 상태가 되었다
- [ ] `replicas: 3`으로 변경 후 push → OutOfSync 확인 → 수동 Sync로 Pod가 3개가 되었다
- [ ] 선택: 자동 Sync를 활성화하고 동작 방식을 이해했다

---

[← 모듈 4](./module4.md) | [← 목차로 돌아가기](./README.md) | [모듈 6 →](./module6.md)
