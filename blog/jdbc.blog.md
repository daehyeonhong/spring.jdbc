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

## 3. Connect to `Database`

`JDBC`를 사용하여 `Database`에 접근하는 방법을 알아보자.<br/>
아래는 `JDBC`를 사용하여 `PostgreSQL`에 접근하는 예제이다.<br/>

```java

public class DatabaseConnectionUtil {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionUtil.class);

    public static Connection getConnection() {
        try {
            final Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection: {}, class: {}", connection, connection.getClass());
            return connection;
        } catch (final SQLException sqlException) {
            throw new IllegalStateException(sqlException);
        }
    }
}
```

`DriverManager.getConnection()`을 통해 `Connection`을 생성할 수 있다.<br/>
`DriverManager`는 어떻게 연결해야하는 `Database`를 알고 찾아줄까?

```java
public class DriverManager {
    private static final CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList<>();

    private static Connection getConnection(
            String url, java.util.Properties info, Class<?> caller) throws SQLException {
        ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
        if (callerCL == null || callerCL == ClassLoader.getPlatformClassLoader()) {
            callerCL = Thread.currentThread().getContextClassLoader();
        }

        if (url == null) {
            throw new SQLException("The url cannot be null", "08001");
        }

        println("DriverManager.getConnection(\"" + url + "\")");

        ensureDriversInitialized();

        SQLException reason = null;

        for (DriverInfo aDriver : registeredDrivers) {
            if (isDriverAllowed(aDriver.driver, callerCL)) {
                try {
                    println("    trying " + aDriver.driver.getClass().getName());
                    Connection con = aDriver.driver.connect(url, info);
                    if (con != null) {
                        println("getConnection returning " + aDriver.driver.getClass().getName());
                        return (con);
                    }
                } catch (SQLException ex) {
                    if (reason == null) {
                        reason = ex;
                    }
                }

            } else {
                println("    skipping: " + aDriver.driver.getClass().getName());
            }

        }

        if (reason != null) {
            println("getConnection failed: " + reason);
            throw reason;
        }

        println("getConnection: no suitable driver found for " + url);
        throw new SQLException("No suitable driver found for " + url, "08001");
    }
}
```

먼저 `DriverManager`는 자신에게 등록된 `Driver` 목록(`registeredDrivers`)을 순회하며, 해당 드라이버가 `ClassLoader`를 통해 로딩 되었는지 확인한다.<br/>
만약 유효한(`ClassLoader`를 통해 로딩된) `Driver`를 찾으면 `Driver`의 `connect()`를 호출하여 `Connection`을 시도한다.<br/>
연결이 성공하면 해당 드라이버를 전달하고, 만약 연결이 실패하면 다음 `Driver`를 순서대로 시도해본다.<br/>
모든 `Driver`를 시도해도 연결이 실패하면 `SQLException`을 발생시킨다.<br/>

### 4.1. `JDBC`개발 - `Insert`

`JDBC`를 사용하여 `Database`에 데이터를 삽입하는 방법은 다음과 같다.<br/>

1. `Connection`을 생성한다.
2. `Statement:SQL`를 생성한다.
    - `Statement`는 `SQL`을 실행하는 역할을 한다.
    - `Statement`를 상속받은 `PreparedStatement`를 사용하면 `SQL`을 미리 컴파일하여 재사용할 수 있다.
        - 파라미터 위치에 `?`를 사용하여 `SQL`을 작성하고, `?`에 값을 바인딩하여 `SQL`을 실행한다.
        - `SQLInjection`을 방지할 수 있다.
        -
3. `Statement`를 통해 `SQL`을 실행한다.
4. `ResultSet`을 통해 결과를 받아온다.
5. 자원을 해제한다.
    - 선언의 역순으로 `ResultSet`, `Statement`, `Connection`을 순서대로 해제한다.
    - `Resource` 정리
        - `Resource`는 `AutoClosable`을 구현하고 있어서 `try-with-resource`를 사용하면 자동으로 정리된다.
        - `try-with-resource`를 사용하지 않는다면 `finally`를 사용하여 정리해야 한다.
        - 정리하지 않으면 `Connection`이 계속 남아있어서 `Database`에 접근할 수 있는 `Connection`의 수가 제한되어 있을 경우 문제가 발생할 수 있다.

### 4.2. `JDBC`개발 - `Select`

`JDBC`를 사용하여 `Database`에서 데이터를 조회하면 `ResultSet`을 통해 결과를 받아온다.<br/>
`ResultSet`은 `Cursor`와 같은 역할을 한다.<br/>
먼저 `Statement`를 통해 `SQL`을 실행하면 `ResultSet`에 `Cursor`가 위치하게 되고, 이를 통해 `SQL`의 결과를 순차적으로 조회할 수 있다.<br/>
`ResultSet`의 `next()`를 호출하면 `Cursor`가 다음 행으로 이동하고, `Cursor`가 위치한 행의 데이터를 조회할 수 있다.<br/>
`Cursor`가 위치한 행의 데이터를 조회하고 싶으면 `ResultSet`의 `getXXX()`를 호출하면 된다.<br/>
`XXX`는 조회하고 싶은 데이터의 타입을 의미한다. ex: `getString()`, `getInt()` ...<br/>
`PK` 조회 시 `ResultSet`의 `next()`로 존재 여부를 확인할 수 있고, `Collection` 조회시에는 `while`문을 통해 `Cursor`가 존재하는 동안 데이터를 조회할 수 있다.<br/>

### 4.3. `JDBC`개발 - `Update`, `Delete`

`JDBC`의 `Update`, `Delete`는 `Insert`와 동일하다.<br/>
