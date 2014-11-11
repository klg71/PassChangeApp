package account;

import generator.Crypt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteOrder;

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
			String encryptedPass=new String(byteArrayOutputStream.toByteArray());
			hash = webClient
					.sendRequest(
							"http://klg71.us.to/PassChangeServer/sendPassword",
							RequestType.POST, URLEncoder.encode("password="
									+ encryptedPass, "UTF-8"),
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
