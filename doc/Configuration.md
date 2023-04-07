# AccessLog 설정에 대해

1. dependency 에 project-cmn-http 를 추가한다.
2. project.access.log 에 관련 설정을 한다.
3. 로깅 사용 여부 중 request-header 의 경우 false 로 설정을 해도 Request Header 를 분석한 데이터는 AccessLogDto 에 담는다.
4. Log Key 생성 방식을 변경하려면 AccessLogFilter 를 수정해야 한다.
5. 패키지 구조가 다른 경우에는 AccessLogAspect 의 execution 내용을 수정해야 StopWatch 가 정상적으로 출력된다.
6. AccessLogInterceptor 를 수정하여 AccessLogDto 에 있는 정보를 DB 에 넣을 수도 있다.
7. 어느 곳에서든 AccessLog.getAccessLogDto() 을 통해 AccessLogDto 에 접근할 수 있다.

## 설정 속성

- com.project.cmn.http.accesslog.AccessLogConfig

| 속성                 | Mapping Class                                  | 설명                |
|--------------------|------------------------------------------------|-------------------|
| project.access.log | com.project.cmn.http.accesslog.AccessLogConfig | Access Log에 대한 설정 |

| 속성                    | 타입                               | 필수여부 | 기본값   | 설명                                                                |
|-----------------------|----------------------------------|------|-------|-------------------------------------------------------------------|
| enabled               | Boolean                          | 필수   | false | Access Log 사용 여부                                                  |
| filter                | Boolean                          | 옵션   | false | AccessLogFilter 사용 여부.<br/>사용으로 해야 Body 부분을 로깅할 수 있다.             |
| filter-order          | Integer                          | 옵션   | 0     | AccessLogFilter 의 필터 내 순서                                         |
| url-patterns          | java.util.List<java.lang.String> | 옵션   | -     | AccessLogFilter 를 적용할 URL 패턴                                      |
| path-patterns         | java.util.List<java.lang.String> | 옵션   | -     | 로깅할 Path Pattern 들                                                |
| exclude-path-patterns | java.util.List<java.lang.String> | 옵션   | -     | 로깅하지 않을 Path Pattern 들                                            |
| request-header        | Boolean                          | 옵션   | true  | Request Header 의 로깅 여부                                            |
| request-header-names  | java.util.List<java.lang.String> | 옵션   | -     | 로깅할 Request Header 들                                              |
| request-uri           | Boolean                          | 옵션   | true  | Request Uri 의 로깅 여부                                               |
| request-param         | Boolean                          | 옵션   | true  | Request Parameter 의 로깅 여부                                         |
| request-body          | Boolean                          | 옵션   | true  | Request Body 의 로깅 여부.<br/>content-type 이 applicaiton/json 일 때만 로깅 |
| request-body-length   | Integer                          | 옵션   | 0     | 로깅할 Request Body 의 길이. 0 인 경우 전체                                  |
| response-header       | Boolean                          | 옵션   | true  | Response Header 의 로깅 여부                                           |
| response-header-names | java.util.List<java.lang.String> | 옵션   | -     | 로깅할 Response Header 들                                             |
| response-body         | Boolean                          | 옵션   | true  | Response Body 의 로깅 여부                                             |
| response-body-length  | Integer                          | 옵션   | true  | 로깅할 Response Body 의 길이. Json 인 경우에만.                              |
| stop-watch            | Boolean                          | 옵션   | true  | org.springframework.util.StopWatch 정보의 로깅 여부                      |

ex)

```yaml

project:
  access.log:
    enabled: true
    filter: true
    request-header: true
    request-param: true
    request-body: true
    response-header: true
    response-body: true
    request-body-length: 1000
    response-body-length: 1000
    request-header-names:
    response-header-names:
    exclude-path-patterns:
```

# DataSource 설정에 대해

1. 연결해야 할 DataSource 가 1개인 경우에는 Spring Boot 의 application.yml 설정을 통한 AutoConfiguration 으로 충분
2. 연결해야 할 DataSource 가 2개 이상인 경우에는 별도의 DataSource 연결과 Transaction 설정이 필요
    - DataSource, SqlSessionFactory, SqlSessionTemplate, DataSourceTransactionManager 를 각각의 DataSource 별로 등록하는 방법
        - 인터넷에서 가장 찾기 쉬운 방법.
        - @Configuration 과 @Bean 을 이용하여 DataSource, SqlSessionFactory, SqlSessionTemplate, DataSourceTransactionManager
          를 직접 생성
    - BeanDefinitionRegistryPostProcessor 를 이용하여 동적으로 Bean 을 등록하는 방법
        - project-common 에서 사용하는 방법.
        - dependency 에 project-cmn-configuration-datasource 를 추가한다.
        - application.yml 에서 다음 AutoConfiguration 은 제외하도록 설정한다.
            - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
            - org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
            - org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration

