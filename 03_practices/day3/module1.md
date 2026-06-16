# 모듈 1 — CI/CD 구조와 GitOps 이해

> **목표**: "우리가 수동으로 하던 배포를 어떻게 자동화하는가"의 큰 그림을 잡는다.
> GitHub Actions(CI)와 Argo CD(CD)의 역할을 명확히 분리해 이해한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다. (`git add day3/k8s/...` 경로가 루트 기준입니다.)

## 1-1. 수동 배포 vs GitOps 배포 비교

| 단계 | 수동 배포 | GitOps 배포 |
|---|---|---|
| 코드 변경 | 개발자가 직접 빌드 | push 시 CI가 자동 트리거 |
| 이미지 빌드 | 로컬에서 `docker build` | GitHub Actions가 자동 빌드 |
| 이미지 배포 | `kubectl apply` 수동 실행 | Git 매니페스트 변경 후 Argo CD Sync로 반영 |
| 배포 이력 | 기억에 의존 | Git 커밋 이력으로 추적 가능 |
| 롤백 | 이전 명령 재실행 | Git revert 또는 Argo CD 롤백 |

---

## 1-2. CI와 CD의 역할 분리

```
┌─────────────────────────────────────────────┐
│           CI (GitHub Actions)               │
│  코드 push → 테스트 → 빌드 → 이미지 push    │
└─────────────────────────────────────────────┘
                      │
                      │ 이미지 태그 확인
                      ▼
┌─────────────────────────────────────────────┐
│           CD (Argo CD)                      │
│  매니페스트 수동 변경 → Sync → 클러스터 반영 │
└─────────────────────────────────────────────┘
```

| 도구 | 역할 | 트리거 |
|---|---|---|
| **GitHub Actions** | CI: 테스트, 빌드, 이미지 생성 | Git push / PR |
| **Argo CD** | CD: 클러스터와 Git 동기화 | Git 변경 감지 후 수동 Sync |

---

## 1-3. 오늘 사용할 파일 구조

Antigravity IDE 또는 사용 중인 IDE에서 아래 구조를 확인한다.

```
docker_k8s_tutorial/
├── .github/workflows/
│   └── ci.yml              ← GitHub Actions CI workflow
└── 03_practices/
    └── workspace/
        └── k8s-day3-gitops/
            ├── app-deployment.yml   ← raw manifest (과거 수동 배포 방식 비교용)
            └── helm/
                ├── Chart.yaml       ← Helm chart 메타데이터
                ├── values.yaml      ← 이미지 태그·replicas·resources 변경 지점
                └── templates/
                    └── deployment.yaml  ← Deployment 템플릿
```

---

## 1-4. GitHub fork 저장소 확인

3일차 실습은 수강생 본인의 GitHub fork 저장소에서 진행한다.

1. 대표 강의 저장소를 본인 GitHub 계정으로 fork한다.
2. fork한 저장소를 clone하거나 기존 로컬 저장소의 remote가 본인 fork를 가리키는지 확인한다.
3. push 권한이 있는지 확인한다.

```powershell
# 원격 저장소 확인
# origin이 본인 GitHub fork URL인지 확인한다
git remote -v

# 현재 브랜치 확인
git branch
```

> 💡 `origin`이 강사/대표 저장소를 가리키면 push가 실패할 수 있다.
> 수강생 실습은 본인 fork 저장소 기준으로 진행한다.

---

## 1-5. 2일차 리소스 선행조건

3일차 GitOps 실습은 2일차에서 만든 Kubernetes 리소스를 이어서 사용한다.
`workspace/k8s-day3-gitops/helm`은 같은 앱 Deployment를 Helm chart로 패키징한 실습 디렉토리다.
Argo CD에서 이 경로를 source로 등록하면 Helm chart로 배포를 관리할 수 있다.

Argo CD 등록 전에 아래 리소스가 이미 `todo-app` 네임스페이스에 있어야 한다.

```powershell
kubectl get namespace todo-app
kubectl get service postgres -n todo-app
kubectl get service todo-app -n todo-app
kubectl get configmap app-config -n todo-app
kubectl get secret db-secret -n todo-app
```

> 위 리소스가 없으면 2일차 실습 매니페스트를 먼저 적용한 뒤 진행한다.

---

## ✅ 모듈 1 완료 기준

- [ ] CI(GitHub Actions)와 CD(Argo CD)의 역할 차이를 설명할 수 있다
- [ ] 오늘 실습할 파일 구조(`ci.yml`, `workspace/k8s-day3-gitops/`, `workspace/k8s-day3-gitops/helm/`)를 파악했다
- [ ] 본인 GitHub fork 저장소가 `origin`으로 연결되어 있다
- [ ] 2일차 리소스(Service/ConfigMap/Secret/PostgreSQL)가 `todo-app` 네임스페이스에 남아 있다

---

[← 목차로 돌아가기](./README.md) | [모듈 2 →](./module2.md)
