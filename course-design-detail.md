# DevOps를 위한 Docker & Kubernetes 실무 — 교안 상세기획안

> 기업 내부 실무 워크숍을 위한 3일 과정 강의 설계 기준 문서.
> 이 문서는 `README.md`, `day*_plan.md`, `day*/module*.md`, `preflight.md`, `troubleshooting.md`, `instructor-runbook.md`를 정리할 때 기준으로 사용한다.

---

## 1. 과정 정의

| 항목 | 내용 |
|---|---|
| 과정명 | DevOps를 위한 Docker & Kubernetes 실무 |
| 과정 형태 | 기업 내부 실무 워크숍 |
| 기간 | 3일 |
| 운영 시간 | 09:00~18:00 |
| 실제 강의/실습 시간 | 점심 1시간과 중간 휴식을 제외한 약 7시간 내외 |
| 기본 환경 | Windows + Docker Desktop |
| 터미널 기준 | Windows PowerShell |
| 권장 IDE | Antigravity IDE |
| 대체 IDE | VS Code, IntelliJ IDEA |
| Kubernetes 관찰 도구 | kubectl 필수, k9s 권장 |

---

## 2. 과정 포지셔닝

이 과정은 단순한 도구 소개가 아니라, 제공된 Spring Boot 애플리케이션을 대상으로 컨테이너화부터 Kubernetes 배포, CI/CD, GitOps, 롤백까지 한 흐름으로 경험하는 실습형 워크숍이다.

핵심 포지션은 다음과 같다.

- Docker/Kubernetes 입문
- Spring 앱을 운영 환경처럼 배포해보는 실습
- CI/CD와 GitOps까지 한 번에 경험
- 수강생 본인 GitHub 저장소에 “내가 직접 배포 파이프라인을 구성했다”는 포트폴리오성 결과물 확보

---

## 3. 대상 수강생과 전제

### 3.1 대상 수강생

- 기업 내부 개발자
- 운영/배포 업무를 이해해야 하는 백엔드 개발자
- Docker/Kubernetes를 처음 실습하는 개발자
- CI/CD와 GitOps 흐름을 한 번에 경험하고 싶은 실무자

### 3.2 선수 지식

| 항목 | 전제 수준 |
|---|---|
| Spring Boot | 코드를 읽을 수 있는 수준 |
| Java/Gradle | 깊은 지식 불필요. 코드는 제공하고 배포에 집중 |
| GitHub | clone/push 가능 |
| GHCR / GitHub Packages | 경험 없음으로 가정 |
| Kubernetes | 완전 처음인 수강생 포함 |
| YAML | 살짝 설명 필요. 전체 문법 강의는 하지 않음 |
| Docker Desktop / WSL2 | 강의 시간에 점검/복구 포함 |

---

## 4. 학습 목표

3일 과정을 마친 수강생은 다음을 수행할 수 있어야 한다.

1. Docker Desktop 기반 로컬 환경에서 컨테이너를 실행하고 상태를 확인할 수 있다.
2. 제공된 Spring Boot 애플리케이션을 Docker 이미지로 빌드할 수 있다.
3. Docker Compose로 Spring Boot 애플리케이션과 PostgreSQL을 함께 실행할 수 있다.
4. Kubernetes에서 Deployment, Service, ConfigMap, Secret, PVC의 역할을 이해하고 적용할 수 있다.
5. Kubernetes 배포 후 `kubectl get`, `describe`, `logs`, rollout 상태 확인으로 기본 진단을 수행할 수 있다.
6. GitHub Actions로 애플리케이션 이미지를 빌드하고 GHCR에 push하는 CI 흐름을 이해할 수 있다.
7. Argo CD를 이용해 Git 저장소의 매니페스트를 Kubernetes 클러스터에 동기화할 수 있다.
8. 잘못된 이미지 태그 배포로 발생하는 장애를 확인하고 Git revert 또는 Argo CD 롤백으로 복구할 수 있다.

---

## 5. 과정 운영 원칙

