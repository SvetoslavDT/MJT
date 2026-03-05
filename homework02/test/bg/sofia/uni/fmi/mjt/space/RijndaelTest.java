package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RijndaelTest {

    private Rijndael rijndael;

    @Test
    void testConstructorWithNullParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Rijndael(null),
            "Expected to be throw IllegalArgumentException.");
    }

    @Test
    void testConstructorWithCorrectParameters() throws NoSuchAlgorithmException {
        SecretKey secretKey = Rijndael.generateSecretKey();
        assertDoesNotThrow(() -> new Rijndael(secretKey),
            "Expected corrected initialisation of rijndael");
    }

    @Test
    void testGenerateSecretKeyReturnsSecretKey() throws NoSuchAlgorithmException {
        SecretKey secretKey = Rijndael.generateSecretKey();
        assertInstanceOf(SecretKey.class, secretKey, "Expected to be returned secretKey type.");
    }

    @Test
    void testGenerateSecretKeyDoesNotThrow() throws NoSuchAlgorithmException {
        assertDoesNotThrow(Rijndael::generateSecretKey, "Expected no thrown exception.");
    }

    @Test
    void testEncryptDoesNotThrow() throws NoSuchAlgorithmException {
        SecretKey secretKey = Rijndael.generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);

        byte[] byteArray = "work test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            assertDoesNotThrow(() -> rijndael.decrypt(in, out), "Expected no thrown exception.");
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void testEncryptNotBlankEncrypted() throws NoSuchAlgorithmException {
        SecretKey secretKey = Rijndael.generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);

        byte[] byteArray = "work test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            rijndael.encrypt(in, out);
            assertTrue(out.size() > 0, "Expected bytes to be encrypted.");
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testDecryptDoesNotThrow() throws NoSuchAlgorithmException {
        SecretKey secretKey = Rijndael.generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);

        byte[] byteArray = "work test".getBytes(StandardCharsets.UTF_8);
        byte[] cipherBytes;

        try (ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
             ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream()) {

            rijndael.encrypt(in, encryptedOut);
            cipherBytes = encryptedOut.toByteArray();
        } catch (Exception e) {
            fail(e);
            return;
        }

        try (ByteArrayInputStream encryptedIn = new ByteArrayInputStream(cipherBytes);
             ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream()) {

            assertDoesNotThrow(() -> rijndael.decrypt(encryptedIn, decryptedOut),
                "Expected no thrown exception.");
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testEncryptAndDecryptWorksCorrectly() throws NoSuchAlgorithmException {
        SecretKey secretKey = Rijndael.generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);

        String originalString = "work test";
        byte[] original = originalString.getBytes(StandardCharsets.UTF_8);
        byte[] cipherBytes;

        try (ByteArrayInputStream in = new ByteArrayInputStream(original);
             ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream()) {

            rijndael.encrypt(in, encryptedOut);
            cipherBytes = encryptedOut.toByteArray();
        } catch (Exception e) {
            fail(e);
            return;
        }

        try (ByteArrayInputStream encryptedIn = new ByteArrayInputStream(cipherBytes);
             ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream()) {

            rijndael.decrypt(encryptedIn, decryptedOut);
            String decyptedString = decryptedOut.toString(StandardCharsets.UTF_8);

            assertEquals(originalString, decyptedString, "Expected different string");
        } catch (Exception e) {
            fail(e);
        }
    }
}