package local.consolerpg.database;

import local.consolerpg.database.exceptions.PasswordEncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {

    private static final Logger logger = LoggerFactory.getLogger(PasswordEncryptor.class);

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String CHARSET_ENCODING = "UTF-8";

    private PasswordEncryptor() {
    }

    public static String getEncryptedPassword(String password) {
        logger.debug("Start encrypting password");
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream encryptKeyStream = classLoader.getResourceAsStream("encryptPassword.key");

        try(BufferedReader keyReader = new BufferedReader(new InputStreamReader(encryptKeyStream))) {

            String encryptKey = keyReader.readLine();
            Key secretKeySpec = new SecretKeySpec(encryptKey.getBytes(CHARSET_ENCODING), ENCRYPTION_ALGORITHM);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] encryptedPassword = cipher.doFinal(password.getBytes(CHARSET_ENCODING));
            return new String(encryptedPassword, CHARSET_ENCODING);

        } catch (NoSuchPaddingException e) {
            logger.error("Internal password encryption error", e);
            throw new PasswordEncryptionException("Internal error", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Internal password encryption error", e);
            throw new PasswordEncryptionException("Internal error", e);
        } catch (InvalidKeyException e) {
            logger.error("Internal password encryption error", e);
            throw new PasswordEncryptionException("Internal error", e);
        } catch (BadPaddingException e) {
            logger.error("Internal password encryption error", e);
            throw new PasswordEncryptionException("Internal error", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("Internal password encryption error", e);
            throw new PasswordEncryptionException("Internal error", e);
        } catch (IOException e) {
            logger.error("Not found file with encrypt key", e);
            throw new PasswordEncryptionException("Not found file with encrypt key", e);
        }
    }
}
