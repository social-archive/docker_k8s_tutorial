# DevOps를 위한 Docker & Kubernetes 실무 - 3일차 기획안

## 과정 개요
- **과정명**: DevOps를 위한 Docker & Kubernetes 실무 - 3일차
- **운영 방향**: GitHub Actions로 CI를 구성하고, 이미지 태그 변경을 Git에 남기는 GitOps 흐름을 실습한다. 이후 Helm을 별도 학습 주제로 다뤄 Kubernetes YAML을 chart/values/templates 구조로 패키징하고, Argo CD가 plain manifest와 Helm chart를 모두 동기화할 수 있음을 연결한다. 클라우드 배포는 제외하고 Docker Desktop Kubernetes 기반 로컬 클러스터만 사용한다.
- **핵심 메시지**: 3일차는 코드 변경이 이미지로 빌드되고, 배포 소스가 Git에 남으며, Helm과 Argo CD를 통해 Kubernetes에 반영되는 전체 흐름을 경험하는 날이다.
- **대상 학습자**: 운영/배포 담당 개발자, DevOps 엔지니어, SRE 지망자.

## 학습목표
- GitHub Actions로 빌드와 테스트를 자동화할 수 있다.
- GitHub Actions가 생성한 GHCR 이미지 태그를 확인하고, 배포 매니페스트에 수동 반영하는 흐름을 수행할 수 있다.
- Helm chart의 기본 구성(`Chart.yaml`, `values.yaml`, `templates/`)을 이해하고, 이미지 태그·replicas·resources 같은 변경점을 values로 분리할 수 있다.
- Argo CD로 plain manifest 디렉토리와 Helm chart를 로컬 Kubernetes 클러스터에 선언형으로 동기화할 수 있다.
- 배포 상태를 확인하고 Git revert 또는 Argo CD rollback으로 복구할 수 있다.

## 3일차 실습 전제
- 2일차 실습에서 `todo-app` 네임스페이스가 이미 생성되어 있어야 한다.
- `postgres` Service/Deployment/PVC, `todo-app` Service, `app-config` ConfigMap, `db-secret` Secret은 2일차 결과물을 그대로 사용한다.
- `day3/k8s`는 GitOps 기본 흐름을 빠르게 이해하기 위한 raw manifest 예제다.
- Helm은 별도 학습 주제로 다룬다. 같은 앱 Deployment를 chart/values/templates 구조로 재구성하되, 2일차 기반 리소스 전체를 새로 설치하는 주제로 확장하지 않는다.

## 내용 구성
| 모듈 | 핵심 주제 | 학습성과(산출물) | 실습/활동 |
|---|---|---|---|
| 모듈 1 | CI/CD 구조와 GitOps 이해 | CI와 CD 역할 분리 도식 | GitHub Actions, Helm, Argo CD 역할 정리 |
| 모듈 2 | GitHub Actions CI 구성 | workflow 파일 | 테스트, 빌드, 이미지 생성 자동화 |
| 모듈 3 | 버전관리와 이미지 태그 수동 반영 | 변경된 배포 매니페스트 | GHCR 이미지 태그 확인, manifest 수정, commit/push |
| 모듈 4 | Helm 기본과 차트 구조 | Helm chart 초안 | Chart.yaml, values.yaml, templates 작성, helm template 확인 |
| 모듈 5 | Argo CD 설치 및 앱 등록 | Argo CD 애플리케이션 | plain manifest와 Helm chart source 등록 |
| 모듈 6 | Sync, 롤백, 운영 체크리스트 | Sync/OutOfSync 확인 결과, 롤백 절차서 | 수동 Sync, 장애 재현, 이전 버전 복구 |

## 상세 내용
### 모듈 1. CI/CD 구조와 GitOps 이해
개발, 검증, 운영 환경 간 변경 관리 흐름과 GitOps의 핵심 개념을 도식으로 정리한다. GitHub Actions는 이미지를 만들고, Helm은 배포 소스를 패키징하며, Argo CD는 Git 상태를 클러스터에 동기화한다는 역할 분리를 먼저 보여준다.

### 모듈 2. GitHub Actions CI 구성
Spring Boot 애플리케이션 코드가 push되었을 때, GitHub Actions가 트리거되어 빌드 및 테스트를 수행하고 이미지를 생성하는 workflow를 작성한다.

### 모듈 3. 버전관리와 이미지 태깅
GHCR에 생성된 이미지 태그를 확인하고, 이를 수강생이 직접 Git 배포 매니페스트에 반영하여 버전 추적이 가능하도록 설계한다.

### 모듈 4. Helm 기본과 차트 구조
Helm을 별도 학습 주제로 다룬다. `Chart.yaml`, `values.yaml`, `templates/`의 역할을 설명하고, 앱 Deployment의 이미지 repository/tag, replicas, resources를 values로 분리한다. `helm template`으로 렌더링된 Kubernetes YAML을 확인해 raw manifest와 Helm chart의 차이를 비교한다.

### 모듈 5. Argo CD 설치 및 앱 등록
로컬 Kubernetes 환경에 Argo CD를 구축하고, Git 배포 저장소를 Argo CD에 등록한다. 기본 raw manifest 경로와 Helm chart 경로를 각각 Application source로 등록하는 차이를 보여준다.

