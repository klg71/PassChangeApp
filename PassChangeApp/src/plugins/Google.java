package plugins;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.passchange.passchangeapp.R;

import ui.MainActivity;
import android.app.Activity;
import android.content.Context;
import core.PassChangeWebsite;
import core.RequestType;
import core.WebClient;
import core.Website;

public class Google extends PassChangeWebsite {

	private String body;
	private String token; // GALX
	private HashMap<String,String> formData;
	String passwordNew;

	public Google(String username, String password,Activity activity) {
		super(activity);
		initialize(username, password);
	}

	public Google(Activity activity) {
		super(activity);
	}

	@Override
	public void initialize(String username, String pass) {
		super.initialize(username,pass);
		webClient = new WebClient();
		token = "";
		formData=new HashMap<String,String>();
		passwordNew="";;
		succesful=false;
		authenticated=false;
	}

	@Override
	public void authenticate() throws Exception {
		body = webClient.sendRequest(
				"https://accounts.google.com/ServiceLogin", RequestType.GET,
				"", "home1", false);
		token = webClient.getCookie("google.com", "GALX");

		if(MainActivity.DEBUG_ACTIVATED)
		 System.out.println(token);
		String post = URLEncoder.encode("GALX", "UTF-8")
				+ "="
				+ URLEncoder.encode(token, "UTF-8")
				+ "&"
				+ URLEncoder.encode("_utf8", "UTF-8")
				+ "="
				+ URLEncoder.encode("\u9731", "UTF-8")
				+ "&bgresponse=js_diabled&pstMsg=0&dnConn=&signIn=Anmelden&checkConnection=&checkedDomains=youtube&Email="
				+ URLEncoder.encode(username, "UTF-8") + "&Passwd="
				+ URLEncoder.encode(pass, "UTF-8");
		if(MainActivity.DEBUG_ACTIVATED)
			System.out.println(post);
		body = webClient.sendRequest(
				"https://accounts.google.com/ServiceLoginAuth",
				RequestType.POST, post, "google1", false);
		if (webClient.getResponseCode() == 302) {
			body = webClient.sendRequest("https://www.google.com/settings/personalinfo", RequestType.GET, "",
					"alternativeResponse", false);
		}
		validateAuthentification();
		

	}

	@Override
	public void changePassword(String newPass) throws Exception {

		passwordNew=newPass;
		body = webClient.sendRequest(
				"https://accounts.google.com/b/0/EditPasswd?hl=de",
				RequestType.GET, "", "pwChangeGoogle", false);
		parseFormData();
		formData.put("OldPasswd", URLEncoder.encode(pass,"UTF-8"));
		formData.put("Passwd",URLEncoder.encode(newPass,"UTF-8"));
		formData.put("PasswdAgain", URLEncoder.encode(newPass,"UTF-8"));
		String post="";
		for(Entry<String, String> entry:formData.entrySet()){
			if(!entry.getKey().equals("cancel"))
			post=post+entry.getKey()+"="+entry.getValue()+"&";
		}
		post=post.substring(0,post.length()-1);
		System.out.println(post);
		body = webClient.sendRequest(
				"https://accounts.google.com/b/0/EditPasswd",
				RequestType.POST, post, "googleChange1", false);
		validatePasswordChange();
	}

	@Override
	protected void validateAuthentification() throws Exception {
		if(body.contains("errormsg_0_Passwd")){
			displayErrorMessage("Google: Login unsuccsessful please try again");
			throw new Exception("Login unsuccsessful please try again");
		}
		authenticated=true;
	}

	

	@Override
	protected void validatePasswordChange() throws Exception {
		String tempPass=pass;
		pass=passwordNew;
		try {
			authenticate();
		} catch (Exception e) {
			pass=tempPass;
			displayErrorMessage("Google: Change Password unsuccessful please try again");
			throw new Exception ("Change Password unsuccessful please try again");
		}
		succesful=true;

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Google";
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

		Pattern pattern = Pattern.compile(
				"<.*=\"GALX\" [A-Za-z0-9\"= \n ]*\".*\">", Pattern.MULTILINE);

		Matcher m = pattern.matcher(body);

		while (m.find()) {
			token = m.group(0).substring(50, m.group(0).length() - 2);

			if(MainActivity.DEBUG_ACTIVATED)
			System.out.println(m.group(0));
		}
		return;
	}

	private void parseFormData() {
		HashMap<String, String> tempFormMap = new HashMap<String, String>();
		Pattern inputPattern = Pattern.compile(
				"(<input[ A-Za-z0-9=\"\n\'\\._\\-\t\\/]+>)", Pattern.MULTILINE
						| Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Pattern namePattern = Pattern.compile("(name=\"[A-Za-z]+\")",
				Pattern.MULTILINE);
		Pattern valuePattern = Pattern
				.compile("(value=(\"|\')[A-Za-z0-9\\.=_\\-\t ]+(\"|\'))",
						Pattern.MULTILINE);
		Matcher m = inputPattern.matcher(body);
		while (m.find()) {
			Matcher MatcherName = namePattern.matcher(m.group(1));
			Matcher MatcherValue = valuePattern.matcher(m.group(1));

			if(MainActivity.DEBUG_ACTIVATED)
			System.out.println(m.group(1));
			MatcherName.find();
			if (MatcherValue.find()){
				tempFormMap.put(MatcherName.group(0).substring(6,MatcherName.group(0).length()-1), MatcherValue.group(0).substring(7,MatcherValue.group(0).length()-1));
				System.out.println(MatcherValue.group(0));
			}
			else
				tempFormMap.put(MatcherName.group(0).substring(6,MatcherName.group(0).length()-1), "");

		}

		if(MainActivity.DEBUG_ACTIVATED)
		System.out.println(tempFormMap);
		formData=tempFormMap;
	}

	@Override
	public Integer getId() {
		return 2;
	}

	@Override
	public boolean validatePassword(String pass) {
		return pass.length()>8;
		
	}

	@Override
	public String getPasswordCondition() {
		// TODO Auto-generated method stub
		return "Password should be at least 9 characters";
	}

	@Override
	public String getWebsiteUrl() {
		return "https://www.google.com/settings/personalinfo";
	}

	@Override
	public int getImageSource() {
		return R.drawable.google;
	}

}
