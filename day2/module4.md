# 모듈 4 — ConfigMap, Secret 기반 설정 분리

> **목표**: 애플리케이션 설정과 민감정보를 이미지에 포함시키지 않고  
> ConfigMap과 Secret으로 외부화한다.  
> 설정 분리 후에도 앱이 정상 기동되는 것을 확인한다.

---

## 4-1. 관련 파일 열기

Antigravity IDE에서 다음 파일들을 확인한다.

```
day2/k8s/
├── app-configmap.yml   ← DB 호스트, 포트, DB명 (일반 설정)
├── app-secret.yml      ← DB 계정, 비밀번호 (민감 정보)
└── app-deployment.yml  ← ConfigMap/Secret을 환경변수로 주입
```

---

## 4-2. ConfigMap 이해

ConfigMap은 **민감하지 않은 설정값**을 외부에서 주입하는 리소스다.

```powershell
kubectl apply -f k8s/app-configmap.yml -n todo

# 생성 확인
kubectl get configmap -n todo
kubectl describe configmap app-config -n todo
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

Secret은 **민감한 정보**를 Base64로 인코딩해 저장하는 리소스다.

```powershell
kubectl apply -f k8s/app-secret.yml -n todo

# 생성 확인
kubectl get secret -n todo
kubectl describe secret app-secret -n todo
```

> ⚠️ `describe`로는 값이 보이지 않는다. 이것이 Secret의 핵심이다.  
> 실제 값을 확인하려면:
> ```powershell
> kubectl get secret app-secret -n todo -o jsonpath="{.data.DB_PASSWORD}" | %{[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))}
> ```

---

## 4-4. Deployment에 ConfigMap/Secret 주입

Antigravity IDE에서 `app-deployment.yml`을 열고 환경변수 주입 구조를 확인한다.

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
        name: app-secret
        key: DB_PASSWORD
```

설정 분리 적용 후 Deployment 재배포:

```powershell
kubectl apply -f k8s/app-deployment.yml -n todo

# Pod 재기동 상태 확인
kubectl rollout status deployment/todo-app -n todo
```

---

## 4-5. 설정 분리 후 동작 확인

```powershell
# API 호출로 정상 동작 확인
curl http://localhost:30080/todos
curl http://localhost:30080/actuator/health
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

- [ ] ConfigMap과 Secret이 `todo` 네임스페이스에 생성되었다
- [ ] 설정 분리 후 Deployment가 정상 재기동된다
- [ ] `http://localhost:30080/todos` API 응답이 여전히 정상이다

---

[← 모듈 3](./module3.md) | [← 목차로 돌아가기](./README.md) | [모듈 5 →](./module5.md)
