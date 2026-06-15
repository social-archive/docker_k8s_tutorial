# [Day 3] 이론 강의: GitHub Actions CI 구성

> 💡 **쉽게 이해하는 비유 (Analogy Box)**
> - **자동 부품 조립 및 출고용 컨베이어 벨트**
>   - 수동 빌드 방식은 부품(소스 코드)을 깎아 조립할 때마다 작업자가 직접 시험대에 올려 망치질로 부하 검사를 하고, 도색(컴파일)을 거친 뒤, 박스 포장(도커 이미지화)을 해서 창고(Registry)까지 수레로 낑낑대며 날라다 쌓는 것과 같습니다. 퇴근 시간 즈음 바빠지면 검사 단계(테스트 코드 실행)를 슬쩍 빼먹거나, 덜 굳은 페인트 상태로 포장해 불량품(버그 덩어리 이미지)을 버젓이 출고하여 큰 사고를 칩니다.
>   - **GitHub Actions**는 소스 보관함(Git)에 코드를 집어넣자마자 윙윙 소리를 내며 전원이 켜지는 **자동 정밀 컨베이어 벨트**입니다. 검사 센서가 물건을 감지하여 불량 여부를 알아서 사전 스캔(`Test`)하고, 자동으로 상자 조립 및 포장(`Build & Dockerize`)을 수행한 후, 안전하게 이미지 보관 창고(**GHCR**) 선반에 한 치의 오차도 없이 예쁘게 자동 입고시켜 줍니다.

---

## 1. 없으면 어떤 점이 불편한가?

개발자가 작성한 코드를 중앙 공통 브랜치에 통합하고 릴리스 이미지를 배포 버전으로 출고하는 전 공정에서, 지속적인 자동화 도구가 결여되어 있으면 다음과 같은 불안정과 수작업 비효율이 강제됩니다.

* **테스트 생략 및 검증되지 않은 불량 코드 배포 (Human Error)**
  - 촉박한 마감 시간이나 핫픽스 패치 도중, 개발자는 로컬에서 테스트 코드 실행 명령어(`./gradlew test`)를 수행하는 과정을 "설마 코드 3줄 바꿨는데 문제 있겠어?"라며 미필적 고의로 건너뛰기 일쑤입니다.
  - 이처럼 검증되지 않고 push된 소스가 실제 상용 서버로 즉시 밀려 들어가, 기동 즉시 Spring Context 로딩 예외나 빈(Bean) 등록 에러를 뿜으며 전체 서버를 죽게 만드는 대형 장애로 이어집니다.
* **무겁고 비효율적인 로컬 빌드로 인한 코딩 지연**
  - 자바 애플리케이션 컴파일 빌드 및 도커 이미지를 로컬 PC에서 직접 구워내는 연산은 CPU 코어를 100% 한계치까지 밀어붙이고 대량의 메모리(RAM)를 고정 점유합니다.
  - 이미지 패키징이 수행되는 수 분 동안 로컬 PC가 느려져 일체의 다른 개발 코딩이나 웹 서핑 작업을 마비시키고 개발자의 업무 집중력을 산산이 분쇄합니다.

---

## 2. 왜 필요할까?

작성된 소스 코드의 컴파일 무결성과 단위 테스트 성공 여부를 판정하는 **지속적 통합(CI, Continuous Integration) 파이프라인 공정이 격리된 자동화 인프라와 결합되어 있지 않기** 때문입니다.

이를 완벽하게 해결하고 개발 주기를 극대화하려면 다음과 같은 인프라적 제약 조건이 설정되어야 합니다.
1. **일회성 격리 빌드 환경 (Ephemeral Runner)**: 개발자 개인 PC의 운영체제 및 설정 파편화에 의존하지 않고, 항상 동일한 초기화 스펙을 제공하는 독립된 가상 머신(VM) 위에서 깨끗하게 빌드를 수행해야 합니다.
2. **배포 게이트 가드 자동화**: 코드가 Git 저장소의 특정 브랜치에 병합(Merge)되는 즉시, 시스템이 사람의 개입 없이 스스로 트리거되어 모든 단위 테스트 코드가 성공(Green Sign)했음을 검증해야만 비로소 배포용 가상 컨테이너 이미지를 조립 및 패키징하도록 공정을 엄격하게 강제화해야 합니다.

---

## 3. 이것은 무엇인가?

