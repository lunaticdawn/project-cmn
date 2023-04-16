# git commit message convenstion

```

<타입>(<범위>): <간단한 제목>

<설명>

[관련 이슈 번호]
```

### <타입>
    - <타입>은 다음 중 하나를 사용합니다:

|타입|설명|
|----|----|
|feat|새로운 기능 추가|
|fix|버그 수정|
|docs|문서 변경|
|style|코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우|
|refactor|코드 리팩토링|
|test|테스트 코드, 리팩토링 테스트 코드 추가|
|chore|빌드 태스크 업데이트, 패키지 매니저 설정 등|

### <범위>
    - <범위>는 변경 사항의 범위를 나타냅니다.
        예를 들어, 파일 이름, 함수 이름, 모듈 이름 등이 될 수 있습니다.

### <간단한 제목>
    - <간단한 제목>은 변경 사항의 간단한 요약입니다.

### <설명>
    - <설명>은 변경 사항에 대한 자세한 설명입니다.

### [관련 이슈 번호]
    - [관련 이슈 번호]는 해당 커밋과 관련된 이슈 번호를 추가하는 것이 좋습니다.

### 예시:

```

feat(user): Add ability to reset password

This commit adds the ability for users to reset their password if they forget it.
It includes a new "forgot password" page and updates to the authentication system.

Fixes #123
```