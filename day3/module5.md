# 모듈 5 — Argo CD 설치 및 앱 등록

> **목표**: 로컬 Kubernetes 환경에 Argo CD를 구축하고
> `day3/k8s/helm`을 Argo CD Application으로 등록해
> Git의 `values.yaml` 변경이 클러스터에 자동 반영되는 GitOps 기반을 만든다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다.

## 5-1. 2일차 기반 리소스 확인

Argo CD Application을 등록하기 전에 2일차 실습 결과가 클러스터에 남아 있는지 확인한다.

```powershell
kubectl get namespace todo-app
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

> ⚠️ `day3/k8s/helm`은 앱 Deployment 하나만 관리한다.
> 위 기반 리소스가 없으면 Argo CD Sync는 되더라도 앱 Pod가
> `CreateContainerConfigError`, `CrashLoopBackOff`, DB 연결 실패 상태가 될 수 있다.
> 없으면 `day2/k8s/` 매니페스트를 먼저 적용한 뒤 진행한다.

---

## 5-2. Argo CD 설치

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

## 5-3. Argo CD UI 접근

```powershell
# 로컬에서 접근하기 위한 포트 포워딩 (별도 터미널 창에서 실행 후 유지)
kubectl port-forward svc/argocd-server -n argocd 8443:443
```

브라우저에서 `https://localhost:8443` 접속한다.
인증서 경고는 무시하고 진행한다.

---

## 5-4. 초기 비밀번호 확인

```powershell
# admin 초기 비밀번호 확인
kubectl get secret argocd-initial-admin-secret -n argocd `
  -o jsonpath="{.data.password}" | %{[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))}
```

- **ID**: `admin`
- **Password**: 위 명령 출력값

---

## 5-5. Argo CD CLI 설치 (선택)

```powershell
# winget으로 설치
winget install ArgoProj.ArgoCD

# 설치 후 터미널 새로 열기
argocd version

# 로그인
argocd login localhost:8443 --username admin --password <위의 비밀번호> --insecure
```

---

## 5-6. Application 등록 — Helm chart 방식

`day3/k8s/helm`을 source로 등록한다.
Argo CD가 `Chart.yaml`을 감지하면 자동으로 Helm source로 인식하고
`values.yaml`을 반영해 렌더링된 Kubernetes 리소스를 클러스터에 배포한다.

**UI 방식**

1. `https://localhost:8443` 접속 → 로그인
2. **New App** 클릭
3. 아래 값 입력:

| 항목 | 값 |
|---|---|
| Application Name | `todo-app` |
| Project | `default` |
| Repository URL | 본인의 GitHub 저장소 URL |
| Revision | `HEAD` |
| Path | `day3/k8s/helm` |
| Cluster URL | `https://kubernetes.default.svc` |
| Namespace | `todo-app` |

4. **Create** 클릭

**CLI 방식**

```powershell
argocd app create todo-app `
  --repo https://github.com/<username>/docker_k8s_tutorial.git `
  --path day3/k8s/helm `
  --dest-server https://kubernetes.default.svc `
  --dest-namespace todo-app
```

> 💡 Argo CD가 `day3/k8s/helm`에서 `Chart.yaml`을 찾으면
> 자동으로 Helm source로 전환한다. 별도 설정 없이도 동작한다.

---

## 5-7. 등록 상태 확인

```powershell
argocd app list
argocd app get todo-app
```

UI에서는 `OutOfSync` 또는 `Synced` 상태가 표시된다.
아직 Sync하지 않은 경우 `OutOfSync`가 정상이다.

---

## 5-8. 첫 수동 Sync

**UI 방식**

1. `todo-app` 클릭 → **Sync** 버튼 클릭 → **Synchronize** 클릭

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

> ✅ API가 정상 응답하면 Helm chart → Argo CD → Kubernetes 파이프라인이 동작하는 것이다.

---

## 5-9. Argo CD가 Helm을 처리하는 방식

Argo CD는 `day3/k8s/helm` 경로에서 `Chart.yaml`을 감지하면
내부적으로 `helm template`을 실행해 Kubernetes 리소스를 생성한다.

```
values.yaml  ←  이미지 태그 변경 지점
    │
    ▼
helm template (Argo CD 내부 실행)
    │
    ▼
Deployment YAML 생성
    │
    ▼
클러스터 반영
```

즉, 수강생이 `values.yaml`의 `image.tag`만 바꾸고 Git push하면
Argo CD가 감지 → Helm 렌더링 → 클러스터 반영 순서로 처리된다.

---

## ✅ 모듈 5 완료 기준

- [ ] Argo CD가 `argocd` 네임스페이스에 설치되었다
- [ ] `https://localhost:8443`으로 UI에 접근 가능하다
- [ ] 2일차 기반 리소스(`todo-app` Service, `postgres`, `app-config`, `db-secret`)가 확인되었다
- [ ] `todo-app` Application이 `day3/k8s/helm` Helm source로 Argo CD에 등록되었다
- [ ] 수동 Sync 후 Pod가 Running 상태이고 `http://localhost:30080/todos`가 정상 응답한다
- [ ] Argo CD가 `values.yaml`을 렌더링해 클러스터에 반영하는 흐름을 설명할 수 있다

---

[← 모듈 4](./module4.md) | [← 목차로 돌아가기](./README.md) | [모듈 6 →](./module6.md)