> **핵심 한 줄 요약**:
> *"GitHub Actions는 **코드가 커밋 푸시되는 이벤트를 감지하여 일회성 클라우드 러너를 즉시 프로비저닝**하고, 정의된 **공정 흐름(Workflow)에 따라 빌드 및 검증을 완료한 후 GHCR 패키지 창고에 입고하는 완전 자동화 파이프라인 엔진**이다."*

<details>
<summary><b>🔍 1회용 청정 장비: Ephemeral VM Runner 가상 환경의 멱등성 보장 원리</b></summary>

GitHub Actions가 작업을 실행할 때 선언하는 `runs-on: ubuntu-latest`는 깃허브가 클라우드(Microsoft Azure 인프라)상에서 실시간으로 대리 생성해 빌려주는 **일회성 Ephemeral VM(가상 머신)**입니다.
- **멱등 보장**:
  - 이 러너 장비는 오직 해당 배포 워크플로우(Workflow)만을 위해 즉석에서 할당됩니다.
  - 빌드 공정이 완전히 성공하거나 실패하여 끝나는 즉시, 깃허브 클라우드는 해당 VM 전체를 물리적으로 즉각 삭제(Tear-down/Destroy)하여 리셋합니다.
  - 이 덕분에 이전 빌드 과정에서 다운로드했던 악성 찌꺼기 파일이나 변조된 환경 변수가 다음 개발자의 빌드 공정에 어떠한 영향도 미칠 수 없는 **완벽한 청정 멱등성 환경**을 얻게 됩니다.
</details>

<details>
<summary><b>🔍 보안 정보 봉인 해제 방지: Secrets 마스킹(Masking) 및 GITHUB_TOKEN 아키텍처</b></summary>

* **Secrets 마스킹 (Masking) 기술**:
  - 워크플로우 실행 도중 데이터베이스 비밀번호나 외부 API 토큰이 가상 콘솔 로그 화면에 평문 노출되는 것을 차단하기 위한 깃허브의 보안 기술입니다.
  - 러너 엔진은 `secrets.YOUR_PASSWORD`로 등록된 특수 키의 값을 메모리에 로드한 뒤, 러너 표준 출력(`stdout`)으로 뿜어지는 모든 문자열 스트림을 실시간 감시합니다. 
  - 등록된 보안 단어가 발견되는 즉시 강제적으로 **`***` 문자**로 실시간 마스킹 치환 처리하여 개발자의 실수에 의한 암호 텍스트 화면 노출을 원천 철통 방어합니다.
* **임시 자격 증명 GITHUB_TOKEN**:
  - 워크플로우 기동 시마다 깃허브는 해당 저장소 전용의 1회용 임시 토큰인 `secrets.GITHUB_TOKEN`을 자동 신설해 러너에 주입합니다.
  - 이 토큰은 컨테이너 레지스트리(GHCR)에 이미지를 쓰고 읽을 수 있는 권한을 한시적으로 위임받아 실행된 뒤, 워크플로우 종료 시 즉시 폐기되어 자격증명 유출 위협을 차단합니다.
</details>

<details>
<summary><b>🔍 속도 저하 극복: actions/cache 플러그인과 Gradle 라이브러리 해시 캐싱</b></summary>

매 빌드마다 깃허브가 새 가상 컴퓨터(Runner)를 빌려주므로, 스프링 부트 빌드 시 필수적인 수백 메가바이트 크기의 외부 오픈소스 라이브러리 파일(JAR)들을 매번 메이븐 센트럴 저장소에서 다운로드받아야 하는 네트워크 낭비 및 지연(5~10분 소요)이 불가피합니다.
- **해결책 (`actions/cache`)**:
  - 프로젝트 내의 의존성 명세 파일인 `build.gradle`과 `gradle-wrapper.properties` 파일의 내용 전체를 해시 암호화하여 고유 캐시 키(예: `gradle-cache-sha256-abcdef123...`)를 산출합니다.
  - 만약 의존성 변경이 없어 해시 키가 이전 빌드와 일치(`Cache Hit`)하면, 깃허브 캐시 서버에 압축 보관되어 있던 기존 라이브러리 폴더(`.gradle/caches`)를 단 5초 만에 러너 디스크로 자동 압축 해제 로드해 줍니다. 
  - 이 캐싱 시스템 덕분에 라이브러리 다운로드 공정이 통째로 생략되어 빌드 성능이 최대 80% 이상 개선됩니다.
