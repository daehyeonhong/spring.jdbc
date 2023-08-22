package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckAppTest {

    @Test
    void checked_catch() {
        final Service service = new Service(new Repository(), new NetworkClient());
        final Controller controller = new Controller(service);
        assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeException.class);
    }

    static class Controller {
        private final Service service;

        public Controller(final Service service) {
            this.service = service;
        }

        public void request() {
            this.service.logic();
        }
    }

    static class Service {
        private final Repository repository;
        private final NetworkClient networkClient;


        public Service(final Repository repository, final NetworkClient networkClient) {
            this.repository = repository;
            this.networkClient = networkClient;
        }

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
