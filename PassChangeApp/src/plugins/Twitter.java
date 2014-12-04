package plugins;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.passchange.passchangeapp.R;

import ui.MainActivity;
import ui.MainFragmentActivity;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import core.PassChangeWebsite;
import core.RequestType;
import core.WebClient;
import core.Website;
import exceptions.AccountCredentialWrongException;

public class Twitter extends PassChangeWebsite {

	private String token;
	private String body;
	private String passwordNew;

	public Twitter(String username, String pass,Activity activity) {
		super(activity);
		initialize(username, pass);
	}
	public Twitter(Activity activity) {
		super(activity);
	}

	@Override
	public void initialize(String username, String pass) {
		super.initialize(username,pass);
		webClient = new WebClient();
		token = "";
		passwordNew = "";
		succesful=false;
		authenticated=false;
	
	}
	@Override
	public void authenticate() throws Exception {
		// webClient.sendRequest("https://twitter.com/", RequestType.GET, "",
		// "home2", false);

		if(MainFragmentActivity.DEBUG_ACTIVATED)
			Log.e("debug","pregets");
		body=webClient.sendRequest("https://twitter.com",RequestType.GET, "", "desktopVersion", false,"",true);
		body=webClient.sendRequest("https://mobile.twitter.com",RequestType.GET, "", "desktopVersion", false,"" ,true);
		body=webClient.sendRequest("https://mobile.twitter.com/session/new", RequestType.GET, "",
				"home2", false,"",true);
		if(MainFragmentActivity.DEBUG_ACTIVATED)
			Log.e("debug","prelogin");
		getToken();
		String post=URLEncoder.encode("username", "UTF-8") + "="
				+ URLEncoder.encode(username, "UTF-8") + "&"
				+ URLEncoder.encode("password", "UTF-8") + "="
				+ URLEncoder.encode(pass, "UTF-8")
				+ "&authenticity_token="
				+ URLEncoder.encode(token, "UTF-8")+"&wfa=1";
		
	
		body = webClient.sendRequest(
				"https://mobile.twitter.com/session",
				RequestType.POST,post, "twitter1", false,"https://mobile.twitter.com/session/new",false);

		if(MainFragmentActivity.DEBUG_ACTIVATED)
			Log.e("debug","afterlogin");
		body=webClient.sendRequest("https://mobile.twitter.com//signup/disablejs", RequestType.GET, "",
				"home5", false,"",true);
		body=webClient.sendRequest("https://mobile.twitter.com/home", RequestType.GET, "",
				"home4", false,"",true);

		if(MainFragmentActivity.DEBUG_ACTIVATED)
			Log.e("debug","afterHome");
		body=webClient.sendRequest("https://mobile.twitter.com/settings/password", RequestType.GET, "",
				"home2", false,"",true);
		validateAuthentification();

	}

	@Override
	public void changePassword(String newPass) throws Exception {

		passwordNew = newPass;
		getToken();

		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(token);
		String post = URLEncoder.encode("settings[current_password]", "UTF-8")
				+ "=" + URLEncoder.encode(pass, "UTF-8")
				+ "&settings[password]=" + URLEncoder.encode(newPass, "UTF-8")
				+ "&settings[password_confirmation]="
				+ URLEncoder.encode(newPass, "UTF-8") + "&authenticity_token="
				+ URLEncoder.encode(token, "UTF-8");

		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(post);
		body = webClient.sendRequest(
				"https://mobile.twitter.com//settings/password/",
				RequestType.POST, post, "pwChange", false);
		validatePasswordChange();

	}

	@Override
	protected void validateAuthentification() throws Exception {
		if (body.indexOf("redirected") > 0) {
			displayErrorMessage("Twitter: Login unsuccessful please check your username and password");
			throw new AccountCredentialWrongException();
		}

		authenticated=true;

	}

	@Override
	protected void validatePasswordChange() throws Exception {
		String tempPass = pass;
		pass = passwordNew;
		try {
			authenticate();
		} catch (Exception e) {
			pass = tempPass;
			displayErrorMessage("Twitter: Change Password unsuccessful maybe u tried it to much?");
			throw new Exception("Change Password unsuccessful maybe u tried it to much?");
		}
		succesful=true;

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Twitter";
	}

	@Override
	public String getTopic() {

		return "Social";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	private void getToken() {
		Pattern pattern = Pattern.compile("\"([a-f0-9]{20,})\"");
		Matcher m = pattern.matcher(body);
		while (m.find()) {
			token = m.group(1).substring(0, m.group(1).length());
			// s now contains "BAR"
		}
	}

	@Override
	public Integer getId() {
		return 3;
	}
	@Override
	public boolean validatePassword(String pass) {
		if(pass.length()>7){
			Pattern pattern = Pattern.compile("(?=.{7,})(((?=.*[A-Za-z]))|((?=.*[A-Z])(?=.*[0-9]))).*$",Pattern.MULTILINE);
			Matcher m=pattern.matcher(pass);
			if(m.find()){
				return true;
			}
		}
		return false;
		
	}
	@Override
	public String getPasswordCondition() {
		// TODO Auto-generated method stub
		return "Your passwords should have at least 7 characters one letter and one number";
	}
	@Override
	public String getWebsiteUrl() {
		return "https://twitter.com/";
	}
	@Override
	public int getImageSource() {
		return R.drawable.twitter;
	}

}
