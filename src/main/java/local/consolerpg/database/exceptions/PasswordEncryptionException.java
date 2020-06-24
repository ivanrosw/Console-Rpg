package local.consolerpg.database.exceptions;

public class PasswordEncryptionException extends RuntimeException{
    public PasswordEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
