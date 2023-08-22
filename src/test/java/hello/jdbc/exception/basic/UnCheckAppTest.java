package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckAppTest {
    private static final Logger log = LoggerFactory.getLogger(UnCheckAppTest.class);

    @Test
    void printEx() {
        final Controller controller = new Controller();
        try {

            controller.request();
        } catch (final Exception e) {
            log.info("예외 처리, message: {}", e.getMessage(), e);
        }
    }

    @Test
    void checked_catch() {
        final Service service = new Service();
        final Controller controller = new Controller();
        assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeException.class);
    }

    static class Controller {
        Service service;

        public void request() {
            this.service.logic();
        }
    }

    static class Service {
        Repository repository;
        NetworkClient networkClient;

        public void logic() {
            this.networkClient.call();
            this.repository.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("Connection Exception");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSqlException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("SQL Exception");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(final String message) {
            super(message);
        }
    }

    static class RuntimeSqlException extends RuntimeException {
        public RuntimeSqlException(final Throwable cause) {
            super(cause);
        }
    }
}
