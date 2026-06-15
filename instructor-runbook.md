# Instructor Runbook — Docker & Kubernetes 3일 강사용 진행안

> 강사가 이 문서 하나를 들고 3일 워크숍을 운영할 수 있도록 만든 진행안이다.  
> 우선순위는 **강사용 운영 판단**이고, 톤은 **수강생이 혼자 따라갈 수 있는 튜토리얼형 설명**을 강사가 그대로 안내할 수 있게 맞춘다.

---

## 0. 확정된 설계 기준

| 항목 | 기준 |
|---|---|
| 과정명 | DevOps를 위한 Docker & Kubernetes 실무 |
| 형태 | 기업 내부 3일 실습형 워크숍 |
| 시간 | 3일, 09:00~18:00 |
| 대상 | 주니어 개발자/운영자 기준 |
| 코드 수준 | Spring Boot 코드는 읽을 수 있으나 Docker/Kubernetes/GHCR 초심자 포함 |
| 실습 초점 | Java/Gradle 코딩이 아니라 제공된 Spring 앱의 컨테이너화, 배포, 운영, 복구 |
| OS/쉘 | Windows + Docker Desktop + PowerShell 기준 |
| IDE | Antigravity 권장, VS Code/IntelliJ 대체 가능 |
| Kubernetes namespace | `todo-app` |
| k9s | 권장 도구. 필수 아님. 모든 절차는 `kubectl`로 대체 가능해야 함 |
| Day3 GitOps | 수동 이미지 태그를 values.yaml에 반영 → commit/push → Argo CD OutOfSync → 수동 Sync |
| 자동 manifest update | values.yaml 자동 갱신 데모 또는 심화자료. 필수 실습 제외 |
| 전원 장애 실습 | 잘못된 이미지 태그 → ImagePullBackOff → 정상 태그 복구 |

---

## 1. 강사용 공통 운영 원칙

### 1.1 진행 패턴

모든 모듈은 아래 순서로 진행한다.

1. **지금 하는 일의 의미 설명**
   - “이 단계가 전체 배포 흐름에서 어디에 해당하는지”를 먼저 말한다.
2. **강사 데모**
   - 한 번에 너무 많은 명령을 실행하지 않는다.
   - 명령 1~2개마다 정상 출력과 확인 포인트를 보여준다.
3. **수강생 따라하기**
   - 수강생이 같은 명령을 실행한다.
   - 강사는 화면 공유를 유지하되, 수강생에게 직접 타이핑/복사 실행 시간을 준다.
4. **정상 확인**
   - “성공한 사람?”으로 끝내지 말고 확인 명령을 반드시 실행시킨다.
5. **오류 1차 진단**
   - 주니어 기준으로 원인 추론보다 `ps`, `logs`, `describe`, `get events` 같은 확인 루틴을 먼저 반복한다.
6. **다음 단계 진입 기준 확인**
   - 전체 인원의 약 70% 이상이 핵심 산출물에 도달하면 다음 단계로 진행한다.
   - 미완료자는 강사 제공 정상 파일/데모 결과를 기준으로 따라오게 한다.

### 1.2 강사 설명 톤

수강생에게는 아래 톤을 유지한다.

- “외우세요”보다 “이 순서로 확인하면 됩니다.”
- “이 명령은 정답 명령입니다”보다 “이 명령으로 현재 상태를 확인합니다.”
- “Kubernetes가 어렵다”보다 “리소스를 나눠서 보고, 상태를 확인하는 도구라고 생각하면 됩니다.”
- “장애가 나면 실패”가 아니라 “장애를 보고 복구하는 것이 오늘의 핵심 실습입니다.”

### 1.3 공통 체크 명령

강사는 매일 시작 전에 아래 상태를 확인한다.

```powershell
docker ps
docker compose version
kubectl version --client
kubectl get nodes
```

Day2/Day3에서 namespace 기준 확인:

```powershell
kubectl get all -n todo-app
```

### 1.4 시간 운영 기준

| 상황 | 운영 판단 |
|---|---|
| 70% 이상 완료 | 다음 단계로 진행하고, 미완료자는 보조 지원 |
| 50~70% 완료 | 10분 이내 추가 복구 후 다음 단계 판단 |
| 50% 미만 완료 | 강사 데모로 전환하고 공통 원인을 먼저 제거 |
| 환경 문제 장기화 | 개별 PC 복구보다 강사 정상 환경으로 흐름 이해를 우선 |
| 시간이 20분 이상 밀림 | 심화 설명/선택 실습을 생략하고 핵심 산출물 확보 |

---

## 2. Day 1 강사용 진행안 — Docker / Docker Compose

### 2.1 Day 1 목표

수강생이 로컬 환경에서 Spring Boot 앱을 컨테이너로 실행하고, PostgreSQL과 연결된 Compose 구성을 완성한다.

Day1 종료 시 반드시 확보할 산출물:

