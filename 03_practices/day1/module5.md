# 모듈 5 — Kubernetes 개요와 로컬 준비

> **목표**: Docker Desktop에서 Kubernetes를 활성화하고
> `kubectl` 명령으로 노드와 시스템 Pod를 확인한다.
> Kubernetes 리소스 배포는 2일차에 진행하며, 이 모듈에서는 준비 상태 확보에 집중한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd workspace`로 이동한 `day1/` 디렉터리 기준입니다.

## 5-1. Docker Desktop에서 Kubernetes 활성화

1. 트레이 아이콘에서 **Docker Desktop** 열기
2. **Settings** → **Kubernetes** 탭 이동
3. **Enable Kubernetes** 체크박스 활성화
4. **Apply & Restart** 클릭

> ⏳ 최초 활성화 시 Kubernetes 시스템 이미지 다운로드로 수 분 소요된다.
> 완료되면 Docker Desktop 좌측 하단에 **초록색 Kubernetes 표시**가 나타난다.

---

## 5-2. Kubernetes 준비 상태 확인

```powershell
# 노드 상태 확인 (Ready가 나와야 정상)
kubectl get nodes
```

**정상 출력 예시**

```
NAME             STATUS   ROLES           AGE   VERSION
docker-desktop   Ready    control-plane   5m    v1.29.x
```

---

## 5-3. 시스템 Pod 조회

```powershell
# 전체 네임스페이스의 Pod 조회
kubectl get pods -A
```

**정상 출력 예시** (kube-system 네임스페이스의 핵심 Pod들이 Running 상태여야 한다)

```
NAMESPACE     NAME                                   READY   STATUS    RESTARTS
kube-system   coredns-xxx                            1/1     Running   0
kube-system   etcd-docker-desktop                    1/1     Running   0
kube-system   kube-apiserver-docker-desktop          1/1     Running   0
kube-system   kube-scheduler-docker-desktop          1/1     Running   0
...
```

---

## 5-4. 현재 컨텍스트 확인

```powershell
# 현재 연결된 Kubernetes 클러스터 확인
kubectl config current-context
```

**예상 출력**

```
docker-desktop
```

---

## 5-5. k9s 설치 및 실행 (권장)

> k9s는 Kubernetes 클러스터를 터미널 UI로 실시간 모니터링하는 도구다.
> `kubectl` 명령을 일일이 입력하지 않아도 Pod, 로그, 이벤트를 한 화면에서 확인할 수 있다.

### 설치 (winget)

```powershell
winget install derailed.k9s
```

> 설치 후 터미널을 **새로 열어야** `k9s` 명령이 인식된다.

### 설치 확인

```powershell
k9s version
```

### k9s 실행

```powershell
k9s
```

실행하면 터미널 전체 화면으로 클러스터 상태가 표시된다.

**주요 단축키**

| 키 | 동작 |
|---|---|
| `0` | 전체 네임스페이스 표시 |
| `:pod` | Pod 목록으로 이동 |
| `:node` | Node 목록으로 이동 |
| `l` | 선택한 Pod의 로그 보기 |
| `d` | 선택한 리소스 상세 정보 (describe) |
| `ctrl+c` | k9s 종료 |

> 💡 2일차 이후 Pod 배포·로그 확인 시 k9s를 주로 활용하게 된다.
> k9s가 설치되어 있다면 kube-system Pod들이 Running 상태인지 화면으로 확인한다. 설치가 어렵다면 `kubectl get pods -A`로 대체한다.

---

## 5-6. Kubernetes 핵심 개념 미리보기

| 개념 | 설명 | 2일차에서 다루는 내용 |
|---|---|---|
| **Node** | 컨테이너가 실행되는 서버 (여기서는 로컬 PC) | 노드 조회 |
| **Pod** | 컨테이너의 실행 단위 | Spring/PostgreSQL Pod 배포 |
| **Deployment** | Pod를 관리하는 상위 리소스 | 앱 배포 관리 |
| **Service** | Pod를 네트워크로 노출 | 외부 접근 설정 |
| **Namespace** | 리소스 격리 단위 | `todo-app` 네임스페이스 생성 |

---

## ✅ 모듈 5 완료 기준

- [ ] Docker Desktop에서 Kubernetes가 활성화되었다
- [ ] `kubectl get nodes`에서 `docker-desktop` 노드가 `Ready` 상태다
- [ ] `kubectl get pods -A`에서 시스템 Pod들이 `Running` 상태다
- [ ] `kubectl config current-context`가 `docker-desktop`을 반환한다
- [ ] k9s 또는 kubectl 명령으로 클러스터 상태를 확인할 수 있다

---

[← 모듈 4](./module4.md) | [← 목차로 돌아가기](./README.md) | [모듈 6 →](./module6.md)
