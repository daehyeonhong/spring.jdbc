package hello.jdbc.connection;

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

    private void useDataSource(final DataSource dataSource) throws SQLException {
        final Connection connection1 = dataSource.getConnection();
        final Connection connection2 = dataSource.getConnection();
        log.info("connection1: {}, class: {}", connection1, connection1.getClass());
        log.info("connection2: {}, class: {}", connection2, connection2.getClass());
    }
}
