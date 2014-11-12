package generator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class Crypt {

	static public String generateMd5(String key) {
		byte[] bytesOfMessage = null;
		bytesOfMessage = key.getBytes();

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return new String(md.digest(bytesOfMessage));
	}

	static public String exportEncode(String pass, String text) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
	
		String key64=new String(Base64.encode((pass.substring(0,18)).getBytes(),Base64.DEFAULT));
		SecretKey key = new SecretKeySpec(pass.substring(0,16).getBytes(), "AES");
			AlgorithmParameterSpec iv = new IvParameterSpec(
			    Base64.decode("5D9r9ZVzEYYgha93/aUK2w==",Base64.DEFAULT)); 
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		String ret=new String(Base64.encode(cipher.doFinal(text
				.getBytes("UTF-8")),Base64.DEFAULT));
		ret=ret.replace("\r","");
		ret=ret.replace("\n","");
		ret=ret.replace(System.getProperty("line.separator"),"");
		ret=ret.replace("\\s+","");
		return ret;

	}

	static public String generateKey(String pass, String salt) throws Exception {
		String key = "";
		if (pass.length() > 24) {
			throw new Exception("Password to long");
		} else {
			pass = pass + salt.substring(0, 23 - pass.length());
		}
		key = new String(Base64.encode(pass.getBytes(), Base64.DEFAULT));
		key = key.substring(1);
		return key;
	}

	static public void encode(byte[] bytes, OutputStream out, String pass)
			throws Exception {
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Key k = new SecretKeySpec(pass.getBytes(), "AES");
		c.init(Cipher.ENCRYPT_MODE, k);

		OutputStream cos = new CipherOutputStream(out, c);
		cos.write(bytes);
		cos.close();
	}

	static public byte[] decode(InputStream is, String pass) throws Exception {
		Cipher c = Cipher.getInstance("AES");
		Key k = new SecretKeySpec(pass.getBytes(), "AES");
		c.init(Cipher.DECRYPT_MODE, k);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		CipherInputStream cis = new CipherInputStream(is, c);

		for (int b; (b = cis.read()) != -1;)
			bos.write(b);

		cis.close();
		return bos.toByteArray();
	}
}
