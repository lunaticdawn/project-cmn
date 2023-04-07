# project-cmn-utils

## com.project.cmn.util.cipher

- 암호화 유틸

### Aes256

- ECB 를 지원하기는 하나 CBC 를 권장.(ECB 는 취약점이 알려져 있음)
- 프로젝트 내에서만 사용한다면 최초 SpringBoot Application 실행 시 Aes256.setConfig() 를 실행한 후에 사용하는 것을 권장
- 상세 내용은 Java Doc 참조

## com.project.cmn.util.logback

- logback 관련 유틸

### MaskPatternLayout

- 로깅 시 특정 값을 마스킹 처리 - 개인정보 보호
- logback.xml 의 appender 내부에 다음과 같이 셋팅

```xml

<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="com.project.cmn.util.logback.MaskPatternLayout">
        <maskPattern>passwd=([^&amp;\n]*)</maskPattern>
        <pattern>%d [%-5level] [%X{LOG_KEY}] %logger.%M\(%line\) - %msg%n</pattern>
    </layout>
    <charset>UTF-8</charset>
</encoder>
```

- maskPattern 은 여러 개 설정 가능
- maskPattern 에 매칭되는 구문을 찾고 그룹핑된 부분을 마스킹 처리 함

## com.project.cmn.util.tree

- tree 관련 유틸

### TreeMaker

- DB 에서 Tree 로 만들 목록을 조회하여 TreeMaker의 getList() 메소드를 호출
- DB 에서 조회된 목록은 TreeDto 를 상속받은 객체의 목록이어야 하고, id 와 parentId 값이 반드시 있어야 함
- Tree 형태의 HTML 이 필요한 경우에는 TreeGroup 과 TreeItem 의 getShowHtml() 메소드를 수정하고 TreeMaker 의 getShowHtml() 메소드를 호출하면 됨

# project-cmn-http

## MessageUtils

- project-cmn-http 를 dependency 로 추가하면 자동으로 설정된다.
- 메소드 중 getMessage(int, Object...) 의 int 는 다음과 같은 메시지 키로 변환된다.

| 범위        | 메시지 키           |
|-----------|-----------------|
| 1 ~ 99    | common.%d       |
| 100 ~ 199 | info.%d         |
| 200 ~ 299 | success.%d      |
| 300 ~ 399 | redirect.%d     |
| 400 ~ 499 | client.error.%d |
| 500 ~ 599 | server.error.%d |
| 그외        | service.%d      |

```yaml

spring:
  messages:
    basename: "messages/messages" # src/main/resources/messages/messages.properties
```

## ValidationUtils

- static 메소드인 validate 를 통해 validation 검증이 가능하다.
- 제약조건을 위반한 경우에는 ConstraintViolationException 을 발생시키고 CommonExceptionHandler 의해 메시지가 만들어진다.
- 일반적으로 Group 을 사용할 수 있는 @Validated 를 사용하고, 메소드 내에서 사용해야 하는 경우 ValidationUtils 를 사용한다.
- ValidationUtils 는 List 형태의 파라미터도 체크한다.
- 날짜-시간 포맷은 @ValidatedDateTime 을 이용해서 체크한다.

ex) 일반적인 경우

```

@ResponseBody
@PostMapping("/make/files")
public void makeFiles(@Validated(Create.class) @RequestBody ProjectInfoDto param) {
    makeFilesService.makeFiles(param);
}
```

ex) List 파라미터를 체크하거나 메소드 내에서 사용하는 경우

```

@ResponseBody
@PostMapping("/make/files")
public void makeFiles(@RequestBody List<ProjectInfoDto> param) {
    ValidationUtils.validate(param, Create.class);

    for (ProjectInfoDto projectInfoDto:param) {
        makeFilesService.makeFiles(projectInfoDto);
    }
}
```