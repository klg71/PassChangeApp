package plugins;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.passchange.passchangeapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import core.PassChangeWebsite;
import core.RequestType;
import core.WebClient;
import exceptions.AccountCredentialWrongException;

public class Gmx extends PassChangeWebsite {
	private String body;
	private String post;
	private String statistics;
	private String url;
	private String url2;
	private String token;
	private String captchaId;
	private String captchaValue;
	private String captchaUrl;
	private String newPass;
	private String webSiteUrl;

	public Gmx(String username, String password, Activity activity) {
		super(activity);

		webSiteUrl = "";
		initialize(username, password);
	}

	public void initialize(String username, String password) {
		super.initialize(username, password);
	}

	public Gmx(Activity activity) {
		super(activity);
	}

	@Override
	public void authenticate() throws Exception {
		body = webClient.sendRequest("http://www.gmx.net/", RequestType.GET,
				"", "prelogin", false, "", true);
		getStatistics();
		post = "loginErrorURL="
				+ URLEncoder.encode("http://www.gmx.net/?status=nologin",
						"UTF-8")
				+ "&loginFailedURL="
				+ URLEncoder
						.encode("http://www.gmx.net/logoutlounge/?status=login-failed&site=gmx&agof=97_L&pg=null&pa=-1&pp=___NULL&region=de",
								"UTF-8") + "&password="
				+ URLEncoder.encode(pass, "UTF-8") + "&service=freemail"
				+ "&statistics=" + URLEncoder.encode(statistics, "UTF-8")
				+ "&successURL"
				+ URLEncoder.encode("https://navigator.gmx.net/login", "UTF-8")
				+ "&username="
				+ URLEncoder.encode(username, "UTF-8");
		body = webClient.sendRequest(
				"https://service.gmx.net/de/cgi/login?hal=true",
				RequestType.POST, post, "login", false);
		body = webClient.sendRequest(webClient.getLocation(), RequestType.GET,
				"", "afterLogin", false);
		validateAuthentification();
		getUrlWithoutJS();
		if (url2 != null) {
			body = webClient.sendRequest(url2, RequestType.GET, "",
					"afterLoginJS", false);
		} else {
			body = webClient.sendRequest(webClient.getLocation(),
					RequestType.GET, "", "withoutRemind", false);
		}
		getUrl("navigator\\/show");
		webSiteUrl = url;
		url = url + "#myaccount";
		body = webClient.sendRequest(url, RequestType.GET, "", "afterLogin1",
				false);
	}

	@Override
	public void changePassword(String newPass) throws Exception {
		this.newPass = newPass;
		getMyAccountUrl();
		url = url.replace("\\", "");
		body = webClient.sendRequest(url, RequestType.GET, "", "myAccount",
				false);
		body = webClient.sendRequest(webClient.getLocation(), RequestType.GET,
				"", "myAccountError", false);
		getPrivatePassToken();
		body = webClient.sendRequest(
				"https://service.gmx.net/de/cgi/g.fcgi/config/password?sid="
						+ token, RequestType.GET, "", "myAccountPassWindow",
				false);
		body = webClient.sendRequest(webClient.getLocation(), RequestType.GET,
				"", "myAccountPassWindow", false);
		getCaptcha();
		body = webClient.getPicture(captchaUrl, RequestType.GET, "",
				"getCaptcha", false,
				"https://service.gmx.net/de/cgi/g.fcgi/config/password/change?sid="
						+ token);
		displayCaptcha();
		post = captchaId + "=" + captchaValue + "&buttonSubmitPassword="
				+ URLEncoder.encode("Passwort ändern", "UTF-8")
				+ "&password_new=" + URLEncoder.encode(newPass, "UTF-8")
				+ "&password_new_confirmation="
				+ URLEncoder.encode(newPass, "UTF-8") + "&password_old="
				+ URLEncoder.encode(pass, "UTF-8");
		body = webClient.sendRequest(
				"https://service.gmx.net/de/cgi/g.fcgi/config/password/change?sid="
						+ token, RequestType.POST, post, "afterPassChange",
				false);
		validatePasswordChange();
	}

