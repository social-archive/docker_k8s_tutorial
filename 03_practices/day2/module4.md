# 모듈 4 — ConfigMap, Secret 기반 설정 분리

> **목표**: 애플리케이션 설정과 민감정보를 이미지에 포함시키지 않고
> ConfigMap과 Secret으로 외부화한다.
> 설정 분리 후에도 앱이 정상 기동되는 것을 확인한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd day2`로 이동한 `day2/` 디렉터리 기준입니다.

## 4-1. 관련 파일 열기

Antigravity IDE 또는 사용 중인 IDE에서 다음 파일들을 확인한다.

```
day2/k8s/
├── app-configmap.yml   ← DB 호스트, 포트, DB명 (일반 설정)
├── app-secret.yml      ← `db-secret` Secret 리소스: DB 계정, 비밀번호 (민감 정보)
└── app-deployment.yml  ← ConfigMap/Secret을 환경변수로 주입
```

---

## 4-2. ConfigMap 이해

ConfigMap은 **민감하지 않은 설정값**을 외부에서 주입하는 리소스다. module3에서 이미 적용했지만, 여기서는 내용을 다시 확인하고 재적용이 안전하게 동작하는지 확인한다.

```powershell
kubectl apply -f k8s/app-configmap.yml -n todo-app

# 생성 확인
kubectl get configmap -n todo-app
kubectl describe configmap app-config -n todo-app
```

**app-configmap.yml 주요 내용**

```yaml
data:
  DB_HOST: postgres      # 서비스 이름으로 접근
  DB_PORT: "5432"
  DB_NAME: tododb
```

---

## 4-3. Secret 이해

Secret은 **민감한 정보**를 Base64로 인코딩해 저장하는 리소스다. module2에서 이미 적용했지만, 여기서는 실제 리소스명과 키를 다시 확인한다.

```powershell
kubectl apply -f k8s/app-secret.yml -n todo-app

# 생성 확인
kubectl get secret -n todo-app
kubectl describe secret db-secret -n todo-app
```

> ⚠️ `describe`로는 값이 보이지 않는다. 이것이 Secret의 핵심이다.
> 실제 값을 확인하려면:
> ```powershell
> kubectl get secret db-secret -n todo-app -o jsonpath="{.data.db-password}" | %{[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))}
> ```

---

## 4-4. Deployment에 ConfigMap/Secret 주입

Antigravity IDE 또는 사용 중인 IDE에서 `app-deployment.yml`을 열고 환경변수 주입 구조를 확인한다.

```yaml
env:
  - name: DB_HOST
    valueFrom:
      configMapKeyRef:
        name: app-config
        key: DB_HOST
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: db-password
```

설정 확인 후 Deployment를 재적용해도 같은 선언형 상태로 유지되는지 확인한다:

```powershell
kubectl apply -f k8s/app-deployment.yml -n todo-app

# Pod 재기동 상태 확인
kubectl rollout status deployment/todo-app -n todo-app
```

---

## 4-5. 설정 분리 후 동작 확인

```powershell
# API 호출로 정상 동작 확인
curl.exe http://localhost:30080/todos
curl.exe http://localhost:30080/actuator/health
```

---

## 4-6. ConfigMap vs Secret 비교

| 구분 | ConfigMap | Secret |
|---|---|---|
| 용도 | 일반 설정값 | 민감 정보 |
| 저장 방식 | 평문 | Base64 인코딩 |
| `describe` 시 값 노출 | ✅ 보임 | ❌ 안 보임 |
| 예시 | DB 호스트, 포트 | DB 비밀번호, API 키 |

---

## ✅ 모듈 4 완료 기준

- [ ] ConfigMap과 Secret이 `todo-app` 네임스페이스에 존재한다
- [ ] 설정 분리 후 Deployment가 정상 재기동된다
- [ ] `http://localhost:30080/todos` API 응답이 여전히 정상이다

---

[← 모듈 3](./module3.md) | [← 목차로 돌아가기](./README.md) | [모듈 5 →](./module5.md)
