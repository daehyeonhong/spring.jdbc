package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConstant.*;

class ConnectionTest {
    private static final Logger log = LoggerFactory.getLogger(ConnectionTest.class);

    @Test
    void driverManager() throws SQLException {
        final Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        final Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection1: {}, class: {}", connection1, connection1.getClass());
        log.info("connection2: {}, class: {}", connection2, connection2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource - 항상 새로운 `Connection`을 생성한다.
        final DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    @Test
    void dataSourceConnectionPoolWithHikariCP() throws SQLException, InterruptedException {
        // HikariCP - `ConnectionPooling`
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyHikariCP");

        useDataSource(dataSource);
        Thread.sleep(1_000 * 5);
    }

    private void useDataSource(final DataSource dataSource) throws SQLException {
        final Connection connection1 = dataSource.getConnection();
        final Connection connection2 = dataSource.getConnection();
        final Connection connection3 = dataSource.getConnection();
        final Connection connection4 = dataSource.getConnection();
        final Connection connection5 = dataSource.getConnection();
        final Connection connection6 = dataSource.getConnection();
        final Connection connection7 = dataSource.getConnection();
        final Connection connection8 = dataSource.getConnection();
        final Connection connection9 = dataSource.getConnection();
        final Connection connection10 = dataSource.getConnection();
        final Connection connection11 = dataSource.getConnection();
        log.info("connection1: {}, class: {}", connection1, connection1.getClass());
        log.info("connection2: {}, class: {}", connection2, connection2.getClass());
        log.info("connection3: {}, class: {}", connection3, connection3.getClass());
        log.info("connection4: {}, class: {}", connection4, connection4.getClass());
        log.info("connection5: {}, class: {}", connection5, connection5.getClass());
        log.info("connection6: {}, class: {}", connection6, connection6.getClass());
        log.info("connection7: {}, class: {}", connection7, connection7.getClass());
        log.info("connection8: {}, class: {}", connection8, connection8.getClass());
        log.info("connection9: {}, class: {}", connection9, connection9.getClass());
        log.info("connection10: {}, class: {}", connection10, connection10.getClass());
        log.info("connection11: {}, class: {}", connection11, connection11.getClass());
    }
}
