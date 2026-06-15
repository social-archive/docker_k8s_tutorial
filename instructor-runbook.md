# Instructor Runbook — Docker & Kubernetes 3일 강사용 운영안

> 강사용 진행표와 운영 체크포인트.
> 수강생 문서가 아니라 강사가 시간, 데모, 장애 대응을 관리하기 위한 문서다.

---

## 1. 전체 운영 원칙

- 개념 설명보다 실습 결과 확인을 우선한다.
- 모든 명령은 Windows PowerShell 기준으로 안내한다.
- Antigravity IDE를 권장하되, VS Code/IntelliJ도 허용한다.
- k9s는 권장 도구로 소개하고, 안 되면 kubectl로 대체한다.
- Kubernetes namespace는 `todo-app`으로 통일한다.
- 3일차 기본 GitOps 실습은 수동 이미지 태그 변경이다.
- 자동 manifest 갱신은 강사 데모 또는 심화자료로만 다룬다.

---

## 2. 1일차 운영안 — Docker / Docker Compose

### 핵심 목표

- Docker Desktop/WSL2 환경을 안정화한다.
- Spring Boot 앱을 Docker 이미지로 빌드한다.
- Docker Compose로 Spring + PostgreSQL을 실행한다.

### 시간표

| 시간 | 내용 | 강사 체크포인트 |
|---|---|---|
| 09:00~10:30 | 사전 설치 점검 + Docker Desktop/WSL2 복구 | `docker ps`, `docker compose version` |
| 10:30~11:30 | Docker 기본 개념과 컨테이너 실행 | 컨테이너 실행/중지/로그 확인 |
| 11:30~12:30 | Spring 앱 컨테이너화 | 이미지 빌드 성공 |
| 12:30~13:30 | 점심 |  |
| 13:30~15:30 | Docker Compose 연동 | 앱 + DB 정상 응답 |
| 15:30~16:30 | Kubernetes 로컬 준비 | `kubectl get nodes` |
| 16:30~18:00 | 운영 관점 점검 | 로그/포트/DB 연결 오류 정리 |

### 1일차 반드시 확보할 것

- `docker build` 성공
- `docker compose up` 성공
- `/actuator/health` 또는 `/todos` 응답

### 시간이 부족할 때 생략 가능

- k9s 상세 사용법
- Kubernetes 리소스 심화 설명

### 1일차 fallback path

| 상황 | 대응 | 유지할 핵심 산출물 |
|---|---|---|
| Docker Desktop/WSL2 복구가 10:30을 넘김 | 공통 강사 PC 또는 사전 준비 VM으로 데모를 먼저 진행하고, 문제 PC는 점심/휴식 시간에 개별 복구 | Docker 명령 흐름 이해, 최소 1회 컨테이너 실행 |
| Compose 연동이 지연됨 | 강사가 정상 `compose.yml`과 예상 로그를 보여주고, 수강생은 API 응답 확인까지 따라온다 | Spring + PostgreSQL 연결 성공 신호 |
| Kubernetes 활성화가 늦음 | 1일차에는 `kubectl` 개념 설명과 강사 데모로 마무리하고, 2일차 09:00에 클러스터 readiness를 재확인 | 2일차 시작 전 `kubectl get nodes` 가능 상태 |

---

## 3. 2일차 운영안 — Kubernetes 배포

### 핵심 목표

- `todo-app` namespace 기준으로 리소스를 배포한다.
- PostgreSQL과 Spring 앱을 Kubernetes에서 연결한다.
- 로그, describe, rollout 상태 확인으로 기본 진단을 수행한다.

### 시간표

| 시간 | 내용 | 강사 체크포인트 |
|---|---|---|
| 09:00~10:00 | Kubernetes 기본 구조 | node, namespace, pod 확인 |
| 10:00~11:30 | PostgreSQL 배포 | PVC Bound, DB Pod Running |
| 11:30~13:00 | Spring 앱 배포 | App Pod Running |
| 13:00~14:00 | 점심 |  |
| 14:00~15:30 | ConfigMap/Secret 기반 설정 분리 | 설정 주입 확인 |
| 15:30~17:00 | 통합 검증 | API 응답 |
| 17:00~18:00 | 운영 관점 문제 진단 | logs/describe/rollout 확인 |

### 2일차 반드시 확보할 것

- namespace: `todo-app`
- PostgreSQL Deployment/Service/PVC 정상
- Spring App Deployment/Service 정상
- `http://localhost:30080/todos` 응답

### 강사 데모 후보

- ConfigMap 누락
- Secret 오류
- Service selector 불일치

### 2일차 fallback path

| 상황 | 대응 | 유지할 핵심 산출물 |
|---|---|---|
| PVC/DB Pod가 늦게 뜸 | DB 내부 접속 심화는 생략하고 `kubectl describe`, 이벤트 확인 중심으로 전환 | PostgreSQL Deployment/Service/PVC 구조 이해 |
| App Pod가 설정 누락으로 기동 실패 | `app-config`, `db-secret` 존재 여부를 먼저 확인하고, 필요하면 강사 제공 정상 YAML을 적용 | 설정 주입 원인-결과 확인 |
| 통합 API 검증이 지연됨 | module5의 API 호출은 강사 데모로 보강하고, 수강생은 `get/describe/logs` 점검표를 완성 | 운영 점검 루틴 확보 |
| 문제 진단 시간이 부족함 | 이미지 태그 오류 1개만 전원 실습하고 selector/ConfigMap 오류는 데모로 전환 | 3일차 롤백 실습으로 연결되는 장애 인식 |