	private void getCaptcha() {
		Pattern patternUrl = Pattern
				.compile("https:\\/\\/service\\.gmx\\.net\\/[a-z]+\\/cgi\\/g.fcgi\\/misc\\/captcha\\?sid=[0-9a-z\\.]+");
		Matcher matcherUrl = patternUrl.matcher(body);
		if (matcherUrl.find())
			captchaUrl = matcherUrl.group().toString();
		System.out.println(captchaUrl);
		Pattern patternId = Pattern.compile("_[0-9a-f]{10,}");
		Matcher matcherID = patternId.matcher(body);
		if (matcherID.find())
			captchaId = matcherID.group().toString();

	}

	private void displayCaptcha() {

		final Context context = activity.getApplicationContext();
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		Bitmap bmpimg = BitmapFactory.decodeStream(new ByteArrayInputStream(
				webClient.getByteArrayOutputStream().toByteArray()));
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
								captchaValue = captchaEnter.getText()
										.toString();
							}
						}).setView(layout);
				builder.create().show();

			}

		});
		while (captchaValue.length() == 0)
			;

	}

	@Override
	protected void validateAuthentification() throws Exception {
		if (body.contains("login-error")) {
			throw new AccountCredentialWrongException();
		}
		authenticated = true;

	}

	@Override
	protected void validatePasswordChange() throws Exception {
		try {
			pass = newPass;
			authenticate();
			throw new Exception(
					"Password Change unsuccesful! Maybe you entered wrong captcha!");
		} catch (Exception e) {
			succesful = true;
		}

	}

	@Override
	public String getName() {
		return "Gmx";
	}

	@Override
	public String getTopic() {
		return "Social";
	}

	@Override
	public String toString() {
		return "Gmx";
	}

	@Override
	public Integer getId() {
		return 8;
	}

	@Override
	public boolean validatePassword(String pass) {
		return pass.length() > 8;
	}

	@Override
	public String getPasswordCondition() {
		return "Password must be 8 letters long";
	}

	private void getPrivatePassToken() {

		Pattern pattern = Pattern.compile("bahi[0-9a-z.]+");
		Matcher m = pattern.matcher(webClient.getLocation());
		if (m.find()) {
			token = m.group().toString();
		}
	}

	private void getStatistics() {
		Pattern input = Pattern
				.compile("<input type=\"hidden\" name=\"statistics\" value=\"[\\/a-zA-Z0-9+]+");
		Matcher m1 = input.matcher(body);
		if (m1.find()) {
			body = m1.group().toString();

			Pattern pattern = Pattern.compile("[0-9A-Za-z/\\+]{30,}");
			Matcher m = pattern.matcher(body);
			while (m.find()) {
				statistics = m.group().toString();
			}
		}

	}

	private void getMyAccountUrl() {
		Pattern pattern = Pattern
				.compile("https:\\\\\\/\\\\\\/uas2.uilogin\\.de\\\\\\/intern\\\\\\/jump\\?serviceID=navigator\\.genesis\\.gmx&session=[0-9a-f]+&[a-z0-9=%A-F\\.]+&partnerdata=iac_appname%3Dmyaccount[a-z0-9=%A-Z\\._-]+");
		Matcher m = pattern.matcher(body);
		if (m.find()) {
			url = m.group(0).toString();
		}
	}

	private void getUrl(String para) {
		Pattern pattern = Pattern
				.compile("https:\\/\\/navigator\\.gmx\\.net\\/" + para
						+ "\\?sid=[0-9a-f]+");
		Matcher m = pattern.matcher(body);
		if (m.find()) {
			url = m.group(0).toString();
		}

	}

	private void getUrlWithoutJS() {
		Pattern pattern = Pattern
				.compile("https:\\/\\/navigator\\.gmx\\.net\\/remindlogout\\?sid=[0-9a-f]+");
		Matcher m = pattern.matcher(body);
		if (m.find()) {
			url2 = m.group(0).toString();
		}
	}

	@Override
	public String getWebsiteUrl() {
		return webSiteUrl + "#home";
	}

	@Override
	public int getImageSource() {
		return R.drawable.gmx;
	}

}
