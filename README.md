# 오픈소스 서포터 (OpenSource Supporter)
> GitHub API와 LLM 기반 분석을 통해 오픈소스 기여자와 후원자를 연결하는 후원 플랫폼

## 프로젝트 요약
- **한 줄 소개**: GitHub API 연동과 LLM분석을 활용하여 정성적 / 정량적으로 오픈소스의 가치를 평가하고 직접적인 후원을 가능케하는 서비스
- **핵심 성과**
    - Spring Boot 기반의 서버를 구축하여 GitHub OAuth 및 실시간 레포지토리 분석 구현
    - LLM 기반 정량적/정성적 투자 분석 시스템으로 후원 의사결정 지원
    - Docker 컨테이너화와 Nginx 리버스 프록시를 통한 아키텍처 구축

<a href="https://opensource-supporter.org" target="_blank" rel="noopener noreferrer">
  <img src="https://github.com/user-attachments/assets/f596fc78-a39b-4f5d-8979-56b4ea2cf400" alt="Landing Page">
</a>

## 프로젝트 개요

### 문제 인식
-  **오픈소스 생태계의 빈익빈 부익부 현상**
    - Linux, React, Flutter 등 대형 프로젝트는 Meta, Google, Linux Foundation 등 대기업 혹은 대형재단의 막대한 지원
    - 소규모 오픈소스 프로젝트들의 자원 부족과 지속가능 동기 문제
    - 개발자의 시간과 노력 대비 정당한 보상 체계 부재

### 솔루션 접근
-  **기술적 접근을 통한 직접적인 후원 플랫폼**
    - GitHub API 연동과 LLM을 통한 실시간 레포지토리 분석 및 평가
    - LLM 기반 정량적·정성적 투자 가치 분석으로 후원 의사결정 지원
    - 현금 충전 및 광고 시청을 통한 다양한 포인트 충전 방식 제공

### 개발 기간
- **개발 기간**: 2024.03 ~ 2024.06 (약 4개월)
- **팀 구성**: Backend 2명(본인: 핵심 API 및 GitHub 연동 담당), Frontend 2명

## 주요 기능

### 핵심 API
- **GitHub API**: OAuth 인증, 레포지토리 자동 동기화, 실시간 커밋 분석, README 파싱, 주간 커밋 통계, GitHub Stats 연동
- **OpenAI API**: 레포지토리 투자 가치 분석(정성적/정량적) 서비스
- **Groq API**: 최고수준의 응답 속도를 보여주는 Groq를 활용한 다국어 번역 서비스
- **자체 RESTful API**: 직접적인 포인트 충전, 티어별 후원, 실시간 랭킹 업데이트

### 사용자 기능
- **GitHub OAuth 간편 로그인**: 별도 회원가입 없이 GitHub 계정으로 즉시 이용
- **레포지토리 등록 및 관리**: 태그 기반 분류, 실시간 업데이트
- **LLM 기반 투자 분석**: 정량적 지표(Star, Commit, PR) + OpenAI 정성적 분석 제공
- **후원 뱃지 시스템**: GitHub README.md에 삽입 가능한 후원 현황 뱃지 자동 생성


