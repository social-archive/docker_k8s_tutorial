# 모듈 2 — Docker 기본 개념과 컨테이너 실행

> **목표**: 이미지와 컨테이너의 차이, 포트 매핑, 볼륨 개념을 명령 실행 결과로 체감한다.
> 개념 설명은 최소화하고 실행 결과를 통해 동작 원리를 확인하는 데 집중한다.

---

> 모든 명령은 Windows PowerShell 기준입니다.
> 파일 편집은 Antigravity IDE를 권장하며, VS Code 또는 IntelliJ IDEA를 사용해도 됩니다.
> 실행 위치는 저장소 루트에서 `cd workspace`로 이동한 `day1/` 디렉터리 기준입니다.

## 2-1. nginx 컨테이너 실행

```powershell
# nginx 이미지를 pull하고 컨테이너 생성 + 실행 (백그라운드)
docker run -d -p 9090:80 --name test-nginx nginx
```

옵션 설명:

| 옵션 | 의미 |
|---|---|
| `-d` | 백그라운드(detach) 실행 |
| `-p 9090:80` | 로컬 9090 포트 → 컨테이너 80 포트 매핑 |
| `--name test-nginx` | 컨테이너 이름 지정 |

---

## 2-2. 실행 상태 확인

```powershell
# 실행 중인 컨테이너 목록
docker ps
```

**정상 출력 예시**

```
CONTAINER ID   IMAGE   COMMAND                  CREATED        STATUS        PORTS                  NAMES
xxxxxxxxxxxx   nginx   "/docker-entrypoint.…"   5 seconds ago  Up 4 seconds  0.0.0.0:9090->80/tcp   test-nginx
```

---

## 2-3. 브라우저 접속 확인

브라우저에서 아래 URL을 열어 nginx 기본 페이지가 보이는지 확인한다.

```
http://localhost:9090
```

---

## 2-4. 컨테이너 로그 확인

```powershell
docker logs test-nginx
```

---

## 2-5. 이미지 목록 확인

```powershell
# 로컬에 있는 Docker 이미지 목록
docker images
```

---

## 2-6. 컨테이너 내부 접속

```powershell
# 컨테이너 내부 bash 접속
docker exec -it test-nginx bash

# 내부에서 확인 가능한 명령 예시
ls /etc/nginx/
cat /etc/nginx/nginx.conf
exit
```

---

## 2-7. 컨테이너 중지 및 삭제

```powershell
# 컨테이너 중지
docker stop test-nginx

# 컨테이너 삭제
docker rm test-nginx

# 이미지 삭제 (선택)
# docker rmi nginx
```

> 💡 `docker rm -f test-nginx` 하나로 강제 중지 + 삭제를 한 번에 할 수 있다.

---

## ✅ 모듈 2 완료 기준

- [ ] `docker run` 명령으로 nginx 컨테이너가 정상 실행된다
- [ ] `http://localhost:9090`에서 nginx 기본 페이지가 보인다
- [ ] `docker stop` / `docker rm`으로 컨테이너를 정리할 수 있다

---

[← 모듈 1](./module1.md) | [← 목차로 돌아가기](./README.md) | [모듈 3 →](./module3.md)
