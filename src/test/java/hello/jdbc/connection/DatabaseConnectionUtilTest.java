package hello.jdbc.connection;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConnectionUtilTest {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionUtilTest.class);

    @Test
    void getConnection() {
        final Connection connection = DatabaseConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
