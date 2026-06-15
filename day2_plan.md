# DevOps를 위한 Docker & Kubernetes 실무 - 2일차 기획안

## 과정 개요
- **과정명**: DevOps를 위한 Docker & Kubernetes 실무 - 2일차
- **운영 방향**: 1일차에 만든 Spring + PostgreSQL 컨테이너 구성을 Docker Desktop Kubernetes 환경으로 옮겨 Deployment, Service, ConfigMap, Secret 중심의 로컬 쿠버네티스 배포 실습을 수행한다.
- **핵심 메시지**: 2일차는 컨테이너 실행에서 끝나지 않고, 쿠버네티스 리소스로 애플리케이션을 배포하고 설정을 분리하며 서비스 상태를 점검하는 날이다.
- **대상 학습자**: 운영/배포 담당 개발자, DevOps 엔지니어, SRE 지망자.

## 학습목표
- Docker Desktop Kubernetes 환경에서 기본 리소스를 조회하고 배포 흐름을 이해할 수 있다.
- Spring 애플리케이션과 PostgreSQL을 Deployment와 Service로 배포할 수 있다.
- ConfigMap과 Secret을 이용해 설정값과 민감정보를 분리할 수 있다.
- 배포 후 Pod, Service, 로그 상태를 점검하고 기본적인 문제를 진단할 수 있다.

## 내용 구성
| 모듈 | 핵심 주제 | 학습성과(산출물) | 실습/활동 |
|---|---|---|---|
| 모듈 1 | Kubernetes 기본 구조 이해 | 로컬 클러스터 점검 결과 | node, pod, namespace 조회 |
| 모듈 2 | PostgreSQL 쿠버네티스 배포 | DB Deployment, Service, PVC | DB 기동 및 내부 접근 확인 |
| 모듈 3 | Spring 애플리케이션 배포 | App Deployment, Service | 애플리케이션 Pod 기동 |
| 모듈 4 | ConfigMap, Secret 기반 설정 분리 | 설정 분리형 매니페스트 | DB 접속 정보 주입 실습 |
| 모듈 5 | 통합 검증과 서비스 확인 | 앱-DB 연동 결과 | API 호출, 로그 확인, k9s로 상태 점검 |
| 모듈 6 | 운영 관점 문제 진단 입문 | 장애 원인 분석 메모 | describe, logs, rollout 상태 확인 |

## 상세 내용
### 모듈 1. Kubernetes 기본 구조 이해
Docker Desktop Kubernetes 환경에서 node, namespace, pod, service 개념을 정리하고 실제 리소스 조회 명령으로 연결한다. 이 모듈은 이론 나열보다 “현재 클러스터에 무엇이 떠 있는가”를 확인하는 관찰 중심으로 운영한다.

### 모듈 2. PostgreSQL 쿠버네티스 배포
PostgreSQL을 Deployment와 Service로 구성하고, 필요 시 PersistentVolumeClaim을 통해 데이터 저장 구조를 함께 설명한다. 상태 저장 워크로드를 복잡하게 깊게 다루기보다, 쿠버네티스에서 DB가 어떻게 배치되는지 이해시키는 수준으로 설계한다.

### 모듈 3. Spring 애플리케이션 배포
1일차에 만든 이미지를 기반으로 Spring Boot 애플리케이션을 Deployment로 배포하고 Service로 노출한다. 이때 이미지, 포트, 환경변수, replica 개념을 함께 다룬다.

### 모듈 4. ConfigMap, Secret 기반 설정 분리
애플리케이션 설정과 민감정보를 코드 또는 이미지에 넣지 않고 외부화하는 방법을 학습한다. 과정안의 “환경변수와 설정 파일을 분리하여 개발 환경과 검증 환경 간 차이를 조정”하는 방향을 실제 매니페스트로 구현하는 단계다.