---

## 4. 3일차 운영안 — GitHub Actions + Argo CD GitOps

### 핵심 목표

- 수강생이 본인 fork 저장소로 실습한다.
- GitHub Actions로 이미지를 GHCR에 push한다.
- 이미지 태그를 수동으로 매니페스트에 반영한다.
- Argo CD 수동 Sync로 클러스터에 배포한다.
- 잘못된 이미지 태그 장애를 재현하고 복구한다.

### 시간표

| 시간 | 내용 | 강사 체크포인트 |
|---|---|---|
| 09:00~10:00 | CI/CD와 GitOps 구조 | CI와 CD 역할 분리 이해 |
| 10:00~11:30 | GitHub Actions CI | Actions 성공, GHCR 이미지 확인 |
| 11:30~12:30 | 이미지 태그 수동 반영 | app-deployment.yml 수정 commit/push |
| 12:30~13:30 | 점심 |  |
| 13:30~15:00 | Argo CD 설치/앱 등록 | 앱 등록, OutOfSync 확인 |
| 15:00~16:30 | 수동 Sync | Pod/API 정상 확인 |
| 16:30~18:00 | 롤백 실습 | ImagePullBackOff → 복구 완료 |

### 3일차 반드시 확보할 것

- GitHub Actions 성공
- GHCR 이미지 태그 확인
- `day3/k8s/app-deployment.yml` 수동 변경
- Argo CD OutOfSync 확인
- 수동 Sync 성공
- 잘못된 이미지 태그 장애 재현
- Git revert 또는 정상 태그 복구

### 자동 manifest 갱신 데모 위치

- 기본 실습에는 포함하지 않는다.
- 시간이 남거나 심화 설명이 필요할 때만 강사 데모로 보여준다.

### 3일차 fallback path

| 상황 | 대응 | 유지할 핵심 산출물 |
|---|---|---|
| GHCR 권한 또는 package visibility 문제 | Actions 성공 로그와 이미지 태그 산출 방식을 먼저 확인하고, 로컬 pull은 선택으로 돌린다 | 7자리 short SHA 태그 확인 |
| Argo CD 설치가 지연됨 | 강사 데모 클러스터로 App 등록/OutOfSync/Sync 흐름을 먼저 보여주고, 개별 설치는 보조 진행 | Git commit → Argo CD Sync 흐름 이해 |
| Sync 후 앱이 기동하지 않음 | 2일차 기반 리소스(`Service`, `ConfigMap`, `Secret`, PostgreSQL)를 먼저 확인한다 | day3가 app Deployment만 관리한다는 전제 확인 |
| 롤백 시간이 부족함 | 발표/공유를 줄이고 `git revert` 또는 정상 태그 복구까지는 반드시 완료한다 | 장애 감지 후 Git 기준 복구 경험 |

---

## 5. 등급형 완료 기준

| 등급 | 기준 |
|---|---|
| Basic | Docker 이미지 빌드 + Compose 실행 성공 |
| Standard | Kubernetes 배포 + API 응답 성공 |
| Advanced | GitHub Actions + Argo CD + 롤백 성공 |

---

## 6. 진행 중 체크 질문

### 1일차

- 컨테이너와 이미지의 차이를 말할 수 있는가?
- 앱과 DB가 Compose 네트워크에서 어떻게 연결되는가?

### 2일차

- Deployment와 Service의 역할을 구분할 수 있는가?
- ConfigMap과 Secret을 왜 분리하는지 설명할 수 있는가?

### 3일차

- CI와 CD의 역할을 구분할 수 있는가?
- GitOps에서 왜 Git commit이 배포 기준인지 설명할 수 있는가?
- 장애 복구가 클러스터 직접 수정이 아니라 Git 변경으로 이뤄진다는 점을 이해했는가?

---

## 7. 강사 운영 리스크

| 리스크 | 대응 |
|---|---|
| Docker Desktop/WSL2 지연 | 1일차 09:00~10:30 복구 시간 사용 |
| k9s 설치 실패 | kubectl 명령으로 대체 |
| GHCR 권한 문제 | Actions 로그와 package 권한 확인 |
| Argo CD 설치 지연 | 강사 데모 환경으로 우선 설명 후 개별 복구 |
| 롤백 시간이 부족 | 결과 공유보다 롤백 완료를 우선 |

---

## 8. 마지막 종료 멘트 방향

3일차 마지막은 발표보다 운영 복구 경험으로 마무리한다.

강조 메시지:

> 오늘의 최종 결과는 단순히 배포 성공이 아니라, 잘못된 배포를 감지하고 Git 기준으로 복구해 본 경험이다.
