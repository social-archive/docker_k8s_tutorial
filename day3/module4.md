# 모듈 4 — Argo CD 설치 및 앱 등록

> **목표**: 로컬 Kubernetes 환경에 Argo CD를 구축하고  
> Git 저장소를 Argo CD에 등록해 클러스터와 Git의 동기화를 준비한다.

---

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

**k9s로 설치 진행 모니터링**

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

## 4-5. Argo CD Application 등록

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
| Namespace | `todo` |

4. **Create** 클릭

**CLI 방식**

```powershell
argocd app create todo-app `
  --repo https://github.com/<username>/docker_k8s_tutorial.git `
  --path day3/k8s `
  --dest-server https://kubernetes.default.svc `
  --dest-namespace todo
```

---

## 4-6. 등록 상태 확인

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
- [ ] `todo-app` Application이 Argo CD에 등록되었다

---

[← 모듈 3](./module3.md) | [← 목차로 돌아가기](./README.md) | [모듈 5 →](./module5.md)
