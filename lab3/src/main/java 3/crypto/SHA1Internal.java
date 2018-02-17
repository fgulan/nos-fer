package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by filipgulan on 09/05/2017.
 */
public class SHA1Internal {

    public static String digest(byte[] input) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = sha1.digest(input);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
