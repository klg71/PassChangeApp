package plugins;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.passchange.passchangeapp.R;

import ui.MainFragmentActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import core.RequestType;
import core.WebClient;
import core.Website;

public class Steam extends Website {

	private String body;
	private String rsaPubKey;
	private String rsaTimeStamp;
	private String imageId;
	private String imageSolved;
	private String emailId;
	private String transferParameter;
	private String emailAuth;
	private boolean emailAuthBool;

	public Steam(String username, String pass, Activity activity) {
		super(activity);
		initialize(username, pass);
		imageSolved = "";
		imageId = "";
		emailAuth = "";
	}

	public Steam(Activity activity) {
		super(activity);
	}

	public void initialize(String username, String password) {
		super.initialize(username, password);
		authenticated=false;
	}

	@Override
	public void authenticate() throws Exception {
		authenticated=false;
		String post = "";
		String key = "";
		body = webClient.sendRequest("https://store.steampowered.com/login/",
				RequestType.GET, "", "steamPreLogin", false,"",true);
		body = webClient.sendRequest(
				"https://store.steampowered.com/login/getrsakey/",
				RequestType.POST, "username=" + username, "getRsaKey", false);
		if (MainFragmentActivity.DEBUG_ACTIVATED)
			System.out.println(body);
		getRsaKey();
		key = encryptRsa1(pass, rsaPubKey);
		do {
			imageSolved = "";
			try {
				getImage();
			} catch (Exception e) {

				imageSolved = "";
				imageId = "";
			}
			while ((imageId != "") && imageSolved.length() == 0)
				;

			post = "username="
					+ username
					+ "&password="
					+ URLEncoder.encode(key, "UTF-8")
					+ "&remember_login=false&rsatimestamp="
					+ rsaTimeStamp
					+ "&twofactorcode=&emailauth=&loginfriendlyname=&captchagid="
					+ imageId + "&captcha_text="
					+ URLEncoder.encode(imageSolved, "UTF-8")
					+ "&emailsteamid=";
			if (MainFragmentActivity.DEBUG_ACTIVATED)
				System.out.println(post);
			body = webClient.sendRequest(
					"https://store.steampowered.com/login/dologin/",
					RequestType.POST, post, "loginFirst", false,
					"https://store.steampowered.com/login/", false);
			if (MainFragmentActivity.DEBUG_ACTIVATED)
				System.out.println(body);
		} while (body.contains("success\":false") && !body.contains("SteamGuard"));
		if (body.contains("SteamGuard")) {
			emailAuthBool=false;
			getEmailID();
			getEmailAuth();
			while(!emailAuthBool){
			}
			post = "username=" + username + "&password="
					+ URLEncoder.encode(key, "UTF-8")
					+ "&remember_login=false&rsatimestamp=" + rsaTimeStamp
					+ "&twofactorcode=&emailauth=" + emailAuth
					+ "&loginfriendlyname=&captchagid=" + imageId
					+ "&captcha_text="
					+ URLEncoder.encode(imageSolved, "UTF-8")
					+ "&emailsteamid=" + emailId;

			body = webClient.sendRequest(
					"https://store.steampowered.com/login/dologin/",
					RequestType.POST, post, "loginFirst", false,
					"https://store.steampowered.com/login/", false);
		}
		if (MainFragmentActivity.DEBUG_ACTIVATED)
			System.out.println(body);
		validateAuthentification();
		getTransferParameters();
		body = webClient.sendRequest(
				"https://steamcommunity.com/login/transfer/", RequestType.POST,
				transferParameter, "transfer", false,
				"https://store.steampowered.com/login/", false);
		body = webClient.sendRequest("http://store.steampowered.com/",
				RequestType.GET, transferParameter, "storePage", false,"",true);
		// Steam Safe Email Authentification
		HashMap<String, Map<String, Map<String, String>>> cookiesToSafe = new HashMap<String, Map<String, Map<String, String>>>();
		for (Entry<String, Map<String, Map<String, String>>> entryWebSite : webClient
				.getCookies().entrySet()) {
			System.out.println(entryWebSite.getKey());
			for (Entry<String, Map<String, String>> entryCookie : entryWebSite
					.getValue().entrySet()) {
				if (entryCookie.getKey().contains("Machine")) {
					HashMap<String, Map<String, String>> tempCookie = new HashMap<String, Map<String, String>>();
					tempCookie
							.put(entryCookie.getKey(), entryCookie.getValue());
					cookiesToSafe.put(entryWebSite.getKey(), tempCookie);
				}
			}
		}
		setCookies(cookiesToSafe);
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
		if(MainFragmentActivity.DEBUG_ACTIVATED)
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

		Pattern urlPattern = Pattern.compile("('|\")([0-9]){18,18}('|\")",
				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		Matcher m = urlPattern.matcher(body);
		m.find();
		imageId = m.group().substring(1, m.group().length() - 1);
		InputStream in = null;
		try {
			URL url = new URL(
					"https://store.steampowered.com/public/captcha.php?gid="
							+ imageId);
			URLConnection urlConn = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) urlConn;
			httpConn.connect();
			in = httpConn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final Context context = activity.getApplicationContext();
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		Bitmap bmpimg = BitmapFactory.decodeStream(in);
		ImageView iv = new ImageView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				700, 200);
		iv.setLayoutParams(layoutParams);

		iv.setImageBitmap(bmpimg);
		layout.addView(iv);
		final EditText captchaEnter = new EditText(activity);
		layout.addView(captchaEnter);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
						.setMessage("Enter Captcha")
						.setPositiveButton("OK", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								imageSolved = captchaEnter.getText().toString();
							}
						}).setView(layout);
				builder.create().show();

			}

		});
		while (imageSolved.length() == 0)
			;

	}

	private void getEmailAuth() {
		final Context context = activity.getApplicationContext();
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		ImageView iv = new ImageView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				700, 200);
		final EditText authEnter = new EditText(activity);
		layout.addView(authEnter);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
						.setMessage("Enter You Email Authentification")
						.setPositiveButton("OK", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								emailAuth = authEnter.getText().toString();
								emailAuthBool=true;
							}
						}).setView(layout);
				builder.create().show();

			}

		});
		while (emailAuth=="");
	}
	//TODO: Remove fixed timestamp length
	private void getRsaKey() {
		Pattern pattern = Pattern.compile("([A-Z])\\w{50,}");
		Matcher m = pattern.matcher(body);
		while (m.find()) {
			rsaPubKey = m.group().toString();
		}
		pattern = Pattern.compile("\"([0-9]){8,12}\"");
		m = pattern.matcher(body);
		while (m.find()) {
			rsaTimeStamp = m.group().substring(1, m.group().length() - 1);
		}

	}

	private String encryptRsa1(String data, String pubkey) {
		BigInteger modulus = new BigInteger(pubkey, 16);
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
			Log.e("DEBUG",e1.getMessage());
			e1.printStackTrace();
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return new String(Base64.encode(cipher.doFinal(data.getBytes("UTF-8")),
					Base64.NO_WRAP));
		} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void validateAuthentification() throws Exception {
		if (!body.contains("success\":true")){
			Log.e("DEBUG","not authenticated");
			throw new Exception(
					"Login unsuccesful check password,username and typed captcha!");
		}
		authenticated=true;
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
	public String getWebsiteUrl() {
		return "http://store.steampowered.com/";
	}

	@Override
	public int getImageSource() {
		return R.drawable.steam_icon;
	}

}