</details>

### 📊 GitHub Actions CI 가상 러너 내부의 빌드 공정 및 캐시 복구 메커니즘

```mermaid
flowchart TD
    subgraph GitHub_Cloud_Service [GitHub 클라우드 저장소]
        Code_Git["소스 코드"]
        Cache_Store[("깃허브 캐시 보관소 <br> key: gradle-cache-hash")]
    end

    subgraph Ephemeral_Runner_VM [일회성 가상 머신 Runner (ubuntu-latest)]
        Checkout["1. actions/checkout <br> (소스 복사)"]
        Cache_Restore["2. actions/cache <br> (의존성 캐시 복구 시도)"]
        setup_java["3. actions/setup-java <br> (JDK 환경 설정)"]
        gradle_build["4. ./gradlew build <br> (컴파일 및 테스트)"]
        docker_push["5. Docker Build & Push <br> (GHCR 인증 및 업로드)"]

        Checkout --> Cache_Restore
        Cache_Restore --> setup_java
        setup_java --> gradle_build
        gradle_build --> docker_push
    end

    Cache_Store -.-> |"Cache Hit! <br> 기존 JAR 라이브러리 5초 내 압축 해제"| Cache_Restore
    docker_push ==> |"6. 컨테이너 이미지 입고"| GHCR["ghcr.io/계정/todo-app:7자리SHA"]
```

---

## 4. 장점과 단점

### 1) 장점
* **자동화된 배포 게이트 가딩을 통한 안정성 확보**
  - 개발자가 작성한 테스트 코드가 100% 통과(Green Sign)해야만 도커 이미지가 완성되므로 불량 바이너리가 배포되어 클러스터를 붕괴시키는 휴먼 에러를 완전 소멸시킵니다.
* **로컬 개발 장비 하드웨어 보호**
  - 컴파일 및 테스트 구동에 소요되는 무거운 디스크 I/O와 CPU 멀티스레드 연산 오버헤드를 클라우드 가상 러너에 전부 위임하여 쾌적한 로컬 개발 속도를 유지합니다.

### 2) 단점
* **자주 변경되는 라이브러리로 인한 캐시 미스 오버헤드**
  - 개발 중 `build.gradle`에 의존성 라이브러리를 추가하거나 변경할 때마다 캐시 키 해시 값이 깨집니다. 
  - 이 경우 어쩔 수 없이 수백 메가바이트의 라이브러리들을 처음부터 다시 풀(Pull) 다운로드해야 하므로 해당 빌드 사이클이 일시적으로 늘어나는 대기 오버헤드가 동반됩니다.

---

## 5. 어떻게 쓰는가?

의존성 캐싱 최적화 기술과 GitHub Container Registry(GHCR) 로그인 및 푸시 공정이 완전 자동화된 실무형 CI 워크플로우 YAML 파일 명세입니다.

### 1) 실무형 `.github/workflows/ci.yml` 스크립트 예시
```yaml
name: CI - Spring Boot Build and Containerize

on:
  push:
    branches: [ main ]  # main 브랜치에 코드가 push/merge 되면 트리거

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write  # GHCR 이미지 업로드를 위해 패키지 쓰기 권한 활성화

    steps:
      # 1단계: 러너에 소스 코드 다운로드
      - name: Checkout Source Code
        uses: actions/checkout@v4

      # 2단계: JDK 17 개발 환경 조립
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      # 3단계: actions/cache를 이용한 Gradle 라이브러리 초고속 복구
      - name: Cache Gradle Packages
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      # 4단계: 테스트 및 jar 빌드 수행
      - name: Run Build and Test
        run: |
          cd spring-app
          chmod +x gradlew
          ./gradlew build --no-daemon

      # 5단계: GitHub 컨테이너 레지스트리(GHCR) 자동 로그인
      # (1회용 GITHUB_TOKEN을 사용하여 별도의 ID/PW 설정 없이 보안 인증을 수행합니다)
      - name: Log in to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      # 6단계: 도커 이미지 빌드 및 GHCR 푸시 (태그는 git 7자리 short SHA 적용)
      - name: Build and Push Docker Image
        uses: docker/build-push-action@v5
        with:
          context: ./spring-app
          push: true
          tags: |
            ghcr.io/${{ github.repository }}:${{ github.sha }}
            ghcr.io/${{ github.repository }}:latest
```
