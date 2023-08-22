package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UncheckedException {
    private final static Logger log = LoggerFactory.getLogger(UncheckedException.class);

    @Test
    void unchecked_catch() {
        final Service service = new Service(new Repository());
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        final Service service = new Service(new Repository());
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUnCheckedException.class);
    }

    /**
     * Exceptions that inherit from runtimeException become unchecked exceptions
     */
    static class MyUnCheckedException extends RuntimeException {
        MyUnCheckedException(final String message) {
            super(message);
        }
    }

    /**
     * Unchecked Exception is thrown, not caught.
     * Automatically throws it out if it doesn't catch an exception
     */
    static class Service {
        private final Repository repository;

        Service(final Repository repository) {
            this.repository = repository;
        }

        public void callCatch() {
            try {
                this.repository.call();
            } catch (MyUnCheckedException e) {
                log.info("예외 처리, message: {}", e.getMessage(), e);
            }
        }

        public void callThrow() {
            this.repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUnCheckedException("Unchecked Exception");
        }
    }
}
