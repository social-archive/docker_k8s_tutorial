# 모듈 4 — Helm 기본과 차트 구조

> **목표**: Kubernetes YAML을 반복·재사용하기 어렵다는 문제를 Helm chart로 해결하는 구조를 이해한다.
> 앱 Deployment의 이미지 태그·replicas·resources를 `values.yaml`로 분리하고,
> `helm template`으로 렌더링 결과를 확인한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트 `docker_k8s_tutorial/` 기준입니다.

## 4-1. Helm이 필요한 이유

지금까지 `day3/k8s-day3-gitops/app-deployment.yml`에서 이미지 태그를 직접 수정했다.
이 방식은 간단하지만, 환경이 여러 개이거나 변경점을 추적할 기준이 모호하다.

| 방식 | 특징 | 단점 |
|---|---|---|
| raw manifest | YAML 그대로 관리 | 환경별 복사본이 늘어남 |
| Helm chart | 템플릿 + values 분리 | 구조 파악에 초기 학습 필요 |

Helm은 Kubernetes 리소스를 재사용 가능한 **chart(패키지)**로 관리하는 도구다.

> 이번 모듈에서는 전체 스택을 새로 설치하지 않고, **앱 Deployment만 Helm chart로 패키징**한다.
> Service, ConfigMap, Secret, PostgreSQL은 2일차 실습 결과를 그대로 사용한다.

---

## 4-2. Helm CLI 설치 확인

```powershell
helm version
```

**설치가 안 되어 있으면**

```powershell
# winget 설치 (권장)
winget install Helm.Helm

# 설치 후 터미널 새로 열기
helm version
```

---

## 4-3. Helm chart 구조 확인

Antigravity IDE 또는 사용 중인 IDE에서 `day3/k8s-day3-gitops/helm/` 디렉토리를 확인한다.

```
day3/k8s-day3-gitops/helm/
├── Chart.yaml               ← chart 이름, 버전 정보
├── values.yaml              ← 변경 가능한 기본값 모음
└── templates/
    └── deployment.yaml      ← {{ .Values.* }} 표현식을 사용하는 Deployment 템플릿
```

---

## 4-4. Chart.yaml 확인

`day3/k8s-day3-gitops/helm/Chart.yaml` 내용:

```yaml
apiVersion: v2
name: todo-app
description: Spring Boot Todo 애플리케이션 Helm chart (실습용)
type: application
version: 0.1.0
appVersion: "1.0"
```

| 필드 | 의미 |
|---|---|
| `name` | chart 이름 |
| `version` | chart 자체 버전 (values 구조 변경 시 올림) |
| `appVersion` | 배포 대상 앱의 버전 (참고용) |

---

## 4-5. values.yaml 확인

`day3/k8s-day3-gitops/helm/values.yaml` 내용:

```yaml
image:
  repository: ghcr.io/YOUR_GITHUB_USERNAME/todo-app
  tag: "replace-with-short-sha"
  pullPolicy: Always

replicaCount: 1

resources:
  requests:
    cpu: "250m"
    memory: "256Mi"
  limits:
    cpu: "500m"
    memory: "512Mi"

configMapName: app-config
secretName: db-secret
```

> 💡 이 파일에서 `image.tag`만 변경하면 모듈 3과 동일한 "이미지 태그 반영"이 이루어진다.
> raw manifest처럼 `image:` 줄을 직접 찾아 수정할 필요가 없다.

---

## 4-6. values.yaml — 이미지 태그 반영

Antigravity IDE 또는 사용 중인 IDE에서 `day3/k8s-day3-gitops/helm/values.yaml`을 열어
모듈 3에서 확인한 7자리 short SHA 태그와 본인 GitHub 계정명으로 수정한다.

```yaml
image:
  repository: ghcr.io/<본인 GitHub 계정명>/todo-app
  tag: "<7자리-short-sha>"
```

> 핵심 비교: 모듈 3에서는 `app-deployment.yml`의 `image:` 한 줄을 직접 바꿨다.
> Helm에서는 `values.yaml`만 바꾸고, 실제 Deployment YAML은 템플릿에서 생성된다.

---

## 4-7. templates/deployment.yaml 확인

`day3/k8s-day3-gitops/helm/templates/deployment.yaml` 핵심 부분:

```yaml
spec:
  replicas: {{ .Values.replicaCount }}       # values.replicaCount 참조
  template:
    spec:
      containers:
        - name: todo-app
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          envFrom:
            - configMapRef:
                name: {{ .Values.configMapName }}
          env:
            - name: DB_USER
              valueFrom:
                secretKeyRef:
                  name: {{ .Values.secretName }}
                  key: db-user
```

`{{ .Values.* }}` 표현식이 `values.yaml`의 값을 읽어와 YAML을 완성한다.

---

## 4-8. helm template으로 렌더링 결과 확인

`helm template` 명령은 chart를 실제 클러스터에 배포하지 않고,
values를 반영한 최종 Kubernetes YAML만 출력한다.

> 실행 위치: 저장소 루트 `docker_k8s_tutorial/` 에서 실행한다.
> (모듈 상단의 실행 위치 안내와 동일)

```powershell
# 저장소 루트에서 실행
helm template todo-app day3/k8s-day3-gitops/helm
```

**출력 예시**

```yaml
---
# Source: todo-app/templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: todo-app
  namespace: todo-app
spec:
  replicas: 1
  template:
    spec:
      containers:
        - name: todo-app
          image: "ghcr.io/<계정>/todo-app:<sha>"
```

이미지 태그만 빠르게 확인하려면:

```powershell
helm template todo-app day3/k8s-day3-gitops/helm | findstr image:
```

> 💡 출력된 YAML이 `day3/k8s-day3-gitops/app-deployment.yml`과 구조적으로 동일하다.
> 차이는 수정 위치다. raw manifest는 `image:` 줄을 직접 찾지만,
> Helm은 `values.yaml`의 `image.tag` 한 줄만 바꾸면 된다.

---

## 4-9. raw manifest vs Helm 비교

| 항목 | raw manifest (`day3/k8s/`) | Helm chart (`day3/k8s-day3-gitops/helm/`) |
|---|---|---|
| 이미지 태그 변경 위치 | `app-deployment.yml` 직접 수정 | `values.yaml`의 `image.tag` 수정 |
| replicas 변경 위치 | `app-deployment.yml` 직접 수정 | `values.yaml`의 `replicaCount` 수정 |
| 환경별 설정 분리 | 파일 복사 | values 파일 분리 (`values-prod.yaml`) |
| Argo CD 연동 | Directory source | Helm source (path + values) |

---

## 4-10. 변경사항 commit / push

values.yaml을 수정했으면 commit/push해서 Git 이력을 남긴다.

```powershell
git add day3/k8s-day3-gitops/helm/values.yaml
git commit -m "helm: update image tag in values"
git push origin main
```

---

## ✅ 모듈 4 완료 기준

- [ ] `helm version` 명령이 정상 출력된다
- [ ] `day3/k8s-day3-gitops/helm/` 구조(`Chart.yaml`, `values.yaml`, `templates/`)를 설명할 수 있다
- [ ] `values.yaml`의 `image.repository`와 `image.tag`를 본인 정보로 수정했다
- [ ] `helm template todo-app day3/k8s-day3-gitops/helm` 결과에서 수정한 이미지 태그가 보인다
- [ ] raw manifest 수정 방식과 Helm values 수정 방식의 차이를 설명할 수 있다

---

[← 모듈 3](./module3.md) | [← 목차로 돌아가기](./README.md) | [모듈 5 →](./module5.md)