```yaml

spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
      - org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration
```

3. 연결해야 할 DataSource 가 2개 이상이고 같은 Transaction 에 묶여야 한다면 XADataSource 를 이용하고, 라이브러리에 spring-boot-starter-jta-atomikos 를
   포함하면 자동으로 JtaTransactionManager 를 설정해 준다. - Atomikos AutoConfiguration
4. SpringBoot 3.x(SpringFramework 6.x) 부터 Transaction 패키지가 javax 에서 jakarta 로 변경되었고 Atomikos 는 아직 변경 전이다.
   따라서 Atomikos 를 사용하려면 SpringBoot 2.x(SpringFramework 5.x)를 사용해야 한다.

## 설정 속성

- com.project.cmn.configuration.datasource.DataSourceConfig

| 속성                           | Mapping Class                                                           | 설명                                                                     |
|------------------------------|-------------------------------------------------------------------------|------------------------------------------------------------------------|
| project.datasource           | com.project.cmn.configuration.datasource.DataSourceConfig               | DataSource 설정                                                          |
| project.datasource.type      | String                                                                  | 사용할 DataSource 타입 ds: javax.sql.DataSource, xa: javax.sql.XADataSource |
| project.datasource.item-list | java.util.List<com.project.cmn.configuration.datasource.DataSourceItem> | 각각의 DataSource 설정                                                      |

- com.project.cmn.configuration.datasource.DataSourceItem

| 속성                       | 타입      | 필수여부 | 설명                                                                                                 |
|--------------------------|---------|------|----------------------------------------------------------------------------------------------------|
| enabled                  | Boolean | 필수   | 해당 설정 사용 여부                                                                                        |
| datasource-name          | String  | 필수   | DataSource 이름. 필수 해당 이름을 pool name 으로 사용                                                           |
| driver-class-name        | String  | 필수   | JDBC 드라이버 클래스명                                                                                     |
| url                      | String  | 필수   | Database 의 JDBC URL                                                                                |
| user                     | String  | 필수   | Database 의 사용자명                                                                                    |
| password                 | String  | 필수   | Database 의 비밀번호                                                                                    |
| connection-test-query    | String  | 옵션   | 연결 테스트 쿼리. 드라이버가 JDBC4를 지원하면 설정하지 않음                                                               |
| connection-timeout       | Integer | 옵션   | client 가 pool 로부터 connection 을 얻기위해 기다리는 시간 초단위. 기본 30초                                            |
| idle-timeout             | Integer | 옵션   | connection 에 pool 에서 idle 상태로 존재하는 시간 초단위. 기본 10분. 최소 10초                                          |
| keepalive-time           | Integer | 옵션   | 데이터베이스나 네트워크 인프라에 의해 타임아웃 상태가 되는 것을 방지하기 위해 설정<br/>maxLifetime 값보다는 작아야 함. 초단위. 기본 0(사용안함). 최소 30초 |
| lazy-connection          | Boolean | 옵션   | org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy 사용여부                             |
| leak-detection-threshold | Integer | 옵션   | 해당 시간이상 동안 connection 을 pool 에 반납하지 않는다면 connection 누수로 판단하고 로그를 출력함<br/>초단위. 기본 0(검출하지 않음). 최소 2초 |
| max-lifetime             | Integer | 옵션   | 커넥션의 최대 유지 시간. 이 시간이 지난 커넥션 중에서 사용중인 커넥션은 종료된 이후에 풀에서 제거한다.<br/>초단위. 기본 30분. 최소 30초                |
| maximum-pool-size        | Integer | 옵션   | pool 에서 관리하는 connection 의 최대 수(idle connection + in-use connection) 기본 10개                         |
| minimum-idle             | Integer | 옵션   | pool 에서 유지하려고 하는 idle connection 의 최소 수 기본은 maximumPoolSize 에 설정값과 동일                              |
| primary                  | Boolean | 옵션   | @Primary 선언 여부                                                                                     |
| transaction-name         | String  | 옵션   | Transaction 이름. 옵션. 없으면 생성안함                                                                       |

ex)

```yaml

project:
  datasource:
    type: "ds"
    item-list:
      - enabled: true
        primary: true
        lazy-connection: true
        datasource-name: "first-ds"
        transaction-name: "first-tm"
        driver-class-name: "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"
        url: "jdbc:log4jdbc:mariadb://xxx.xxx.xxx.xxx:3306/first_db?useSSL=false&allowMultiQueries=true"
        user: "user"
        password: "password"
        maximum-pool-size: 3
        connection-timeout: 3
      - enabled: true
        lazy-connection: true
        datasource-name: "second-ds"
        transaction-name: "second-tm"
        driver-class-name: "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"
        url: "jdbc:log4jdbc:oracle:thin:@xxx.xxx.xxx.xxx:1521:sid"
        user: "user"
        password: "password"
        maximum-pool-size: 3
        connection-timeout: 3
```

