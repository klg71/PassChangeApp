package plugins;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;

import com.passchange.passchangeapp.R;

import ui.MainActivity;
import core.PassChangeWebsite;
import core.RequestType;
import core.WebClient;
import core.Website;
import exceptions.AccountCredentialWrongException;

public class Ebay extends PassChangeWebsite {

	private String body;
	private HashMap<String, String> formData;
	private String post;

	public Ebay(String username, String password,Activity activity) {
		super(activity);
		initialize(username, password);
	}

	public void initialize(String username, String password) {
		super.initialize(username, password);
		formData = new HashMap<String, String>();
	}

	public Ebay(Activity activity) {
		super(activity);
	}

	@Override
	public void authenticate() throws Exception {
		authenticated = false;
		// webClient.setAgent("Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		body = webClient
				.sendRequest(
						"https://signin.ebay.de/ws/eBayISAPI.dll?SignIn&UsingSSL=1&pUserId=&co_partnerId=2&siteid=77&ru=http%3A%2F%2Fsignin.ebay.de%2Fws%2FeBayISAPI.dll%3FChangePasswordAndCreateHint%26guest%3D1&pp=pass&pageType=708&i1=0",
						RequestType.GET, "", "ebayNormal", false);
		// body=webClient.sendRequest("https://m.ebay.de/signin?redirectUrl=http%3A%2F%2Fm.ebay.de",RequestType.GET,"","ebayPreLogin",false);
		parseFormData();
		formData.put("userid", username);
		formData.put("pass", pass);
		post = "";
		for (Entry<String, String> entry : formData.entrySet()) {
			post = post + entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
		}
		post = post.substring(0, post.length() - 1);
		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(post);
		body = webClient
				.sendRequest(
						"https://signin.ebay.de/ws/eBayISAPI.dll?co_partnerId=2&siteid=77&UsingSSL=1",
						RequestType.POST, post, "ebayLogin", false,
						"https://m.ebay.de/signin?redirectUrl=http%3A%2F%2Fm.ebay.de",false);

		validateAuthentification();
	}

	@Override
	public void changePassword(String newPass) throws Exception {
		body = webClient
				.sendRequest(
						"http://signin.ebay.de/ws/eBayISAPI.dll?ChangePasswordAndCreateHint",
						RequestType.GET, "", "ebayPreChange", false);
		body = webClient
				.sendRequest(
						webClient.getLocation(),
						RequestType.GET, "", "ebayPreChange1", false);
		parseFormData();
		formData.put("opass", pass);
		formData.put("npass", newPass);
		formData.put("rpass", newPass);
		post = "";
		for (Entry<String, String> entry : formData.entrySet()) {
			post = post + entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
		}
		post = post.substring(0, post.length() - 1);
		
		body = webClient.sendRequest("https://scgi.ebay.de/ws/eBayISAPI.dll", RequestType.POST,
				post, "ebayAfterChange", false,"https://scgi.ebay.de/ws/eBayISAPI.dll",false);
		validatePasswordChange();
	}

	@Override
	protected void validateAuthentification() throws Exception {
		if (body.length() > 0) {
			authenticated = true;
		} else {
			displayErrorMessage("Ebay: Login unsuccessful please check your username and password");
			throw new AccountCredentialWrongException();
		}

	}

	@Override
	protected void validatePasswordChange() throws Exception {
		if(body.contains("iconError_16x16.gif")){
			throw new Exception(
					"Ebay: Password change failed look at password conditions");
			
		} else {
			succesful=true;
		}

	}

	@Override
	public String getName() {
		return "Ebay";
	}

	@Override
	public String getTopic() {
		return "Shopping";
	}

	@Override
	public String toString() {
		return "Ebay";
	}

	@Override
	public Integer getId() {
		return 6;
	}

	@Override
	public boolean validatePassword(String pass) {
		if(pass.length()>5){
			Pattern pattern = Pattern.compile("(?=.{6,})(((?=.*[A-Za-z]))|((?=.*[A-Z])(?=.*[0-9]))).*$",Pattern.MULTILINE);
			Matcher m=pattern.matcher(pass);
			if(m.find()){
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPasswordCondition() {
		return "Passwords have to be at least 6 letters long and contain Letters and Nummbers and u cant use a password u used before!";
	}

	private void parseFormData() {
		HashMap<String, String> tempFormMap = new HashMap<String, String>();
		body = body.substring(0, body.indexOf("</form>"));
		Pattern inputPattern = Pattern.compile(
				"(<input[A-Za-z0-9-\\+\\._\"=:\\*%\\/\\?;& ']+>)",
				Pattern.MULTILINE);
		Pattern namePattern = Pattern.compile(
				"(name\\s?=\"[A-Za-z\\._0-9]+\")", Pattern.MULTILINE);
		Pattern valuePattern = Pattern
				.compile(
						"(value=(\"|\')[A-Za-z0-9\\.=_\\-:\\*%;&\\/\\*\\?\\+ ]*(\"|\'))",
						Pattern.MULTILINE);
		Matcher m = inputPattern.matcher(body);
		while (m.find()) {
			Matcher MatcherName = namePattern.matcher(m.group(1));
			Matcher MatcherValue = valuePattern.matcher(m.group(1));

			if(MainActivity.DEBUG_ACTIVATED)
			System.out.println("Input found:" + m.group(1));
			if (MatcherName.find()) {
				if (!MatcherName
						.group(0)
						.substring(MatcherName.group(0).indexOf("\"") + 1,
								MatcherName.group(0).length() - 1)
						.equals("signIn")) {

					if (MatcherValue.find()) {
						tempFormMap.put(
								MatcherName.group(0).substring(
										MatcherName.group(0).indexOf("\"") + 1,
										MatcherName.group(0).length() - 1),
								MatcherValue.group(0).substring(7,
										MatcherValue.group(0).length() - 1));
					} else
						tempFormMap.put(
								MatcherName.group(0).substring(6,
										MatcherName.group(0).length() - 1), "");
				}
			}

		}
		formData = tempFormMap;
	}

	@Override
	public String getWebsiteUrl() {
		return "http://m.ebay.de/";
	}

	@Override
	public int getImageSource() {
		return R.drawable.ebay;
	}

}