| 원칙 | 내용 |
|---|---|
| 실습 우선 | 개념 설명은 필요한 만큼만 하고, 명령 실행과 결과 확인을 중심으로 진행한다. |
| 코드 작성 최소화 | Spring 코드는 제공한다. 수강생은 배포, 설정, 운영 확인에 집중한다. |
| 로컬 완결성 | 클라우드 없이 Windows + Docker Desktop + GitHub 중심으로 운영한다. |
| 운영 관점 | “실행됐다”에서 끝내지 않고 로그, 상태, 장애, 롤백까지 확인한다. |
| 포트폴리오성 결과물 | 수강생 각자의 fork 저장소, Actions 이력, GHCR 이미지, 매니페스트 변경 이력을 남긴다. |
| 환경 이슈 흡수 | 사전 설치 가이드를 제공하고, 1일차 오전에 설치 점검/복구 시간을 둔다. |

---

## 6. 도구 기준

### 6.1 IDE

- 권장: Antigravity IDE
- 대체 가능: VS Code, IntelliJ IDEA

문서 표현 기준:

> Antigravity IDE 또는 사용 중인 IDE에서 파일을 엽니다.

전체 문서에서 Antigravity를 기본 흐름으로 설명하되, VS Code/IntelliJ를 사용할 수 있음을 명시한다.

### 6.2 터미널

모든 명령은 Windows PowerShell 기준으로 작성한다.

문서 상단 공통 안내 문구:

```md
> 모든 명령은 Windows PowerShell 기준입니다. Git Bash, macOS, Linux 터미널을 사용하는 경우 일부 명령이 다를 수 있습니다.
```

### 6.3 k9s

- k9s는 권장 도구다.
- 설치가 어렵거나 시간이 부족한 경우 `kubectl get`, `kubectl describe`, `kubectl logs`로 대체한다.

완료 기준 표현은 다음처럼 작성한다.

```text
k9s 또는 kubectl 명령으로 클러스터 상태를 확인할 수 있다.
```

---

## 7. 3일 전체 흐름

```text
1일차: Docker / Docker Compose
  - Docker Desktop, WSL2, Git, PowerShell 환경 점검
  - 컨테이너 기본 실행
  - Spring Boot 앱 Docker 이미지 빌드
  - Docker Compose로 Spring + PostgreSQL 실행
  - Kubernetes 활성화 준비

2일차: Kubernetes 배포
  - 로컬 Kubernetes 클러스터 확인
  - namespace, PVC, Deployment, Service 적용
  - ConfigMap/Secret으로 설정 분리
  - API 응답, 로그, rollout 상태 확인
  - 기본 장애 진단 입문

3일차: GitHub Actions + Argo CD GitOps
  - 수강생 각자 fork 저장소 준비
  - GitHub Actions로 이미지 빌드 및 GHCR push
  - 수동으로 매니페스트 이미지 태그 변경
  - Argo CD로 Git 상태를 클러스터에 Sync
  - 잘못된 이미지 태그 장애 재현 및 복구
```

---

## 8. 일차별 상세 설계

## 8.1 1일차 — Docker / Docker Compose

### 핵심 메시지

컨테이너를 설명으로만 이해하지 않고, Spring Boot 애플리케이션과 PostgreSQL을 직접 컨테이너로 실행해 이후 Kubernetes 실습의 기반을 만든다.

### 운영 조정

Docker Desktop/WSL2 문제를 강의 시간에 함께 다루므로, 1일차 오전 1.5시간을 환경 점검/복구에 배정한다.

### 권장 시간표

| 시간 | 모듈 | 내용 | 운영 메모 |
|---|---|---|---|
| 09:00~10:30 | 모듈 1 | 사전 설치 점검 + Docker Desktop/WSL2 복구 | 필수 환경 점검. 지연 가능성 높음 |
| 10:30~11:30 | 모듈 2 | Docker 기본 개념과 컨테이너 실행 | nginx 또는 hello-world 중심 |
| 11:30~12:30 | 모듈 3 | Spring 앱 컨테이너화 1차 | Dockerfile 구조와 이미지 빌드 |
| 12:30~13:30 | 점심 |  |  |
| 13:30~15:30 | 모듈 4 | Docker Compose 기반 Spring + PostgreSQL 연동 | 1일차 핵심 실습 |
| 15:30~16:30 | 모듈 5 | Kubernetes 개요와 로컬 준비 | kubectl 확인. k9s는 권장 |
| 16:30~18:00 | 모듈 6 | 운영 관점 점검과 2일차 연결 | 포트 충돌, DB 연결 오류, 로그 확인 |

### 1일차 필수 완료 기준

