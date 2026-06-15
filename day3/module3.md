# 모듈 3 — 버전관리와 이미지 태깅

> **목표**: 빌드된 이미지에 커밋 SHA 기반 태그를 부여하고  
> 배포 매니페스트에 반영해 버전 추적이 가능하도록 설계한다.  
> "어떤 커밋이 어떤 배포 버전인지" 역추적할 수 있는 구조를 만든다.

---

## 3-1. 이미지 태깅 전략

| 태그 유형 | 예시 | 사용 시점 |
|---|---|---|
| `latest` | `todo-app:latest` | 항상 최신 빌드를 가리킴 |
| 커밋 SHA | `todo-app:abc1234` | 특정 커밋의 빌드를 고정 |
| 날짜 | `todo-app:20240612` | 날짜 기준 추적 |
| 시맨틱 버전 | `todo-app:v1.2.3` | 릴리스 단위 관리 |

> 💡 **운영 권장**: `latest` 단독 사용은 위험하다.  
> `latest` + 커밋 SHA를 함께 push해 추적 가능성을 확보한다.

---

## 3-2. CI에서 커밋 SHA 태깅 확인

Antigravity IDE에서 `.github/workflows/ci.yml`을 열어  
이미지 태깅 부분을 확인한다.

```yaml
- name: Build Docker image
  run: |
    docker build -t ghcr.io/${{ github.repository }}/todo-app:${{ github.sha }} .
    docker tag ghcr.io/${{ github.repository }}/todo-app:${{ github.sha }} \
               ghcr.io/${{ github.repository }}/todo-app:latest
```

---

## 3-3. 배포 매니페스트 이미지 태그 갱신

CI 파이프라인 마지막 Step에서 `day3/k8s/app-deployment.yml`의 이미지 태그를  
현재 커밋 SHA로 자동 업데이트한다.

```yaml
- name: Update manifest
  run: |
    sed -i "s|image: .*todo-app:.*|image: ghcr.io/${{ github.repository }}/todo-app:${{ github.sha }}|" \
      day3/k8s/app-deployment.yml
    git config user.email "ci@github.com"
    git config user.name "GitHub Actions"
    git add day3/k8s/app-deployment.yml
    git commit -m "ci: update image tag to ${{ github.sha }}"
    git push
```

---

## 3-4. 버전 추적 실습

```powershell
# Git 로그로 배포 이력 확인
git log --oneline day3/k8s/app-deployment.yml

# 특정 커밋의 매니페스트 내용 확인
git show HEAD:day3/k8s/app-deployment.yml | findstr image
```

---

## 3-5. Antigravity IDE에서 매니페스트 확인

`day3/k8s/app-deployment.yml`을 열어 현재 이미지 태그가  
커밋 SHA로 업데이트되어 있는지 확인한다.

```yaml
containers:
  - name: todo-app
    image: ghcr.io/<username>/todo-app:<commit-sha>
```

---

## ✅ 모듈 3 완료 기준

- [ ] CI 실행 후 `day3/k8s/app-deployment.yml`의 이미지 태그가 커밋 SHA로 갱신되었다
- [ ] `git log`로 배포 이미지 태그 변경 이력을 확인할 수 있다
- [ ] 이미지 태그와 Git 커밋 SHA가 연결됨을 이해한다

---

[← 모듈 2](./module2.md) | [← 목차로 돌아가기](./README.md) | [모듈 4 →](./module4.md)
