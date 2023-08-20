# Spring Data JDBC

## 1. `JDBC` 이해

애플리케이션에서 `DataBase`에 접근하여 데이터를 가져오는 동작에는 다양한 단계가 존재한다.<br/>
먼저 `DataBase`에 접근하기 위해서는 `Connection`을 생성해야 하고, `Connection`을 통해 `Statement:SQL`를 생성하고, `Statement`를 통해 `Query`를
실행하고, `ResultSet:SQLResult`을 통해 결과를 받아와야 한다.<br/>
문제는 각 `Database`별로 해당 동작을 수행하는 방법이 다르다는 것이다.<br/>

### 1.1. `JDBC`란?

> JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API이다. JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공한다.
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
