# 모듈 3 — 버전관리와 이미지 태그 수동 반영

> **목표**: GitHub Actions가 GHCR에 push한 이미지 태그를 확인하고
> `day3/k8s/app-deployment.yml`에 수동으로 반영한다.
> Git commit 이력을 통해 "어떤 이미지 버전이 어떤 배포 상태인지" 추적할 수 있는 구조를 만든다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다. (`git add day3/k8s/...` 경로가 루트 기준입니다.)

## 3-1. 이미지 태깅 전략

| 태그 유형 | 예시 | 사용 시점 |
|---|---|---|
| `latest` | 참고용 태그 | 항상 최신 빌드를 가리키지만, 기본 배포 실습에는 사용하지 않음 |
| 7자리 short SHA | `todo-app:abc1234` | 특정 커밋의 빌드를 고정 |
| 날짜 | `todo-app:20240612` | 날짜 기준 추적 |
| 시맨틱 버전 | `todo-app:v1.2.3` | 릴리스 단위 관리 |

> 💡 **운영 권장**: `latest` 단독 사용은 위험하다.
> 강의 기본 실습에서는 Actions 로그 또는 GHCR Packages 화면에서 생성된 7자리 short SHA 태그를 확인하고, 그 값을 매니페스트에 직접 반영한다.

---

## 3-2. GitHub Actions에서 생성된 이미지 태그 확인

1. GitHub 저장소 → **Actions** 탭으로 이동한다.
2. 성공한 workflow run을 연다.
3. `Docker Build & Push` 로그에서 이미지 태그를 확인한다.
4. 또는 저장소의 **Packages**에서 `todo-app` 이미지를 확인한다.

예시 태그:

```text
ghcr.io/<username>/todo-app:abc1234
```

---

## 3-3. 배포 매니페스트 이미지 태그 수동 변경

Antigravity IDE 또는 사용 중인 IDE에서 `day3/k8s/app-deployment.yml`을 연다.

기존 placeholder 예시:

```yaml
image: ghcr.io/YOUR_GITHUB_USERNAME/todo-app:replace-with-short-sha
```

수강생 본인의 GitHub 계정과 GitHub Actions/GHCR에서 확인한 7자리 short SHA 이미지 태그로 변경한다.

```yaml
image: ghcr.io/<username>/todo-app:<7자리-short-sha>
```

> 이 과정이 GitOps의 핵심이다. 클러스터를 직접 수정하는 대신, Git에 저장된 매니페스트를 수정하고 Argo CD가 그 상태를 클러스터에 반영하게 한다.

---

## 3-4. 변경사항 commit / push

```powershell
git add day3/k8s/app-deployment.yml
git commit -m "deploy: update image tag"
git push origin main
```

---

## 3-5. 버전 추적 확인

```powershell
# Git 로그로 배포 이력 확인
git log --oneline day3/k8s/app-deployment.yml

# 특정 커밋의 매니페스트 내용 확인
git show HEAD:day3/k8s/app-deployment.yml | findstr image
```

---

## 3-6. 심화/강사 데모 — manifest 자동 갱신

GitHub Actions가 이미지 빌드 후 `day3/k8s/app-deployment.yml`을 자동 수정하고 commit/push하는 방식도 가능하다.
다만 이 방식은 다음 추가 고려가 필요하므로 기본 실습에서는 제외하고 강사 데모 또는 심화자료로 다룬다.

- GitHub Actions `contents: write` 권한
- branch protection 설정
- commit loop 방지
- 개인/조직 저장소 권한 차이
- 실패 시 수강생별 복구 난이도 증가

예시 흐름:

```yaml
- name: Update manifest (advanced demo)
  run: |
    # 이미지 태그 치환 후 commit/push
    git config user.email "ci@github.com"
    git config user.name "GitHub Actions"
    git add day3/k8s/app-deployment.yml
    git commit -m "ci: update image tag"
    git push
```

---

## ✅ 모듈 3 완료 기준

- [ ] GitHub Actions 또는 GHCR Packages에서 생성된 이미지 태그를 확인했다
- [ ] `day3/k8s/app-deployment.yml`의 이미지 태그를 수동으로 변경했다
- [ ] 변경사항을 commit/push 했다
- [ ] 이미지 태그와 Git commit 이력이 연결됨을 이해한다

---

[← 모듈 2](./module2.md) | [← 목차로 돌아가기](./README.md) | [모듈 4 →](./module4.md)