### 모듈 5. 통합 검증과 서비스 확인
배포가 끝난 뒤 `kubectl get`, `describe`, `logs`, 포트 포워딩 또는 서비스 접근을 통해 애플리케이션과 PostgreSQL의 연결 상태를 검증한다. k9s를 활용해 Pod 상태와 로그를 실시간으로 모니터링하며, "배포 성공 = Pod Running"이 아니라 "서비스 응답까지 확인"이라는 운영 관점을 강조한다.

### 모듈 6. 운영 관점 문제 진단 입문
이미지 태그 오류, 환경변수 누락, 서비스 셀렉터 불일치 등 자주 발생하는 문제를 간단히 재현한다. 이를 통해 3일차의 배포 오류 대응, 롤백, 운영 체크리스트 실습으로 자연스럽게 넘어가도록 설계한다.

## 실습 시나리오
### 실습 1. 로컬 클러스터 상태 점검
- 목표: Docker Desktop Kubernetes의 기본 상태를 확인하고 실습 대상 namespace를 준비한다.
- 체크포인트: `kubectl get nodes`, `kubectl get pods -A`, `kubectl create namespace` 명령이 정상 수행된다.
- 성공 조건: 실습용 namespace가 준비되고 기본 리소스 조회가 가능하다.

### 실습 2. PostgreSQL 배포
- 목표: PostgreSQL을 쿠버네티스 리소스로 배포하고 내부 서비스로 연결한다.
- 체크포인트: Deployment 생성, Service 생성, Pod Running, 내부 연결 확인.
- 성공 조건: PostgreSQL Pod가 정상 기동되고 애플리케이션에서 접근 가능한 상태가 된다.

### 실습 3. Spring 애플리케이션 배포
- 목표: Spring Boot 애플리케이션을 Deployment와 Service로 배포한다.
- 체크포인트: 이미지 지정, replica 설정, Service 노출, Pod 로그 확인.
- 성공 조건: 애플리케이션 Pod가 정상 기동되고 서비스 접근이 가능하다.

### 실습 4. ConfigMap과 Secret 적용
- 목표: DB 호스트, 포트, 계정, 비밀번호 등을 매니페스트 외부 설정으로 분리한다.
- 체크포인트: ConfigMap 생성, Secret 생성, 환경변수 주입 성공.
- 성공 조건: 설정 분리 후에도 애플리케이션이 정상 기동된다.

### 실습 5. 서비스 통합 검증
- 목표: Spring 애플리케이션이 PostgreSQL과 정상 연결되는지 확인한다.
- 체크포인트: API 호출 성공, 애플리케이션 로그 확인, DB 연결 확인.
- 성공 조건: 수강생이 배포 후 점검 절차를 순서대로 수행할 수 있다.

## 강의 운영 포인트
- 2일차의 초점은 “쿠버네티스 명령 암기”가 아니라 “배포 단위가 어떻게 달라지는지 이해”에 둔다.
- PostgreSQL까지 쿠버네티스에 올리더라도 운영 심화보다 애플리케이션 연동과 서비스 확인에 집중한다.
- `kubectl apply`만 반복하지 말고 `get`, `describe`, `logs`를 반드시 함께 사용하게 해야 운영 관점이 형성된다.
- 매니페스트는 처음부터 길게 주기보다 Deployment, Service, ConfigMap, Secret을 단계적으로 추가하는 방식이 학습 부담을 줄인다.

## 준비물
- Docker Desktop Kubernetes 활성화 완료 환경
- 1일차에 사용한 Spring Boot 이미지 또는 예제 프로젝트
- PostgreSQL용 기본 매니페스트
- **Antigravity IDE** (코드 및 YAML 편집)
- kubectl
- k9s
- 실습용 namespace 및 YAML 파일 템플릿

## 2일차 완료 기준
- 로컬 Kubernetes 환경에서 Spring 애플리케이션과 PostgreSQL을 각각 배포할 수 있다.
- Deployment, Service, ConfigMap, Secret의 역할을 구분 설명할 수 있다.
- 배포 후 상태 확인 명령으로 기본적인 문제를 진단할 수 있다.
- 3일차의 GitHub Actions + Argo CD 기반 자동화 실습으로 이어질 수 있는 매니페스트 구조를 확보한다.