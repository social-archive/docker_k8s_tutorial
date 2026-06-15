# 모듈 2 — GitHub Actions CI 구성

> **목표**: Spring Boot 코드가 push될 때
> GitHub Actions가 자동으로 테스트·빌드·이미지 생성을 수행하고
> GHCR에 이미지를 push하는 CI workflow를 이해한다.
> 배포 매니페스트 이미지 태그 변경은 다음 모듈에서 수동으로 진행한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다. (`git add day3/k8s/...` 경로가 루트 기준입니다.)

## 2-1. workflow 파일 열기

Antigravity IDE 또는 사용 중인 IDE에서 `.github/workflows/ci.yml`을 열어 구조를 확인한다.

**workflow 핵심 구조**

```yaml
on:
  push:
    branches: [ main ]      # main 브랜치 push 시 트리거

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4        # 코드 체크아웃
      - name: Build & Test               # Gradle 빌드 + 테스트
      - name: Build Docker image         # 이미지 빌드
      - name: Push to GHCR               # GitHub Container Registry push
```

실제 workflow의 이미지 이름과 태그 규칙:

```yaml
env:
  IMAGE_NAME: ${{ github.repository_owner }}/todo-app

# docker/metadata-action 결과
# ghcr.io/<GitHub-username>/todo-app:<7자리-short-sha>
# latest 태그도 함께 생성되지만 배포 실습에는 7자리 short SHA 태그를 사용한다
```

---

## 2-2. GitHub Actions 트리거

```powershell
# 변경사항을 커밋하고 push하면 CI가 자동 실행된다
git add .
git commit -m "ci: trigger workflow test"
git push origin main
```

---

## 2-3. 실행 결과 확인

1. GitHub 저장소 → **Actions** 탭 클릭
2. 실행 중인 workflow 클릭
3. 각 Step의 로그 확인

**확인할 항목**

| Step | 확인 내용 |
|---|---|
| Checkout | 코드가 정상 받아졌는지 |
| Build & Test | Gradle 빌드/테스트가 성공인지 |
| Build Docker image | 이미지 빌드 성공인지 |
| Push to GHCR | GHCR에 이미지가 올라갔는지 |

---

## 2-4. GHCR 이미지 확인

기본 확인은 GitHub 화면에서 진행한다.

1. GitHub 저장소 → **Packages** 섹션으로 이동
2. `todo-app` 이미지가 생성되었는지 확인
3. 이미지 태그에서 7자리 short SHA 태그를 확인한다. `latest` 태그도 보일 수 있지만 배포 실습에는 사용하지 않는다

로컬 pull 확인은 선택이다. GHCR 패키지가 private이면 Personal Access Token이 필요할 수 있으므로, 기본 실습에서는 Actions 로그와 Packages 화면 확인까지만 필수로 둔다.

```powershell
# 선택: public package이거나 GHCR 로그인 준비가 된 경우만 실행
docker pull ghcr.io/<GitHub-username>/todo-app:<7자리-short-sha>
```

---

## 2-5. workflow 주요 환경변수

| 변수 | 의미 |
|---|---|
| `GITHUB_SHA` | 현재 커밋의 SHA. workflow에서는 7자리 short SHA 태그로 사용 |
| `GITHUB_REPOSITORY` | `username/repo` 형식의 저장소 이름 |
| `GITHUB_TOKEN` | 자동 생성되는 인증 토큰 (GHCR push용) |

---

## ✅ 모듈 2 완료 기준

- [ ] `git push` 후 GitHub Actions workflow가 자동 실행된다
- [ ] 빌드·테스트·이미지 생성 Step이 모두 성공(초록색)이다
- [ ] GHCR 또는 Actions 로그에서 이미지 생성을 확인했다

---

[← 모듈 1](./module1.md) | [← 목차로 돌아가기](./README.md) | [모듈 3 →](./module3.md)