- Docker Desktop 정상 동작
- Spring 앱 Docker 이미지 빌드 성공
- Docker Compose로 Spring + PostgreSQL 실행
- `/actuator/health` 또는 `/todos` 응답 확인
- Kubernetes 실습을 위한 `kubectl get nodes` 가능 상태 확인

### 2.2 Day 1 시간표

| 시간 | 모듈 | 운영 목표 | 강사 체크포인트 |
|---|---|---|---|
| 09:00~10:30 | 환경 점검 | Docker Desktop/WSL2/PowerShell 준비 | `docker ps`, `docker compose version` |
| 10:30~11:30 | Docker 기본 | 이미지/컨테이너/포트/로그 이해 | 컨테이너 실행/중지/로그 확인 |
| 11:30~12:30 | Spring 앱 이미지화 | Dockerfile 기반 이미지 빌드 | `docker images`에서 앱 이미지 확인 |
| 12:30~13:30 | 점심 |  |  |
| 13:30~15:30 | Docker Compose | 앱 + DB 구성 실행 | Compose 서비스 정상, API 응답 |
| 15:30~16:30 | Kubernetes 준비 | Docker Desktop Kubernetes 활성화 | `kubectl get nodes` |
| 16:30~18:00 | 운영 관점 정리 | 로그/포트/DB 연결 오류 진단 루틴 | `docker logs`, 포트, 환경변수 확인 |

### 2.3 09:00~10:30 — 환경 점검

#### 강사 멘트

> 오늘은 코드를 새로 짜는 시간이 아니라, 이미 있는 Spring Boot 앱을 컨테이너로 실행하고 운영 가능한 형태로 바꾸는 시간입니다.  
> 첫 90분은 진도를 나가는 시간이 아니라, 3일 동안 막히지 않게 환경을 안정화하는 시간입니다.

#### 진행 순서

1. 수강생에게 PowerShell을 열게 한다.
2. Docker Desktop 실행 상태를 확인한다.
3. 아래 명령을 순서대로 실행시킨다.

```powershell
docker ps
docker compose version
```

#### 정상 확인

- `docker ps`가 에러 없이 표를 출력한다.
- `docker compose version`이 버전을 출력한다.

#### 흔한 막힘

| 증상 | 1차 확인 | 조치 |
|---|---|---|
| Docker daemon 에러 | Docker Desktop 실행 여부 | Docker Desktop 재실행 |
| WSL2 관련 에러 | Docker Desktop settings | WSL2 backend 확인 |
| compose 명령 실패 | `docker compose version` | Docker Desktop 업데이트 또는 강사 PC 데모로 전환 |

#### 다음 단계 진입 기준

- 전체 70% 이상이 `docker ps` 성공.
- 실패자는 점심/휴식 시간에 개별 복구하고, 강의 흐름은 강사 데모로 따라오게 한다.

### 2.4 10:30~11:30 — Docker 기본

#### 강사 멘트

> 이미지는 실행 전 패키지이고, 컨테이너는 이미지를 실행한 프로세스입니다.  
> 오늘은 이 둘을 구분하고, 문제가 생겼을 때 `ps`와 `logs`로 상태를 확인하는 습관을 만드는 것이 중요합니다.

#### 진행 순서

1. 강사가 이미지와 컨테이너 관계를 짧게 설명한다.
2. 간단한 컨테이너를 실행한다.
3. 실행 중인 컨테이너를 확인한다.
4. 로그와 종료를 확인한다.

#### 수강생 확인 명령 예시

```powershell
docker ps
docker ps -a
docker logs <container-name-or-id>
```

#### 정상 확인

- 실행 중인 컨테이너와 종료된 컨테이너를 구분할 수 있다.
- 로그 확인 명령을 실행할 수 있다.

#### 다음 단계 질문

- “이미지와 컨테이너의 차이를 한 문장으로 말할 수 있나요?”
- “컨테이너가 죽었을 때 제일 먼저 볼 명령은 무엇인가요?”

### 2.5 11:30~12:30 — Spring 앱 이미지화

#### 강사 멘트

> 이제 우리가 운영할 애플리케이션을 이미지로 만듭니다.  
> Dockerfile은 애플리케이션을 어떤 실행 환경에 담을지 적어둔 설계도라고 보면 됩니다.

#### 진행 순서

1. 프로젝트 구조를 보여준다.
2. Dockerfile 위치와 역할을 설명한다.
3. 이미지 빌드를 실행한다.
4. 이미지 목록에서 결과를 확인한다.

#### 수강생 실행/확인

```powershell
docker build -t todo-app:1.0 .
docker images
```

> 실제 경로는 수강생 모듈 문서의 작업 디렉터리 지시를 따른다. 강사는 명령 실행 전 반드시 현재 위치를 확인시킨다.

