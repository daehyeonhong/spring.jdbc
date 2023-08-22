package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CheckedTest {
    private final static Logger log = LoggerFactory.getLogger(CheckedTest.class);

    @Test
    void checked_catch() {
        final Service service = new Service(new Repository());
        service.callCatch();
    }

    @Test
    void checked_throw() {
        final Service service = new Service(new Repository());
        Assertions.assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyCheckedException.class);
    }


    static class MyCheckedException extends Exception {
        MyCheckedException(final String message) {
            super(message);
        }
    }

    static class Service {
        private final Repository repository;

        Service(final Repository repository) {
            this.repository = repository;
        }

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                this.repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message: {}", e.getMessage(), e);
            }
        }

        /**
         * Checked Exception is thrown, not caught.
         *
         * @throws MyCheckedException 예외
         */
        public void callThrow() throws MyCheckedException {
            this.repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("MyCheckedException");
        }
    }
}
