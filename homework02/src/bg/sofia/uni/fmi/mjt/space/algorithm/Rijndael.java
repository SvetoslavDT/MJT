package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {

    private static final int KILOBYTE = 1024;
    public static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("SecretKey can't be null");
        }

        this.secretKey = secretKey;
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);

        return keyGenerator.generateKey();
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (OutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
                 InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

                byte[] buffer = new byte[KILOBYTE];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }

            } catch (IOException e) {
                throw new UncheckedIOException("IO exception thrown", e);
            }

        } catch (Exception e) {
            throw new CipherException("Cipher exception thrown", e);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (BufferedInputStream encryptedInputStream = new BufferedInputStream(inputStream);
                 OutputStream decryptedOutputStream = new CipherOutputStream(outputStream, cipher)) {

                byte[] buffer = new byte[KILOBYTE];
                int bytesRead;

                while ((bytesRead = encryptedInputStream.read(buffer)) != -1) {
                    decryptedOutputStream.write(buffer, 0, bytesRead);
                }

            } catch (IOException e) {
                throw new UncheckedIOException("IO exception thrown", e);
            }

        } catch (Exception e) {
            throw new CipherException("Cipher exception thrown", e);
        }
    }
}