```powershell
pwd
```

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| Dockerfile not found | `pwd` | 올바른 디렉터리로 이동 |
| Gradle/build 실패 | 로그 상단의 실패 원인 | 강사 정상 빌드 결과로 흐름 유지 |
| 네트워크 다운로드 지연 | 빌드 로그 | 기다리되 10분 이상 지연 시 데모 전환 |

#### 다음 단계 진입 기준

- `docker images`에서 앱 이미지가 보인다.
- 일부 수강생이 실패해도 이미지가 무엇인지, 빌드 로그를 어디서 보는지 이해하면 진행 가능.

### 2.6 13:30~15:30 — Docker Compose 연동

#### 강사 멘트

> 실제 서비스는 앱 하나만 떠 있지 않습니다.  
> Compose는 앱과 DB처럼 같이 떠야 하는 구성요소를 한 번에 실행하고 같은 네트워크에서 연결해 주는 도구입니다.

#### 진행 순서

1. `compose.yml`의 서비스 구성을 보여준다.
2. `app`, `postgres` 같은 서비스 이름이 네트워크 이름으로 쓰인다는 점을 설명한다.
3. Compose 실행.
4. 로그와 API 응답 확인.

#### 수강생 실행/확인

```powershell
docker compose up -d
docker compose ps
docker compose logs
```

API 확인은 문서 기준 엔드포인트를 사용한다.

```powershell
curl.exe http://localhost:<PORT>/actuator/health
curl.exe http://localhost:<PORT>/todos
```

> PowerShell에서는 `curl`이 별칭일 수 있으므로 강사는 `curl.exe` 사용을 안내한다.

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| 포트 충돌 | `docker compose ps` | 기존 컨테이너 종료 또는 포트 변경 |
| DB 연결 실패 | `docker compose logs app` | 환경변수/서비스명 확인 |
| API 응답 없음 | app 로그, 포트 매핑 | 컨테이너 상태와 포트 확인 |

#### 다음 단계 진입 기준

- `docker compose ps`에서 앱과 DB가 실행 중이다.
- `/actuator/health` 또는 `/todos` 응답을 최소 1회 확인했다.

### 2.7 15:30~16:30 — Kubernetes 로컬 준비

#### 강사 멘트

> 내일부터는 같은 앱을 Kubernetes에서 실행합니다.  
> 오늘은 Kubernetes를 깊게 배우기보다, 내일 실습할 클러스터가 준비됐는지만 확인합니다.

#### 수강생 실행/확인

```powershell
kubectl version --client
kubectl get nodes
```

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| `kubectl` 없음 | PATH/설치 상태 | Docker Desktop Kubernetes 또는 kubectl 설치 확인 |
| node 없음 | Docker Desktop Kubernetes 활성화 | Settings에서 Kubernetes enable |
| 오래 Pending | Docker Desktop 리소스 | 재시작 또는 Day2 09:00 재확인 |

#### 다음 단계 진입 기준

- 가능한 수강생은 `kubectl get nodes`에서 Ready 확인.
- 실패자는 Day2 시작 전까지 복구 대상으로 분리.

### 2.8 Day 1 fallback

| 상황 | 대응 | 반드시 유지할 산출물 |
|---|---|---|
| Docker Desktop/WSL2 복구가 10:30을 넘김 | 강사 PC 데모로 흐름 진행, 문제 PC는 점심/휴식에 개별 복구 | Docker 명령 흐름 이해 |
| Compose 연동 지연 | 강사 정상 `compose.yml`과 예상 로그 제공 | 앱 + DB 연결 성공 신호 이해 |
| Kubernetes 준비 지연 | Day1은 개념/데모로 마무리, Day2 09:00 readiness 재확인 | `kubectl get nodes` 목표 인식 |

---

## 3. Day 2 강사용 진행안 — Kubernetes 배포

### 3.1 Day 2 목표

수강생이 `todo-app` namespace에 PostgreSQL과 Spring 앱을 배포하고, `kubectl get/describe/logs/rollout` 기반으로 기본 운영 진단을 수행한다.

Day2 종료 시 반드시 확보할 산출물:

- namespace `todo-app`
- PostgreSQL Deployment/Service/PVC 정상
- Spring App Deployment/Service 정상
- `http://localhost:30080/todos` 응답
- 장애 상황에서 `logs`, `describe`, `get events`를 볼 수 있음

### 3.2 Day 2 시간표

| 시간 | 모듈 | 운영 목표 | 강사 체크포인트 |
|---|---|---|---|
| 09:00~10:00 | Kubernetes 기본 구조 | node/namespace/pod 이해 | `kubectl get nodes`, namespace 생성 |
| 10:00~11:30 | PostgreSQL 배포 | DB 리소스 배포 | PVC Bound, DB Pod Running |
| 11:30~12:30 | Spring 앱 배포 1 | 앱 Deployment/Service 배포 | App Pod Running |
| 12:30~13:30 | 점심 |  |  |
| 13:30~14:30 | Spring 앱 배포 2 | API 응답 확인 | NodePort/API 응답 |
| 14:30~16:00 | ConfigMap/Secret | 설정 분리 이해 | 설정 주입 상태 확인 |
| 16:00~17:00 | 통합 검증 | 앱+DB 연결 확인 | `/todos` 응답 |
| 17:00~18:00 | 운영 진단 | logs/describe/events 루틴 | 장애 확인 루틴 수행 |

