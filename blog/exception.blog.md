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