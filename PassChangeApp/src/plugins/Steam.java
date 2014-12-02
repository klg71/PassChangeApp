package plugins;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.util.Base64;
import core.RequestType;
import core.WebClient;
import core.Website;

public class Steam extends Website {

	private final String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	private String body;
	private WebClient webClient;
	private String rsaPubKey;
	private String rsaTimeStamp;
	private String imageId;
	private String imageSolved;
	private String emailId;
	private String transferParameter;

	public Steam(String username, String pass,Activity activity) {
		super(activity);
		initialize(username, pass);
		webClient = new WebClient();
		imageSolved = "";
		imageId = "";
	}

	public Steam(Activity activity) {
		super(activity);
	}

	public void initialize(String username, String password) {
		super.initialize(username, password);
		webClient = new WebClient();
	}

	@Override
	public void authenticate() throws Exception {
		webClient.setCookie("steampowered.com", "steamMachineAuth76561198141740116", "D402F133A7F98C6717AC776C6310372195C39825");
		body = webClient.sendRequest("https://store.steampowered.com/login/",
				RequestType.GET, "", "steamPreLogin", false);
		try {
			getImage();
		} catch (Exception e) {

			imageSolved = "";
			imageId = "";
		}
		body = webClient.sendRequest(
				"https://store.steampowered.com/login/getrsakey/",
				RequestType.POST, "username=" + username, "getRsaKey", false);
		getRsaKey();
		
		String key = encryptRsa1(pass, rsaPubKey);
		String post = "username=" + username + "&password="
				+ URLEncoder.encode(key, "UTF-8")
				+ "&remember_login=false&rsatimestamp=" + rsaTimeStamp
				+ "&twofactorcode=&emailauth=&loginfriendlyname=&captchagid="
				+ imageId + "&captcha_text="
				+ URLEncoder.encode(imageSolved, "UTF-8") + "&emailsteamid=";
		body = webClient.sendRequest(
				"https://store.steampowered.com/login/dologin/",
				RequestType.POST, post, "loginFirst", false,
				"https://store.steampowered.com/login/",false);
		System.out.println(body);
		if (body.contains("SteamGuard")) {
			getEmailID();
			Scanner scanner = new Scanner(System.in);
			String auth = scanner.nextLine();
			post = "username=" + username + "&password="
					+ URLEncoder.encode(key, "UTF-8")
					+ "&remember_login=false&rsatimestamp=" + rsaTimeStamp
					+ "&twofactorcode=&emailauth=" + auth
					+ "&loginfriendlyname=&captchagid=" + imageId
					+ "&captcha_text="
					+ URLEncoder.encode(imageSolved, "UTF-8")
					+ "&emailsteamid=" + emailId;

			body = webClient.sendRequest(
					"https://store.steampowered.com/login/dologin/",
					RequestType.POST, post, "loginFirst", false,
					"https://store.steampowered.com/login/",false);
		}
		System.out.println(body);
		validateAuthentification();
		getTransferParameters();
		body = webClient.sendRequest(
				"https://steamcommunity.com/login/transfer/", RequestType.POST,
				transferParameter, "transfer", false,
				"https://store.steampowered.com/login/",false);
		body = webClient.sendRequest(
				"http://store.steampowered.com/", RequestType.GET,
				transferParameter, "storePage", false);
		
		for(Entry<String, Map<String, Map<String, String>>> entryWebSite:webClient.getCookies().entrySet()){
			
		}
		

	}

	private void getTransferParameters() {

		Pattern pattern = Pattern.compile("\"transfer_parameters\".+");
		Matcher m = pattern.matcher(body);
		String transferString1 = "";
		String transferString = "";
		if (m.find()) {
			transferString1 = m.group().toString();
		}

		pattern = Pattern.compile("[0-9]{17,17}");
		m = pattern.matcher(transferString1);
		if (m.find())
			transferString = "steamid=" + m.group().toString();

		pattern = Pattern.compile("[0-9A-F]{40,40}");
		m = pattern.matcher(transferString1);
		if (m.find())
			transferString = transferString + "&token=" + m.group().toString();

		pattern = Pattern.compile("[0-9a-f]{32,32}");
		m = pattern.matcher(transferString1);
		if (m.find())
			transferString = transferString + "&auth=" + m.group().toString();

		transferString = transferString + "&remember_login=false";

		pattern = Pattern.compile("[0-9A-F]{40,40}");
		m = pattern.matcher(transferString1);
		if (m.find())
			if (m.find())
				transferString = transferString + "&webcookie="
						+ m.group().toString();

		pattern = Pattern.compile("[0-9A-F]{40,40}");
		m = pattern.matcher(transferString1);
		if (m.find())
			if (m.find())
				if (m.find())
					transferString = transferString + "&token_secure="
							+ m.group().toString();
		transferParameter = transferString;
		System.out.println(transferString);

	}

	private void getEmailID() {
		Pattern pattern = Pattern.compile("[0-9]{17,17}");
		Matcher m = pattern.matcher(body);
		while (m.find()) {
			emailId = m.group().toString();
		}
	}

	private void getImage() {

//		Pattern urlPattern = Pattern.compile("('|\\\")([0-9]){18,18}('|\\\")",
//				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//
//		Matcher m = urlPattern.matcher(body);
//		m.find();
//		imageId = m.group().substring(1, m.group().length() - 1);
//
//		try {
//			BufferedImage captchaImage = ImageIO.read(new URL(
//					"https://store.steampowered.com/public/captcha.php?gid="
//							+ imageId));
//
//			JLabel picLabel = new JLabel(new ImageIcon(captchaImage));
//			JTextField captchaField = new JTextField();
//			JPanel dialogPanel = new JPanel();
//			dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
//			dialogPanel.add(picLabel);
//			dialogPanel.add(captchaField);
//			JOptionPane.showMessageDialog(null, dialogPanel, "About",
//					JOptionPane.PLAIN_MESSAGE, null);
//			imageSolved = captchaField.getText();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void getRsaKey() {
		Pattern pattern = Pattern.compile("([A-Z])\\w{50,}");
		Matcher m = pattern.matcher(body);
		while (m.find()) {
			rsaPubKey = m.group().toString();
		}
		pattern = Pattern.compile("\"([0-9]){12,12}\"");
		m = pattern.matcher(body);
		while (m.find()) {
			rsaTimeStamp = m.group().substring(1, m.group().length() - 1);
		}

	}

	private String encryptRsa1(String data, String pubkey) {
		BigInteger modulus = new BigInteger(pubkey, 16);
		System.out.println("Modulus: " + modulus);
		BigInteger encryptionExponent = new BigInteger("010001", 16);
		PublicKey key = null;

		try {
			key = KeyFactory.getInstance("RSA").generatePublic(
					new RSAPublicKeySpec(modulus, encryptionExponent));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return new String(Base64.encode(
					cipher.doFinal(data.getBytes()),Base64.DEFAULT));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}


	@Override
	protected void validateAuthentification() throws Exception {
		if(!body.contains("success\":true"))
			throw new Exception("Login unsuccesful check password,username and typed captcha!");

	}

	

	@Override
	public String getName() {
		return "Steam";
	}

	@Override
	public String getTopic() {
		return "Games";
	}

	@Override
	public String toString() {
		return "Steam";
	}

	@Override
	public Integer getId() {
		return 8;
	}


	@Override
	public String getPasswordCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWebsiteUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getImageSource() {
		// TODO Auto-generated method stub
		return 0;
	}

}
