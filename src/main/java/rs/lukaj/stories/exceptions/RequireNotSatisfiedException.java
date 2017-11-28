package rs.lukaj.stories.exceptions;

public class RequireNotSatisfiedException extends PreprocessingException {
    public RequireNotSatisfiedException(String msg) {
        super(msg);
    }

    public RequireNotSatisfiedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
