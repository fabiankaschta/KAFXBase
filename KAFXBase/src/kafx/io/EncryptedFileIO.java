package kafx.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.NoSuchElementException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import kafx.controller.Controller;

public abstract class EncryptedFileIO extends FileIO {

	@Override
	public boolean readFromFile(File file)
			throws InvalidKeyException, IOException, InvalidAlgorithmParameterException, ClassNotFoundException {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] iv = fileInputStream.readNBytes(16); // iv length
			SecretKey secretKey = Controller.requestSecretKey();
			Cipher cipher = Controller.getEncryptionHelper().getCipher();
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			try {
				return read(new CipherInputStream(fileInputStream, cipher));
			} catch (StreamCorruptedException e) {
				Controller.invalidPassword();
				return readFromFile(file);
			}
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public boolean writeToFile(File file) throws InvalidKeyException, IOException {
		SecretKey secretKey = Controller.getSecretKey();
		Cipher cipher = Controller.getEncryptionHelper().getCipher();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		fileOutputStream.write(cipher.getIV());
		return write(new CipherOutputStream(fileOutputStream, cipher));
	}

}