### 3.3 09:00~10:00 — Kubernetes 기본 구조

#### 강사 멘트

> Kubernetes는 컨테이너를 직접 하나씩 실행하는 도구라기보다, 원하는 상태를 선언하면 그 상태를 유지하려고 하는 운영 플랫폼입니다.  
> 오늘은 모든 개념을 외우는 것보다 `get`으로 보고, `describe`로 자세히 보고, `logs`로 앱 로그를 보는 순서를 익힙니다.

#### 수강생 실행/확인

```powershell
kubectl get nodes
kubectl create namespace todo-app
kubectl get namespace
```

이미 namespace가 있으면 아래로 확인한다.

```powershell
kubectl get all -n todo-app
```

#### 용어 한 줄 설명

- **Pod**: 컨테이너가 실제로 실행되는 최소 단위.
- **Deployment**: Pod를 몇 개, 어떤 이미지로 유지할지 선언하는 리소스.
- **Service**: Pod에 접근할 고정된 입구.
- **Namespace**: 실습 리소스를 한 공간에 묶는 논리적 구역.

#### 다음 단계 진입 기준

- `todo-app` namespace가 존재한다.
- `kubectl get nodes`가 정상 출력된다.

### 3.4 10:00~11:30 — PostgreSQL 배포

#### 강사 멘트

> 애플리케이션보다 DB를 먼저 배포합니다.  
> 앱은 DB 주소와 계정 정보를 필요로 하므로, 기반 리소스를 먼저 안정화하는 것이 운영 순서입니다.

#### 진행 순서

1. PostgreSQL 관련 YAML을 보여준다.
2. PVC, Deployment, Service가 각각 무엇을 담당하는지 설명한다.
3. 리소스를 적용한다.
4. 상태를 확인한다.

#### 수강생 실행/확인

```powershell
kubectl apply -f <postgres-yaml> -n todo-app
kubectl get pvc -n todo-app
kubectl get pods -n todo-app
kubectl get svc -n todo-app
```

> 실제 파일명과 작업 디렉터리는 수강생 모듈 문서를 기준으로 안내한다. 강사는 실행 전 `pwd`와 파일 위치를 확인시킨다.

#### 정상 확인

- PVC 상태가 `Bound`.
- PostgreSQL Pod 상태가 `Running`.
- PostgreSQL Service가 보인다.

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| PVC Pending | `kubectl describe pvc -n todo-app` | Docker Desktop storage 상태 확인 |
| Pod Pending/CrashLoop | `kubectl describe pod -n todo-app` | 이벤트/환경변수 확인 |
| Service 없음 | `kubectl get svc -n todo-app` | YAML 적용 여부 확인 |

#### 다음 단계 진입 기준

- PostgreSQL Pod가 `Running`이거나, 적어도 문제 원인을 `describe`로 확인했다.

### 3.5 11:30~14:30 — Spring 앱 배포와 API 확인

#### 강사 멘트

> 이제 앱을 Kubernetes에 올립니다.  
> Docker Compose에서는 서비스 이름으로 연결했다면, Kubernetes에서는 Service와 환경설정으로 연결합니다.

#### 진행 순서

1. 앱 Deployment/Service YAML을 보여준다.
2. 이미지, 포트, 환경변수 위치를 설명한다.
3. 리소스를 적용한다.
4. Pod/Service 상태와 API 응답을 확인한다.

#### 수강생 실행/확인

```powershell
kubectl apply -f <app-yaml> -n todo-app
kubectl get pods -n todo-app
kubectl get svc -n todo-app
kubectl rollout status deployment/<app-deployment-name> -n todo-app
```

API 확인:

```powershell
curl.exe http://localhost:30080/todos
```

#### 정상 확인

- 앱 Pod가 `Running`.
- Service가 존재.
- `/todos` 응답 확인.

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| ImagePullBackOff | `kubectl describe pod -n todo-app` | 이미지명/태그 확인 |
| CrashLoopBackOff | `kubectl logs <pod> -n todo-app` | 앱 설정/DB 연결 확인 |
| API 응답 없음 | Service/Pod/포트 확인 | `kubectl get svc`, `kubectl describe svc` |

#### 다음 단계 진입 기준

- 전체 70% 이상이 `/todos` 응답 확인.
- 미완료자는 정상 YAML 제공 또는 강사 데모 결과로 흐름 유지.

### 3.6 14:30~16:00 — ConfigMap/Secret 기반 설정 분리

#### 강사 멘트

