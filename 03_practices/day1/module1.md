# 모듈 1 — Docker Desktop 실습 환경 준비

> **목표**: 3일간의 실습을 진행할 로컬 환경을 점검하고 복구한다.
> 환경 이슈 없이 `docker run`이 성공하는 상태가 이 모듈의 완료 기준이다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.

## 1-1. 왜 컨테이너인가

개발 환경에서 "내 PC에서는 되는데 서버에서 안 된다"는 문제는 매우 흔하다.
컨테이너는 이 문제를 해결하기 위해 **애플리케이션 실행에 필요한 모든 것**을 하나의 패키지로 묶는다.

| 구분 | 설명 |
|---|---|
| **이미지** | 앱 코드 + 런타임 + 설정이 담긴 읽기 전용 패키지 |
| **컨테이너** | 이미지를 실행한 프로세스 단위. 여러 컨테이너가 같은 이미지에서 실행될 수 있다 |
| **Docker** | 이미지를 만들고 컨테이너를 실행·관리하는 도구 |
| **Docker Desktop** | Windows/macOS에서 Docker를 편리하게 쓸 수 있도록 UI와 함께 제공하는 패키지 |

> 💡 오늘은 직접 실행해보면서 이 개념을 체감하는 것이 목표다.
> 이론을 먼저 외우려 하지 않아도 된다.

---

## 1-2. IDE에서 PowerShell 터미널 열기

1. **Antigravity IDE** (권장) 또는 VS Code / IntelliJ IDEA를 실행한다.
2. 터미널을 열고 종류가 **PowerShell**인지 확인한다.
3. PowerShell 프롬프트가 보이면 준비 완료.

```powershell
# 버전 확인으로 PowerShell인지 확인
$PSVersionTable.PSVersion
```

---

## 1-3. 저장소 clone 및 구조 확인

강의에서 사용하는 예제 코드는 GitHub에 있다. 아직 clone하지 않았으면 지금 진행한다.

```powershell
# 본인 fork 저장소 URL로 clone
git clone https://github.com/<본인-계정>/docker_k8s_tutorial.git

# 저장소 디렉토리로 이동
cd docker_k8s_tutorial
```

**저장소 구조 확인**

```powershell
# 최상위 디렉토리 목록 확인
dir
```

Antigravity IDE 또는 사용 중인 IDE의 파일 탐색기에서 아래 구조를 확인한다.

```
docker_k8s_tutorial/
├── .github/workflows/    ← GitHub Actions CI workflow (3일차)
├── 01_design/            ← 과정 기획 및 설계 가이드
├── 02_lectures/          ← 이론 강의 자료
└── 03_practices/         ← 실습 자료 모음
    ├── day1/             ← 1일차 가이드 문서
    ├── day2/             ← 2일차 가이드 문서
    ├── day3/             ← 3일차 가이드 문서
    └── workspace/        ← 통합 실습 작업 공간
        ├── spring-app/   ← Spring Boot 예제 소스 (Dockerfile 포함)
        ├── compose.yml   ← Docker Compose 설정
        ├── .env          ← 환경변수
        ├── k8s-day2/     ← Kubernetes 매니페스트 (2일차)
        └── k8s-day3-gitops/ ← GitOps 매니페스트 및 Helm (3일차)
```

> 💡 코드를 작성할 일은 많지 않다. 주어진 파일을 이해하고 배포·운영하는 데 집중한다.

---

## 1-4. WSL2 활성화 확인

Docker Desktop on Windows는 **WSL2(윈도우 Linux 서브시스템)**를 기반으로 동작한다.
WSL2가 비활성화 상태면 Docker가 실행되지 않는다.

```powershell
# WSL2 상태 확인
wsl --status

# 설치된 배포판 목록
wsl --list --verbose
```

**정상 출력 예시**

```
Default Distribution: Ubuntu
Default Version: 2
```

| 상황 | 해결 |
|---|---|
| `wsl` 명령 자체가 없다 | PowerShell을 **관리자 권한**으로 실행 후 `wsl --install` |
| VERSION이 1이다 | `wsl --set-version Ubuntu 2` |
| WSL2는 있지만 Ubuntu가 없다 | `wsl --install -d Ubuntu` |
| 설치 후 재부팅 필요 | 재부팅 후 다시 확인 |

---

## 1-5. Docker Desktop 상태 확인

```powershell
# Docker 클라이언트 / 서버 버전 확인
docker version

# 실행 중인 컨테이너 목록 (초기에는 빈 목록이 정상)
docker ps
```

**정상 출력 예시**

```
Client: Docker Engine - Community
 Version:           26.x.x
Server: Docker Desktop
 Engine:
  Version:          26.x.x
```

> ❌ `Cannot connect to the Docker daemon` 오류가 나오면
> 트레이 아이콘에서 Docker Desktop이 실행 중인지 확인한다.

---

## 1-6. Compose 플러그인 확인

```powershell
docker compose version
```

**정상 출력 예시**

```
Docker Compose version v2.x.x
```

---

## 1-7. Docker Desktop UI 탐색

Docker Desktop을 열어 아래 탭을 직접 확인한다.

| 탭 | 내용 |
|---|---|
| **Containers** | 실행 중인 컨테이너 목록. 강의 중 여기서 상태를 바로 확인할 수 있다 |
| **Images** | 로컬에 저장된 이미지 목록. 빌드한 이미지가 여기 나타난다 |
| **Volumes** | 컨테이너 데이터 저장 공간. PostgreSQL 데이터가 여기에 보존된다 |

> 💡 명령줄과 UI를 번갈아 쓰면서 동일한 상태를 보는 연습을 한다.

---

## 1-8. Docker Desktop 리소스 설정 확인

`Docker Desktop → Settings → Resources`

| 항목 | 권장 값 | 비고 |
|---|---|---| 
| **CPUs** | 4 코어 이상 | Kubernetes 시스템 Pod 포함 |
| **Memory** | 6 GB 이상 | PostgreSQL + Spring + K8s |
| **Disk image size** | 60 GB 이상 | 이미지 캐시 공간 |

부족하면 강사에게 알린다. 최소 Memory 4 GB, CPU 2코어도 실습 진행은 가능하나 느릴 수 있다.

---

## 1-9. 기본 동작 확인 (hello-world)

```powershell
docker run --rm hello-world
```

`Hello from Docker!` 메시지가 보이면 환경 준비 완료.

**Docker Desktop Containers 탭**에서 방금 실행된 컨테이너 기록도 확인해본다.
(`--rm` 옵션으로 종료 시 자동 삭제됨)

---

## ✅ 모듈 1 완료 기준

- [ ] 저장소가 clone되어 있고 `day1/`, `spring-app/` 디렉토리가 보인다
- [ ] WSL2가 활성화되어 있다 (`wsl --status`)
- [ ] `docker version` 명령이 정상 출력된다
- [ ] `docker compose version` 명령이 정상 출력된다
- [ ] `docker run --rm hello-world` 실행 후 정상 메시지가 출력된다
- [ ] Docker Desktop Images / Containers / Volumes 탭을 확인했다

---

[← 목차로 돌아가기](./README.md) | [모듈 2 →](./module2.md)
