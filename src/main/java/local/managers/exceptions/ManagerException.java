package local.managers.exceptions;

public class ManagerException extends RuntimeException {

    public ManagerException(String message) {
        super(message);
    }

    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
