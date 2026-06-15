# DevOps를 위한 Docker & Kubernetes 실무 — 강의기획서

> Spring Boot + PostgreSQL Todo API를 직접 컨테이너화하고,
> Docker Compose → Kubernetes → GitOps(CI/CD) 순서로 배포 경험을 쌓는 실무 중심 3일 과정.

---

## 목차

1. [과정 개요](#1-과정-개요)
2. [학습 대상 및 선수 지식](#2-학습-대상-및-선수-지식)
3. [환경 요구사항 및 버전 명세](#3-환경-요구사항-및-버전-명세)
4. [3일 커리큘럼 전체 구성표](#4-3일-커리큘럼-전체-구성표)
5. [1일차 상세 기획 — Docker / Docker Compose](#5-1일차-상세-기획--docker--docker-compose)
6. [2일차 상세 기획 — Kubernetes 배포](#6-2일차-상세-기획--kubernetes-배포)
7. [3일차 상세 기획 — GitHub Actions + Helm + Argo CD GitOps](#7-3일차-상세-기획--github-actions--helm--argo-cd-gitops)
8. [실습 앱 소개](#8-실습-앱-소개)
9. [프로젝트 파일 구조](#9-프로젝트-파일-구조)
10. [강의 운영 총괄 원칙](#10-강의-운영-총괄-원칙)
11. [사전 설치 점검 방법](#11-사전-설치-점검-방법)

---

## 1. 과정 개요

| 항목 | 내용 |
|---|---|
| **과정명** | DevOps를 위한 Docker & Kubernetes 실무 |
| **기간** | 3일 (1일 9시간 × 3일) |
| **운영 시간** | 09:00 ~ 18:00 |
| **운영 환경** | Windows + Docker Desktop 로컬 환경 (클라우드 배포 없음) |
| **핵심 철학** | 컨테이너를 "설명"하는 과정이 아니라, 직접 실행하며 운영 관점을 쌓는 과정 |

### 3일 흐름 연결 구조

```
1일차                         2일차                         3일차
─────────────────────         ─────────────────────         ─────────────────────
Dockerfile 작성          →    K8s Deployment/Service   →    GitHub Actions CI
docker build                  ConfigMap / Secret             이미지 태깅
docker compose up             kubectl 진단 명령               Helm 차트 구조 이해
Spring + PostgreSQL 연동       운영 관점 문제 진단              Argo CD GitOps / 롤백
Kubernetes 활성화 준비          → 3일차 배포 소스 확보           빌드-패키징-배포-롤백 한 사이클
```

---

## 2. 학습 대상 및 선수 지식

### 대상 학습자
- 운영/배포 담당 개발자
- DevOps 엔지니어 지망자
- SRE 지망자

### 선수 지식

| 항목 | 필요 수준 |
|---|---|
| Linux 기본 명령 | 파일 조작, 프로세스 확인 수준 |
| Java / Spring Boot | 빌드 경험 있는 수준 (코드 신규 작성 없음) |
| Git | `clone`, `commit`, `push` 사용 가능 |
| YAML | 기본 문법 읽기 가능 |

---

## 3. 환경 요구사항 및 버전 명세

### 필수 설치 도구

| 도구 | 권장 버전 | 용도 | 다운로드 |
|---|---|---|---|
| **Docker Desktop** | 4.30 이상 | 컨테이너 실행 + 로컬 Kubernetes | [docker.com](https://www.docker.com/products/docker-desktop/) |
| **Git** | 2.40 이상 | 소스 관리 / GitOps | [git-scm.com](https://git-scm.com/) |
| **Antigravity IDE** | 최신 | 권장 코드/YAML 편집 환경 | — |
| **VS Code / IntelliJ IDEA** | 최신 | 대체 가능 IDE | — |
| **kubectl** | 1.29 이상 | Kubernetes CLI (Docker Desktop에 포함) | — |

> **Java / Gradle 별도 설치 불필요**
> 제공되는 Dockerfile에 멀티스테이지 빌드가 포함되어 있어, `docker build` 한 번으로 JAR 빌드부터 이미지 생성까지 자동 처리된다.

### Docker Desktop 권장 리소스 (`Settings → Resources`)

| 항목 | 권장 값 | 비고 |
|---|---|---|
| **CPUs** | 4 코어 이상 | Kubernetes 시스템 Pod 포함 |
| **Memory** | 6 GB 이상 | PostgreSQL + Spring + K8s |
| **Disk image size** | 60 GB 이상 | 이미지 레이어 캐시 공간 |

### 컨테이너 이미지 버전 명세

| 이미지 | 버전 태그 | 용도 |
|---|---|---|
| `eclipse-temurin` | `17-jdk-alpine` | Spring Boot 빌드 스테이지 |
| `eclipse-temurin` | `17-jre-alpine` | Spring Boot 실행 스테이지 |
| `postgres` | `16-alpine` | PostgreSQL 데이터베이스 |

### 애플리케이션 스택 버전 명세

| 기술 | 버전 |
|---|---|
| Java | 17 LTS (Eclipse Temurin) |
| Spring Boot | 3.2.5 |
| Spring Data JPA / Hibernate | Spring Boot BOM 관리 (Hibernate 6.x) |
| PostgreSQL JDBC Driver | Spring Boot BOM 관리 |
| Spring Boot Actuator | Spring Boot BOM 관리 |
| Gradle | 8.x (Wrapper 포함) |

### Kubernetes 관련 버전 명세

| 항목 | 버전 |
|---|---|
| Kubernetes | 1.29 이상 (Docker Desktop 내장) |
| kubectl | 클러스터 버전 ±1 (Docker Desktop 포함) |
| Argo CD | 2.10 이상 (3일차 — 로컬 kubectl로 직접 설치) |
| Helm | 3.14 이상 권장 (3일차 — 별도 학습 주제: chart/values/templates) |

### IDE 안내 및 VS Code 사용 시 권장 확장

> 기본 실습 설명은 Antigravity IDE 기준이며, VS Code 또는 IntelliJ IDEA를 사용해도 된다.
> 아래 확장은 VS Code를 사용할 때의 권장 항목이다.

| 확장 | 용도 |
|---|---|
| Docker (`ms-azuretools.vscode-docker`) | Dockerfile, compose.yml |
| Kubernetes (`ms-kubernetes-tools.vscode-kubernetes-tools`) | YAML 매니페스트 |
| YAML (`redhat.vscode-yaml`) | YAML 자동완성·검증 |
| REST Client (`humao.rest-client`) | API 테스트 |

---

### 운영 문서

| 문서 | 역할 |
|---|---|
| [`course-design-detail.md`](./course-design-detail.md) | 전체 강의 설계 기준 문서 |
| [`preflight.md`](./preflight.md) | 강의 전 사전 설치/점검 가이드 |
| [`troubleshooting.md`](./troubleshooting.md) | Docker/Kubernetes/GitOps 문제 해결 가이드 |
| [`instructor-runbook.md`](./instructor-runbook.md) | 강사용 진행표와 운영 체크포인트 |

### 일차별 실습 모듈 바로가기

| 일차 | 가이드 | 모듈 링크 |
|---|---|---|
| 1일차 Docker / Compose | [`day1/README.md`](./day1/README.md) | [`1`](./day1/module1.md) · [`2`](./day1/module2.md) · [`3`](./day1/module3.md) · [`4`](./day1/module4.md) · [`5`](./day1/module5.md) · [`6`](./day1/module6.md) |
| 2일차 Kubernetes | [`day2/README.md`](./day2/README.md) | [`1`](./day2/module1.md) · [`2`](./day2/module2.md) · [`3`](./day2/module3.md) · [`4`](./day2/module4.md) · [`5`](./day2/module5.md) · [`6`](./day2/module6.md) |
| 3일차 GitHub Actions + Helm + Argo CD | [`day3/README.md`](./day3/README.md) | [`1`](./day3/module1.md) · [`2`](./day3/module2.md) · [`3`](./day3/module3.md) · [`4`](./day3/module4.md) · [`5`](./day3/module5.md) · [`6`](./day3/module6.md) |

---

## 4. 3일 커리큘럼 전체 구성표

| 일차 | 시간 | 모듈 | 핵심 주제 | 학습성과(산출물) | 실습/활동 |
|---|---:|---|---|---|---|
| **1일차** | 09:00~10:30 | 모듈 1 | 사전 설치 점검 + Docker Desktop/WSL2 복구 | Docker 실행 가능 로컬 환경 | 설치 확인, CLI 점검, 리소스 설정 |
| | 10:30~11:30 | 모듈 2 | Docker 기본 개념과 컨테이너 실행 | 단일 컨테이너 실행 결과 | 이미지/컨테이너 확인, 포트 매핑 실습 |
| | 11:30~12:30 | 모듈 3 | Spring 애플리케이션 컨테이너화 | Dockerfile, 애플리케이션 이미지 | JAR 빌드, Docker 이미지 생성 |
| | 13:30~15:30 | 모듈 4 | Docker Compose 기반 Spring + PostgreSQL 연동 | compose.yml, 통합 실행 결과 | 앱-DB 연계 실행, 환경변수 분리 |
| | 15:30~16:30 | 모듈 5 | Kubernetes 개요와 로컬 준비 | Docker Desktop Kubernetes 활성화 상태 | kubectl 연결 확인 |
| | 16:30~18:00 | 모듈 6 | 운영 관점 점검과 2일차 연결 | 실습 점검표, 장애 포인트 메모 | 로그 확인, 실패 원인 정리 |
| **2일차** | 09:00~10:00 | 모듈 1 | Kubernetes 기본 구조 이해 | 로컬 클러스터 점검 결과 | node, pod, namespace 조회 |
| | 10:00~11:30 | 모듈 2 | PostgreSQL 쿠버네티스 배포 | DB Deployment, Service, PVC | DB 기동 및 내부 접근 확인 |
| | 11:30~13:00 | 모듈 3 | Spring 애플리케이션 배포 | App Deployment, Service | 애플리케이션 Pod 기동 |
| | 14:00~15:30 | 모듈 4 | ConfigMap, Secret 기반 설정 분리 | 설정 분리형 매니페스트 | DB 접속 정보 주입 실습 |
| | 15:30~17:00 | 모듈 5 | 통합 검증과 서비스 확인 | 앱-DB 연동 결과 | API 호출, 로그 확인, 상태 점검 |
| | 17:00~18:00 | 모듈 6 | 운영 관점 문제 진단 입문 | 장애 원인 분석 메모 | describe, logs, rollout 상태 확인 |
| **3일차** | 09:00~10:00 | 모듈 1 | CI/CD 구조와 GitOps 이해 | CI와 CD 역할 분리 도식 | GitHub Actions와 Argo CD 역할 정리 |
| | 10:00~11:20 | 모듈 2 | GitHub Actions CI 구성 | workflow 파일 | 테스트, 빌드, 이미지 생성 자동화 |
| | 11:20~12:30 | 모듈 3 | 버전관리와 이미지 태그 수동 반영 | 변경된 배포 매니페스트 | GHCR 이미지 태그 확인, manifest 수정, commit/push |
| | 13:30~14:40 | 모듈 4 | Helm 기본과 차트 구조 | Helm chart 초안, values 변경 | Chart.yaml, values.yaml, templates 역할 이해 및 이미지 태그 반영 |
| | 14:40~16:00 | 모듈 5 | Argo CD 설치 및 앱 등록 | Argo CD 애플리케이션 | 로컬 Kubernetes와 Git 저장소 연결, Helm chart source 등록 |
| | 16:00~18:00 | 모듈 6 | Sync, 롤백, 운영 체크리스트 | Sync/OutOfSync 확인, 롤백 절차서 | 잘못된 이미지 태그 장애 재현과 복구 |

---

## 5. 1일차 상세 기획 — Docker / Docker Compose

> **핵심 메시지**: 1일차는 컨테이너를 "설명"하는 날이 아니라, Spring 애플리케이션과 PostgreSQL을 직접 컨테이너화하고 실행해보며 이후 Kubernetes 실습의 기반을 만드는 날이다.

### 학습목표

- Docker Desktop 기반의 로컬 컨테이너 실습 환경을 준비할 수 있다.
- Dockerfile을 이용해 Spring 애플리케이션 이미지를 생성할 수 있다.
- Docker Compose로 Spring 애플리케이션과 PostgreSQL을 함께 실행할 수 있다.
- Docker Desktop의 Kubernetes 기능을 활성화해 2일차 쿠버네티스 배포 실습 준비를 완료할 수 있다.

### 모듈 상세 내용

| 모듈 | 시간 | 내용 |
|---|---|---|
| **모듈 1** 사전 설치 점검 + Docker Desktop/WSL2 복구 | 09:00~10:30 | Docker Desktop, WSL2, Git, PowerShell 상태를 점검하고 실습 가능 환경으로 복구한다. 이 단계는 이후 실습 실패를 줄이기 위한 준비 단계로 설계한다. |
| **모듈 2** Docker 기본 개념과 컨테이너 실행 | 10:30~11:30 | 이미지와 컨테이너의 차이, 실행 환경 분리, 포트 매핑과 볼륨 개념을 짧게 설명한 뒤 바로 단일 컨테이너 실행으로 연결한다. 개념 설명은 최소화하고 명령 실행 결과를 통해 동작 원리를 체감하게 한다. |
| **모듈 3** Spring 애플리케이션 컨테이너화 | 11:30~12:30 | Spring Boot 예제 애플리케이션을 대상으로 JAR 빌드와 Dockerfile 작성, 이미지 빌드와 실행 흐름을 학습한다. 목표는 "애플리케이션이 이미지가 되고 컨테이너로 실행된다"는 흐름을 확실히 잡는 것이다. |
| **모듈 4** Docker Compose 기반 Spring + PostgreSQL 연동 | 13:30~15:30 | Spring 애플리케이션과 PostgreSQL을 각각 컨테이너로 구성하고, Compose를 통해 함께 기동한다. 환경변수와 설정 파일을 분리해 개발 환경과 실행 환경 간 차이를 이해하게 한다. |
| **모듈 5** Kubernetes 개요와 로컬 준비 | 15:30~16:30 | Docker Desktop에서 Kubernetes를 활성화하고 `kubectl` 명령으로 노드와 시스템 Pod를 확인한다. Kubernetes 리소스 실습은 2일차 이후로 넘기되, 1일차에는 로컬 클러스터 준비 상태를 확보하는 데 집중한다. |
| **모듈 6** 운영 관점 점검과 2일차 연결 | 16:30~18:00 | 컨테이너 기동 실패, 포트 충돌, DB 연결 오류, 환경변수 누락 등 초반에 자주 발생하는 오류를 점검한다. 이를 통해 2일차의 쿠버네티스 배포와 운영 오류 대응 실습으로 자연스럽게 연결한다. |

### 실습 시나리오

| 실습 | 목표 | 체크포인트 | 성공 조건 |
|---|---|---|---|
| **실습 1** Docker Desktop 설치 및 동작 확인 | 로컬 PC에서 Docker 명령이 정상 동작하는 환경 준비 | `docker version`, `docker ps` 명령이 정상 실행된다 | 수강생 PC에서 컨테이너 실행 준비가 완료된다 |
| **실습 2** Spring 애플리케이션 이미지 빌드 | 예제 Spring Boot 애플리케이션을 Docker 이미지로 패키징 | `docker build` 성공, 이미지 목록 확인, 컨테이너 실행 성공 | 브라우저 또는 API 호출로 애플리케이션 기동을 확인한다 |
| **실습 3** Docker Compose로 PostgreSQL 연동 | Spring 애플리케이션과 PostgreSQL을 함께 기동하고 연결 검증 | `docker compose up` 성공, DB 컨테이너 기동, 로그에서 DB 연결 확인 | API 호출 시 PostgreSQL 연동 결과가 정상 응답한다 |
| **실습 4** Docker Desktop Kubernetes 활성화 | 로컬 Kubernetes 클러스터를 활성화하고 배포 실습 기반 준비 | Kubernetes 활성화, `kubectl get nodes` 성공, 시스템 Pod 조회 가능 | 2일차에 바로 Deployment 실습으로 연결 가능한 상태가 된다 |

### 강의 운영 포인트

- 이론 비중을 낮추고, 실습 성공 경험을 누적시키는 방향으로 운영한다.
- Spring 코드는 신규 작성보다 템플릿 프로젝트를 제공하는 편이 학습 집중도와 시간 운영 측면에서 유리하다.
- PostgreSQL은 DBA 관점 심화보다 애플리케이션 연동과 설정 분리 경험에 초점을 둔다.
- Kubernetes는 1일차에 "배포"까지 밀어 넣기보다, 활성화와 준비 상태 확인까지만 다루는 편이 전체 커리큘럼 흐름에 안정적이다.

### 1일차 준비물

- Docker Desktop
- Antigravity IDE 권장 (VS Code 또는 IntelliJ IDEA 대체 가능)
- 예제 Spring Boot 프로젝트 (제공)
- kubectl

### 1일차 완료 기준

- [ ] Docker Desktop이 정상 동작한다.
- [ ] Spring 애플리케이션 이미지를 직접 빌드할 수 있다.
- [ ] Docker Compose로 Spring + PostgreSQL 통합 실행이 가능하다.
- [ ] Docker Desktop Kubernetes가 활성화되어 있고 `kubectl`로 상태 확인이 가능하다.

---

## 6. 2일차 상세 기획 — Kubernetes 배포

> **핵심 메시지**: 2일차는 컨테이너 실행에서 끝나지 않고, 쿠버네티스 리소스로 애플리케이션을 배포하고 설정을 분리하며 서비스 상태를 점검하는 날이다.

### 학습목표

- Docker Desktop Kubernetes 환경에서 기본 리소스를 조회하고 배포 흐름을 이해할 수 있다.
- Spring 애플리케이션과 PostgreSQL을 Deployment와 Service로 배포할 수 있다.
- ConfigMap과 Secret을 이용해 설정값과 민감정보를 분리할 수 있다.
- 배포 후 Pod, Service, 로그 상태를 점검하고 기본적인 문제를 진단할 수 있다.

### 모듈 상세 내용

| 모듈 | 시간 | 내용 |
|---|---|---|
| **모듈 1** Kubernetes 기본 구조 이해 | 09:00~10:00 | Docker Desktop Kubernetes 환경에서 node, namespace, pod, service 개념을 정리하고 실제 리소스 조회 명령으로 연결한다. 이론 나열보다 "현재 클러스터에 무엇이 떠 있는가"를 확인하는 관찰 중심으로 운영한다. |
| **모듈 2** PostgreSQL 쿠버네티스 배포 | 10:00~11:30 | PostgreSQL을 Deployment와 Service로 구성하고, PersistentVolumeClaim을 통해 데이터 저장 구조를 함께 설명한다. 상태 저장 워크로드를 깊게 다루기보다, 쿠버네티스에서 DB가 어떻게 배치되는지 이해시키는 수준으로 설계한다. |
| **모듈 3** Spring 애플리케이션 배포 | 11:30~13:00 | 1일차에 만든 이미지를 기반으로 Spring Boot 애플리케이션을 Deployment로 배포하고 Service로 노출한다. 이미지, 포트, 환경변수, replica 개념을 함께 다룬다. |
| **모듈 4** ConfigMap, Secret 기반 설정 분리 | 14:00~15:30 | 애플리케이션 설정과 민감정보를 이미지에 넣지 않고 외부화하는 방법을 학습한다. 환경변수와 설정 파일을 분리하여 개발 환경과 검증 환경 간 차이를 조정하는 방향을 실제 매니페스트로 구현한다. |
| **모듈 5** 통합 검증과 서비스 확인 | 15:30~17:00 | `kubectl get`, `describe`, `logs`, 포트 포워딩을 통해 애플리케이션과 PostgreSQL의 연결 상태를 검증한다. "배포 성공 = Pod Running"이 아니라 "서비스 응답까지 확인"이라는 운영 관점을 강조한다. |
| **모듈 6** 운영 관점 문제 진단 입문 | 17:00~18:00 | 이미지 태그 오류, 환경변수 누락, 서비스 셀렉터 불일치 등 자주 발생하는 문제를 간단히 재현한다. 3일차의 배포 오류 대응, 롤백, 운영 체크리스트 실습으로 자연스럽게 넘어가도록 설계한다. |

### 실습 시나리오

| 실습 | 목표 | 체크포인트 | 성공 조건 |
|---|---|---|---|
| **실습 1** 로컬 클러스터 상태 점검 | Docker Desktop Kubernetes 기본 상태 확인, 실습 namespace 준비 | `kubectl get nodes`, `kubectl get pods -A`, `kubectl create namespace` 정상 수행 | 실습용 namespace 준비, 기본 리소스 조회 가능 |
| **실습 2** PostgreSQL 배포 | PostgreSQL을 K8s 리소스로 배포하고 내부 서비스로 연결 | Deployment 생성, Service 생성, Pod Running, 내부 연결 확인 | PostgreSQL Pod 정상 기동, 애플리케이션에서 접근 가능 |
| **실습 3** Spring 애플리케이션 배포 | Spring Boot 앱을 Deployment와 Service로 배포 | 이미지 지정, replica 설정, Service 노출, Pod 로그 확인 | 애플리케이션 Pod 정상 기동, 서비스 접근 가능 |
| **실습 4** ConfigMap과 Secret 적용 | DB 호스트, 포트, 계정, 비밀번호를 외부 설정으로 분리 | ConfigMap 생성, Secret 생성, 환경변수 주입 성공 | 설정 분리 후에도 애플리케이션이 정상 기동된다 |
| **실습 5** 서비스 통합 검증 | Spring 애플리케이션이 PostgreSQL과 정상 연결되는지 확인 | API 호출 성공, 애플리케이션 로그 확인, DB 연결 확인 | 수강생이 배포 후 점검 절차를 순서대로 수행할 수 있다 |

### 강의 운영 포인트

- 초점은 "쿠버네티스 명령 암기"가 아니라 "배포 단위가 어떻게 달라지는지 이해"에 둔다.
- PostgreSQL까지 쿠버네티스에 올리더라도 운영 심화보다 애플리케이션 연동과 서비스 확인에 집중한다.
- `kubectl apply`만 반복하지 말고 `get`, `describe`, `logs`를 반드시 함께 사용하게 해야 운영 관점이 형성된다.
- 매니페스트는 처음부터 길게 주기보다 Deployment → Service → ConfigMap → Secret을 단계적으로 추가하는 방식이 학습 부담을 줄인다.

### 2일차 준비물

- Docker Desktop Kubernetes 활성화 완료 환경
- 1일차에 사용한 Spring Boot 이미지 또는 예제 프로젝트
- PostgreSQL용 기본 매니페스트 (제공)
- Antigravity IDE 권장 (VS Code 또는 IntelliJ IDEA 대체 가능)
- kubectl
- 실습용 namespace 및 YAML 파일 템플릿 (제공)

### 2일차 완료 기준

- [ ] 로컬 Kubernetes 환경에서 Spring 애플리케이션과 PostgreSQL을 각각 배포할 수 있다.
- [ ] Deployment, Service, ConfigMap, Secret의 역할을 구분 설명할 수 있다.
- [ ] 배포 후 상태 확인 명령으로 기본적인 문제를 진단할 수 있다.
- [ ] 3일차의 GitHub Actions + Argo CD 기반 자동화 실습으로 이어질 수 있는 매니페스트 구조를 확보한다.

---

## 7. 3일차 상세 기획 — GitHub Actions + Helm + Argo CD GitOps

> **핵심 메시지**: 3일차는 코드 변경이 빌드되고, 이미지가 만들어지고, Git 상태를 기준으로 Kubernetes에 반영되는 전체 흐름을 경험하는 날이다.

### 학습목표

- GitHub Actions로 빌드와 테스트를 자동화할 수 있다.
- Docker 이미지 버전을 확인하고, 배포 매니페스트 또는 Helm values에 수동으로 반영하는 GitOps 기본 흐름을 수행할 수 있다.
- Helm chart의 기본 구성(`Chart.yaml`, `values.yaml`, `templates/`)과 values 기반 배포 설정 변경 방식을 설명할 수 있다.
- Argo CD로 로컬 Kubernetes 클러스터에 선언형 배포를 적용할 수 있다.
- 배포 상태를 확인하고 롤백 또는 재동기화로 복구할 수 있다.

### GitOps 전체 흐름

> 3일차 Argo CD 실습은 2일차에서 만든 `todo-app` 네임스페이스와 `Service`, `ConfigMap`, `Secret`, PostgreSQL 리소스가 클러스터에 남아 있다는 전제로 진행한다.
> 3일차는 `day3/k8s`의 애플리케이션 Deployment로 GitOps 흐름을 먼저 잡은 뒤, Helm을 별도 학습 주제로 다룬다.
> Helm 파트에서는 같은 앱 배포를 `Chart.yaml`, `values.yaml`, `templates/` 구조로 재구성하고, 이미지 태그 변경 지점을 values로 분리하는 방식을 학습한다.
> 2일차 기반 리소스(`todo-app` Service, `postgres` Service/Deployment/PVC, `app-config`, `db-secret`)는 다시 생성하지 않는다.

```
코드 push (GitHub main 브랜치)
        │
        ▼
GitHub Actions (CI)
  ├─ 1. Gradle 빌드 & 테스트
  ├─ 2. Docker 이미지 빌드
  └─ 3. GHCR push (태그: 7자리 short SHA / latest)
                │
                ▼
  수강생이 GHCR 이미지 태그 확인
                │
                ▼
  day3/k8s/app-deployment.yml 이미지 태그 수동 변경
                │
                ▼
  Helm chart/values로 같은 변경 지점 구조화
                │
                ▼
  Git commit & push
                │
                ▼
  Argo CD OutOfSync 감지
                │
                ▼
  Argo CD Sync → 클러스터 반영
                │
                ▼
  kubectl rollout status 확인
```

### 모듈 상세 내용

| 모듈 | 시간 | 내용 |
|---|---|---|
| **모듈 1** CI/CD 구조와 GitOps 이해 | 09:00~10:00 | 개발·검증·운영 환경 간 변경 관리 흐름과 GitOps의 핵심 개념을 도식으로 정리한다. |
| **모듈 2** GitHub Actions CI 구성 | 10:00~11:20 | Spring Boot 코드가 push되었을 때 GitHub Actions가 빌드·테스트를 수행하고 GHCR에 이미지를 push하는 workflow를 작성한다. |
| **모듈 3** 버전관리와 이미지 태깅 | 11:20~12:30 | GHCR 이미지 태그를 확인하고 `day3/k8s/app-deployment.yml`에 반영해 Git 기반 배포 이력을 만든다. |
| **모듈 4** Helm 기본과 차트 구조 | 13:30~14:40 | Helm을 별도 학습 주제로 다룬다. `Chart.yaml`, `values.yaml`, `templates/` 구조를 만들고 이미지 태그를 values로 분리한다. |
| **모듈 5** Argo CD 설치 및 앱 등록 | 14:40~16:00 | 로컬 Kubernetes에 Argo CD를 설치하고, plain manifest 경로와 Helm chart 경로를 Application source로 등록하는 차이를 학습한다. |
| **모듈 6** Sync, 롤백, 운영 체크리스트 | 16:00~18:00 | 수동 Sync, OutOfSync/Healthy 확인, 잘못된 이미지 태그 장애 재현, Git revert 또는 Argo CD rollback 복구를 실습한다. |

### 실습 시나리오

| 실습 | 목표 | 체크포인트 | 성공 조건 |
|---|---|---|---|
| **실습 1** GitHub Actions CI 파이프라인 구성 | 코드 변경 시 테스트와 빌드가 자동 실행되도록 workflow 작성 | push 또는 PR 발생 시 job이 실행되고, 테스트와 빌드가 성공 | Docker 이미지가 생성되며 레지스트리(GHCR) 반영 완료 |
| **실습 2** 이미지 태그 수동 반영 | GHCR 이미지 태그를 확인하고 기본 매니페스트에 직접 반영 | 이미지 태그 확인, `day3/k8s/app-deployment.yml` 수정, commit/push | 어떤 이미지 태그가 어떤 Git commit으로 배포됐는지 역추적할 수 있다 |
| **실습 3** Helm 차트 작성 | 앱 Deployment를 Helm chart로 패키징하고 values로 변경점을 분리 | `Chart.yaml`, `values.yaml`, `templates/deployment.yaml`, `helm template` 결과 확인 | raw manifest와 Helm values 방식의 차이를 설명할 수 있다 |
| **실습 4** Argo CD 설치와 애플리케이션 등록 | 로컬 Kubernetes에 Argo CD를 설치하고 plain manifest/Helm chart 앱 등록 | Argo CD UI/CLI 접근, 앱 등록, Sync 상태 확인 | Git 저장소의 manifest 또는 Helm chart가 클러스터 대상 앱으로 연결된다 |
| **실습 5** 롤백 및 복구 | 잘못된 버전이나 설정을 되돌리고 서비스 정상 상태 복구 | 이전 revision 선택, rollback 또는 재동기화 수행, 정상 상태 확인 | 이전 정상 버전으로 서비스가 복구된다 |

### Argo CD 설치 절차 (모듈 5 실습)

```powershell
# 1. argocd 네임스페이스 생성
kubectl create namespace argocd

# 2. Argo CD 설치 (stable 버전)
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 3. Pod 기동 완료 대기 (전체 Running까지 수 분 소요)
kubectl get pods -n argocd -w

# 4. UI 접근을 위한 포트 포워딩 (별도 터미널)
kubectl port-forward svc/argocd-server -n argocd 8443:443

# 5. 초기 admin 비밀번호 확인
kubectl get secret argocd-initial-admin-secret -n argocd `
  -o jsonpath="{.data.password}" | `
  [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))

# 브라우저에서 https://localhost:8443 접속 (ID: admin)
```

### Argo CD 앱 등록 (모듈 5 실습)

```powershell
# CLI로 등록
argocd login localhost:8443 --insecure

argocd app create todo-app `
  --repo https://github.com/[계정명]/docker_k8s_tutorial.git `
  --path day3/k8s/helm `
  --dest-server https://kubernetes.default.svc `
  --dest-namespace todo-app
```

> Argo CD가 `day3/k8s/helm` 경로에서 `Chart.yaml`을 감지하면 자동으로 Helm source로 인식한다.
> 이미지 태그 변경은 `values.yaml`의 `image.tag`에서 수행한다.

### Argo CD 동기화 상태 설명

| 상태 | 의미 |
|---|---|
| `Synced` | Git 상태 = 클러스터 상태 (정상) |
| `OutOfSync` | Git이 변경됐지만 클러스터에 미반영 |
| `Healthy` | Pod가 정상 동작 중 |
| `Degraded` | Pod 일부 또는 전체 비정상 |
| `Progressing` | 배포 진행 중 |

### 강의 운영 포인트

- 도구 소개보다 **빌드-패키징-배포-롤백이 한 흐름으로 이어진다**는 경험을 주는 데 집중한다.
- GitHub Actions는 CI, Helm은 배포 패키징, Argo CD는 CD/GitOps로 역할을 명확히 분리하면 수강생 이해도가 높다.
- 자동 동기화만 강조하면 롤백 개념이 흐려질 수 있으므로, 처음에는 수동 Sync와 롤백을 보여준 뒤 자동화로 넘어가는 편이 좋다.
- 로컬 Kubernetes 환경이므로 외부 클라우드 연결 이슈 없이 GitOps 개념 자체를 학습하는 데 집중한다.

### 3일차 준비물

- Docker Desktop Kubernetes 활성화 완료 환경
- GitHub 저장소 (앱 소스 + 배포 매니페스트)
- GitHub Actions Workflow 파일 (제공)
- 수강생 개인 GitHub fork 저장소
- Argo CD 설치 YAML 또는 manifest
- kubectl, argocd CLI (선택)
- 1일차/2일차에서 사용한 Spring Boot + PostgreSQL 예제

### 3일차 완료 기준

- [ ] GitHub Actions로 CI가 자동 실행된다.
- [ ] Argo CD가 Git 저장소를 기준으로 로컬 Kubernetes에 동기화된다.
- [ ] 버전 태그와 배포 이력이 연결되어 추적 가능하다.
- [ ] 장애 또는 잘못된 배포 상황에서 롤백 절차를 수행할 수 있다.

---

## 8. 실습 앱 소개

### Todo CRUD REST API

> Spring Boot 3.2 + PostgreSQL 16 기반의 Todo 목록 API.
> 동일한 이미지에서 환경변수만 교체해 로컬·Compose·Kubernetes·GitOps 환경 모두 동작한다.

#### API 엔드포인트

| 메서드 | 경로 | 설명 |
|---|---|---|
| `GET` | `/todos` | 전체 목록 조회 |
| `GET` | `/todos/{id}` | 단건 조회 |
| `POST` | `/todos` | 항목 추가 |
| `PATCH` | `/todos/{id}/done` | 완료 여부 변경 |
| `DELETE` | `/todos/{id}` | 삭제 |
| `GET` | `/actuator/health` | 헬스체크 (K8s probe용) |

#### DB 연결 환경변수

| 환경변수 | 기본값 | 설명 |
|---|---|---|
| `DB_HOST` | `localhost` | DB 호스트 |
| `DB_PORT` | `5432` | DB 포트 |
| `DB_NAME` | `tododb` | DB 이름 |
| `DB_USER` | `todo` | DB 계정 |
| `DB_PASSWORD` | `todo1234` | DB 비밀번호 |

---

## 9. 프로젝트 파일 구조

```
docker_k8s_tutorial/
│
├── README.md                          ← 이 강의기획서
│
├── spring-app/                        ← 공통 실습 앱 소스
│   ├── Dockerfile                     ← 멀티스테이지 빌드 (Java 불필요)
│   ├── build.gradle
│   ├── settings.gradle
│   └── src/main/java/com/tutorial/app/
│       ├── TodoApplication.java
│       ├── controller/TodoController.java
│       ├── service/TodoService.java
│       └── domain/
│           ├── Todo.java
│           └── TodoRepository.java
│
├── day1/                              ← 1일차 실습 파일
│   ├── README.md                      ← 실습 가이드 (단계별 명령 포함)
│   ├── compose.yml                    ← Spring + PostgreSQL Compose
│   └── .env                           ← 환경변수 분리 예제
│
├── day2/                              ← 2일차 실습 파일
│   ├── README.md                      ← 실습 가이드 (kubectl 명령 포함)
│   └── k8s/
│       ├── namespace.yml
│       ├── postgres-pvc.yml
│       ├── postgres-deployment.yml
│       ├── postgres-service.yml
│       ├── app-configmap.yml          ← DB 호스트/포트/이름
│       ├── app-secret.yml             ← DB 계정/비밀번호 (base64)
│       ├── app-deployment.yml
│       └── app-service.yml            ← NodePort (localhost:30080)
│
├── day3/                              ← 3일차 실습 파일
│   ├── README.md                      ← 실습 가이드 (GitOps + Helm 흐름 포함)
│   └── k8s/
│       ├── app-deployment.yml         ← raw manifest (이미지 태그 수동 변경 대상)
│       └── helm/
│           ├── Chart.yaml             ← Helm chart 메타데이터
│           ├── values.yaml            ← 이미지 태그/replicas/resources 변경 지점
│           └── templates/
│               └── deployment.yaml    ← Deployment 템플릿
│
│   ※ day3/k8s/helm 은 app Deployment만 포함한다. Service/ConfigMap/Secret/PostgreSQL은 day2 실습 결과를 전제로 한다.
│
└── .github/
    └── workflows/
        └── ci.yml                     ← GitHub Actions CI (GHCR push)
```

---

## 10. 강의 운영 총괄 원칙

| 원칙 | 내용 |
|---|---|
| **이론 최소화** | 개념 설명은 실습 직전 5분 이내 요약, 명령 결과로 원리를 체감한다 |
| **실습 성공 경험 누적** | 모듈마다 확인 가능한 산출물(명령 출력, API 응답)을 만든다 |
| **오류 대응 내재화** | 자주 발생하는 오류는 강사가 먼저 재현해 진단 과정을 보여준다 |
| **코드 작성 없음** | Spring 코드 신규 작성 없이 제공된 템플릿 프로젝트 활용 |
| **환경 독립성** | 클라우드 없이 로컬 Docker Desktop만 사용, 네트워크 의존성 최소화 |
| **일차 간 연결** | 매 일차 끝에서 다음 일차 시작으로 자연스럽게 이어진다 |

---

## 11. 사전 설치 점검 방법

> 수강 전 아래 명령을 모두 실행해 오류가 없는지 확인한다.

```powershell
# Docker 버전 확인
docker version

# Compose 플러그인 확인
docker compose version

# Docker 정상 동작 확인 (hello-world 컨테이너 실행)
docker run --rm hello-world

# Git 버전 확인
git --version

# Kubernetes (Docker Desktop에서 활성화 후)
kubectl version --client
kubectl get nodes
```

### 정상 출력 예시

```
Client: Docker Engine - Community
 Version: 26.x.x

Docker Compose version v2.x.x

git version 2.4x.x

kubectl version: v1.29.x

NAME             STATUS   ROLES           AGE
docker-desktop   Ready    control-plane   Xd
```
