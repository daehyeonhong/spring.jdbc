package hello.jdbc.repository.exception;

public class MyDuplicateKeyException extends MyDatabaseException {
    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(final String message) {
        super(message);
    }

    public MyDuplicateKeyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(final Throwable cause) {
        super(cause);
    }
}
