# Spring Data JDBC

## 1. `JDBC` 이해

애플리케이션에서 `DataBase`에 접근하여 데이터를 가져오는 동작에는 다양한 단계가 존재한다.<br/>
먼저 `DataBase`에 접근하기 위해서는 `Connection`을 생성해야 하고, `Connection`을 통해 `Statement:SQL`를 생성하고, `Statement`를 통해 `Query`를
실행하고, `ResultSet:SQLResult`을 통해 결과를 받아와야 한다.<br/>
문제는 각 `Database`별로 해당 동작을 수행하는 방법이 다르다는 것이다.<br/>

### 1.1. `JDBC`란?

> JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API이다.<br/>
> JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공한다.<br/>
>
> \- [Wikipedia](https://ko.wikipedia.org/wiki/JDBC)

`JDBC`는 `Java`에서 `Database`에 접근하기 위한 `API`이다.<br/>
`JDBC`를 사용하면 `Database`에 접근하기 위한 `Connection`을 생성하고, `Statement`를 생성하고, `Query`를 실행하고, `ResultSet`을 받아오는 등의 작업을 `Java`로
할 수 있다.<br/>
각 `Database`별로 `JDBC`를 지원하는 `Driver`가 존재하며, `Driver`를 통해 `Database`에 접근할 수 있다.<br/>
`Driver`는 해당 `DataBase Vender`에서 제공하는 `Driver`를 사용하면 된다.<br/>

### 1.2. `JDBC` 표준화의 한계

각각의 `Database`마다 `SQL`문법이 조금씩 다르다.<br/>
`ANSI SQL`이라는 표준이 존재하지만, `Database`마다 `ANSI SQL`을 완벽하게 지원하지 않는다.<br/>
따라서 `JDBC`를 사용하더라도 `Database`마다 `SQL`문법이 다르기 때문에 `SQL`문법을 변경해야 하는 경우가 발생한다.<br/>

## 2. `JDBC`와 최신 데이터 접근 기술

`JDBC`는 1997년에 출시된 이래로 많은 시간이 흘렀으며, 사용법 또한 복잡하다는 단점이 있다.<br/>
최근에는 `JDBC`를 직접 사용하는 것보다는 `JPA`나 `MyBatis`와 같은 `ORM`, `SQL Mapper`를 사용하는 것이 일반적이다.<br/>

- `SQL Mapper`
    - 장점: `JDBC`를 편리하게 사용하도록 도와준다.
        - `SQL` 응답 결과를 `Java`객체로 편리하게 매핑할 수 있다.
        - `JDBC`의 반복적인 코드를 줄일 수 있다.
    - 단점: `SQL`을 직접 작성해야 한다.
        - `SQL`을 직접 작성해야 하기 때문에 `Database`마다 `SQL`문법이 다르다는 문제점이 존재한다.

- `ORM`
    - 장점: 반복적으로 단순한 `SQL`을 직접 작성하지 않아도 된다.
        - `Database`마다 `SQL`문법이 다르다는 문제점을 해결할 수 있다.
    - 대표 기술: `JPA`, `Hibernate`, `EclipseLink`, `Exposed` ...
    - `JPA`는 `Java` 진영의 `ORM 표준`이다. 이것을 구현한 것이 `Hibernate`, `EclipseLink` 등이다.

*SQL Mapper vs ORM*
`SQL Mapper`는 `SQL`을 직접 작성해야 하기 때문에 `Database`마다 `SQL`문법이 다르다는 문제점이 존재하지만, `SQL`만 작성하면 나머지 부분은 `SQL Mapper`가 처리해주기
때문에 `SQL`작성만 할 줄 알면 사용에 어려움이 없다.<br/>
반면에 `ORM`은 `SQL`을 직접 작성하지 않아도 되기 때문에 `Database`마다 `SQL`문법이 다르다는 문제점을 해결할 수 있지만, `ORM`을 사용하기 위해서는 `ORM`을 추가로 학습해야 한다는 단점이
있다.<br/>
실무에서 사용하려면 깊이 있는 학습이 필요하다.<br/>

### 2.1. `ORM`이란?

> 객체 관계 매핑(Object-relational mapping; ORM)은 데이터베이스와 객체 지향 프로그래밍 언어 간의 호환되지 않는 데이터를 변환하는 프로그래밍 기법이다.<br/>
> 객체 지향 언어에서 사용할 수 있는 "가상" 객체 데이터베이스를 구축하는 방법이다.<br/>
> 객체 관계 매핑을 가능하게 하는 상용 또는 무료 소프트웨어 패키지들이 있고, 경우에 따라서는 독자적으로 개발하기도 한다.<br/>
>
> \- [Wikipedia](https://ko.wikipedia.org/wiki/객체_관계_매핑)
