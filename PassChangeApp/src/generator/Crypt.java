package generator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
	
	static public String generateMd5(String key){
		byte[] bytesOfMessage = null;
		try {
			bytesOfMessage = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return new String(md.digest(bytesOfMessage));
	}
	
	
	static public void encode( byte[] bytes, OutputStream out, String pass ) throws Exception
	  {
	    Cipher c = Cipher.getInstance( "AES" );
	    Key k = new SecretKeySpec( pass.getBytes(), "AES" );
	    c.init( Cipher.ENCRYPT_MODE, k );

	    OutputStream cos = new CipherOutputStream( out, c );
	    cos.write( bytes );
	    cos.close();
	  }

	  static public byte[] decode( InputStream is, String pass ) throws Exception
	  {
	    Cipher c = Cipher.getInstance( "AES" );
	    Key k = new SecretKeySpec( pass.getBytes(), "AES" );
	    c.init( Cipher.DECRYPT_MODE, k );

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    CipherInputStream cis = new CipherInputStream( is, c );

	    for ( int b; (b = cis.read()) != -1; )
	      bos.write( b );

	    cis.close();
	    return bos.toByteArray();
	  }
}
