# 모듈 1 — Docker Desktop 실습 환경 준비

> **목표**: 이후 실습 실패를 줄이기 위한 준비 단계.
> Docker Desktop 설치 상태, Docker Engine 동작 여부, Compose 플러그인을 모두 확인한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd day1`로 이동한 `day1/` 디렉터리 기준입니다.

## 1-1. IDE에서 PowerShell 터미널 열기

1. 권장: **Antigravity IDE**에서 터미널을 연다.
   VS Code 또는 IntelliJ IDEA를 사용해도 된다.
2. 터미널 종류가 **PowerShell**인지 확인한다.
3. PowerShell 프롬프트가 열리면 준비 완료

---

## 1-2. WSL2 활성화 확인

Docker Desktop on Windows는 **WSL2(윈도우 Linux 서브시스템)**를 기반으로 동작한다.
WSL2가 비활성화 상태면 Docker가 실행되지 않으므로 반드시 사전 확인이 필요하다.

```powershell
# WSL2 설치 상태 확인
wsl --status

# 설치된 Linux 배포판 목록 (강의 전 미리 설치해 두면 좋음)
wsl --list --verbose
```

**WSL2 정상 출력 예시**

```
Default Distribution: Ubuntu
Default Version: 2
```

| 상황 | 해결 |
|---|---|
| `wsl --status` 명령이 없다 | PowerShell을 **관리자 권한**으로 실행 후 `wsl --install` |
| VERSION이 1이다 | `wsl --set-version Ubuntu 2` 로 업그레이드 |
| Docker Desktop 시작 안 됨 | Docker Desktop 설치 시 WSL2 연동 체크확인 |

---

## 1-3. Docker Desktop 상태 확인

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
 ...
Server: Docker Desktop
 Engine:
  Version:          26.x.x
```

> ❌ `Cannot connect to the Docker daemon` 오류가 나오면
> 트레이 아이콘에서 Docker Desktop이 실행 중인지 확인한다.

---

## 1-4. Compose 플러그인 확인

```powershell
docker compose version
```

**정상 출력 예시**

```
Docker Compose version v2.x.x
```

---

## 1-5. Docker Desktop 리소스 설정 확인

`Docker Desktop → Settings → Resources`

| 항목 | 권장 값 | 비고 |
|---|---|---|
| **CPUs** | 4 코어 이상 | Kubernetes 시스템 Pod 포함 |
| **Memory** | 6 GB 이상 | PostgreSQL + Spring + K8s |
| **Disk image size** | 60 GB 이상 | 이미지 캐시 공간 |

---

## 1-6. 기본 동작 확인 (hello-world)

```powershell
docker run --rm hello-world
```

`Hello from Docker!` 메시지가 보이면 환경 준비 완료.

---

## ✅ 모듈 1 완료 기준

- [ ] WSL2가 활성화되어 있다 (`wsl --status`)
- [ ] `docker version` 명령이 정상 출력된다
- [ ] `docker compose version` 명령이 정상 출력된다
- [ ] `docker run --rm hello-world` 실행 후 정상 메시지가 출력된다

---

[← 목차로 돌아가기](./README.md) | [모듈 2 →](./module2.md)
