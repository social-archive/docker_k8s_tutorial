# 1일차 실습 가이드 — Docker / Docker Compose

> **핵심 메시지**: 1일차는 컨테이너를 "설명"하는 날이 아니라,  
> Spring 애플리케이션과 PostgreSQL을 직접 컨테이너화하고 실행해보며  
> 이후 Kubernetes 실습의 기반을 만드는 날이다.

---

## 학습 목표

- Docker Desktop 기반의 로컬 컨테이너 실습 환경을 준비할 수 있다.
- Dockerfile을 이용해 Spring 애플리케이션 이미지를 생성할 수 있다.
- Docker Compose로 Spring 애플리케이션과 PostgreSQL을 함께 실행할 수 있다.
- Docker Desktop의 Kubernetes 기능을 활성화해 2일차 실습 준비를 완료할 수 있다.

---

## 모듈 구성

| 모듈 | 제목 | 파일 |
|:---:|---|---|
| 모듈 1 | Docker Desktop 실습 환경 준비 | [module1.md](./module1.md) |
| 모듈 2 | Docker 기본 개념과 컨테이너 실행 | [module2.md](./module2.md) |
| 모듈 3 | Spring 애플리케이션 컨테이너화 | [module3.md](./module3.md) |
| 모듈 4 | Docker Compose 기반 Spring + PostgreSQL | [module4.md](./module4.md) |
| 모듈 5 | Kubernetes 개요와 로컬 준비 | [module5.md](./module5.md) |
| 모듈 6 | 운영 관점 점검과 2일차 연결 | [module6.md](./module6.md) |

---

## 1일차 완료 기준

- [ ] Docker Desktop이 정상 동작한다
- [ ] Spring 애플리케이션 이미지를 직접 빌드할 수 있다
- [ ] Docker Compose로 Spring + PostgreSQL 통합 실행이 가능하다
- [ ] `kubectl get nodes`에서 `docker-desktop` 노드가 `Ready` 상태다
