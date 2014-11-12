package account;

import generator.Crypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;

import core.RequestType;
import core.WebClient;

public class AccountExporter {
	private WebClient webClient;
	private String salt;
	private String masterPass;

	public AccountExporter(String masterPass) {
		this.masterPass=masterPass;
		salt = "1234567890ABCDEFGHIJKLMONPQRSTUVWXYZ";
		webClient = new WebClient();
	}
	
	public String toHex(String arg) throws UnsupportedEncodingException{
		return String.format("%x",new BigInteger(1,arg.getBytes("ISO-8859-1")));
	}
	
	public String exportAccount(Account account) throws Exception {
		String hash = "";
		try {
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			Crypt.encode(account.getActualPassword().getBytes("ISO-8859-1"), byteArrayOutputStream, Crypt.generateKey(masterPass, salt));
			String encryptedPass=byteArrayOutputStream.toString("ISO-8859-1");
			System.out.println("Encrypted Pass:"+encryptedPass);
			//encryptedPass=new String(Crypt.decode(new ByteArrayInputStream(encryptedPass.getBytes("ISO-8859-1")),Crypt.generateKey(masterPass, salt)),"ISO-8859-1");
			hash = webClient
					.sendRequest(
							"http://klg71.us.to/PassChangeServer/sendPassword.php",
							RequestType.POST, "password="+toHex(URLEncoder.encode(encryptedPass, "ISO-8859-1")),
							"exportAnswer", false);
			if(hash.contains("failed")){
				throw new Exception("Export failed");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;
	}
}