> 운영 환경에서는 설정과 비밀번호를 이미지 안에 넣지 않습니다.  
> ConfigMap은 일반 설정, Secret은 민감한 값을 분리하는 용도로 사용합니다.

#### 수강생 실행/확인

```powershell
kubectl get configmap -n todo-app
kubectl get secret -n todo-app
kubectl describe deployment <app-deployment-name> -n todo-app
```

#### 정상 확인

- `app-config` ConfigMap 존재.
- `db-secret` Secret 존재.
- 앱 Deployment가 해당 설정을 참조한다.

#### 강사 데모 후보

전원 실습으로 깊게 들어가지 말고, 시간이 남을 때만 데모한다.

- ConfigMap 누락 시 앱 기동 실패.
- Secret 키 이름 불일치 시 환경변수 주입 실패.
- Service selector 불일치 시 Pod는 떠 있지만 트래픽이 가지 않음.

### 3.7 16:00~18:00 — 통합 검증과 운영 진단

#### 강사 멘트

> 운영자는 문제가 났을 때 YAML 전체를 처음부터 다시 읽기보다, 상태를 좁혀 가며 봅니다.  
> 오늘의 순서는 `get` → `describe` → `logs` → `events`입니다.

#### 수강생 진단 루틴

```powershell
kubectl get all -n todo-app
kubectl get events -n todo-app --sort-by=.lastTimestamp
kubectl describe pod <pod-name> -n todo-app
kubectl logs <pod-name> -n todo-app
kubectl rollout status deployment/<deployment-name> -n todo-app
```

#### 전원 체크 질문

- Deployment와 Service의 역할을 구분할 수 있는가?
- 앱이 DB에 연결되지 않을 때 먼저 볼 곳은 어디인가?
- ConfigMap과 Secret을 왜 이미지 밖으로 분리하는가?

### 3.8 Day 2 fallback

| 상황 | 대응 | 반드시 유지할 산출물 |
|---|---|---|
| PVC/DB Pod가 늦게 뜸 | DB 내부 접속 심화 생략, `describe`/events 중심 전환 | PostgreSQL 리소스 구조 이해 |
| App Pod 기동 실패 | `app-config`, `db-secret`, 이미지 태그 확인 후 정상 YAML 제공 | 설정 주입 원인-결과 이해 |
| API 검증 지연 | 강사 데모로 API 응답 확인, 수강생은 진단 체크리스트 완성 | 운영 점검 루틴 확보 |
| 장애 진단 시간 부족 | ImagePullBackOff만 전원 실습, 나머지는 데모 | Day3 롤백 실습 연결 |

---

## 4. Day 3 강사용 진행안 — GitHub Actions + Argo CD GitOps

### 4.1 Day 3 목표

수강생이 본인 fork 저장소에서 CI로 이미지를 만들고, Git 변경을 기준으로 Argo CD가 배포 상태를 감지/동기화하는 흐름을 경험한다. 마지막에는 잘못된 이미지 태그 장애를 재현하고 Git 기준으로 복구한다.

Day3 종료 시 반드시 확보할 산출물:

- GitHub Actions 성공
- GHCR 이미지 태그 확인
- `day3/k8s/helm/values.yaml` 수동 이미지 태그 반영
- `values.yaml` scale/resource 변경 및 `helm template` 검증
- commit/push 후 Argo CD OutOfSync 확인 및 첫 수동 Sync 성공
- 잘못된 이미지 태그로 ImagePullBackOff 재현
- 정상 태그 복구 또는 `git revert` 완료

### 4.2 Day 3 시간표

| 시간 | 모듈 | 운영 목표 | 강사 체크포인트 |
|---|---|---|---|
| 09:00~10:00 | CI/CD와 GitOps 구조 (M1) | CI와 CD 역할 분리 | 그림/흐름 설명 완료 |
| 10:00~11:30 | GitHub Actions CI (M2) | 이미지 빌드/push | Actions 성공, GHCR 이미지 확인 |
| 11:30~12:30 | 버전관리와 이미지 태그 반영 (M3) | values.yaml 수정 commit/push | values.yaml 내 short SHA 태그 반영 |
| 12:30~13:30 | 점심 |  |  |
| 13:30~14:30 | Helm 기본과 차트 구조 (M4) | Helm 패키징 및 values 변경 | values.yaml scale/resource 변경 및 template 확인 |
| 14:30~16:00 | Argo CD 설치 및 앱 등록 (M5) | Argo CD 설치 및 Helm App 등록 | Argo CD 설치 상태 및 Synced/Healthy 확인 |
| 16:00~18:00 | Sync, 장애 재현, 롤백 (M6) | scale 변경 Sync, ImagePullBackOff 유도 | Git revert 또는 Argo CD rollback으로 복구 완료 |

### 4.3 09:00~10:00 — CI/CD와 GitOps 구조

#### 강사 멘트

