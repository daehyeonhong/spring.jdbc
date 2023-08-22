package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CheckAppTest {

    @Test
    void checked_catch() {
        final Service service = new Service(new Repository(), new NetworkClient());
        final Service.Controller controller = new Service.Controller(service);
        assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    static class Service {
        private final Repository repository;
        private final NetworkClient networkClient;

        static class Controller {
            private final Service service;

            public Controller(final Service service) {
                this.service = service;
            }

            public void request() throws SQLException, ConnectException {
                this.service.logic();
            }
        }

        public Service(final Repository repository, final NetworkClient networkClient) {
            this.repository = repository;
            this.networkClient = networkClient;
        }

        public void logic() throws ConnectException, SQLException {
            this.networkClient.call();
            this.repository.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("Connection Exception");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("SQL Exception");
        }
    }
}
