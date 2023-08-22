package hello.jdbc.exception.translator;


import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConstant;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.exception.MyDatabaseException;
import hello.jdbc.repository.exception.MyDuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

class ExceptionTranslatorV1Test {
    private
    static final Logger log = LoggerFactory.getLogger(ExceptionTranslatorV1Test.class.getName());
    Repository repository;
    Service service;

    @BeforeEach
    void setUp() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(ConnectionConstant.URL);
        dataSource.setUsername(ConnectionConstant.USERNAME);
        dataSource.setPassword(ConnectionConstant.PASSWORD);
        this.repository = new Repository(dataSource);
        this.service = new Service(this.repository);
    }

    @Test
    void create() {
        this.service.create("memberA");
    }

    static class Service {
        private final Repository repository;

        Service(final Repository repository) {
            this.repository = repository;
        }

        public void create(final String memberId) {
            try {
                this.repository.save(new Member(memberId, 0));
            } catch (final MyDuplicateKeyException myDuplicateKeyException) {
                log.info("myDuplicateKeyException", myDuplicateKeyException);
                final String retryId = generateNewId(memberId);
                log.info("retryId: {}", retryId);
                this.repository.save(new Member(retryId, 0));
            } catch (final MyDatabaseException myDatabaseException) {
                log.info("myDatabaseException", myDatabaseException);
                throw myDatabaseException;
            }
        }

        private static String generateNewId(final String memberId) {
            return memberId + UUID.randomUUID().toString().substring(0, 2);
        }
    }

    static class Repository {
        private final DataSource dataSource;

        Repository(final DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Member save(final Member member) {
            final String sql = "INSERT INTO MEMBER(MEMBER_ID, MONEY) VALUES (?, ?)";
            log.info("sql: {}", sql);
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = dataSource.getConnection();
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, member.getMemberId());
                preparedStatement.setInt(2, member.getMoney());
                final int affectedRows = preparedStatement.executeUpdate();
                log.info("affectedRows: {}", affectedRows);
                return member;
            } catch (SQLException sqlException) {
                if (Objects.equals(sqlException.getSQLState(), "23505")) {
                    throw new MyDuplicateKeyException(sqlException);
                }
                throw new MyDatabaseException(sqlException);
            } finally {
                JdbcUtils.closeStatement(preparedStatement);
                JdbcUtils.closeConnection(connection);
            }
        }
    }
}
