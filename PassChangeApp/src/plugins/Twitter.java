package plugins;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.RequestType;
import core.WebClient;
import core.Website;

public class Twitter extends Website {

	private WebClient webClient;
	private String token;
	private String body;
	private String passwordNew;

	public Twitter(String username, String pass) {
		initialize(username, pass);
	}
	public Twitter() {
		super();
	}

	@Override
	public void initialize(String username, String pass) {
		super.initialize(username,pass);
		webClient = new WebClient();
		token = "";
		passwordNew = "";
	
	}
	@Override
	public void authenticate() throws Exception {
		// webClient.sendRequest("https://twitter.com/", RequestType.GET, "",
		// "home2", false);
		System.out.println();
		body = webClient.sendRequest("https://mobile.twitter.com/session/",
				RequestType.GET, "", "home3", false);
		System.out.println();
		getToken();
		System.out.println(token);
		String post = URLEncoder.encode("username", "UTF-8") + "="
				+ URLEncoder.encode(username, "UTF-8") + "&"
				+ URLEncoder.encode("password", "UTF-8") + "="
				+ URLEncoder.encode(pass, "UTF-8")
				+ "&submit=submit&authenticity_token="
				+ URLEncoder.encode(token, "UTF-8");

		System.out.println(post);
		System.out.println();
		body = webClient.sendRequest("https://mobile.twitter.com/session/",
				RequestType.POST, post, "twitter1", false);
		System.out.println();
		validateAuthentification();
		body = webClient.sendRequest(
				"https://mobile.twitter.com/settings/password",
				RequestType.GET, "", "home2", false);

	}

	@Override
	public void changePassword(String newPass) throws Exception {

		passwordNew = newPass;
		getToken();
		System.out.println(token);
		String post = URLEncoder.encode("settings[current_password]", "UTF-8")
				+ "=" + URLEncoder.encode(pass, "UTF-8")
				+ "&settings[password]=" + URLEncoder.encode(newPass, "UTF-8")
				+ "&settings[password_confirmation]="
				+ URLEncoder.encode(newPass, "UTF-8") + "&authenticity_token="
				+ URLEncoder.encode(token, "UTF-8");

		System.out.println(post);
		System.out.println();
		body = webClient.sendRequest(
				"https://mobile.twitter.com//settings/password/",
				RequestType.POST, post, "pwChange", false);
		validatePasswordChange();

	}

	@Override
	protected void validateAuthentification() throws Exception {
		if (body.indexOf("signup-field") > 0) {
			throw new Exception("Login unsuccsessful please try again");
		}

	}

	@Override
	protected void validatePasswordChange() throws Exception {
		String tempPass = pass;
		pass = passwordNew;
		try {
			authenticate();
		} catch (Exception e) {
			pass = tempPass;
			throw new Exception("Change Password unsuccessful please try again");
		}

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
		Pattern pattern = Pattern.compile("\"([a-f0-9]{20})\"");
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

}
