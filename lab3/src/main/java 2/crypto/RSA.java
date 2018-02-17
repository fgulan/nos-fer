package crypto;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

public class RSA {
    private Cipher cipher;

	public RSA() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.cipher = Cipher.getInstance("RSA");
	}

	public byte[] encrypt(byte[] input, Key key) throws IOException, GeneralSecurityException {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(input);
	}

	public byte[] decrypt(byte[] input, Key key) throws IOException, GeneralSecurityException {
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(input);
	}
}
