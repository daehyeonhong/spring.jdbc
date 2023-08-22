# `Java Exception`

## 1. `Java`의 기본적인 `Exception`의 구조

```text
java.lang.Throwable
|
|-- java.lang.Error (Unchecked Exception)
|   |
|   |-- OutOfMemoryError, VirtualMachineError, StackOverflowError, ...
|
|-- java.lang.Exception (Checked Exception)
    |
    |-- java.lang.RuntimeException (Unchecked Exception)
    |   |
    |   |-- NullPointerException, IllegalArgumentException, ArithmeticException, ...
    |
    |-- java.io.IOException (Checked Exception)
    |   |
    |   |-- java.io.FileNotFoundException, ...
    |
    |-- java.sql.SQLException (Checked Exception)
    |-- ...
```

## 2. `Exception` 처리

```text
Web Application (Servlet Container)
    |
    |-- Presentation Layer
    |   |
    |   |-- Controller
    |
    |-- Application Layer
    |   |
    |   |-- Service
    |
    |-- Infrastructure Layer
    |   |
    |   |-- Repository
    |
    |-- ...
```

구조에서 자신의 `Layer`에서 발생하거나 전달된 `Exception`을 처리하지 못하면, 상위 `Layer`로 `Exception`을 전달하게 된다.  
만약 최종 `Presentation Layer`에서까지 `Exception`을 처리하지 못하면 `Servlet Container`가 처리하게 된다.(`MVC 강의 참조`)

### 2.1. `Exception` 처리 방법

`Exception` 클래스를 상속 받으면 `Checked Exception`이 된다.  
`Checked Exception`은 `try-catch` 블록으로 처리하거나, `throws`로 호출자에 `Exception`을 전달해야 한다.  
전달하지 않으면, `Compile Error`가 발생한다.

`Exception`을 처리하는 방법으로는

1. `try-catch` 블록으로 처리
2. `throws`로 호출자에 `Exception`을 전달

두 가지 방법이 존재한다.

#### *`Checked Exception`의 장단점*

- 장점: `Exception`을 처리하지 않으면 `Compile Error`가 발생하기 때문에, `Exception`에 대한 처리를 강제한다.
- 단점: `Exception`을 처리하는 코드가 많아지고, 코드의 가독성이 떨어진다.
    - 번거롭다.
    - `Exception`을 처리하는 코드가 많아지면, `Exception`을 처리하는 코드가 실제 코드를 가리기 때문에 가독성이 떨어진다.

### 2.2. `Unchecked Exception` 기본 이해

`RuntimeException`을 상속 받으면 `Unchecked Exception`이 된다.  
이 경우 `throws`로 호출자에 `Exception`을 전달하지 않아도 된다.

#### *`Unchecked Exception`의 장단점*

- 장점: `Exception`을 처리하지 않아도 `Compile Error`가 발생하지 않기 때문에, 무시할 수 있다.
- 단점: 누락의 경우가 생길 수 있다.

## `Checked Exception` 활용

### 기본 원칙

1. 기본적으로 `Unchecked Exception`을 사용한다.
2. `Checked Exception`은 `BusinessLogicException`의 경우에만 사용한다.

### `Checked Exception`의 문제

1. 복구 불가능
    - `Checked Exception`은 복구가 가능한 `Exception`이 아니다.
    - 결과적으로는 이용자에게는 간략한 `Exception` 메시지만 전달하고, 로그에는 자세한 `Exception` 메시지를 남긴다.
2. 의존 관계에 대한 문제
    - `Checked Exception`은 `throws`로 호출자에 `Exception`을 전달해야 한다.
    - 현재 `Layer`에서 발생한 `Exception`을 상위 `Layer`로 전달해야 하는데, `throws`로 전달하면 상위 `Layer`는 하위 `Layer`의 `Exception`에 의존하게 된다.
        - 해당 문제를 해결하기 위해 `CheckedException`의 최상위 클래스인 `Exception`을 `throws`로 전달하게 되면, 중요한 `Exception`에 대한 처리를 놓칠 수 있다.
        - `Complier`는 상위 `Exception`을 `throws`로 전달하면, 하위 `Exception`을 `throws`로 전달하지 않아도 되는 것으로 인식한다.

### `Unchecked Exception` 활용

`RuntimeException`을 사용하면, `Layer`간의 강결합을 피할 수 있다.  
호출 간에 `Exception`을 명시적으로 전달하지 않아도 되기 때문에 공통 예외 처리부에만 `Exception`을 처리하면 된다.

## `Exception`포함과 `Stack Trace`

`Exception`이 발생하면 `Stack Trace`가 출력된다.
`e.printStackTrace()`를 사용하면 `System.out`으로 `Stack Trace`를 출력하기 때문에, 사용을 지양해야 한다.

기존 `Exception`을 포함하여 새로운 `Exception`을 발생시키면, 기존 `Exception`의 `Stack Trace`가 함께 출력된다.  
해당 로그를 누락한다면 `Exception`의 원인을 파악하기 어렵다.

## `Spring Exception Abstraction`

`Spring`은 `Database`관련 오류를 `DataAccessException`으로 추상화하였다.  
`DataAccessException`은 `RuntimeException`을 상속 받기 때문에, `Check` 할 필요가 없다.  
`DataAccessException`에는 두 가지 종류가 존재하는데 `NonTransientDataAccessException`과 `TransientDataAccessException`이다.  
`Transient`란 `Database`가 잠시 불안정한 상태를 의미한다.  
일시적으로 `Database`가 불안정한 상태라면 `TransientDataAccessException`을 사용하고, 그렇지 않다면 `NonTransientDataAccessException`을 사용한다.

1. `NonTransientDataAccessException`
    - `ConstraintViolationException`
    - `SQL`문법 오류
2. `TransientDataAccessException`
    - `ConnectionException`
    - `TimeoutException`

`SQL` 동작 결과로 `SQLException`이 발생하면, `Spring`은 `SQLExceptionTranslator`를 사용하여 `DataAccessException`으로 변환한다.  
`sql-error-codes.xml`에 `SQLException`에 대한 `DataAccessException`을 정의할 수 있다.  
