package rs.luka.stories.exceptions;

public class ExecutionException extends RuntimeException {
    public ExecutionException(String msg) {
        super(msg);
    }

    public ExecutionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
