package account;

import generator.Crypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
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
			Crypt.encode(account.getActualPassword().getBytes(), byteArrayOutputStream, Crypt.generateKey(masterPass, salt));
			String encryptedPass=byteArrayOutputStream.toString("ISO-8859-1");
			hash = webClient
					.sendRequest(
							"http://klg71.us.to/PassChangeServer/sendPassword.php",
							RequestType.POST, "password="+URLEncoder.encode(encryptedPass, "ISO-8859-1"),
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
