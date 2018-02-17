package crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAKeysGenerator {

	private KeyPairGenerator generator;
    private PublicKey publicKey;
    private PrivateKey privateKey;

	public RSAKeysGenerator(int length) throws NoSuchAlgorithmException, NoSuchProviderException {
        generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(length);
	}

	public void createKeys() {
		KeyPair pair = generator.generateKeyPair();
		publicKey = pair.getPublic();
		privateKey = pair.getPrivate();
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void storeKeysToFolder(Path folder) throws IOException {
	    writeToFile(folder.resolve("Public.key"), publicKey.getEncoded());
        writeToFile(folder.resolve("Private.key"), privateKey.getEncoded());
    }

    public PrivateKey loadPrivateKey(Path file) throws Exception {
        byte[] keyBytes = Files.readAllBytes(file);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
        return privateKey;
    }

    public PublicKey loadPublicKey(Path file) throws Exception {
        byte[] keyBytes = Files.readAllBytes(file);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
        return publicKey;
    }

    private void writeToFile(Path file, byte[] key) throws IOException {
        Files.write(file, key, StandardOpenOption.CREATE);
    }
}