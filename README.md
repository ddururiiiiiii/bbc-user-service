# 👤 bbc-user-service

> BookBookClub-MSA의 **회원 서비스**를 담당하는 마이크로서비스입니다.  
> 회원가입, 로그인, 소셜 로그인, 프로필 조회 등의 기능을 제공합니다.

<br>

----

## 📌 프로젝트 링크

- **🧱 모놀리식 버전**: [BookBookClub (Monolith)](https://github.com/ddururiiiiiii/bookbookclub)
- **📁 MSA 버전**: [BookBookClub-MSA](https://github.com/ddururiiiiiii/BookBookClub-MSA)
- **📄 도메인별 **:
-   [bbc-user-service](https://github.com/ddururiiiiiii/bbc-user-service)
-   [bbc-post-service]([https://github.com/ddururiiiiiii/bbc-user-service](https://github.com/ddururiiiiiii/bbc-post-service))


<br>

----

## 📌 주요 기능

### 🔐 인증/인가
- JWT Access / Refresh Token 기반 인증
- Refresh Token은 Redis에 저장 및 만료 관리
- AccessToken 블랙리스트 관리 (로그아웃 등)

### 📧 이메일 인증
- 이메일 인증 토큰을 Redis에 저장
- 최종 인증 여부는 MySQL에도 기록
- 이메일 인증 실패 제한 기능 포함 (횟수 제한, 시간 제한)

### 🧑‍💻 회원 관리
- 회원가입 (일반, 소셜)
- 닉네임 중복 검사
- 마이페이지 조회 (닉네임, 프로필 등)
- 프로필 이미지 업로드 (현재는 로컬 저장, 추후 S3 예정)
- 회원 탈퇴 (상태값 변경 방식)

  
----

<br>

## ✅ 완료된 구현

- [x] 이메일 인증 API 및 토큰 만료 처리
- [x] 소셜 로그인(Google, Naver) 연동
- [x] JWT 기반 인증 필터 설정
- [x] OAuth2 SuccessHandler 커스터마이징
- [x] AccessToken/RefreshToken 발급 및 갱신
- [x] 사용자 마이페이지 조회
- [x] 프로필 이미지 업로드/삭제

----

<br>

## 📂 패키지 구조

~~~bash
com.bookbookclub.bbc_user_service
├── auth # 인증/인가 관련 로직
├── controller # API 컨트롤러
├── dto # DTO
├── global # 전역 설정 및 보안 필터
├── user # 사용자 도메인
└── utils # 유틸리티 (예: 이미지 처리 등)
~~~


----

<br>

## 💬 참고

- 공통 에러코드 및 응답 포맷은 `bbc-common` 모듈에서 관리합니다.
- 다른 서비스와의 통신은 FeignClient를 통해 처리합니다.
