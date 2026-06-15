# DevOps를 위한 Docker & Kubernetes 실무 - 1일차 기획안

## 과정 개요
- **과정명**: DevOps를 위한 Docker & Kubernetes 실무 - 1일차
- **운영 방향**: 클라우드 배포는 제외하고 Docker Desktop 기반 로컬 실습 환경에서 Docker, Docker Compose, Kubernetes 준비까지 연결한다.
- **핵심 메시지**: 1일차는 컨테이너를 “설명”하는 날이 아니라, Spring 애플리케이션과 PostgreSQL을 직접 컨테이너화하고 실행해보며 이후 Kubernetes 실습의 기반을 만드는 날이다.
- **대상 학습자**: 운영/배포 담당 개발자, DevOps 엔지니어, SRE 지망자.

## 학습목표
- Docker Desktop 기반의 로컬 컨테이너 실습 환경을 준비할 수 있다.
- Dockerfile을 이용해 Spring 애플리케이션 이미지를 생성할 수 있다.
- Docker Compose로 Spring 애플리케이션과 PostgreSQL을 함께 실행할 수 있다.
- Docker Desktop의 Kubernetes 기능을 활성화해 다음 차시의 쿠버네티스 배포 실습 준비를 완료할 수 있다.

## 내용 구성
| 모듈 | 핵심 주제 | 학습성과(산출물) | 실습/활동 |
|---|---|---|---|
| 모듈 1 | Docker Desktop 실습 환경 준비 | Docker 실행 가능 로컬 환경 | 설치 확인, CLI 점검, 리소스 설정 |
| 모듈 2 | Docker 기본 개념과 컨테이너 실행 | 단일 컨테이너 실행 결과 | 이미지/컨테이너 확인, 포트 매핑 실습 |
| 모듈 3 | Spring 애플리케이션 컨테이너화 | Dockerfile, 애플리케이션 이미지 | JAR 빌드, Docker 이미지 생성 |
| 모듈 4 | Docker Compose 기반 Spring + PostgreSQL 연동 | compose.yml, 통합 실행 결과 | 앱-DB 연계 실행, 환경변수 분리 |
| 모듈 5 | Kubernetes 개요와 로컬 준비 | Kubernetes 활성화, k9s 권장 설치 | kubectl 연결 확인, k9s 또는 kubectl로 상태 확인 |
| 모듈 6 | 운영 관점 점검과 2일차 연결 | 실습 점검표, 장애 포인트 메모 | 로그 확인, 실패 원인 정리 |

## 상세 내용
### 모듈 1. Docker Desktop 실습 환경 준비
Docker Desktop 설치, WSL2 활성화 상태, Docker Engine 상태 확인, Compose 사용 가능 여부, 실습에 필요한 CPU/메모리 설정을 점검한다. Docker Desktop on Windows는 WSL2 백엔드를 기반으로 동작하며, WSL2가 정상적으로 활성화되어 있지 않으면 Docker가 실행되지 않으므로 사전 점검이 반드시 필요하다. 이 단계는 이후 실습 실패를 줄이기 위한 준비 단계로 설계한다.

### 모듈 2. Docker 기본 개념과 컨테이너 실행
이미지와 컨테이너의 차이, 실행 환경 분리, 포트 매핑과 볼륨 개념을 짧게 설명한 뒤 바로 단일 컨테이너 실행으로 연결한다. 개념 설명은 최소화하고 명령 실행 결과를 통해 동작 원리를 체감하게 한다.

### 모듈 3. Spring 애플리케이션 컨테이너화
Spring Boot 예제 애플리케이션을 대상으로 JAR 빌드와 Dockerfile 작성, 이미지 빌드와 실행 흐름을 학습한다. 이 모듈의 목표는 “애플리케이션이 이미지가 되고 컨테이너로 실행된다”는 흐름을 확실히 잡는 것이다.

### 모듈 4. Docker Compose 기반 Spring + PostgreSQL 연동
Spring 애플리케이션과 PostgreSQL을 각각 컨테이너로 구성하고, Compose를 통해 함께 기동한다. 환경변수와 설정 파일을 분리해 개발 환경과 실행 환경 간 차이를 이해하게 한다.

