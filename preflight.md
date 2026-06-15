# Preflight — 강의 전 사전 설치/점검 가이드

> Docker & Kubernetes 3일 실습 과정에 참여하기 전에 아래 항목을 준비한다.
> 모든 명령은 Windows PowerShell 기준이다.

---

## 1. 필수 준비물

| 항목 | 필수 여부 | 확인 방법 |
|---|---:|---|
| Windows 10/11 | 필수 | `winver` |
| Docker Desktop | 필수 | `docker version` |
| WSL2 | 필수 | `wsl --status` |
| Git | 필수 | `git --version` |
| GitHub 계정 | 필수 | github.com 로그인 |
| Antigravity IDE | 권장 | 프로젝트 열기 |
| VS Code / IntelliJ IDEA | 대체 가능 | 사용 중인 IDE |
| kubectl | 필수 | `kubectl version --client` |
| k9s | 권장 | `k9s version` |

---

## 2. Docker Desktop 확인

```powershell
docker version
docker compose version
docker ps
```

정상 기준:

- Docker Client와 Server 정보가 출력된다.
- `docker ps` 실행 시 오류가 없어야 한다.

---

## 3. WSL2 확인

```powershell
wsl --status
wsl --list --verbose
```

정상 기준:

- 기본 버전이 WSL2이거나, Docker Desktop이 WSL2 backend를 사용할 수 있어야 한다.
- Docker Desktop Settings에서 **Use the WSL 2 based engine**이 활성화되어 있어야 한다.

---

## 4. Git 확인

```powershell
git --version
git config --global user.name
git config --global user.email
```

이름/이메일이 비어 있으면 설정한다.

```powershell
git config --global user.name "YOUR_NAME"
git config --global user.email "YOUR_EMAIL@example.com"
```

---

## 5. GitHub 준비

강의 전 확인:

- GitHub 계정 로그인 가능
- 대표 강의 저장소 접근 가능
- 저장소 fork 가능
- Actions 탭 접근 가능
- Packages/GHCR 화면 접근 가능

수강생은 강의 중 대표 저장소를 본인 계정으로 fork해서 실습한다.

---

## 6. IDE 준비

권장:

- Antigravity IDE

대체 가능:

- VS Code
- IntelliJ IDEA

VS Code 사용 시 권장 확장:

- Docker
- Kubernetes
- YAML
- REST Client

---

## 7. Kubernetes 준비

Docker Desktop Settings에서 Kubernetes를 활성화한다.

확인 명령:

```powershell
kubectl version --client
kubectl get nodes
kubectl get pods -A
```

정상 기준:

- `docker-desktop` node가 `Ready` 상태다.
- `kube-system` Pod들이 `Running` 상태다.

---

## 8. k9s 준비 — 권장

k9s는 필수가 아니라 권장이다. 설치가 어렵다면 `kubectl` 명령으로 대체한다.

확인:

```powershell
k9s version
```

---

## 9. 강의 전 체크리스트

- [ ] Docker Desktop 실행 가능
- [ ] `docker ps` 정상 실행
- [ ] `docker compose version` 정상 실행
- [ ] `git --version` 정상 실행
- [ ] GitHub 로그인 가능
- [ ] 강의 저장소 접근 가능
- [ ] Antigravity IDE 또는 대체 IDE 설치 완료
- [ ] `kubectl get nodes` 정상 실행
- [ ] 선택: k9s 실행 가능

---

## 10. 강의 당일 환경 점검 시간

1일차 09:00~10:30은 사전 설치 점검과 Docker Desktop/WSL2 복구 시간으로 사용한다.
사전 준비가 덜 된 경우 이 시간에 강사와 함께 복구한다.
