# 모듈 1 — Kubernetes 기본 구조 이해

> **목표**: "현재 클러스터에 무엇이 떠 있는가"를 확인하는 관찰 중심으로 운영한다.  
> 이론 나열보다 실제 리소스 조회 명령으로 Kubernetes 구조를 체감한다.

---

## 1-1. 클러스터 상태 점검

```powershell
# 노드 상태 확인
kubectl get nodes

# 전체 네임스페이스 Pod 조회
kubectl get pods -A

# 현재 컨텍스트 확인
kubectl config current-context
```

**정상 출력 예시**

```
NAME             STATUS   ROLES           AGE
docker-desktop   Ready    control-plane   1d
```

---

## 1-2. k9s로 클러스터 전체 상태 확인

```powershell
k9s
```

- `0` 키: 전체 네임스페이스 표시
- `:pod` 입력: Pod 목록 확인
- `:node` 입력: Node 상태 확인
- `ctrl+c`: 종료

---

## 1-3. 실습용 Namespace 생성

```powershell
# namespace.yml 적용
kubectl apply -f k8s/namespace.yml

# 생성 확인
kubectl get namespaces
```

**namespace.yml 내용 확인** (Antigravity IDE에서 `day2/k8s/namespace.yml` 열기)

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: todo
```

이후 모든 실습은 `todo` 네임스페이스에서 진행한다.

```powershell
# 기본 네임스페이스를 todo로 설정 (선택)
kubectl config set-context --current --namespace=todo
```

---

## 1-4. Kubernetes 핵심 리소스 개념 정리

| 리소스 | 역할 | 이번 실습 활용 |
|---|---|---|
| **Namespace** | 리소스 격리 단위 | `todo` 네임스페이스 |
| **Pod** | 컨테이너 실행 단위 | Spring / PostgreSQL |
| **Deployment** | Pod 생명주기 관리 | 앱·DB 배포 |
| **Service** | Pod 네트워크 노출 | ClusterIP / NodePort |
| **ConfigMap** | 일반 설정값 외부화 | DB 호스트, 포트 |
| **Secret** | 민감 정보 외부화 | DB 비밀번호 |
| **PVC** | 영구 볼륨 요청 | PostgreSQL 데이터 |

---

## ✅ 모듈 1 완료 기준

- [ ] `kubectl get nodes`에서 `docker-desktop`이 `Ready` 상태다
- [ ] `todo` 네임스페이스가 생성되었다
- [ ] k9s에서 클러스터 전체 Pod 상태가 확인된다

---

[← 목차로 돌아가기](./README.md) | [모듈 2 →](./module2.md)
