# project-cmn

Springboot 프로젝트 시 사용할 공통 모듈

## 구성

1. project-cmn-util: Servlet 과 관련 없이 공통으로 사용할 수 있는 유틸리티
2. project-cmn-configuration-datasource: 다중 datasource 를 사용할 경우, dependency 에 추가
3. project-cmn-configuration-mybatis: 다중 datasource 를 사용할 경우, dependency 에 추가
4. project-cmn-http: Servlet 을 사용하는 서버 개발 시 사용할 수 있는 유틸리티
5. project-cmn-mybatis: mybatis 를 이용해 일부 소스를 자동으로 생성하기 위한 어플리케이션

## 신경쓰는거

1. 로깅
2. Validation Check
3. 공통 Exception 처리

## TODO

1. 연동을 위한 WebClient
2. 각종 명명규칙
3. 개발 가이드
4. 샘플 소스

## History
2023-04-11: Exception 에 대한 처리를 설정을 통해서 할 수 있도록 함
2023-04-12: 멀티 데이터소스 생성 시 보안을 위해 JASYPT 암호화를 지원하도록 수정