- Docker Desktop이 정상 동작한다.
- `docker build`로 Spring Boot 애플리케이션 이미지를 만들 수 있다.
- `docker compose up`으로 Spring + PostgreSQL을 실행할 수 있다.
- `/actuator/health` 또는 `/todos` API가 정상 응답한다.

### 1일차 권장 완료 기준

- Docker Desktop Kubernetes가 활성화되어 있다.
- `kubectl get nodes`가 정상 동작한다.
- k9s 또는 kubectl로 클러스터 상태를 확인할 수 있다.

---

## 8.2 2일차 — Kubernetes 배포

### 핵심 메시지

컨테이너 실행에서 끝나지 않고, Kubernetes 리소스로 애플리케이션을 배포하고 설정을 분리하며 서비스 상태를 점검한다.

### namespace 기준

모든 Kubernetes namespace는 `todo-app`으로 통일한다.

정리 완료 기준:

```text
모든 kubectl 명령은 -n todo-app 사용
Namespace 리소스 이름은 todo-app 사용
```

### 권장 시간표

| 시간 | 모듈 | 내용 | 운영 메모 |
|---|---|---|---|
| 09:00~10:00 | 모듈 1 | Kubernetes 기본 구조 이해 | node, namespace, pod, service 관찰 |
| 10:00~11:30 | 모듈 2 | PostgreSQL Kubernetes 배포 | PVC, Deployment, Service |
| 11:30~12:30 | 모듈 3 | Spring 애플리케이션 배포 1차 | Deployment 적용, Pod 로그 확인 |
| 12:30~13:30 | 점심 |  |  |
| 13:30~14:30 | 모듈 3/4 | Service 접근 + ConfigMap/Secret 개념 | YAML은 필요한 만큼만 설명 |
| 14:30~16:00 | 모듈 4 | ConfigMap, Secret 기반 설정 분리 | 설정 외부화 경험 |
| 16:00~17:00 | 모듈 5 | 통합 검증과 서비스 확인 | API 응답까지 확인 |
| 17:00~18:00 | 모듈 6 | 운영 관점 문제 진단 입문 | 일부는 강사 데모로 운영 가능 |

### 2일차 완료 기준

- `todo-app` namespace가 준비되어 있다.
- PostgreSQL Deployment, Service, PVC가 정상 동작한다.
- Spring App Deployment, Service가 정상 동작한다.
- ConfigMap/Secret을 통해 DB 설정을 주입한다.
- `http://localhost:30080/todos`가 정상 응답한다.
- `kubectl get`, `describe`, `logs`, rollout 상태 확인을 사용할 수 있다.

---

## 8.3 3일차 — GitHub Actions + Argo CD GitOps

### 핵심 메시지

코드 변경이 이미지로 빌드되고, 이미지 태그가 Git 매니페스트에 반영되며, Argo CD가 Git 상태를 기준으로 Kubernetes에 배포하는 흐름을 경험한다.


### 3일차 Kubernetes 리소스 전제

3일차 `day3/k8s`는 Argo CD가 동기화할 앱 Deployment와 kustomization만 포함한다. 다음 기반 리소스는 2일차 실습 결과를 그대로 사용한다.

- `todo-app` Namespace
- `todo-app` Service
- `postgres` Service/Deployment/PVC
- `app-config` ConfigMap
- `db-secret` Secret

따라서 day3 module4~6에는 Sync/롤백 전에 위 리소스 존재 여부를 확인하는 명령을 둔다.

### GitHub 운영 방식

- 수강생 각자 대표 저장소를 fork한다.
- 각자 fork 저장소에서 GitHub Actions, GHCR, 매니페스트 변경, Argo CD Sync를 수행한다.

### GitOps 기본 실습 범위

기본 실습은 수동 이미지 태그 변경 방식으로 운영한다.

```text
GitHub Actions가 이미지 빌드/푸시
→ 수강생이 이미지 태그 확인
→ Antigravity IDE에서 day3/k8s/app-deployment.yml 이미지 태그 수동 변경
→ git commit / push
→ Argo CD가 OutOfSync 감지
→ 수동 Sync
→ 정상 API 응답 확인
→ 잘못된 이미지 태그 배포
→ 장애 확인
→ git revert 또는 Argo CD rollback으로 복구
```

### 자동 manifest 갱신의 위치

자동 manifest 갱신은 기본 실습에서 제외한다.