> CI는 코드를 검사하고 이미지를 만드는 흐름입니다.  
> CD는 그 결과를 실제 환경에 반영하는 흐름입니다.  
> 오늘은 배포 기준을 사람의 로컬 명령이 아니라 Git commit으로 바꿔 봅니다.

#### 설명 포인트

- GitHub Actions: 코드 변경 → 빌드 → 이미지 생성 → GHCR push.
- GHCR: 컨테이너 이미지 저장소.
- Argo CD: Git에 선언된 상태와 클러스터 상태를 비교하고 Sync.
- OutOfSync: Git과 클러스터가 다르다는 신호.
- Sync: Git 상태를 클러스터에 반영.

#### 다음 단계 질문

- CI가 만드는 산출물은 무엇인가?
- Argo CD는 무엇을 기준으로 배포하는가?

### 4.4 10:00~11:30 — GitHub Actions CI

#### 강사 멘트

> 이제 수강생 각자의 저장소에서 이미지가 만들어지는지 확인합니다.  
> 여기서 중요한 것은 workflow 파일을 외우는 것이 아니라, Actions 로그에서 성공 여부와 이미지 태그를 찾는 것입니다.

#### 진행 순서

1. fork 저장소 확인.
2. Actions 탭 확인.
3. workflow 실행/성공 확인.
4. GHCR 이미지와 태그 확인.

#### 수강생 확인 포인트

- Actions run이 성공했는가?
- 이미지가 GHCR에 생성됐는가?
- 태그가 short SHA 형태로 확인되는가?

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| Actions 실패 | 실패 step 로그 | 권한/경로/빌드 로그 확인 |
| GHCR 이미지 없음 | package visibility/permissions | Actions 로그에서 push 여부 확인 |
| 태그를 못 찾음 | workflow 로그 | 강사가 태그 위치 화면 공유 |

#### fallback

GHCR 권한 문제로 로컬 pull이 안 되더라도, Actions 성공 로그와 태그 산출 방식 확인을 우선한다.

### 4.5 11:30~12:30 — 버전관리와 이미지 태그 반영 (M3)

#### 강사 멘트

> 자동으로 YAML을 바꾸는 도구도 있지만, 오늘 기본 실습은 사람이 이미지 태그를 직접 바꿉니다.  
> 그래야 GitOps에서 “Git 변경이 배포 요청이 된다”는 구조를 눈으로 확인할 수 있습니다.
> 3일차 기본 실습은 Helm chart의 values.yaml 파일을 수정해 이미지 태그를 수동으로 반영하는 흐름입니다.

#### 진행 순서

1. `day3/k8s/helm/values.yaml`에서 `image.tag` 필드를 찾는다.
2. GHCR에서 확인한 short SHA 태그와 본인의 GitHub 계정명(`image.repository`)으로 수정한다.
3. commit/push 한다.

#### 수강생 실행/확인 예시

```powershell
git status
git add day3/k8s/helm/values.yaml
git commit -m "deploy: update image tag to <7자리-short-sha>"
git push origin main
```

#### 정상 확인

- GitHub 저장소에 commit이 올라갔다.
- 이미지 태그가 `latest` 같은 모호한 값이 아니라 실제 빌드 태그(short SHA)다.

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| 파일 위치 못 찾음 | `pwd`, repo root | 작업 디렉터리 재확인 |
| push 실패 | remote/fork 권한 | fork 저장소 여부 확인 |
| 잘못된 태그 입력 | GHCR/Actions 로그 | short SHA 태그 다시 복사 |

---

### 4.6 13:30~14:30 — Helm 기본과 차트 구조 (M4)

#### 강사 멘트

> Kubernetes YAML 파일은 환경이 바뀌거나 정보가 바뀔 때 매번 파일을 수정해야 하는 불편함이 있습니다.
> Helm은 이 문제를 YAML을 템플릿화하고, 변하는 값들을 values.yaml로 관리하여 해결합니다.
> 여기서는 values.yaml의 설정값 변경이 렌더링된 최종 manifest에 어떻게 적용되는지 확인합니다.

#### 진행 순서

1. Helm CLI 설치 상태 확인 (`helm version`)
2. `day3/k8s/helm/` 폴더 내의 구조 (`Chart.yaml`, `values.yaml`, `templates/deployment.yaml`) 설명
3. `values.yaml`의 `replicaCount` 또는 `resources` 확인 및 변경 실습
4. `helm template todo-app day3/k8s/helm` 명령을 저장소 루트에서 수행하여 최종 렌더링 결과 검증
5. 수정된 `values.yaml`을 커밋하고 푸시

#### 수강생 실행/확인 예시

```powershell
# Helm CLI 버전 확인
helm version

# 저장소 루트에서 template 실행 결과 확인
helm template todo-app day3/k8s/helm

# 변경사항 commit & push
git add day3/k8s/helm/values.yaml
git commit -m "helm: update image tag in values"
git push origin main
```

#### 정상 확인

