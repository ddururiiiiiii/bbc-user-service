# 👤 BookBookClub - User Service

- **bbc-user-service**는 BookBookClub MSA 구조에서 **회원 관리 및 인증**을 담당하는 서비스입니다.
- 회원가입, 로그인, 소셜 로그인 등 사용자의 인증 및 계정 정보를 처리합니다.

<br>

----

<br>

## 🚀 개요

- **서비스명**: User Service
- **역할**: 회원 관련 기능 (가입, 로그인, 소셜 로그인, 프로필 등)
- **소속 프로젝트**: [BookBookClub-MSA](https://github.com/ddururiiiiiii/bookbookclub-msa)
- **초기 깃허브**: [Monolith 프로젝트 보기](https://github.com/ddururiiiiiii/bookbookclub)

<br>

----

<br>

## ✨ 주요 기능

| 기능                    | 설명                                                     | 상태      |
|-----------------------|--------------------------------------------------------|----------|
| 회원가입               | 이메일 회원가입 + 이메일 인증                            | ✅ 완료    |
| 로그인                | 이메일 로그인 + JWT 발급                                 | ✅ 완료    |
| 소셜 로그인 (OAuth2)   | Google, Naver 소셜 로그인                                | ✅ 완료    |
| 닉네임 중복 체크         | 닉네임 중복 여부 조회 API                                | ✅ 완료    |
| 이메일 중복 체크        | 이메일 중복 여부 조회 API                                | ✅ 완료    |
| 프로필 조회/수정        | 회원 기본 정보 및 프로필 이미지 조회 및 수정               | 🟠 진행 중 |
| 로그아웃               | JWT 로그아웃 처리 (Access 블랙리스트 + Refresh 토큰 제거) | 🟠 진행 중 |
| 토큰 재발급            | Refresh Token으로 Access Token 재발급                    | ✅ 완료    |

<br>

----

<br>

## 🏗️ 기술 스택

- Java 17**
- **Spring Boot 3.x**
- **JPA + QueryDSL**
- **MySQL**
- **Spring Security + OAuth2**
- **JWT (Access / Refresh Token)**
- **Redis (토큰 관리, 인증 코드 관리)**
- **Docker (도입 예정)**

<br>

----

<br>

## 🗂️ 프로젝트 구조

```bash
📦bbc_user_service
 ┣ 📂global
 ┃ ┣ 📂common           # 공통 유틸리티, ApiResponse 등
 ┃ ┣ 📂config           # 전역 설정 (SecurityConfig 등)
 ┃ ┣ 📂exception        # 글로벌 예외 처리
 ┃ ┣ 📂jwt              # JWT 관련 (필터, 토큰 유틸)
 ┃ ┗ 📂security         # 보안 관련 설정 및 커스텀 시큐리티
 ┃
 ┣ 📂user
 ┃ ┣ 📂controller       # 사용자 관련 API 컨트롤러
 ┃ ┣ 📂domain           # User 엔티티
 ┃ ┣ 📂dto              # User 관련 DTO 클래스
 ┃ ┣ 📂enums            # 사용자 관련 Enum
 ┃ ┣ 📂exception        # 사용자 도메인 전용 예외 처리
 ┃ ┣ 📂policy           # 정책성 로직 (ex. 비밀번호 정책)
 ┃ ┣ 📂repository       # UserRepository 등 JPA 레포지토리
 ┃ ┣ 📂scheduler        # 배치/스케줄러 (ex. 만료 토큰 제거)
 ┃ ┗ 📂service          # 사용자 서비스 레이어
 ┃
 ┗ BbcUserServiceApplication  # 메인 실행 클래스
```