운영 방식:

- 강사 데모로 보여준다.
- 심화자료로 제공한다.
- 수강생 필수 실습에는 포함하지 않는다.

제외 이유:

- GitHub Actions 권한 설정 필요
- `contents: write` 권한 필요
- branch protection 변수 존재
- commit loop 방지 필요
- GitHub/GHCR 권한 이슈가 초심자에게 과도할 수 있음

### 권장 시간표

| 시간 | 모듈 | 내용 | 운영 메모 |
|---|---|---|---|
| 09:00~10:00 | 모듈 1 | CI/CD 구조와 GitOps 이해 | CI와 CD 역할 분리 |
| 10:00~11:30 | 모듈 2 | GitHub Actions CI 구성 | fork, Actions, GHCR 확인 |
| 11:30~12:30 | 모듈 3 | 이미지 태그 확인과 수동 매니페스트 변경 | 기본 실습 핵심 |
| 12:30~13:30 | 점심 |  |  |
| 13:30~15:00 | 모듈 4 | Argo CD 설치 및 앱 등록 | 설치 지연 가능성 고려 |
| 15:00~16:30 | 모듈 5 | 수동 Sync와 자동 Sync 개념 | 기본은 수동 Sync |
| 16:30~18:00 | 모듈 6 | 잘못된 이미지 태그 장애 재현 및 롤백 | 마지막은 롤백 완료까지 진행 |

### 3일차 완료 기준

- 수강생 fork 저장소에서 GitHub Actions가 성공한다.
- GHCR에 이미지가 생성된다.
- 수강생이 이미지 태그를 확인하고 `day3/k8s/app-deployment.yml`에 수동 반영한다.
- Git commit/push 후 Argo CD에서 OutOfSync를 확인한다.
- 수동 Sync 후 Pod와 API가 정상 동작한다.
- 잘못된 이미지 태그 배포로 장애를 재현하고 복구한다.

---

## 9. 등급형 완료 기준

기업 내부 워크숍에서는 환경 이슈가 발생할 수 있으므로, 단일 수료 기준 대신 등급형 완료 기준을 사용한다.

## 9.1 Basic

Docker / Docker Compose까지 성공.

완료 조건:

- Docker 이미지 빌드 성공
- Docker Compose로 Spring + PostgreSQL 실행 성공
- `http://localhost:8080/actuator/health` 응답
- `/todos` API 응답

## 9.2 Standard

Kubernetes 배포까지 성공.

완료 조건:

- `todo-app` namespace 생성
- PostgreSQL Deployment/Service/PVC 배포
- Spring App Deployment/Service 배포
- ConfigMap/Secret 적용
- `http://localhost:30080/todos` 응답

## 9.3 Advanced

CI/CD + GitOps + 롤백까지 성공.

완료 조건:

- GitHub Actions 성공
- GHCR 이미지 생성
- `app-deployment.yml` 이미지 태그 수동 변경
- git commit/push
- Argo CD OutOfSync 확인
- 수동 Sync
- 정상 API 응답
- 잘못된 이미지 태그 장애 재현 후 Git revert 또는 Argo CD rollback으로 복구

---

## 10. 장애 재현 실습 설계

### 10.1 수강생 전원 직접 실습

전원 직접 실습은 하나로 제한한다.

```text
잘못된 이미지 태그 배포
→ ImagePullBackOff 확인
→ Argo CD / kubectl / k9s로 상태 확인
→ git revert 또는 정상 태그 복구
```

이 실습을 선택한 이유:

- Kubernetes 초심자도 이해하기 쉽다.
- 실제 현업에서 자주 발생한다.
- Argo CD의 Degraded/OutOfSync 상태와 연결된다.
- 복구 절차가 비교적 명확하다.

### 10.2 강사 데모

다음 장애는 강사 데모로 운영한다.

- ConfigMap 누락
- Secret 오류
- Service selector 불일치
- Argo CD rollback 상세

---

## 11. 최종 산출물

수강생은 다음 결과물을 남긴다.

### 11.1 로컬 실행 결과

- Docker 이미지 빌드 성공
- Docker Compose로 Spring + PostgreSQL 실행
- Kubernetes에서 Spring + PostgreSQL 배포
- Argo CD Sync로 Git 상태를 클러스터에 반영
- 잘못된 이미지 태그 장애 재현 및 복구