- `helm template todo-app day3/k8s/helm` 실행 결과 렌더링된 Deployment YAML이 출력된다.
- 출력 내용 중 `image:` 부분에 본인이 수정한 레포지토리와 short SHA 태그가 들어가 있다.

---

### 4.7 14:30~16:00 — Argo CD 설치 및 앱 등록 (M5)

#### 강사 멘트

> Argo CD는 Git 저장소를 바라보다가, Git에 적힌 상태와 클러스터 상태가 다르면 OutOfSync로 표시합니다.  
> 오늘은 자동 Sync가 아니라 사람이 직접 Sync 버튼을 눌러 반영합니다.

#### Day3 시작 전 기반 리소스 확인

Day3의 GitOps 경로는 Helm chart 기반의 앱 Deployment 중심이다. 2일차에서 만든 아래 기반 리소스가 미리 생성되어 있어야 한다.

```powershell
kubectl get namespace todo-app
kubectl get svc todo-app -n todo-app
kubectl get configmap app-config -n todo-app
kubectl get secret db-secret -n todo-app
kubectl get pvc postgres-pvc -n todo-app
kubectl get deployment postgres -n todo-app
```

> ⚠️ 만약 2일차 리소스가 없다면, `day2/k8s/` 리소스들을 먼저 적용하고 진행해야 배포 성공 시 앱이 정상 동작한다.

#### 진행 순서

1. `argocd` 네임스페이스를 생성하고 Argo CD를 설치한다.
2. Argo CD UI에 접근할 수 있도록 포트 포워딩을 수행하고, admin 초기 비밀번호를 조회한다.
3. Argo CD UI 또는 CLI를 통해 `day3/k8s/helm` 경로를 Helm chart 방식의 Application으로 등록한다. (Application Name: `todo-app`, Path: `day3/k8s/helm`, Target Namespace: `todo-app`)
4. 첫 번째 수동 Sync를 실행하여 클러스터에 배포하고 검증한다.

#### 수강생 실행/확인 예시

```powershell
# Argo CD 설치 및 포트 포워딩
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl port-forward svc/argocd-server -n argocd 8443:443

# admin 비밀번호 조회
kubectl get secret argocd-initial-admin-secret -n argocd `
  -o jsonpath="{.data.password}" | %{[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))}

# Sync 및 배포 검증
kubectl get pods -n todo-app
kubectl rollout status deployment/todo-app -n todo-app
curl.exe http://localhost:30080/todos
```

#### 앱 등록 후 확인

- Argo CD 앱(`todo-app`)이 정상적으로 등록되었다.
- 첫 수동 Sync 전에는 상태가 `OutOfSync`로 보이고, Sync를 완료하면 `Synced` 및 `Healthy`로 전환된다.
- 클러스터의 Pod 상태가 `Running`이며 `http://localhost:30080/todos` API가 정상 응답한다.

#### 흔한 막힘

| 증상 | 확인 | 조치 |
|---|---|---|
| Argo CD 설치 지연 | Pod 상태 | 강사 데모 환경으로 먼저 설명 |
| repo 연결 실패 | URL/권한 | public fork 여부 또는 credential 확인 |
| 경로 오류 | app path | `day3/k8s/helm` 경로 확인 |

---

### 4.8 16:00~18:00 — Sync, 장애 재현, 롤백 (M6)

#### 강사 멘트

> 마지막 실습은 일부러 잘못 배포해 보는 것입니다.  
> 운영에서 중요한 것은 장애가 안 나는 것이 아니라, 장애를 감지하고 안전한 기준으로 복구하는 것입니다. 오늘 복구 기준은 클러스터 직접 수정이 아니라 Git입니다.

#### 전원 실습 범위

1. `values.yaml`의 `replicaCount`를 3으로 수정하여 commit/push 한 뒤, Argo CD `OutOfSync`를 확인하고 수동 Sync하여 Scale-out을 확인한다.
2. `values.yaml`의 이미지 태그를 존재하지 않는 값(`broken-version`)으로 수정하여 배포 장애(ImagePullBackOff)를 유도한다.
3. `git revert` 또는 Argo CD rollback으로 정상 버전으로 복구한다.

#### 확인 명령

```powershell
# scale 변경
git add day3/k8s/helm/values.yaml
git commit -m "scale: increase replicas to 3"
git push origin main

# 장애 유도
git add day3/k8s/helm/values.yaml
git commit -m "bug: wrong helm image tag (intentional)"
git push origin main

# Pod 상태 및 이벤트 확인
kubectl get pods -n todo-app
kubectl describe pod -l app=todo-app -n todo-app
kubectl get events -n todo-app --sort-by=.lastTimestamp

# Git revert 복구
git revert HEAD --no-edit
git push origin main
```

#### 정상 복구 기준

