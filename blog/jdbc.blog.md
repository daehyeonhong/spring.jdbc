# Spring Data JDBC

## 1. `JDBC` 이해

애플리케이션에서 `DataBase`에 접근하여 데이터를 가져오는 동작에는 다양한 단계가 존재한다.  
먼저 `DataBase`에 접근하기 위해서는 `Connection`을 생성해야 하고, `Connection`을 통해 `Statement:SQL`를 생성하고, `Statement`를 통해 `Query`를
실행하고, `ResultSet:SQLResult`을 통해 결과를 받아와야 한다.  
문제는 각 `Database`별로 해당 동작을 수행하는 방법이 다르다는 것이다.

### 1.1. `JDBC`란?

> JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API이다.  
> JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공한다.
>
> \- [Wikipedia](https://ko.wikipedia.org/wiki/JDBC)

`JDBC`는 `Java`에서 `Database`에 접근하기 위한 `API`이다.  
`JDBC`를 사용하면 `Database`에 접근하기 위한 `Connection`을 생성하고, `Statement`를 생성하고, `Query`를 실행하고, `ResultSet`을 받아오는 등의 작업을 `Java`로
할 수 있다.  
각 `Database`별로 `JDBC`를 지원하는 `Driver`가 존재하며, `Driver`를 통해 `Database`에 접근할 수 있다.  
`Driver`는 해당 `DataBase Vender`에서 제공하는 `Driver`를 사용하면 된다.

### 1.2. `JDBC` 표준화의 한계

각각의 `Database`마다 `SQL`문법이 조금씩 다르다.  
`ANSI SQL`이라는 표준이 존재하지만, `Database`마다 `ANSI SQL`을 완벽하게 지원하지 않는다.  
따라서 `JDBC`를 사용하더라도 `Database`마다 `SQL`문법이 다르기 때문에 `SQL`문법을 변경해야 하는 경우가 발생한다.

## 2. `JDBC`와 최신 데이터 접근 기술

`JDBC`는 1997년에 출시된 이래로 많은 시간이 흘렀으며, 사용법 또한 복잡하다는 단점이 있다.  
최근에는 `JDBC`를 직접 사용하는 것보다는 `JPA`나 `MyBatis`와 같은 `ORM`, `SQL Mapper`를 사용하는 것이 일반적이다.

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
때문에 `SQL`작성만 할 줄 알면 사용에 어려움이 없다.  
반면에 `ORM`은 `SQL`을 직접 작성하지 않아도 되기 때문에 `Database`마다 `SQL`문법이 다르다는 문제점을 해결할 수 있지만, `ORM`을 사용하기 위해서는 `ORM`을 추가로 학습해야 한다는 단점이
있다.  
실무에서 사용하려면 깊이 있는 학습이 필요하다.

### 2.1. `ORM`이란?

> 객체 관계 매핑(Object-relational mapping; ORM)은 데이터베이스와 객체 지향 프로그래밍 언어 간의 호환되지 않는 데이터를 변환하는 프로그래밍 기법이다.  
> 객체 지향 언어에서 사용할 수 있는 "가상" 객체 데이터베이스를 구축하는 방법이다.  
> 객체 관계 매핑을 가능하게 하는 상용 또는 무료 소프트웨어 패키지들이 있고, 경우에 따라서는 독자적으로 개발하기도 한다.
>
> \- [Wikipedia](https://ko.wikipedia.org/wiki/객체_관계_매핑)

## 3. Connect to `Database`

`JDBC`를 사용하여 `Database`에 접근하는 방법을 알아보자.  
아래는 `JDBC`를 사용하여 `PostgreSQL`에 접근하는 예제이다.

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

`DriverManager.getConnection()`을 통해 `Connection`을 생성할 수 있다.  
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

먼저 `DriverManager`는 자신에게 등록된 `Driver` 목록(`registeredDrivers`)을 순회하며, 해당 드라이버가 `ClassLoader`를 통해 로딩 되었는지 확인한다.  
만약 유효한(`ClassLoader`를 통해 로딩된) `Driver`를 찾으면 `Driver`의 `connect()`를 호출하여 `Connection`을 시도한다.  
연결이 성공하면 해당 드라이버를 전달하고, 만약 연결이 실패하면 다음 `Driver`를 순서대로 시도해본다.  
모든 `Driver`를 시도해도 연결이 실패하면 `SQLException`을 발생시킨다.

### 4.1. `JDBC`개발 - `Insert`

`JDBC`를 사용하여 `Database`에 데이터를 삽입하는 방법은 다음과 같다.

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

`JDBC`를 사용하여 `Database`에서 데이터를 조회하면 `ResultSet`을 통해 결과를 받아온다.  
`ResultSet`은 `Cursor`와 같은 역할을 한다.  
먼저 `Statement`를 통해 `SQL`을 실행하면 `ResultSet`에 `Cursor`가 위치하게 되고, 이를 통해 `SQL`의 결과를 순차적으로 조회할 수 있다.  
`ResultSet`의 `next()`를 호출하면 `Cursor`가 다음 행으로 이동하고, `Cursor`가 위치한 행의 데이터를 조회할 수 있다.  
`Cursor`가 위치한 행의 데이터를 조회하고 싶으면 `ResultSet`의 `getXXX()`를 호출하면 된다.  
`XXX`는 조회하고 싶은 데이터의 타입을 의미한다. ex: `getString()`, `getInt()` ...  
`PK` 조회 시 `ResultSet`의 `next()`로 존재 여부를 확인할 수 있고, `Collection` 조회시에는 `while`문을 통해 `Cursor`가 존재하는 동안 데이터를 조회할 수 있다.

### 4.3. `JDBC`개발 - `Update`, `Delete`

`JDBC`의 `Update`, `Delete`는 `Insert`와 동일하다.

### 5. `ConnectionPool`과 `DataSource`

`JDBC`를 사용하여 `Database`에 직접 접근하는 방법은 위와 같다.  
다만 `DriverManager`를 통해 `Connection`을 생성하면 비효율적인 점이 존재한다.  
`Connection`생성의 단계를 간략화하면 아래와 같다.

*`Connection`생성 단계*

1. `DatabaseDriver`를 통해 `Connection`을 생성한다.
2. `DatabaseDriver`는 `Database`에 접근하기 위한 `Socket`을 생성하고, `3-way-handshake`를 통해 `TCP/IP`연결을 수립한다.
3. `TCP/IP` `Connection`이 수립되면 `username`, `password`를 `Database`에 전달한다.
4. `Database`는 `username`, `password`로 인증을 완료하고, 내부에 `DatabaseSession`을 생성한다.
5. `Database`는 `Connection`을 생성했다는 응답을 `DatabaseDriver`에 전달한다.
6. `DatabaseDriver`는 `Connection` 객체를 생성하고, `Client`에 전달한다.

매 요청마다 위와 같은 단계를 거치기 때문에 성능상의 문제가 발생할 수 있다.  
`SQL` 수행 시간보다 `Connection`을 생성하는 시간이 더 오래 걸릴 수 있고, 이는 고객 경험에 영향을 줄 수 있다.  
이러한 문제를 해결하기 위해 `ConnectionPool`을 사용한다.

### 5.1. `ConnectionPool`이란?

`ConnectionPool`은 `Connection`을 미리 생성해두고, 필요할 때마다 `Connection`을 꺼내서 사용하는 방식이다.  
`ServletContainer`에서 `TreadPool`을 사용하는 것처럼 일정한 개수의 `Connection`을 미리 생성해두고, 필요할 때마다 `Connection`을 꺼내서 사용한다.  
`ConnectionPool`을 사용하면 `Connection`을 생성하는 시간을 줄일 수 있고, `Connection`을 재사용할 수 있기 때문에 성능상의 이점이 있다.  
추가로 설정한 `Connection` 이상의 요청이 들어오면 `Connection`을 대기시키기 때문에 `Database`를 보호하는 효과도 있다.

### 5.2. `DataSource`란?

`DataSource`는 `Connection`을 획득하는 방법을 추상화하는 인터페이스이다.

```java
package javax.sql;

public interface DataSource extends CommonDataSource, Wrapper {
    Connection getConnection() throws SQLException;
}
```

`DataSource`는 `Connection`을 획득하는 `getConnection()`을 제공한다.  
사용하는 `ConnectionPool`의 종류에 따라서 `DataSource`를 구현한 클래스가 존재한다.  
`DriverManager`는 `DataSource`의 구현체가 아니기 때문에 `DataManager`를 사용하면 `ConnectionPool`을 사용할 수 없다.  
이 문제를 해결하기 위해 `DriverManager`도 `DataSource`를 구현한 `DriverManagerDataSource`를 제공한다.

### 5.3.1. `DataSource` - `DriverManager`

`DriverManager`는 `Connection`을 획득할 때 마다 인증 정보(`URL`, `USERNAME`, `PASSWORD`)를 전달해야 한다.  
`DataSource`를 사용하면 `Connection`을 획득할 때 마다 인증 정보를 전달할 필요가 없다.  
사용하는 곳에서 단순하게 `dataSource.getConnection()`을 호출하면 된다.

설정과 사용을 분리하여 안전하게 관리가 가능하다.  
`DataSource`를 사용하면 `Connection`을 획득할 때 마다 인증 정보를 전달할 필요가 없기 때문에 `Connection`을 획득하는 코드를 공유할 수 있다.

### 5.3.2. `DataSource` - `HikariCP`

`HikariCP`는 `DataSource`를 구현한 `HikariDataSource`를 제공한다.  
`HikariDataSource`는 `ConnectionPool`을 사용하기 때문에 `Connection`을 획득할 때 마다 `Connection`을 생성하는 단계를 거치지 않는다.  
`HikariCP`는 여러가지 설정을 통해 `ConnectionPool`을 튜닝할 수 있다.

```java
class HikariDataSourceTest {
    @Test
    void hikariCpTest() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyHikariCP");
    }
}
```

`HikariCP`는 `DataSource`를 구현한 `HikariDataSource`를 제공한다.

### 5.3.3. `DataSource` - 적용

`DriverManagerDataSource`를 사용하여 `DataSource`를 적용하면 `Connection` 획득시 인증 정보를 전달할 필요가 없다는 장점이 있지만, `ConnectionPool`을 사용하지
않기 때문에 성능이 떨어진다.  
`DriverManagerDataSource`를 사용한 테스트 코드를 실행하면 요청마다 다른 `Connection`이 생성되는 것을 확인할 수 있다.  
반면에 `HikariDataSource`를 사용하면 `ConnectionPool`을 사용하기 때문에 기존에 생성한 `Connection`을 재사용하고, 불필요한 `Resource`를 낭비하지 않는다.

# `Transaction`

## 1. `Transaction`이란?

`Transaction`은 `Database`의 상태를 변화시키기 위해 수행하는 작업의 단위이다.  
`Transaction`은 `ACID`를 보장해야 한다.

### 1.1. `ACID`란?

> ACID(원자성, 일관성, 고립성, 지속성)는 데이터베이스 트랜잭션이 안전하게 수행된다는 것을 보장하기 위한 성질을 가리키는 약어이다. 짐 그레이는 1970년대 말에 신뢰할 수 있는 트랜잭션 시스템의 이러한
> 특성들을 정의하였으며 자동으로 이들을 수행하는 기술을 개발해 냈다.
>
> 1983년 안드레아스 로이테르(Andreas Reuter)와 테오 해르데르(Theo Härder)는 ACID라는 용어를 만들면서 이를 기술했다.
>
> 데이터베이스에서 데이터에 대한 하나의 논리적 실행단계를 트랜잭션이라고 한다. 예를 들어, 은행에서의 계좌이체를 트랜잭션이라고 할 수 있는데, 계좌이체 자체의 구현은 내부적으로 여러 단계로 이루어질 수 있지만
> 전체적으로는 '송신자 계좌의 금액 감소', '수신자 계좌의 금액 증가'가 한 동작으로 이루어져야 하는 것을 의미한다.
>
> \- [Wikipedia](https://ko.wikipedia.org/wiki/ACID)

|      `ACID`       |                                                                                                                                                                      Description                                                                                                                                                                       |
|:-----------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|  `Atomicity:원자성`  |                                  트랜잭션과 관련된 작업들이 부분적으로 실행되다가 중단되지 않는 것을 보장하는 능력이다. 예를 들어, 자금 이체는 성공할 수도 실패할 수도 있지만 보내는 쪽에서 돈을 빼 오는 작업만 성공하고 받는 쪽에 돈을 넣는 작업을 실패해서는 안된다. 원자성은 이와 같이 중간 단계까지 실행되고 실패하는 일이 없도록 하는 것이다.   `Transaction`은 `All or Nothing`이다. `Transaction`의 모든 작업이 성공하면 `Commit`되고, 하나라도 실패하면 `Rollback`된다.                                  |
| `Consistency:정합성` |                                                                                 트랜잭션 처리 전과 처리 후 데이터 모순이 없는 상태를 유지하는 것을 의미한다. 무결성 제약이 모든 계좌는 잔고가 있어야 한다면 이를 위반하는 트랜잭션은 중단된다.   `Transaction`은 `Database`의 상태를 변화시킨다. `Transaction`이 종료되면 `Database`의 상태는 일관성을 유지해야 한다.                                                                                  |
|  `Isolation:고립성`  | 트랜잭션을 수행 시 다른 트랜잭션의 연산 작업이 끼어들지 못하도록 보장하는 것을 의미한다. 이것은 트랜잭션 밖에 있는 어떤 연산도 중간 단계의 데이터를 볼 수 없음을 의미한다. 은행 관리자는 이체 작업을 하는 도중에 쿼리를 실행하더라도 특정 계좌간 이체하는 양 쪽을 볼 수 없다. 공식적으로 고립성은 트랜잭션 실행내역은 연속적이어야 함을 의미한다. 성능관련 이유로 인해 이 특성은 가장 유연성 있는 제약 조건이다.   `Transaction`은 동시에 실행되더라도 `Transaction`끼리는 서로 영향을 주지 않는다. `Transaction`은 동시에 실행되더라도 `Transaction`의 결과는 동일하다. |
| `Durability:지속성`  |                                      성공적으로 수행된 트랜잭션은 영원히 반영되어야 함을 의미한다. 시스템 문제, DB 일관성 체크 등을 하더라도 유지되어야 함을 의미한다. 전형적으로 모든 트랜잭션은 로그로 남고 시스템 장애 발생 전 상태로 되돌릴 수 있다. 트랜잭션은 로그에 모든 것이 저장된 후에만 commit 상태로 간주될 수 있다.   `Transaction`이 성공하면 `Database`의 상태는 영구적으로 변경된다. `Database`의 상태는 `Transaction`이 성공하기 전으로 되돌릴 수 없다.                                      |

`Isolation:격리성`은 `Transaction`이 동시에 실행되더라도 `Transaction`끼리는 서로 영향을 주지 않는다는 것을 의미한다.  
`Transaction`이 `격리성`을 완전하게 보장하려면, 거의 순서대로 실행해야 하는데, 그렇게 되면 `Database`의 성능이 떨어진다.  
이런 문제로 `ANSI:American National Standards Institute`에서 `격리성`을 4단계로 나누어 정의했다.

## ANSI Isolation Levels

트랜잭션 동시성을 위한 ANSI 표준 isolation level에 따라 다양한 read 현상들이 발생할 수 있습니다.

| Isolation Level                | Description                                                           | Dirty Read:오염 읽기 | Non-Repeatable Read: 비 반복 읽기 | Phantom Read: 팬텀 리드 |
|--------------------------------|-----------------------------------------------------------------------|------------------|------------------------------|---------------------|
| `Serializable:직렬화 가능`          | 가장 엄격한 레벨. 트랜잭션을 순차적으로 실행하므로 동시성 문제가 발생하지 않음                          | No               | No                           | No                  |
| `Repeatable Read: 반복 가능 읽기`    | 커밋되지 않은 변경을 읽지 못하며, 트랜잭션 내에서는 항상 일관된 데이터를 볼 수 있음. 그러나 새로운 레코드 추가는 가능  | No               | No                           | Yes                 |
| `Read Committed: 커밋된 읽기`       | 커밋된 데이터만 읽을 수 있으므로 Dirty Read는 발생하지 않음. 그러나 다른 트랜잭션에서 변경된 데이터를 볼 수 있음 | No               | Yes                          | Yes                 |
| `Read Uncommitted: 커밋되지 않은 읽기` | 다른 트랜잭션에서 커밋되지 않은 데이터도 읽을 수 있음                                        | Yes              | Yes                          | Yes                 |

[^1]: Dirty Read (오염 읽기): 다른 트랜잭션에서 커밋되지 않은 변경사항을 읽을 수 있는 현상.
[^2]: Non-Repeatable Read (비반복 읽기): 동일한 트랜잭션 내에서 두 번의 쿼리 사이에 다른 트랜잭션의 변경사항을 볼 수 있는 현상.
[^3]: Phantom Read (환영 읽기): 동일한 트랜잭션 내에서 두 번의 쿼리 사이에 새로운 레코드가 추가되거나 제거될 수 있는 현상.

단계는 `Read Uncommitted`, `Read Committed`, `Repeatable Read`, `Serializable` 순으로 높은 격리성을 보장한다.  
`Serializable`은 `Transaction`이 완료될 때까지 `Database`의 상태가 변경되지 않는다.

## 2. `Transaction` 개념 이해

`Database`와 `Connection`을 맺으면 하나의 `Session`을 가지게 된다.  
해당 `Session`은 `Transaction`을 가질 수 있고, `Transaction`은 `Database`의 상태를 변경하는 작업의 단위이다.  
`AutoCommit`을 해제한 상태에서 `CUD:데이터 변경`작업을 진행하면, `Commit`을 호출하기 전까지는 임시로 데이터를 저장하게 된다.  
해당 내용은 `Transaction`이 시작된 `Session`에서만 조회할 수 있고(`Isolation Level`에 따라 다를 수 있음), `Transaction`이 `Commit`되면 `
Database`의 상태가 변경된다.  
`Commit`되지 않은 데이터가 다른 `Session`에서 조회(`DirtyRead`) 된다면, 데이터 정합면에서 문제가 발생할 수 있다.  
