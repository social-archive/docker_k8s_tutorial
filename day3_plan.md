# DevOps를 위한 Docker & Kubernetes 실무 - 3일차 기획안

## 과정 개요
- **과정명**: DevOps를 위한 Docker & Kubernetes 실무 - 3일차
- **운영 방향**: GitHub Actions로 CI를 구성하고, 수동 이미지 태그 변경을 통해 Argo CD GitOps 배포 흐름을 실습하며 마무리한다. 클라우드 배포는 제외하고 Docker Desktop Kubernetes 기반 로컬 클러스터만 사용한다.
- **핵심 메시지**: 3일차는 코드 변경이 빌드되고, 이미지가 만들어지고, Git 상태를 기준으로 Kubernetes에 반영되는 전체 흐름을 경험하는 날이다.
- **대상 학습자**: 운영/배포 담당 개발자, DevOps 엔지니어, SRE 지망자.

## 학습목표
- GitHub Actions로 빌드와 테스트를 자동화할 수 있다.
- GitHub Actions가 생성한 GHCR 이미지 태그를 확인하고, 배포 매니페스트에 수동 반영하는 흐름을 수행할 수 있다.
- Argo CD로 로컬 Kubernetes 클러스터에 선언형 배포를 적용할 수 있다.
- 배포 상태를 확인하고 롤백 또는 재동기화로 복구할 수 있다.

## 3일차 실습 전제

- 2일차 실습에서 `todo-app` 네임스페이스가 이미 생성되어 있어야 한다.
- `postgres` Service/Deployment/PVC, `todo-app` Service, `app-config` ConfigMap, `db-secret` Secret은 2일차 결과물을 그대로 사용한다.
- `day3/k8s`에는 Argo CD가 관리할 `app-deployment.yml`과 `kustomization.yml`만 둔다. 즉 3일차는 전체 애플리케이션 스택을 처음부터 재생성하는 날이 아니라, 기존 Kubernetes 기반 위에서 GitOps 방식으로 앱 Deployment 변경을 반영하는 날이다.

## 내용 구성
| 모듈 | 핵심 주제 | 학습성과(산출물) | 실습/활동 |
|---|---|---|---|
| 모듈 1 | CI/CD 구조와 GitOps 이해 | CI와 CD 역할 분리 도식 | GitHub Actions와 Argo CD 역할 정리 |
| 모듈 2 | GitHub Actions CI 구성 | workflow 파일 | 테스트, 빌드, 이미지 생성 자동화 |
| 모듈 3 | 버전관리와 이미지 태그 수동 반영 | 변경된 배포 매니페스트 | GHCR 이미지 태그 확인, 매니페스트 수정, commit/push |
| 모듈 4 | Argo CD 설치 및 앱 등록 | Argo CD 애플리케이션 | 로컬 Kubernetes와 Git 저장소 연결 |
| 모듈 5 | 선언형 CD와 동기화 실습 | Sync/OutOfSync 확인 결과 | 수동 Sync, 자동 Sync 선택 확인, 상태 점검 |
| 모듈 6 | 롤백과 운영 체크리스트 | 롤백 절차서, 점검표 | 이전 버전 복구, 실패 원인 정리 |

## 상세 내용
### 모듈 1. CI/CD 구조와 GitOps 이해
개발, 검증, 운영 환경 간 변경 관리 흐름과 GitOps의 핵심 개념을 도식으로 정리한다. 이론 위주보다는 "우리가 수동으로 하던 배포를 어떻게 자동화하는가"의 큰 그림을 보여준다.

### 모듈 2. GitHub Actions CI 구성
Spring Boot 애플리케이션 코드가 push되었을 때, GitHub Actions가 트리거되어 빌드 및 테스트를 수행하고 이미지를 생성하는 workflow를 작성한다.

### 모듈 3. 버전관리와 이미지 태깅
GHCR에 생성된 이미지 태그를 확인하고, 이를 수강생이 직접 Git 배포 매니페스트에 반영하여 버전 추적이 가능하도록 설계한다.

### 모듈 4. Argo CD 설치 및 앱 등록
로컬 Kubernetes 환경에 Argo CD를 구축하고, Git 배포 저장소를 Argo CD에 등록하여 쿠버네티스 상태와 Git의 동기화를 준비한다.

