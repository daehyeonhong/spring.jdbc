package hello.jdbc.repository.exception;

public class MyDatabaseException extends RuntimeException{
    public MyDatabaseException() {
    }

    public MyDatabaseException(final String message) {
        super(message);
    }

    public MyDatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MyDatabaseException(final Throwable cause) {
        super(cause);
    }
}