## 기술 스택

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![AWS](https://img.shields.io/badge/AWS_EC2-232F3E?style=for-the-badge&logo=googlecloudstorage&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)

## 아키텍처

```
브라우저 → Nginx (Port: 80/443) → React+Vite(정적 파일)
                ↓(reverse proxy - /api/*)
  Spring Boot (Port: 8080) → MySQL (Port: 3306)
                        ↓(HTTP)
          외부 API (GitHub, OpenAI, Groq)
```

- **단일 진입점**: Nginx를 통한 모든 트래픽 라우팅(리버스 프록시 구성)
- **서비스 분리**: 프론트엔드와 백엔드, 데이터베이스로 구성한 3-Tier-Architecture
- **보안 강화**: 데이터베이스와 백엔드 서버의 직접 접근 차단(EC2 보안설정)

### 외부 API 통합 아키텍처

#### FeignClient 기반 통합 API
```java
// 동일 도메인으로 요청하는 API를 하나의 인터페이스로 관리
@FeignClient(name = "github-api", url = "https://api.github.com")
public interface GithubApiFeignClient {
    @GetMapping("/user/repos")
    Object getUserRepoItem(@RequestHeader("Authorization") String token,
                          @RequestParam("sort") String sort,
                          @RequestParam("per_page") int perPage);

    @GetMapping("/repos/{owner}/{repo}/languages")
    Object getMostLanguage(@PathVariable String owner,
                          @PathVariable String repo,
                          @RequestParam("access_token") String token);
}
```

## 프로젝트 구조

```
opensource-supporter/
├── src/main/java/me/jejunu/opensource_supporter/
│   ├── config/                     # 외부 API 설정
│   │   ├── OpenAIFeignClient.java      # OpenAI API 클라이언트
│   │   ├── GithubApiFeignClient.java   # GitHub API 클라이언트
│   │   ├── GroqFeignClient.java        # Groq API 클라이언트
│   │   └── CacheConfig.java            # Caffeine 캐시 설정
│   ├── domain/                     # Entity
│   ├── service/                    # Business Logic
│   ├── controller/                 # Api Endpoint
│   └── repository/                 # JPA
├── docker-compose.yml              # 컨테이너 오케스트레이션
└── Dockerfile                      # Spring Boot 컨테이너화
```

## 트러블 슈팅

### 문제 상황
외부 API 연동 과정에서 마주한 다양한 이슈들과 이를 해결하기 위한 다양한 접근이 백엔드 개발이 처음이었던 저에겐 난관이자 학습포인트가 되었습니다.
특히 **특정 GitHub API에서만 적용되는 낯선 인증 방식, 처음 시도하는 OAuth 플로우의 복잡성 및 GitHub Oauth만의 차별성, 그리고 대량 포인트 계산의 성능 최적화**가 주요 난제였습니다.

### 해결 과정

#### 1. GitHub API Basic Authentication 미스터리 해결

**문제**: 대다수의 GitHub API는 Bearer를 사용하지만 관리자 권한이 필요했던 특정 API는 호출 시 공식 문서에 명시되지 않은 별도의 인증 방식이 필요했습니다.

**도전 과정**
- GitHub 공식 문서에서는 `Bearer` 토큰 방식만 안내하고 있었으나, 특정 관리자 권한이 필요한 API에서는 동작하지 않음
- 수 일간의 시행착오 끝에 해외 개발자 커뮤니티 댓글에서 `Basic` 인증 힌트 발견
- `clientId:clientSecret` 형태를 Base64 인코딩하는 비표준 방식임을 확인

```java
// 해결 코드
String adminAuth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
```

**성과**
- 공식 문서의 한계를 넘어 전세계 개발자 커뮤니티에서 정보를 찾는 능력또한 중요함을(특히 영어) 체감했습니다.

#### 2. GitHub OAuth 메커니즘 이해를 위해 서버사이드 렌더링 활용

**문제**: KAKAO Developer Documentation과 같은 국내 공식 문서와 달리 GitHub REST API 방식의 OAuth 구현은 절차가 명확히 나와있지 않았습니다. 때문에 처음 ClientId를 활용해 인증 페이지를 띄우는 것 까진 성공했지만 개발 초기인 단계였기 때문에 프론트엔드와 연동을 진행하지 않아 `authorization_code` 수신이후 부터의 메커니즘을 파악할 수가 없었습니다.

**도전 과정**
- 프론트엔드와의 협업이 필수적인 OAuth 메커니즘을 이해하기 위해 임시로 Thymeleaf 도입
- 서버사이드 렌더링을 통해 `code` 파라미터가 어떻게 전달되는지 직접 mock 사이트를 제작해 확인
- 국내 문서와 달리 GitHub OAuth는 단계별 상세 설명이 부족했던 상황 극복

```java
    //BackEnd 테스트용 페이지 리다이렉션 링크
    @GetMapping("/api/auth/login/page")
    public RedirectView redirectToGithubAuthPage(){
        String codeUrl = "https://github.com/login/oauth/authorize?client_id=";
        String url = codeUrl + clientId;
        return new RedirectView(url);
    }
}
```

**성과**
- 백엔드 개발자 관점에서 OAuth 플로우를 완전히 이해하고, 프론트엔드팀과 협업할 수 있는 토대를 마련하였습니다. 또한 이후 찾아본 다양한 OAuth 제공 서비스들의 Documentation을 추가로 확인한 결과 미세한 차이가 있을 뿐 거의 메커니즘은 동일하다는 결론을 내렸고, 이를 기반으로 (주)아이디어콘서트 인턴 초창기 Vietoon 프로젝트에서 기획상의 이유로 비활성화 되어있던 4개의 OAuth 로직(Facebook, Kakao, Apple, Google)을 모두 실사용 가능하게끔 코드를 리팩터링했던 경험이 있습니다.
- GitHub Oauth를 예로 들자면 모든 서비스에서 같은 형식의 request와 response를 사용하진 않음 역시도 알았습니다(GitHub는 refresh token이 없어 로그아웃시 별도로 블랙리스트에 추가하는 API에 요청해야함)

#### 3. JPA 생명주기를 활용한 실시간 포인트 계산 최적화

**문제**: 후원 포인트 총합 계산을 위해 매번 SUM 쿼리를 실행 시 막대한 성능 저하가 발생함을 mock데이터를 삽입하고 테스트를 진행할 때 경험적으로 알았습니다.

**도전 과정**
- JPA 엔티티 생명주기 이벤트 활용으로 실시간 포인트 집계 자동화
- `@PrePersist`, `@PreUpdate` 어노테이션으로 INSERT/UPDATE 시점에 totalPoint 자동 계산
- N+1 쿼리 문제 해결: 매번 SUM 계산 대신 미리 계산된 값 조회로 성능 개선

```java
// Entity에서 구현한 코드
@PrePersist
@PreUpdate
public void updatePoints(){
    if(this.supportedPointList != null){
        this.totalPoint = this.supportedPointList.stream()
                .mapToInt(SupportedPoint::getPrice)
                .sum();
    }
}
```

**성과**
- 포인트 조회 쿼리 성능이 **N배 향상** 되었습니다.
- 베타 테스트 인원 동시 접속 + 추가적인 JMeter 부하에서도 안정적으로 동작함을 확인하였습니다.

## 인프라 및 배포

### DevOps 역할 인수와 아키텍처 전환 과정

현재 도메인인 `opensource-supporter.org`와 달리 과거 프로젝트 운영 초기 1년간은 팀의 동료 개발자가 DevOps를 담당하여 `opensource-supporter.site`로 서비스를 운영했습니다. 당시 배포 환경은 아래와 같았습니다.

**기존 아키텍처 (2024.03-2025.03):**
- EC2에 Java, Nginx 직접 설치
- AWS RDS 원격 데이터베이스 사용

이후 동료 개발자가 취업으로 떠나게 되면서 SQL 덤프파일을 받아 DevOps 역할을 맡게 되었습니다. 1년 동안 거의 손대지 않은 프로젝트를 다시 살리고,
배포부터 도메인 연결까지 풀 사이클을 한번도 경험하지 않은 상태에서 프로젝트를 다시 살리는건 꽤나 힘든 일이었습니다. 때문에 후에 제가 이 프로젝트를 배포하는 과정을 까먹을지라도,
Docker와 Docker-Compose만 있다면 쉽게 다시 동일한 환경으로 배포를 할 수 있도록 이번엔 컨테이너 방식으로 인프라를 완전히 재구축했습니다.

### Docker 기반 컨테이너 배포로 전환

아래는 환경 일관성을 위해 Docker-Compose를 활용하기로 결심하고, 각종 자료를 서칭해 작성한 구성 파일입니다.

```yaml
# docker-compose.yml
version: '3.8'
services:
  oss_mysql:
    image: mysql:latest
    container_name: oss_mysql
    environment:
      # MYSQL_ROOT_PASSWORD: ********
      # MYSQL_DATABASE: ***
    volumes:
      - ./mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  oss_backend:
    build: .
    container_name: oss_backend
    ports:
      - "8080:8080"
    depends_on:
      - oss_mysql

  oss_frontend:
    build: ../opensource-supporter-frontend
    container_name: oss_frontend
    ports:
      - "80:80"
    depends_on:
      - oss_backend
```

### Nginx 리버스 프록시 아키텍처 재설계

기존 설정을 분석하여 외부에서 직접적으로 서버에 접근하지 못하도록 리버스 프록시 구조로 개선했습니다.

```nginx
# nginx.conf
server {
    listen 80;
    server_name opensource-supporter.org;

    # 정적 파일 서빙 (React)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 요청 리버스 프록시
    location /api/ {
        proxy_pass http://oss_backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 아키텍처 전환 성과(2025.08-)

**기술적 개선사항**
- **MySQL 로컬화**: AWS RDS → Docker 컨테이너로 마이그레이션하여 비용 절감
- **서비스 격리**: Docker 네트워크를 통한 컨테이너 간 통신으로 보안 강화
- **배포 자동화**: docker-compose를 통한 일관적인 배포 환경 구축
- **환경 일관성**: 개발/스테이징/운영 환경 동일성 보장

**성과**
이 과정을 통해 기존 개발자가 겪었을 운영의 어려움을 이해하게 되었습니다. 수동 배포와 서버 직접 관리의 부담, 그리고 다양한 환경에서의 일관성 유지가 얼마나 복잡한 작업인지를 체감할 수 있었습니다. 이러한 경험은 DevOps의 중요성과 함께 팀 내 다른 역할에 대한 이해도를 높이는 계기가 되었습니다.

### 베타 테스트 성능 검증
같은 대학 지인들을 모아놓고 2024.06.23 실시한 소규모 베타 테스트에서 시스템 안정성을 모니터링 해보았습니다. 학생 신분이었기 때문에 AWS의 비용이 감당되지 않아 AWS FreeTier 환경으로 배포한 만큼 몇 명까지 수용이 가능한지 테스트 할 필요가 있었습니다.

**테스트 환경**
- 동시 접속자: 20+명 + JMeter 추가 부하
- 테스트 시간: 19:50 ~ 20:05 (15분간)
- 인프라: AWS FreeTier EC2 인스턴스

## 베타 테스트 결과

**성능 지표**
- CPU 사용률: 최고 48% (안정적)
- 메모리 사용량: 350-400MB 유지
- 응답 시간: 평균 2초 이내
- 에러율: 0%

**사용자 만족도**
- 매우 만족: 50%, 만족: 50% (총 12명 응답)
- 발전 가능성: 매우 높음 50%, 높음 33.3%

**가장 높은 평가를 받은 기능**
1. **GitHub 스탯과 레포지토리 평가 시스템** (75% 선택)
2. **오픈 라이브러리 탐색 및 검색** (50% 선택)
3. **레포지토리 후원 및 정산 시스템** (50% 선택)

**피드백**
> "AI 기반 정량적·정성적 분석이 후원 결정에 매우 도움이 되었습니다."

> "GitHub와의 연동이 매끄럽고 사용자 경험이 직관적입니다."


## 시연 영상

<div align="center">
  
https://github.com/user-attachments/assets/e275e45e-9457-4087-88d8-18e7ff102ff2

</div>


## 팀 구성 및 역할

|<img src="https://avatars.githubusercontent.com/u/5442985?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/128115881?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/96350424?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/79962121?v=4" width="150" height="150"/>|
|:-:|:-:|:-:|:-:|
|권혁원<br/>PM / UX / BackEnd|김재형<br/>BackEnd(DevOps)|고강민<br/>FrontEnd(Business Logic)|김한나<br/>FrontEnd(View Logic)|

## 성과
- 한국정보기술학회 추계학술대회 **은상**
- 벤처스타트업아카데미 성과발표공유회 **은상**
- 교내 컴퓨터공학전공 작품전시회 **우수상**
- 교내 캡스톤디자인3 **A+ 등급**

---

## 개발 회고

### 프론트엔드 개발자에서 백엔드 개발자로 - 첫 백엔드 도전기

대학생활 동안 4개의 프론트엔드 프로젝트를 진행하며 React-Native, React에 익숙했던 저에겐 이 프로젝트는 **생애 최초의 백엔드 도전**이었습니다.

### "컨트롤러? 서비스? 이게 뭔가요?"

프론트엔드만 하던 저에게 Spring Boot는 외계어 보다도 어려웠습니다. `@RestController`가 뭔지, JPA가 뭔지도 모르는 상태에서 시작했습니다. 팀원들이 "API 설계부터 해야지"라고 할 때, 솔직히 말하면 **"API를 설계한다는 게 구체적으로 뭘 하는 건데?"** 라는 생각이 먼저 들었습니다.
이제 와서 생각해보니, 프론트엔드를 개발 할 때 api를 사용해볼 생각만 했지 도대체 어떻게 코딩이 되어있는걸까에 대해선 의문을 갖지 않았던 것 같습니다.

하지만 팀의 PM을 맡고 있던 입장에서 "모르겠다"고 말할 수는 없습니다. 제가 백엔드를 맡겠다고 선언한만큼 밤새 교재를 보며 기초부터 차근차근 익혀나갔습니다.

### "문서에 없는 건 어떻게 하라고..."

백엔드 개발의 진짜 어려움은 **공식 문서와 실 구현 사이의 간극**에서 시작되었습니다.

GitHub API 연동을 시도했을 때, 공식 문서대로 `Bearer` 토큰을 사용했는데 특정 관리자 API에서 계속 401 에러가 발생했습니다. **일주일간 수시간을 투자해가며** 원인을 찾았지만 해답은 공식 문서가 아닌 **해외 개발자 커뮤니티의 작은 댓글 하나**에서 찾을 수 있었습니다.

```java
// 공식 문서에는 없지만 실제로는 필수인 인증 방식
String adminAuth = "Basic " + Base64.getEncoder().encodeToString(
    (clientId + ":" + clientSecret).getBytes()
);
```

### "이제 좀 알겠는데?"

OAuth 구현에서도 비슷한 문제가 발생했습니다. 프론트엔드만 하던 경험으로는 라이브러리만 설치해서 딸깍 하면 되었기 때문에 OAuth의 전체 플로우를 이해하기 어려웠습니다. 그래서 프론트엔드 경험을 살려 빠르게 Thymeleaf로 임시 서버사이드 렌더링 페이지를 만들었고, OAuth 플로우를 직접 눈으로 확인했습니다.

프론트엔드 개발에서 벗어나 뒤늦게 백엔드를 시작했던 약점이 오히려 **"사용자 관점에서 시스템을 이해하려는 강점"**이 되어있었습니다.

성능 최적화에서도 마찬가지였습니다. 매번 SUM 쿼리로 포인트를 계산하는 로직을 보고 **"이거 프론트에서 state를 관리하는 것처럼 미리 계산해두면 안 될까?"** 라는 생각이 들었습니다.

```java
@PrePersist
@PreUpdate
public void updatePoints(){
    // 데이터 변경 시점에 미리 계산
    if(this.supportedPointList != null){
        this.totalPoint = this.supportedPointList.stream()
                .mapToInt(SupportedPoint::getPrice)
                .sum();
    }
}
```

이 아이디어로 **N배의 성능 향상**을 달성했을 때, 비로소 "아, 지금 백엔드를 시작해도 늦지 않았구나"라는 자신감을 얻을 수 있었습니다.

### "논문까지 나오다니..."

4개월간의 개발을 마치고 나니, 단순히 "백엔드를 배웠다"는 것 이상의 여러 성과를 가져올 수 있었습니다.

**수상 실적**
- 한국정보기술학회 추계학술대회 **은상**
- 벤처스타트업아카데미 성과발표공유회 **은상**
- 교내 컴퓨터공학전공 작품전시회 **우수상**
- 교내 캡스톤디자인3 **A+ 등급**

그런데 정말 예상치 못했던 것은 **이 프로젝트가 학술논문으로 게재**되었다는 것이었습니다. 프론트엔드만 하던 제가 처음 시도한 백엔드 프로젝트가 학회에서 인정받을 만한 기술적 가치를 갖고 있었다니 너무나 보람있었습니다.

### 정리: 첫 백엔드 프로젝트가 가져다준 것들

**1. 기술적 성장**
- 외부 API 통합 아키텍처 설계 역량
- 성능 최적화에 대한 실질적 경험
- 프론트엔드-백엔드 협업의 깊은 이해

**2. 문제 해결 능력**
- 공식 문서의 한계를 넘어서는 정보 수집 능력
- 다른 개발자 커뮤니티 활용 경험
- **"모르는 것을 모른다고 인정하고, 그래도 해결해내는 끈기"**

**3. 협업 관점의 변화**
프론트엔드만 할 때는 "API 왜 이렇게 느려?"라고 투덜거렸는데, 직접 백엔드를 구현해보니 **데이터베이스 쿼리 최적화, 외부 API 의존성, 서버 리소스 제약** 등 과거 동료였던 백엔드 개발자들이 대단해보였습니다.

무엇보다 가장 큰 수확은 프로젝트를 통해 프론트엔드와 백엔드 양쪽 모두의 관점에서 서비스를 설계하고 구현할 수 있게 되었고, 이는 후에 (주)아이디어콘서트에서 인턴으로 일할 때 **다양한 기술을 거리낌 없이 습득하고 적응하는 능력**으로 이어질 수 있었습니다.

---
## 부록(API 명세서)

### Auth (인증 관리)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/auth/login/page` | GitHub OAuth 인증 페이지로 리다이렉트 | ❌ |
| `GET` | `/api/auth/login` | GitHub OAuth 콜백 처리 및 사용자 정보 반환 | ❌ |
| `GET` | `/api/auth/refresh` | Access token으로 사용자 정보 조회 | ✅ |
| `DELETE` | `/api/auth/logout` | GitHub 토큰 만료 처리 | ✅ |
| `DELETE` | `/api/auth/withdrawal` | 계정 완전 삭제 및 토큰 만료 | ✅ |

### Repository (레포지토리 관리)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/repo` | 새로운 레포지토리를 서비스에 등록 | ✅ |
| `GET` | `/api/repo` | ID로 특정 레포지토리 정보 조회 | ❌ |
| `PUT` | `/api/repo` | 등록된 레포지토리 정보 업데이트 | ✅ |
| `DELETE` | `/api/repo` | 등록된 레포지토리 삭제 (후원 받지 않은 경우만) | ✅ |
| `GET` | `/api/repos/modal` | GitHub에서 사용자의 레포지토리 목록 조회 | ✅ |
| `GET` | `/api/repos/supported` | 특정 사용자가 소유한 레포지토리 목록 | ❌ |
| `GET` | `/api/repos/supporting` | 특정 사용자가 후원한 레포지토리 목록 | ❌ |
| `GET` | `/api/repo/recommended/recentlyCommit` | 최근 커밋 기준 추천 레포지토리 목록 | ❌ |
| `GET` | `/api/repo/recommended/mostViewed` | 조회수 기준 인기 레포지토리 목록 | ❌ |
| `GET` | `/api/repo/recommended/myPartners` | 사용자가 후원한 레포지토리 기반 추천 목록 | ✅ |
| `GET` | `/api/repo/detail` | README, 주간 커밋, GitHub 통계, GPT 분석 포함 상세 정보 | ❌ |

### User (사용자 관리)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/user/card` | 사용자 요약 카드 정보 조회 | ❌ |
| `GET` | `/api/user` | 사용자 전체 정보 조회 | ❌ |

### Point (포인트 관리)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/point/charge` | PayPal을 통한 포인트 구매 | ✅ |
| `POST` | `/api/repo/point` | 특정 레포지토리에 포인트 후원 | ✅ |
| `GET` | `/api/point/spent` | 사용자가 후원한 포인트 내역 조회 | ✅ |
| `GET` | `/api/point/earned` | 사용자가 충전/획득한 포인트 내역 조회 | ✅ |
| `GET` | `/api/point/summary` | 총 포인트, 사용 포인트, 잔여 포인트 요약 | ✅ |

### Rank (랭킹 시스템)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/rank/userRank` | 포인트 기준 전체 사용자 랭킹 조회 | ❌ |
| `GET` | `/api/rank/myRank` | 현재 사용자의 랭킹 정보 조회 (캐시 적용) | ✅ |

### Search (통합 검색)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/search` | 키워드로 사용자와 레포지토리 동시 검색 | ❌ |

### Translation (번역 서비스)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/translate` | OpenAI/Groq API를 활용한 텍스트 번역 | ❌ |

### Advertisement (광고 시스템)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/advertisement` | 새로운 광고 등록 (테스트용) | ❌ |
| `DELETE` | `/api/advertisement` | 등록된 광고 삭제 (테스트용) | ❌ |
| `GET` | `/api/advertisement/random` | 시청할 랜덤 광고 반환 | ❌ |
| `POST` | `/api/advertisement/viewed` | 광고 시청 완료 시 포인트 지급 | ❌ |