### 모듈 5. 선언형 CD와 동기화 실습
Git의 매니페스트 수정이 Argo CD를 거쳐 로컬 쿠버네티스 클러스터로 배포되는 과정을 눈으로 확인하고 동기화(Sync) 상태의 변화를 추적한다.

### 모듈 6. 롤백과 운영 체크리스트
배포가 실패하거나 오동작할 때 Argo CD의 롤백 기능이나 Git revert를 통해 이전 안정화 버전으로 즉각 복구하는 시나리오를 실습한다.

## 실습 시나리오
### 실습 1. GitHub Actions CI 파이프라인 구성
- 목표: 코드 변경 시 테스트와 빌드가 자동으로 실행되도록 workflow를 작성한다.
- 체크포인트: push 또는 PR 발생 시 job이 실행되고, 테스트와 빌드가 성공한다.
- 성공 조건: Docker 이미지가 생성되며 아티팩트 또는 레지스트리 반영이 완료된다.

### 실습 2. 버전 태깅과 배포 기준 정리
- 목표: GHCR 이미지 태그를 확인하고 배포 매니페스트에 수동 반영한다.
- 체크포인트: 이미지 태그 확인, `day3/k8s/app-deployment.yml` 수정, commit/push, 배포 이력 추적 가능.
- 성공 조건: 어떤 이미지 태그가 어떤 Git commit으로 배포됐는지 역추적할 수 있다.

### 실습 3. Argo CD 설치와 애플리케이션 등록
- 목표: 로컬 Kubernetes에 Argo CD를 설치하고 애플리케이션을 등록한다.
- 체크포인트: Argo CD UI 또는 CLI 접근, 앱 등록, Sync 상태 확인.
- 성공 조건: Git 저장소의 매니페스트가 클러스터 대상 앱으로 연결된다.

### 실습 4. 선언형 배포와 동기화 확인
- 목표: Git 저장소의 변경이 클러스터에 반영되는 흐름을 확인한다.
- 체크포인트: 매니페스트 수정, Sync 실행, Pod/Service 상태 확인.
- 성공 조건: Argo CD에서 동기화 성공 상태를 확인하고 애플리케이션이 정상 응답한다.

### 실습 5. 롤백 및 복구
- 목표: 잘못된 버전이나 잘못된 설정을 되돌리고 서비스 정상 상태를 복구한다.
- 체크포인트: 이전 revision 선택, rollback 또는 재동기화 수행, 정상 상태 확인.
- 성공 조건: 이전 정상 버전으로 서비스가 복구된다.

## 강의 운영 포인트
- 3일차는 도구 소개보다 **빌드-배포-롤백이 한 흐름으로 이어진다**는 경험을 주는 데 집중한다.
- GitHub Actions는 CI 전용, Argo CD는 CD 전용으로 역할을 분리하면 수강생 이해도가 높다.
- 자동 동기화만 강조하면 롤백 개념이 흐려질 수 있으므로, 기본 실습은 수동 Sync와 롤백을 중심으로 진행하고 자동 manifest 갱신은 강사 데모/심화자료로 분리한다.
- 로컬 Kubernetes 환경이므로 외부 클라우드 연결 이슈 없이 GitOps 개념 자체를 학습하는 데 집중한다.

## 준비물
- Docker Desktop Kubernetes 활성화 완료 환경
- 수강생 개인 GitHub fork 저장소
- GitHub Actions Workflow 파일
- Argo CD 설치 YAML 또는 manifest
- kubectl, argocd CLI(선택)
- 1일차/2일차에서 사용한 Spring Boot + PostgreSQL 예제
- 2일차에서 생성한 `todo-app` Service, `postgres`, `app-config`, `db-secret` 리소스

## 3일차 완료 기준
- GitHub Actions로 CI가 자동 실행된다.
- Argo CD가 Git 저장소를 기준으로 로컬 Kubernetes에 동기화된다.
- 버전 태그와 배포 이력이 연결되어 추적 가능하다.
- 장애 또는 잘못된 배포 상황에서 롤백 절차를 수행할 수 있다.

## 참고
Argo CD는 선언형 GitOps CD 도구이며, GitHub Actions와 함께 쓰는 조합이 실습용으로 적합하다.
