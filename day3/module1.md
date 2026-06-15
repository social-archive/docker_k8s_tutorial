# 모듈 1 — CI/CD 구조와 GitOps 이해

> **목표**: "우리가 수동으로 하던 배포를 어떻게 자동화하는가"의 큰 그림을 잡는다.  
> GitHub Actions(CI)와 Argo CD(CD)의 역할을 명확히 분리해 이해한다.

---

## 1-1. 수동 배포 vs GitOps 자동 배포 비교

| 단계 | 수동 배포 | GitOps 자동 배포 |
|---|---|---|
| 코드 변경 | 개발자가 직접 빌드 | push 시 CI가 자동 트리거 |
| 이미지 빌드 | 로컬에서 `docker build` | GitHub Actions가 자동 빌드 |
| 이미지 배포 | `kubectl apply` 수동 실행 | Argo CD가 Git 상태 기준으로 자동 반영 |
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
                      │ 이미지 태그 갱신 후 Git push
                      ▼
┌─────────────────────────────────────────────┐
│           CD (Argo CD)                      │
│  Git 변경 감지 → Sync → 클러스터 반영       │
└─────────────────────────────────────────────┘
```

| 도구 | 역할 | 트리거 |
|---|---|---|
| **GitHub Actions** | CI: 테스트, 빌드, 이미지 생성 | Git push / PR |
| **Argo CD** | CD: 클러스터와 Git 동기화 | Git 변경 감지 |

---

## 1-3. 오늘 사용할 파일 구조

Antigravity IDE에서 아래 구조를 확인한다.

```
docker_k8s_tutorial/
├── .github/workflows/
│   └── ci.yml          ← GitHub Actions CI workflow
└── day3/
    └── k8s/
        ├── app-deployment.yml   ← 이미지 태그 갱신 대상
        └── kustomization.yml    ← Argo CD가 참조하는 진입점
```

---

## 1-4. GitHub 저장소 확인

3일차 실습은 GitHub 저장소가 필요하다.

```powershell
# 원격 저장소 확인
git remote -v

# 현재 브랜치 확인
git branch
```

> 💡 저장소가 없으면 GitHub에서 새 저장소를 생성하고  
> 현재 프로젝트를 push한 뒤 진행한다.

---

## ✅ 모듈 1 완료 기준

- [ ] CI(GitHub Actions)와 CD(Argo CD)의 역할 차이를 설명할 수 있다
- [ ] 오늘 실습할 파일 구조(`ci.yml`, `day3/k8s/`)를 파악했다
- [ ] GitHub 원격 저장소가 연결되어 있다

---

[← 목차로 돌아가기](./README.md) | [모듈 2 →](./module2.md)
