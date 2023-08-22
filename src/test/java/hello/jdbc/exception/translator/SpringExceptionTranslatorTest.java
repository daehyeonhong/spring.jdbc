package hello.jdbc.exception.translator;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringExceptionTranslatorTest {
    HikariDataSource dataSource;
private final static Logger log = LoggerFactory.getLogger(SpringExceptionTranslatorTest.class.getName());
    @BeforeEach
    void setUp() {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(ConnectionConstant.URL);
        this.dataSource.setUsername(ConnectionConstant.USERNAME);
        this.dataSource.setPassword(ConnectionConstant.PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        final String sql = "select bad query";
        try {
            final Connection connection = dataSource.getConnection();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(1064);
        }
    }
    @Test
    void sqlExceptionTranslator() {
        final String sql = "select bad query";
        try {
            final Connection connection = dataSource.getConnection();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(1064);
            final SQLErrorCodeSQLExceptionTranslator sqlErrorCodeSQLExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            final DataAccessException result = sqlErrorCodeSQLExceptionTranslator.translate("select", sql, e);
            log.info("result: {}", result);
            assertThat(result).isInstanceOf(BadSqlGrammarException.class);
        }
    }
}
