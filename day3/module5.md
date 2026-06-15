# 모듈 5 — 선언형 CD와 동기화 실습

> **목표**: Git의 매니페스트 수정이 Argo CD를 거쳐  
> 로컬 Kubernetes 클러스터로 배포되는 과정을 눈으로 확인한다.  
> 수동 Sync → 자동 Sync 순서로 경험한다.

---

## 5-1. 초기 수동 Sync

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
kubectl get pods -n todo
curl http://localhost:30080/todos
```

---

## 5-2. Git 변경 → 클러스터 반영 흐름 체험

매니페스트를 수정하고 push하면 Argo CD가 감지해 반영하는 흐름을 확인한다.

```powershell
# day3/k8s/app-deployment.yml 에서 replica 수 변경
# Antigravity IDE에서 파일 열기
```

`app-deployment.yml`에서 `replicas: 1` → `replicas: 2`로 수정한 뒤:

```powershell
git add day3/k8s/app-deployment.yml
git commit -m "scale: increase replicas to 2"
git push origin main
```

---

## 5-3. Argo CD 변경 감지 확인

**UI에서 확인**

- `todo-app`이 `OutOfSync` 상태로 변경됨
- **Sync** 버튼 클릭 → Pod가 2개로 늘어남

**CLI에서 확인**

```powershell
# OutOfSync 상태 확인
argocd app get todo-app

# Sync 실행
argocd app sync todo-app

# Pod 개수 확인
kubectl get pods -n todo
```

---

## 5-4. 자동 Sync 활성화

매번 수동으로 Sync하지 않도록 자동 동기화를 활성화한다.

**UI 방식**

1. `todo-app` → **App Details** → **Sync Policy**
2. **Enable Auto-Sync** 활성화

**CLI 방식**

```powershell
argocd app set todo-app --sync-policy automated
```

이후 `git push` 만 하면 Argo CD가 자동으로 감지하고 클러스터에 반영한다.

---

## 5-5. k9s로 배포 실시간 확인

```powershell
k9s
```

- `:pod` → `todo` 네임스페이스 → Pod 개수가 늘어나는 과정 확인
- Pod 선택 → `l`: 새 Pod의 기동 로그 확인

---

## 5-6. Sync 상태 의미

| 상태 | 의미 |
|---|---|
| `Synced` | Git과 클러스터 상태가 일치 |
| `OutOfSync` | Git에 변경이 있으나 아직 클러스터에 미반영 |
| `Healthy` | 앱이 정상 동작 중 |
| `Degraded` | 앱에 문제 발생 (Pod 오류 등) |
| `Progressing` | 배포 진행 중 |

---

## ✅ 모듈 5 완료 기준

- [ ] 수동 Sync로 `todo-app`이 `Synced` 상태가 되었다
- [ ] `replicas: 2`로 변경 후 push → Argo CD가 반영해 Pod가 2개가 되었다
- [ ] 자동 Sync를 활성화했다

---

[← 모듈 4](./module4.md) | [← 목차로 돌아가기](./README.md) | [모듈 6 →](./module6.md)
