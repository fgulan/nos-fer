package crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by filipgulan on 06/05/2017.
 */
public class AES {

    private byte[] key;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String INIT_VECTOR = "RandomInitVector";
    private static final String PASSWORD_HASH_ALGORITHM = "SHA-256";

    public AES(byte[] key) {
        this.key = key;
    }

    public AES(String key) {
        this(key.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] encrypt(byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = buildKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = buildKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private SecretKeySpec buildKey(byte[] key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digester = MessageDigest.getInstance(PASSWORD_HASH_ALGORITHM);
        digester.update(key);
        byte[] newKey = Arrays.copyOfRange(digester.digest(), 0, 16);
        SecretKeySpec spec = new SecretKeySpec(newKey, "AES");
        return spec;
    }
}