```yaml

project:
  datasource:
    type: "xa"
    item-list:
      - enabled: true
        primary: true
        lazy-connection: true
        datasource-name: "first-ds"
        transaction-name: "first-tm"
        driver-class-name: "org.mariadb.jdbc.MariaDbDataSource"
        url: "jdbc:mariadb://xxx.xxx.xxx.xxx:3306/first_db?useSSL=false&allowMultiQueries=true"
        user: "user"
        password: "password"
        maximum-pool-size: 3
        connection-timeout: 3
      - enabled: true
        lazy-connection: true
        datasource-name: "second-ds"
        transaction-name: "second-tm"
        driver-class-name: "oracle.jdbc.xa.client.OracleXADataSource"
        url: "jdbc:oracle:thin:@xxx.xxx.xxx.xxx:1521:lmmdev11"
        user: "user"
        password: "password"
        maximum-pool-size: 3
        connection-timeout: 3
```

# MyBatis 설정에 대해

1. 연결해야 할 DataSource 가 1개인 경우에는 Spring Boot 의 application.yml 설정을 통한 AutoConfiguration 으로 충분
2. 연결해야 할 DataSource 가 2개 이상인 경우
    - dependency 에 project-cmn-configuration-datasource 와 project-cmn-configuration-mybatis 를 추가한다.
    - 사용할 DataSource 를 설정한다.
    - MyBatis 를 설정한다.

## 설정 속성

- com.project.cmn.configuration.mybatis.MyBatisConfig

| 속성                        | Mapping Class                                                     | 설명               |
|---------------------------|-------------------------------------------------------------------|------------------|
| project.mybatis           | com.project.cmn.configuration.mybatis.MyBatisConfig               | MyBatis 설정       |
| project.mybatis.enabled   | Boolean                                                           | MyBatis 설정 사용 여부 |
| project.mybatis.item-list | java.util.List<com.project.cmn.configuration.mybatis.MyBatisItem> | 각각의 MyBatis 설정   |

- com.project.cmn.configuration.mybatis.MyBatisItem

| 속성                        | 타입                               | 필수여부 | 설명                                                                                                                                                 |
|---------------------------|----------------------------------|------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| enabled                   | Boolean                          | 필수   | 설정 사용 여부                                                                                                                                           |
| config-location           | String                           | 필수   | MyBatis 설정 파일 위치. org.mybatis.spring.SqlSessionFactoryBean 의 configLocation.<br/>ex) classpath:mybatis/mybatis-config.xml                          |
| datasource-name           | String                           | 필수   | 사용할 DataSource 명. org.mybatis.spring.SqlSessionFactoryBean 의 dataSource                                                                            |
| sql-session-factory-name  | String                           | 필수   | org.apache.ibatis.session.SqlSessionFactory 의 이름                                                                                                   |
| sql-session-template-name | String                           | 필수   | org.mybatis.spring.SqlSessionTemplate 의 이름                                                                                                         |
| mapper-base-package       | String                           | 필수   | Mapper 클래스의 패키지 경로. org.mybatis.spring.mapper.MapperScannerConfigurer 의 basePackage                                                                |
| mapper-locations          | java.util.List<java.lang.String> | 필수   | 쿼리 XML 파일의 위치들. {@link org.mybatis.spring.SqlSessionFactoryBean}의 mapperLocations.<br/>ex)classpath*:mapper/**/*.xml                               |
| type-aliases-packages     | java.util.List<java.lang.String> | 필수   | ParameterType, ResultType 으로 사용할 클래스들이 있는 Base 패키지들                                                                                                |
| primary                   | Boolean                          | 옵션   | @Primary  선언 여부                                                                                                                                    |
| annotation-class-name     | String                           | 옵션   | Mapper 로 등록할 Annotation Class 이름. org.mybatis.spring.mapper.MapperScannerConfigurer 의 annotationClass.<br/>기본 org.apache.ibatis.annotations.Mapper |

ex)

```yaml

project:
  mybatis:
    enabled: true
    item-list:
      - enabled: true
        primary: true
        datasource-name: "first-ds"
        sql-session-factory-name: "first-fac"
        sql-session-template-name: "first-tem"
        config-location: "classpath:mybatis/mybatis-config.xml"
        mapper-locations:
          - "classpath*:mapper/mybatis/**/*.xml"
        type-aliases-packages:
          - "com.project.cmn.mybatis"
        mapper-base-package: "com.project.cmn.mybatis"
      - enabled: false
        datasource-name: "second-ds"
        sql-session-factory-name: "second-fac"
        sql-session-template-name: "second-tem"
        config-location: "classpath:mybatis/mybatis-config.xml"
        mapper-locations:
          - "classpath*:mapper/oracle/**/*.xml"
        type-aliases-packages:
          - "com.project.cmn.oracle"
        mapper-base-package: "com.project.cmn.oracle"
```
