package plugins;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.passchange.passchangeapp.R;

import ui.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import core.PassChangeWebsite;
import core.RequestType;
import core.WebClient;
import core.Website;
import exceptions.AccountCredentialWrongException;

public class LeagueOfLegends extends PassChangeWebsite {
	private String body;
	private ImageView captchaImage;
	private String solvedTask;
	private String recaptchaID;
	private Handler captchaHandler;

	public LeagueOfLegends(String username, String password, Activity activity) {
		super(activity);
		initialize(username, password);
	}

	public void initialize(String username, String password) {
		super.initialize(username, password);
		body = "";
		webClient = new WebClient();
		solvedTask = "";
		recaptchaID = "";
		succesful = false;
		authenticated=false;
	}

	public LeagueOfLegends(Activity activity) {
		super(activity);
	}

	@Override
	public void authenticate() throws Exception {
		// /get Cookies

		webClient.sendRequest("http://euw.leagueoflegends.com/de/news/",
				RequestType.GET, "", "lolStart", false);
		webClient.sendRequest("https://account.leagueoflegends.com/login",
				RequestType.GET, "", "lologin", false);

		body = webClient
				.sendRequest(											  
						"https://www.google.com/recaptcha/api/challenge?k=6LcwdeESAAAAAJg_ltVGdjrqlf7Bmbg449SyUcSW&ajax=1&lang=de",
						RequestType.GET, "", "lolCaptcha", false);
		getImage();
		
		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(solvedTask);

		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(recaptchaID);
		String post = "";
		post = "username=" + URLEncoder.encode(username, "UTF-8")
				+ "&password=" + URLEncoder.encode(pass, "UTF-8")
				+ "&recaptcha_challenge_field="
				+ URLEncoder.encode(recaptchaID, "UTF-8")
				+ "&recaptcha_response_field="
				+ URLEncoder.encode(solvedTask, "UTF-8");
		webClient.setCookie("leagueoflegends.com", "PVPNET_REGION", "euw");
		webClient.setCookie("leagueoflegends.com", "PVPNET_LANG", "de_DE");
		body = webClient
				.sendRequest(
						"https://account.leagueoflegends.com/auth",
						RequestType.POST,
						post,
						"lollogin",
						false,
						"https://account.leagueoflegends.com/pm.html?xdm_e=http%3A%2F%2Feuw.leagueoflegends.com&xdm_c=default3117&xdm_p=1",false);
		validateAuthentification();
	}

	@Override
	public void changePassword(String newPass) throws Exception {
		String post = "password-old=" + URLEncoder.encode(pass, "UTF-8")
				+ "&password-new=" + URLEncoder.encode(newPass, "UTF-8")
				+ "&password-confirm=" + URLEncoder.encode(newPass, "UTF-8");
		body = webClient
				.sendRequest(
						"https://account.leagueoflegends.com/update/password",
						RequestType.POST,
						post,
						"lolpasschange",
						false,
						"	https://account.leagueoflegends.com/pm.html?xdm_e=http%3A%2F%2Feuw.leagueoflegends.com&xdm_c=default1602&xdm_p=1",false);

		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(body);
		validatePasswordChange();

	}

	@Override
	public boolean validatePassword(String pass) {
		Pattern number = Pattern.compile("([0-9]+)");
		Pattern letter = Pattern.compile("([A-Za-z]+)");
		Matcher numberMatcher;
		Matcher letterMatcher;
		if (pass.length() > 7 && pass.length() < 24) {
			if (!pass.contains("\\") && !pass.contains("/")
					&& !pass.contains(" ")) {
				numberMatcher = number.matcher(pass);
				letterMatcher = letter.matcher(pass);
				if (numberMatcher.find() && letterMatcher.find())
					return true;
			}
		}
		return false;

	}

	@Override
	protected void validateAuthentification() throws Exception {
		if (!body.contains("\"success\":true")) {
			displayErrorMessage("League of Legends: Login unsuccesful! Maybe you entered a wrong captcha or wrong username/password!");
			throw new AccountCredentialWrongException();
		}

		authenticated = true;

	}

	@Override
	protected void validatePasswordChange() throws Exception {

		if (!body.contains("\"success\":true")) {
			displayErrorMessage("League of Legends: Password change unsuccesful!");
			throw new Exception("Password change unsuccesful!");
		}
		succesful = true;

	}

	@Override
	public String getName() {
		return "League of Legends";
	}

	@Override
	public String getTopic() {
		// TODO Auto-generated method stub
		return "Games";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "League of Legends";
	}

	private void getImage() {
		Looper.prepare();
		Pattern urlPattern = Pattern.compile("'([0-9A-Za-z_-]{200,300}')",
				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Pattern reloadPattern = Pattern.compile("([0-9A-Za-z_-]{206,})");
		Matcher m = urlPattern.matcher(body);
		m.find();
		recaptchaID = m.group().substring(1, m.group().length() - 1);
		String path = m.group();

		body = webClient
				.sendRequest(
						"https://www.google.com/recaptcha/api/reload?c="
								+ recaptchaID
								+ "&k=6LcwdeESAAAAAJg_ltVGdjrqlf7Bmbg449SyUcSW&type=image&reason=i&lang=de",
						RequestType.GET, "", "reloadCaptcha", false);

		Matcher r = reloadPattern.matcher(body);
		r.find();
		recaptchaID = r.group();

		InputStream in = null;
		try {
			URL url = new URL("https://www.google.com/recaptcha/api/image?c="
					+ recaptchaID);
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
								solvedTask = captchaEnter.getText().toString();
							}
						}).setView(layout);
				builder.create().show();

			}

		});
		while (solvedTask.length() == 0);
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public String getPasswordCondition() {
		return "Password needs one letter, one number and needs to be between 8 and 24 characters long";
	}

	@Override
	public String getWebsiteUrl() {
		return "http://leagueoflegends.com/";
	}

	@Override
	public int getImageSource() {
		return R.drawable.leagueoflegends;
	}

}
