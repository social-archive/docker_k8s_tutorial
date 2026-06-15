# 2일차 실습 가이드 — Kubernetes 배포

> **핵심 메시지**: 2일차는 컨테이너 실행에서 끝나지 않고,  
> 쿠버네티스 리소스로 애플리케이션을 배포하고 설정을 분리하며  
> 서비스 상태를 점검하는 날이다.

---

## 학습 목표

- Docker Desktop Kubernetes 환경에서 기본 리소스를 조회하고 배포 흐름을 이해할 수 있다.
- Spring 애플리케이션과 PostgreSQL을 Deployment와 Service로 배포할 수 있다.
- ConfigMap과 Secret을 이용해 설정값과 민감정보를 분리할 수 있다.
- 배포 후 Pod, Service, 로그 상태를 점검하고 기본적인 문제를 진단할 수 있다.

---

## 모듈 구성

| 모듈 | 제목 | 파일 |
|:---:|---|---|
| 모듈 1 | Kubernetes 기본 구조 이해 | [module1.md](./module1.md) |
| 모듈 2 | PostgreSQL 쿠버네티스 배포 | [module2.md](./module2.md) |
| 모듈 3 | Spring 애플리케이션 배포 | [module3.md](./module3.md) |
| 모듈 4 | ConfigMap, Secret 기반 설정 분리 | [module4.md](./module4.md) |
| 모듈 5 | 통합 검증과 서비스 확인 | [module5.md](./module5.md) |
| 모듈 6 | 운영 관점 문제 진단 입문 | [module6.md](./module6.md) |

---

## 2일차 완료 기준

- [ ] 로컬 Kubernetes 환경에서 Spring 앱과 PostgreSQL을 각각 배포할 수 있다
- [ ] Deployment, Service, ConfigMap, Secret의 역할을 구분 설명할 수 있다
- [ ] 배포 후 상태 확인 명령으로 기본적인 문제를 진단할 수 있다
- [ ] 3일차 GitOps 실습으로 이어질 수 있는 매니페스트 구조를 확보한다
