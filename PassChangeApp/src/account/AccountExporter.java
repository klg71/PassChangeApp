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
	

	
	public String exportAccount(Account account) throws Exception {
		String hash = "";
		try {
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			String encryptedPass=Crypt.exportEncode(Crypt.generateKey(masterPass, salt), account.getActualPassword());
			hash = webClient
					.sendRequest(
							"http://klg71.us.to/PassChangeServer/sendPassword.php",
							RequestType.POST, "password="+encryptedPass,
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
