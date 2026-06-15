# 모듈 4 — Argo CD 설치 및 앱 등록

> **목표**: 로컬 Kubernetes 환경에 Argo CD를 구축하고
> Git 저장소를 Argo CD에 등록해 클러스터와 Git의 동기화를 준비한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다. (`git add day3/k8s/...` 경로가 루트 기준입니다.)

## 4-1. Argo CD 설치

```powershell
# argocd 네임스페이스 생성
kubectl create namespace argocd

# Argo CD 설치
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 설치 완료 대기 (모든 Pod가 Running이 될 때까지)
kubectl get pods -n argocd -w
```

> ⏳ 처음 설치 시 이미지 다운로드로 2~5분 소요된다.

**k9s로 설치 진행 모니터링 (권장)**

```powershell
k9s
```
`:pod` → `argocd` 네임스페이스 선택 → Pod 상태 확인

---

## 4-2. Argo CD UI 접근

```powershell
# 로컬에서 접근하기 위한 포트 포워딩
kubectl port-forward svc/argocd-server -n argocd 8443:443
```

브라우저에서 `https://localhost:8443` 접속
(인증서 경고는 무시하고 진행)

---

## 4-3. 초기 비밀번호 확인

```powershell
# admin 초기 비밀번호 확인
kubectl get secret argocd-initial-admin-secret -n argocd `
  -o jsonpath="{.data.password}" | %{[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))}
```

- **ID**: `admin`
- **Password**: 위 명령 출력값

---

## 4-4. Argo CD CLI 설치 (선택)

```powershell
# winget으로 설치
winget install ArgoProj.ArgoCD

# 로그인
argocd login localhost:8443 --username admin --password <위의 비밀번호> --insecure
```

---

## 4-5. 2일차 기반 리소스 확인

Argo CD Application을 등록하기 전에 2일차 실습 결과가 클러스터에 남아 있는지 확인한다.

```powershell
# namespace 확인
kubectl get namespace todo-app

# 2일차에서 만든 기반 리소스 확인
kubectl get svc,configmap,secret,pvc -n todo-app
kubectl get deployment postgres -n todo-app
```

필수 전제:

| 리소스 | 2일차 생성 파일 | 3일차에서 필요한 이유 |
|---|---|---|
| `todo-app` Namespace | `day2/k8s/namespace.yml` | Argo CD 배포 대상 namespace |
| `todo-app` Service | `day2/k8s/app-service.yml` | `localhost:30080` API 확인 |
| `postgres` Service/Deployment/PVC | `day2/k8s/postgres-*.yml` | Spring 앱 DB 연결 |
| `app-config` ConfigMap | `day2/k8s/app-configmap.yml` | `DB_HOST`, `DB_PORT`, `DB_NAME` 주입 |
| `db-secret` Secret | `day2/k8s/app-secret.yml` | DB 계정/비밀번호 주입 |

> ⚠️ `day3/k8s`에는 Argo CD가 관리할 `app-deployment.yml`과 `kustomization.yml`만 있다.
> 위 기반 리소스가 없으면 Argo CD Sync는 되더라도 앱 Pod가 `CreateContainerConfigError`, `CrashLoopBackOff`, DB 연결 실패 상태가 될 수 있다.

---

## 4-6. Argo CD Application 등록

Argo CD UI 또는 CLI로 애플리케이션을 등록한다.

**UI 방식**

1. `https://localhost:8443` 접속 → 로그인
2. **New App** 클릭
3. 아래 값 입력:

| 항목 | 값 |
|---|---|
| Application Name | `todo-app` |
| Project | `default` |
| Repository URL | 본인의 GitHub 저장소 URL |
| Path | `day3/k8s` |
| Cluster URL | `https://kubernetes.default.svc` |
| Namespace | `todo-app` |

4. **Create** 클릭

**CLI 방식**

```powershell
argocd app create todo-app `
  --repo https://github.com/<username>/docker_k8s_tutorial.git `
  --path day3/k8s `
  --dest-server https://kubernetes.default.svc `
  --dest-namespace todo-app
```

---

## 4-7. 등록 상태 확인

```powershell
# CLI로 앱 상태 확인
argocd app list
argocd app get todo-app
```

UI에서는 `OutOfSync` 또는 `Synced` 상태가 표시된다.

---

## ✅ 모듈 4 완료 기준

- [ ] Argo CD가 `argocd` 네임스페이스에 설치되었다
- [ ] `https://localhost:8443`으로 UI에 접근 가능하다
- [ ] 2일차 기반 리소스(`todo-app` Service, `postgres`, `app-config`, `db-secret`)가 확인되었다
- [ ] `todo-app` Application이 Argo CD에 등록되었다

---

[← 모듈 3](./module3.md) | [← 목차로 돌아가기](./README.md) | [모듈 5 →](./module5.md)