### 11.2 GitHub 저장소 결과물

- 본인 fork 저장소
- GitHub Actions 성공 이력
- GHCR 이미지 생성 이력
- `day3/k8s/app-deployment.yml` 이미지 태그 변경 commit
- 롤백 또는 revert commit

---

## 12. 문서 역할 분리

최종 문서 구조는 다음 기준으로 정리한다.

| 문서 | 역할 |
|---|---|
| `README.md` | 과정 소개, 전체 흐름, 환경 요구사항, 최종 결과물 |
| `day1_plan.md` | 1일차 강사용 기획/운영안 |
| `day2_plan.md` | 2일차 강사용 기획/운영안 |
| `day3_plan.md` | 3일차 강사용 기획/운영안 |
| `day*/module*.md` | 수강생 실습지 |
| `preflight.md` | 강의 전 사전 설치/점검 가이드 |
| `troubleshooting.md` | 문제 해결 가이드 |
| `instructor-runbook.md` | 강사용 진행표와 운영 대본 |
| `course-design-detail.md` | 전체 강의 설계 기준 문서 |

---

## 13. 추가 작성 예정 문서

### 13.1 preflight.md

강의 전 수강생에게 제공한다.

포함 내용:

- Docker Desktop 설치
- WSL2 확인
- Git 설치
- Antigravity IDE 설치 권장
- kubectl 확인
- k9s 선택 설치
- GitHub 계정 준비
- 대표 저장소 fork 준비

### 13.2 troubleshooting.md

수강생과 강사용 문제 해결 문서.

포함 내용:

- Docker Desktop 실행 안 됨
- WSL2 비활성화
- 포트 충돌
- docker build 실패
- compose up 실패
- ImagePullBackOff
- CrashLoopBackOff
- ConfigMap/Secret 누락
- GHCR push/pull 문제
- Argo CD OutOfSync/Degraded

### 13.3 instructor-runbook.md

강사용 운영 문서.

포함 내용:

- 각 일차 시간 운영표
- 각 모듈 핵심 멘트
- 강사 데모와 수강생 실습 구분
- 실패 시 우회 루트
- 체크포인트
- 시간이 부족할 때 생략 가능한 파트

---

## 14. 현재 자료 수정 체크리스트

### P0 — 강의 전 반드시 수정

- [x] 모든 namespace를 `todo-app`으로 통일한다.
- [x] kubectl namespace 명령을 `-n todo-app`으로 통일한다.
- [x] 3일차 기본 실습에서 자동 manifest 갱신 표현을 제거한다.
- [x] 자동 manifest 갱신은 강사 데모/심화자료로 분리한다.
- [x] README와 day 문서의 IDE 기준을 Antigravity 권장, VS Code/IntelliJ 대체 가능으로 통일한다.
- [x] 모든 명령 기준을 PowerShell로 명시한다.
- [x] k9s는 필수가 아니라 권장으로 표현한다.

### P1 — 운영 안정성 개선

- [x] `preflight.md`를 추가한다.
- [x] `troubleshooting.md`를 추가한다.
- [x] `instructor-runbook.md`를 추가한다.
- [x] 3일차 선행조건을 명시한다. day2 리소스가 적용되어 있어야 day3 GitOps 실습이 정상 진행된다.
- [x] GitHub fork/GHCR 준비 절차를 명확히 작성한다.

### P2 — 품질 향상

- [ ] 수강생 실습지와 강사용 기획안의 표현을 분리한다.
- [ ] 각 모듈에 예상 소요시간과 성공 조건을 명확히 적는다.
- [ ] 장애 실습은 전원 직접 실습과 강사 데모로 구분한다.
- [ ] 최종 완료 기준을 Basic/Standard/Advanced로 정리한다.

---

## 15. 다음 작업 순서

권장 순서:

1. 이 문서를 기준으로 대표 설계 기준을 확정한다.
2. `README.md`를 과정 소개 문서로 재정리한다.
3. `day*_plan.md`를 강사용 기획안으로 재정리한다.
4. `day*/module*.md`를 수강생 실습지로 정리한다.
5. `preflight.md`, `troubleshooting.md`, `instructor-runbook.md`를 추가한다.
6. 실제 명령 dry-run으로 Docker/Compose/Kubernetes/GitOps 흐름을 검증한다.