### 모듈 6. Sync, 롤백, 운영 체크리스트
Git의 manifest 또는 Helm values 변경이 Argo CD를 거쳐 로컬 Kubernetes 클러스터에 반영되는 과정을 확인한다. 잘못된 이미지 태그를 배포해 장애를 재현하고, Git revert 또는 Argo CD rollback으로 이전 정상 버전으로 복구한다.

## 실습 시나리오
### 실습 1. GitHub Actions CI 파이프라인 구성
- 목표: 코드 변경 시 테스트와 빌드가 자동으로 실행되도록 workflow를 작성한다.
- 체크포인트: push 또는 PR 발생 시 job이 실행되고, 테스트와 빌드가 성공한다.
- 성공 조건: Docker 이미지가 생성되며 GHCR 반영이 완료된다.

### 실습 2. 버전 태깅과 배포 기준 정리
- 목표: GHCR 이미지 태그를 확인하고 배포 매니페스트에 수동 반영한다.
- 체크포인트: 이미지 태그 확인, `day3/k8s/app-deployment.yml` 수정, commit/push, 배포 이력 추적 가능.
- 성공 조건: 어떤 이미지 태그가 어떤 Git commit으로 배포됐는지 역추적할 수 있다.

### 실습 3. Helm chart 작성
- 목표: 앱 Deployment를 Helm chart로 패키징하고 변경점을 values로 분리한다.
- 체크포인트: `Chart.yaml`, `values.yaml`, `templates/deployment.yaml` 작성, `helm template` 결과 확인.
- 성공 조건: raw manifest의 `image:` 수정 방식과 Helm `values.yaml` 수정 방식의 차이를 설명할 수 있다.

### 실습 4. Argo CD 설치와 애플리케이션 등록
- 목표: 로컬 Kubernetes에 Argo CD를 설치하고 plain manifest 앱과 Helm chart 앱을 등록한다.
- 체크포인트: Argo CD UI 또는 CLI 접근, 앱 등록, Sync 상태 확인.
- 성공 조건: Git 저장소의 manifest 또는 Helm chart가 클러스터 대상 앱으로 연결된다.

### 실습 5. 선언형 배포와 동기화 확인
- 목표: Git 저장소의 변경이 클러스터에 반영되는 흐름을 확인한다.
- 체크포인트: manifest 또는 values 수정, Sync 실행, Pod/Service 상태 확인.
- 성공 조건: Argo CD에서 동기화 성공 상태를 확인하고 애플리케이션이 정상 응답한다.

### 실습 6. 롤백 및 복구
- 목표: 잘못된 버전이나 잘못된 설정을 되돌리고 서비스 정상 상태를 복구한다.
- 체크포인트: 이전 revision 선택, Git revert 또는 rollback 수행, 정상 상태 확인.
- 성공 조건: 이전 정상 버전으로 서비스가 복구된다.

## 강의 운영 포인트
- 3일차는 도구 소개보다 **빌드-패키징-배포-롤백이 한 흐름으로 이어진다**는 경험을 주는 데 집중한다.
- GitHub Actions는 CI, Helm은 배포 패키징, Argo CD는 CD/GitOps로 역할을 분리하면 수강생 이해도가 높다.
- Helm은 별도 학습 주제로 키운다. 단, 전체 스택 재설치가 아니라 앱 Deployment를 chart로 패키징하는 범위에 집중한다.
- 자동 동기화만 강조하면 롤백 개념이 흐려질 수 있으므로, 기본 실습은 수동 Sync와 롤백을 중심으로 진행하고 자동 manifest 갱신은 강사 데모/심화자료로 분리한다.
- 로컬 Kubernetes 환경이므로 외부 클라우드 연결 이슈 없이 Helm/GitOps 개념 자체를 학습하는 데 집중한다.

## 준비물
- Docker Desktop Kubernetes 활성화 완료 환경
- 수강생 개인 GitHub fork 저장소
- GitHub Actions Workflow 파일
- Helm CLI
- Argo CD 설치 YAML 또는 manifest
- kubectl, argocd CLI(선택)
- 1일차/2일차에서 사용한 Spring Boot + PostgreSQL 예제
- 2일차에서 생성한 `todo-app` Service, `postgres`, `app-config`, `db-secret` 리소스

## 3일차 완료 기준
- GitHub Actions로 CI가 자동 실행된다.
- GHCR 이미지 태그를 배포 매니페스트에 반영하고 Git 이력으로 추적할 수 있다.
- Helm chart/values/templates 구조를 설명하고 `helm template` 결과를 확인할 수 있다.
- Argo CD가 Git 저장소의 manifest 또는 Helm chart를 기준으로 로컬 Kubernetes에 동기화된다.
- 장애 또는 잘못된 배포 상황에서 롤백 절차를 수행할 수 있다.

## 참고
Argo CD는 선언형 GitOps CD 도구이며, Helm chart source를 직접 동기화할 수 있다. GitHub Actions + Helm + Argo CD 조합은 실습용으로 적합하다.