### 모듈 5. Kubernetes 개요와 로컬 준비
Docker Desktop에서 Kubernetes를 활성화하고 `kubectl` 명령으로 노드와 시스템 Pod를 확인한다. 터미널 UI 기반 Kubernetes 모니터링 도구인 k9s는 권장 도구로 소개하되, 설치가 어렵다면 kubectl 명령으로 대체한다. Kubernetes 리소스 실습은 2일차 이후로 넘기되, 1일차에는 로컬 클러스터 준비 상태를 확보하는 데 집중한다.

### 모듈 6. 운영 관점 점검과 2일차 연결
컨테이너 기동 실패, 포트 충돌, DB 연결 오류, 환경변수 누락 등 초반에 자주 발생하는 오류를 점검한다. 이를 통해 다음 차시의 쿠버네티스 배포와 운영 오류 대응 실습으로 자연스럽게 연결한다.

## 실습 시나리오
### 실습 1. Docker Desktop 설치 및 동작 확인
- 목표: 로컬 PC에서 Docker 명령이 정상 동작하는 환경을 준비한다.
- 체크포인트: WSL2 활성화 확인, `docker version`, `docker ps` 명령이 정상 실행된다.
- 성공 조건: 수강생 PC에서 컨테이너 실행 준비가 완료된다.

### 실습 2. Spring 애플리케이션 이미지 빌드
- 목표: 예제 Spring Boot 애플리케이션을 Docker 이미지로 패키징한다.
- 체크포인트: `docker build` 성공, 이미지 목록 확인, 컨테이너 실행 성공.
- 성공 조건: 브라우저 또는 API 호출로 애플리케이션 기동을 확인한다.

### 실습 3. Docker Compose로 PostgreSQL 연동
- 목표: Spring 애플리케이션과 PostgreSQL을 함께 기동하고 연결을 검증한다.
- 체크포인트: `docker compose up` 성공, DB 컨테이너 기동, 애플리케이션 로그에서 DB 연결 확인.
- 성공 조건: API 호출 시 PostgreSQL 연동 결과가 정상 응답한다.

### 실습 4. Docker Desktop Kubernetes 활성화 + k9s 권장 설치
- 목표: 로컬 Kubernetes 클러스터를 활성화하고 이후 배포 실습을 위한 기반을 준비한다.
- 체크포인트: Kubernetes 활성화, `kubectl get nodes` 성공, 시스템 Pod 조회 가능, k9s 또는 kubectl로 클러스터 상태 확인.
- 성공 조건: 2일차에 바로 Deployment 실습으로 연결 가능한 상태가 된다.

## 강의 운영 포인트
- 1일차는 이론 비중을 낮추고, 실습 성공 경험을 누적시키는 방향으로 운영한다.
- Spring 코드는 신규 작성보다 템플릿 프로젝트를 제공하는 편이 학습 집중도와 시간 운영 측면에서 유리하다.
- PostgreSQL은 DBA 관점 심화보다 애플리케이션 연동과 설정 분리 경험에 초점을 둔다.
- Kubernetes는 1일차에 “배포”까지 밀어 넣기보다, 활성화와 준비 상태 확인까지만 다루는 편이 전체 3일 커리큘럼 흐름에 안정적이다.

## 준비물
- Docker Desktop
- Antigravity IDE 권장 (VS Code 또는 IntelliJ IDEA 대체 가능)
- 예제 Spring Boot 프로젝트
- PostgreSQL 접속 확인용 API 또는 샘플 SQL
- kubectl
- k9s 권장 (설치가 어렵다면 kubectl로 대체)

## 1일차 완료 기준
- Docker Desktop이 정상 동작한다.
- Spring 애플리케이션 이미지를 직접 빌드할 수 있다.
- Docker Compose로 Spring + PostgreSQL 통합 실행이 가능하다.
- Docker Desktop Kubernetes가 활성화되어 있고 `kubectl`로 상태 확인이 가능하다.
- k9s 또는 kubectl 명령으로 클러스터 상태를 확인할 수 있다.
