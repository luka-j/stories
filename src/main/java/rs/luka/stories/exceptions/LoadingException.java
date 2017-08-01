package rs.luka.stories.exceptions;

public class LoadingException extends RuntimeException {
    public LoadingException(String msg) {
        super(msg);
    }

    public LoadingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
