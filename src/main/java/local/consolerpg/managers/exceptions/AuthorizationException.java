package local.consolerpg.managers.exceptions;

public class AuthorizationException extends RuntimeException{
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
