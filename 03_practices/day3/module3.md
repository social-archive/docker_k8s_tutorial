# 모듈 3 — 버전관리와 이미지 태그 반영

> **목표**: GitHub Actions가 GHCR에 push한 이미지 태그를 확인하고
> `day3/k8s-day3-gitops/helm/values.yaml`의 `image.tag`에 수동으로 반영한다.
> Git commit 이력으로 "어떤 이미지 버전이 언제 배포됐는지" 추적하는 흐름을 체험하고,
> 이미지 태그 전략과 배포 이력 관리 방식을 이해한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다.

## 3-1. 이미지 태깅 전략

| 태그 유형 | 예시 | 특징 |
|---|---|---|
| `latest` | `todo-app:latest` | 항상 최신을 가리키지만 어떤 커밋인지 불명확 |
| 7자리 short SHA | `todo-app:abc1234` | 특정 커밋과 1:1 대응. 롤백 기준으로 활용 |
| 날짜 | `todo-app:20240612` | 날짜 기준 추적 가능하지만 같은 날 여러 배포 구분 불가 |
| 시맨틱 버전 | `todo-app:v1.2.3` | 릴리스 관리에 적합. 수동 태깅 필요 |

> 💡 **이 과정의 기본 방식**: GitHub Actions가 만든 7자리 short SHA 태그를 확인하고
> `values.yaml`의 `image.tag`에 반영한다.
> `latest` 태그 단독 사용은 롤백이 불가능하고 어떤 커밋이 실행 중인지 알 수 없어 운영에 적합하지 않다.

---

## 3-2. GitHub Actions에서 생성된 이미지 태그 확인

1. GitHub 저장소 → **Actions** 탭으로 이동한다.
2. 모듈 2에서 성공한 workflow run을 클릭한다.
3. `Docker Build & Push` Step 로그에서 이미지 태그를 확인한다.

또는 저장소의 **Packages** 섹션에서:

1. 저장소 메인 페이지 우측 **Packages** 클릭
2. `todo-app` 이미지 클릭
3. 태그 목록에서 7자리 short SHA 태그를 확인한다.

**확인할 이미지 태그 형식**

```text
ghcr.io/<username>/todo-app:abc1234
```

> 이 7자리 값(`abc1234` 부분)을 복사해 다음 섹션에서 사용한다.

---

## 3-3. values.yaml 이미지 태그 반영

Antigravity IDE 또는 사용 중인 IDE에서 `day3/k8s-day3-gitops/helm/values.yaml`을 연다.

**변경 전**

```yaml
image:
  repository: ghcr.io/YOUR_GITHUB_USERNAME/todo-app
  tag: "replace-with-short-sha"
```

**변경 후** (본인 계정명과 확인한 7자리 SHA 태그로)

```yaml
image:
  repository: ghcr.io/<username>/todo-app
  tag: "<7자리-short-sha>"
```

> 이것이 GitOps의 핵심이다.
> 클러스터를 직접 수정하는 대신, Git에 저장된 `values.yaml`을 수정하고
> Argo CD가 그 상태를 클러스터에 반영하게 한다.
>
> Helm을 사용하기 전에는 `app-deployment.yml`의 `image:` 줄을 직접 찾아 수정했다.
> Helm에서는 **변경 위치가 `values.yaml`의 `image.tag` 한 줄**로 명확히 정해진다.

---

## 3-4. 변경사항 commit / push

```powershell
git add day3/k8s-day3-gitops/helm/values.yaml
git commit -m "deploy: update image tag to <7자리-short-sha>"
git push origin main
```

---

## 3-5. Git log로 배포 이력 추적

```powershell
# values.yaml에 대한 커밋 이력 확인
git log --oneline day3/k8s-day3-gitops/helm/values.yaml

# 특정 커밋의 이미지 태그 확인
git show HEAD:day3/k8s-day3-gitops/helm/values.yaml | findstr tag
```

**출력 예시**

```
a1b2c3d deploy: update image tag to abc1234
9f8e7d6 helm: update image tag in values
```

> 💡 Git 이력이 곧 배포 이력이다.
> 어떤 커밋에서 어떤 이미지가 배포됐는지 역추적할 수 있다.

---

## 3-6. GHCR 이미지 로컬 확인 (선택)

GHCR 이미지가 public이거나 로컬 Docker가 GHCR에 로그인되어 있으면
직접 pull해서 이미지 구성을 확인할 수 있다.

```powershell
# 이미지 pull
docker pull ghcr.io/<username>/todo-app:<7자리-short-sha>

# 이미지 레이어 확인
docker history ghcr.io/<username>/todo-app:<7자리-short-sha>

# 로컬 이미지 목록에서 확인
docker images | findstr todo-app
```

> 💡 GHCR 패키지가 private이면 Personal Access Token(PAT)이 필요하다.
> 권한 이슈가 있으면 이 섹션은 건너뛰고 Actions 로그와 Packages 화면 확인으로 대체한다.

---

## 3-7. latest vs SHA 태그 동작 비교

```powershell
# latest 태그 pull (있는 경우)
docker pull ghcr.io/<username>/todo-app:latest

# 두 태그의 IMAGE ID 비교
docker images ghcr.io/<username>/todo-app
```

**출력 예시**

```
REPOSITORY                          TAG       IMAGE ID
ghcr.io/<username>/todo-app         latest    abc123456789
ghcr.io/<username>/todo-app         abc1234   abc123456789
```

> IMAGE ID가 같으면 두 태그가 같은 이미지다.
> `latest`는 항상 최신 빌드를 가리키도록 덮어써진다.
>
> `values.yaml`에서 `latest`를 사용하면:
> - 같은 태그로 다음 빌드가 push돼도 Argo CD는 변경을 감지하지 못한다
> - 어떤 커밋이 실행 중인지 알 수 없다
>
> 이 때문에 SHA 태그를 사용한다.

---

## 3-8. 심화/강사 데모 — values.yaml 자동 갱신

GitHub Actions가 이미지 빌드 후 `values.yaml`의 `image.tag`를
자동으로 수정·commit·push하는 방식도 가능하다.
다음 추가 설정이 필요하므로 기본 실습에서는 제외하고 강사 데모 또는 심화자료로 다룬다.

- GitHub Actions `contents: write` 권한
- branch protection 설정
- commit loop 방지
- 개인/조직 저장소 권한 차이

예시 흐름:

```yaml
- name: Update values.yaml image tag (advanced demo)
  run: |
    $sha = "${{ steps.sha.outputs.short }}"
    (Get-Content day3/k8s-day3-gitops/helm/values.yaml) `
      -replace 'tag: ".*"', "tag: `"$sha`"" `
      | Set-Content day3/k8s-day3-gitops/helm/values.yaml
    git config user.email "ci@github.com"
    git config user.name "GitHub Actions"
    git add day3/k8s-day3-gitops/helm/values.yaml
    git commit -m "ci: update image tag to $sha"
    git push
```

---

## ✅ 모듈 3 완료 기준

- [ ] GitHub Actions 또는 GHCR Packages에서 7자리 short SHA 이미지 태그를 확인했다
- [ ] `day3/k8s-day3-gitops/helm/values.yaml`의 `image.repository`와 `image.tag`를 수정했다
- [ ] 변경사항을 commit/push 했다
- [ ] `git log --oneline day3/k8s-day3-gitops/helm/values.yaml`로 배포 이력을 확인했다
- [ ] `latest` 태그만 쓰면 안 되는 이유를 설명할 수 있다

---

[← 모듈 2](./module2.md) | [← 목차로 돌아가기](./README.md) | [모듈 4 →](./module4.md)