- `replicaCount`가 3개로 정상 증설되는 것을 확인했다.
- 잘못된 태그로 인해 ImagePullBackOff가 발생한 것을 확인했다.
- Git에서 정상 태그로 되돌렸다.
- Argo CD Sync 후 Pod가 Running으로 돌아왔다.
- API 응답이 정상이다.

#### 강사 데모로만 다룰 장애 후보

시간이 남을 때만 보여준다.

- ConfigMap 누락
- Secret 오류
- Service selector 불일치
- 자동 values.yaml 갱신 흐름 (GitHub Actions contents write 권한 이용)

---

### 4.9 Day 3 fallback

| 상황 | 대응 | 반드시 유지할 산출물 |
|---|---|---|
| GHCR 권한 문제 | Actions 성공 로그와 이미지 태그 산출 방식 우선 확인 | short SHA 태그 확인 |
| Argo CD 설치 지연 | 강사 데모 클러스터로 App 등록/OutOfSync/Sync 흐름 설명 | Git commit → Argo CD Sync 이해 |
| Sync 후 앱 실패 | Day2 기반 리소스 먼저 확인 | day3는 app Deployment 중심이라는 전제 이해 |
| 롤백 시간 부족 | 발표/공유 생략, 정상 태그 복구까지 우선 | Git 기준 복구 경험 |

---

## 5. 등급형 완료 기준

| 등급 | 기준 | 강사 확인 방법 |
|---|---|---|
| Basic | Docker 이미지 빌드 + Compose 실행 성공 | `docker images`, `docker compose ps`, API 응답 |
| Standard | Kubernetes 배포 + API 응답 성공 | `kubectl get all -n todo-app`, `/todos` 응답 |
| Advanced | GitHub Actions + Argo CD + 롤백 성공 | Actions 성공, Argo CD Synced/Healthy, 장애 복구 확인 |

---

## 6. 진행 중 체크 질문

### Day 1

- 이미지와 컨테이너의 차이를 말할 수 있는가?
- 컨테이너가 죽었을 때 어떤 명령으로 상태를 확인하는가?
- 앱과 DB가 Compose 네트워크에서 어떻게 연결되는가?

### Day 2

- Deployment와 Service의 역할을 구분할 수 있는가?
- 앱이 DB에 연결되지 않을 때 먼저 무엇을 확인하는가?
- ConfigMap과 Secret을 왜 이미지 밖으로 분리하는가?

### Day 3

- CI와 CD의 역할을 구분할 수 있는가?
- GitOps에서 왜 Git commit이 배포 기준인가?
- Argo CD OutOfSync는 어떤 의미인가?
- 장애 복구가 클러스터 직접 수정이 아니라 Git 변경으로 이뤄진다는 점을 이해했는가?

---

## 7. 강사 운영 리스크와 대응

| 리스크 | 조기 신호 | 대응 |
|---|---|---|
| Docker Desktop/WSL2 지연 | `docker ps` 실패자가 많음 | 1일차 09:00~10:30 복구 시간 사용, 이후 데모 병행 |
| k9s 설치 실패 | k9s 실행 불가 | 전 과정 `kubectl`로 대체 |
| Gradle/Docker build 지연 | 빌드 다운로드가 오래 걸림 | 강사 정상 이미지/로그로 흐름 유지 |
| PVC/DB 지연 | PVC Pending, DB Pod Pending | `describe`, events 확인 후 심화 생략 |
| GHCR 권한 문제 | Actions 성공이나 이미지 접근 실패 | package 권한/visibility 확인, 로그 중심 진행 |
| Argo CD 설치 지연 | Argo CD Pod 지연 | 강사 데모 환경으로 우선 설명 |
| 롤백 시간이 부족 | 16:30 이후 진도 밀림 | 결과 공유 생략, 복구 완료 우선 |

---

## 8. 시간 부족 시 축소 운영안

### Day 1 축소

- 생략 가능: k9s 상세 사용법, Docker 내부 구조 심화.
- 반드시 유지: 이미지 빌드, Compose 실행, API 응답.

### Day 2 축소

- 생략 가능: ConfigMap/Secret 장애 상세 실습, selector 오류 실습.
- 반드시 유지: namespace, DB, 앱 Deployment/Service, `/todos` 응답, `logs/describe` 루틴.

### Day 3 축소

- 생략 가능: 자동 manifest update, 여러 장애 유형 비교, 발표.
- 반드시 유지: Actions 성공, values.yaml 이미지 태그 수동 반영, Argo CD OutOfSync/Sync, ImagePullBackOff 복구.

---

## 9. 마지막 종료 멘트 방향

3일차 마지막은 발표보다 운영 복구 경험으로 마무리한다.

강조 메시지:

> 오늘의 최종 결과는 단순히 배포 성공이 아니라, 잘못된 배포를 감지하고 Git 기준으로 복구해 본 경험입니다.  
> 실무에서는 명령을 외우는 것보다, 상태를 확인하고 안전한 기준으로 되돌릴 수 있는지가 더 중요합니다.
