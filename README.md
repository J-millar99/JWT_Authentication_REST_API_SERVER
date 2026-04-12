# JWT Authentication REST API Server

Spring Security와 JWT 기반으로 회원·인증·관리자 API를 제공하는 REST 백엔드입니다.

## 기술 스택

[![Java](https://img.shields.io/badge/Java-17-437291?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.4.1-02303A?style=flat&logo=gradle&logoColor=white)](https://gradle.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![JJWT](https://img.shields.io/badge/JJWT-0.12.6-000000?style=flat)](https://github.com/jwtk/jjwt)
[![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?style=flat&logo=hibernate&logoColor=white)](https://hibernate.org/orm/)
[![Lombok](https://img.shields.io/badge/Lombok-used-dc3820?style=flat)](https://projectlombok.org/)

## 아키텍처 다이어그램

이 저장소에는 **diagrams.net(draw.io) 네이티브 소스 파일**이 포함되어 있습니다. 스크린샷이 아니라 `docs/architecture.drawio`를 직접 열어 보거나 수정할 수 있습니다.

| 도구 | 방법 |
|------|------|
| **diagrams.net (웹)** | [app.diagrams.net](https://app.diagrams.net/) 접속 → **파일 → 디바이스에서 열기** → `docs/architecture.drawio` 선택 |
| **VS Code / Cursor** | [Draw.io Integration](https://marketplace.visualstudio.com/items?itemName=hediet.vscode-drawio) 확장 설치 후 동일 파일 열기 |

**`d.setId is not a function` 등 오류가 나올 때:** VS Code/Cursor의 Draw.io 확장이 기대하는 모델 형식과 맞지 않으면 내부 mxGraph 역직렬화 단계에서 이런 오류가 날 수 있습니다. `docs/architecture.drawio`는 숫자 `id`만 쓰는 표준에 가깝게 맞춰 두었으니, 한 번 파일을 다시 열어 보고, 계속되면 확장을 최신(또는 Pre-release)으로 올리거나 **웹 diagrams.net**에서 여는 것을 권장합니다.

다이어그램 요약: HTTP 클라이언트 → Spring Security(JWT 필터) → REST 컨트롤러 → 서비스 → JPA 리포지토리 → MySQL 8, 선택적으로 Docker Compose로 `app`·`db` 구동.

## 핵심 기능

- **인증**: 회원가입(`POST /api/auth/signup`), 로그인·JWT 발급(`POST /api/auth/login`), 로그아웃(클라이언트 토큰 폐기 안내용 `POST /api/auth/logout`)
- **사용자**: 내 정보 조회·수정·탈퇴(`GET` / `PUT` / `DELETE /api/users/me`, 인증 필요)
- **관리자**: 전체 사용자 목록(`GET /api/admin/users`), 강제 탈퇴(`DELETE /api/admin/users/{username}`), `ROLE_ADMIN` 전용
- **보안**: Stateless JWT, `JwtAuthenticationFilter`, 비밀번호 인코딩, 메서드 단위 `@PreAuthorize`
- **데이터**: JPA + MySQL, 스키마는 `mysql/init.sql` 기준(`ddl-auto: none`), 기동 시 관리자 계정 시드(`DataInitializer`)

## 로컬 실행 방법

다른 개발자가 저장소를 클론한 뒤 **최소한의 단계**로 돌릴 수 있도록 정리했습니다. 사전 요건은 **JDK 17**, **Docker Desktop**(또는 Docker Engine + Compose), 선택적으로 로컬에서만 앱을 띄울 때 **Gradle**입니다.

### 1. 저장소 클론 및 환경 파일

```bash
git clone <저장소-URL>
cd JWT_Authentication_REST_API_SERVER

--env는 따로 구성--
# mysql
MYSQL_DATABASE=
MYSQL_ROOT_PASSWORD=
LANG=C.UTF-8

# JWT
JWT_SECRET=
JWT_ACCESS_TOKEN_EXPIRATION=1800000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# 초기 관리자 계정 (DataInitializer)
ADMIN_USERNAME=
ADMIN_PASSWORD=
ADMIN_NAME=관리자
ADMIN_PHONE=010-0000-0000
ADMIN_EMAIL=admin@admin.com

```

`.env`를 연 뒤 `MYSQL_ROOT_PASSWORD`, `JWT_SECRET`, `ADMIN_PASSWORD` 등 **반드시 운영에 맞게 변경**하세요. `.env`는 `.gitignore`에 포함되어 커밋되지 않습니다.

### 2-A. Docker Compose로 MySQL + API 한 번에 실행 (권장)

```bash
docker compose up -d --build
```

- API: `http://localhost:8080`
- MySQL(호스트에서 접속 시): `localhost:3306`, DB 이름은 `.env`의 `MYSQL_DATABASE`(기본 예시: `jwt_project_db`)

중지 및 삭제:

```bash
docker compose down
```

### 2-B. MySQL만 Docker로 두고 애플리케이션은 IDE / Gradle로 실행

1. DB만 기동:

   ```bash
   docker compose up -d db
   ```

2. `.env`의 DB 비밀번호가 `application.yml`의 기본 datasource와 일치하는지 확인합니다(`SPRING_DATASOURCE_*`를 쓰지 않으면 `localhost:3306` + root / `.env`의 `MYSQL_ROOT_PASSWORD`와 맞추면 됩니다).

3. 프로젝트 루트에서:

   ```bash
   ./gradlew bootRun
   ```

   또는 IDE에서 `JwtAuthenticationRestApiServerApplication` 실행. 작업 디렉터리는 **프로젝트 루트**로 두어야 `spring.config.import`의 `optional:file:.env`가 로드됩니다.

### 동작 확인 예시

서버 기동 후 (회원가입이 구현되어 있다면) 로그인 등 API를 `curl`이나 HTTP 클라이언트로 호출해 JWT가 내려오는지 확인합니다.

<<<<<<< HEAD
---
=======
---
>>>>>>> f7582a614025a7305d28f098d1197ffe04eb3f3f
>>>>>>> f7582a614025a7305d28f098d1197ffe04eb3f3f
>>>>>>> f7582a614025a7305d28f098d1197ffe04eb3f3f
