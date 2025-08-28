package kafx.secure;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import kafx.controller.Controller;

// PBE / AES-256
public class EncryptionHelper {

	private final static String SecretKeyFactoryMode = "PBKDF2WithHmacSHA256";
	private final static String cipherTransformation = "AES/CBC/PKCS5Padding";
	private final static String secretKeySpecAlgorithm = "AES";
	private final static int iterationCount = 65536;
	private final static int keyLength = 256;

	private final Cipher cipher;
	private byte[] salt;

	/**
	 * 
	 * @param salt 8 bytes
	 */
	public EncryptionHelper(byte[] salt) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(cipherTransformation);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			Controller.exception(e);
		}
		this.cipher = cipher;
		this.salt = salt;
	}

	public Cipher getCipher() {
		return this.cipher;
	}

	public SecretKey generateFromPassword(String password) {
		KeySpec keyspec = new PBEKeySpec(password.toCharArray(), this.salt, iterationCount, keyLength);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(SecretKeyFactoryMode);
			return new SecretKeySpec(factory.generateSecret(keyspec).getEncoded(), secretKeySpecAlgorithm);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			Controller.exception(e);
			return null;
		}
	}

}
