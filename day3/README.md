# 3일차 실습 가이드 — GitHub Actions + Argo CD GitOps

> **핵심 메시지**: 3일차는 코드 변경이 빌드되고, 이미지가 만들어지고,
> Git 상태를 기준으로 Kubernetes에 반영되는 전체 흐름을 경험하는 날이다.

---

## 학습 목표

- GitHub Actions로 빌드와 테스트를 자동화할 수 있다.
- GitHub Actions가 만든 Docker 이미지 태그를 확인하고 배포 매니페스트에 수동 반영하는 흐름을 수행할 수 있다.
- Argo CD로 로컬 Kubernetes 클러스터에 선언형 배포를 적용할 수 있다.
- 배포 상태를 확인하고 롤백 또는 재동기화로 복구할 수 있다.

---

## GitOps 전체 흐름

```
코드 push (GitHub)
      │
      ▼
GitHub Actions CI
  ├─ 테스트 (Gradle build)
  ├─ Docker 이미지 빌드
  └─ GHCR push (태그: 7자리 short SHA / latest)
            │
            ▼
  수강생이 배포 매니페스트 이미지 태그 수동 변경
            │
            ▼
  Git push (day3/k8s/)
            │
            ▼
  Argo CD OutOfSync 감지
            │
            ▼
  Argo CD Sync → 클러스터 반영
```

---

## 모듈 구성

| 모듈 | 제목 | 파일 |
|:---:|---|---|
| 모듈 1 | CI/CD 구조와 GitOps 이해 | [module1.md](./module1.md) |
| 모듈 2 | GitHub Actions CI 구성 | [module2.md](./module2.md) |
| 모듈 3 | 버전관리와 이미지 태그 수동 반영 | [module3.md](./module3.md) |
| 모듈 4 | Argo CD 설치 및 앱 등록 | [module4.md](./module4.md) |
| 모듈 5 | 선언형 CD와 동기화 실습 | [module5.md](./module5.md) |
| 모듈 6 | 롤백과 운영 체크리스트 | [module6.md](./module6.md) |

---

## 3일차 완료 기준

- [ ] GitHub Actions로 CI가 자동 실행된다
- [ ] Argo CD가 Git 저장소를 기준으로 로컬 Kubernetes에 동기화된다
- [ ] 버전 태그와 배포 이력이 연결되어 추적 가능하다
- [ ] 장애 또는 잘못된 배포 상황에서 롤백 절차를 수행할 수 있다
