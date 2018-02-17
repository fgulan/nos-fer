package crypto;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Created by filipgulan on 09/05/2017.
 */
public class DigitalEnvelope {

    public static ArrayList<String> generateEnvelope(String aesKey, byte[] input, Path publicKeyPath) throws Exception {
        AES aes = new AES(aesKey);
        byte[] encryptedData = aes.encrypt(input);

        RSA rsa = new RSA();
        RSAKeysGenerator rsaGenerator = new RSAKeysGenerator(2048);
        byte[] key = aesKey.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedKey = rsa.encrypt(key, rsaGenerator.loadPublicKey(publicKeyPath));

        String base64Key = new String(Base64.getEncoder().encode(encryptedKey));
        String base64Data = new String(Base64.getEncoder().encode(encryptedData));
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Data:");
        lines.add(base64Data);
        lines.add("Key:");
        lines.add(base64Key);
        return lines;
    }

    public static byte[] openEnvelope(String dataString, String keyString, Path privateKeyPath) throws Exception {
        byte[] data = Base64.getDecoder().decode(dataString);
        byte[] key = Base64.getDecoder().decode(keyString);
        RSA rsa = new RSA();
        RSAKeysGenerator rsaGenerator = new RSAKeysGenerator(2048);
        byte[] decryptedKey = rsa.decrypt(key, rsaGenerator.loadPrivateKey(privateKeyPath));
        String aesKey = new String(decryptedKey, StandardCharsets.UTF_8);
        AES aes = new AES(aesKey);
        return aes.decrypt(data);
    }
}
