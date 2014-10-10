package plugins;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.passchange.passchangeapp.R;

import android.app.Activity;
import core.RequestType;
import core.WebClient;
import core.Website;

public class Amazon extends Website {

	private String body,pwLink;
	private HashMap<String, String> formData;
	private String passwordNew;

	public Amazon(String username, String password,Activity activity) {
		super(activity);
		initialize(username, password);
	}

	public void initialize(String username, String password) {
		super.initialize(username, password);
		webClient = new WebClient();
		body = "";
		pwLink="";
		passwordNew="";
		authenticated=false;
		succesful=false;
	}

	public Amazon(Activity activity) {
		super(activity);
		authenticated=false;
		succesful=false;
	}

	@Override
	public void authenticate() throws Exception {
		authenticated=false;
		succesful=false;
		body = webClient
				.sendRequest(
						"https://www.amazon.com/ap/signin?_encoding=UTF8&openid.assoc_handle=anywhere_v2_us&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.ns.pape=http%3A%2F%2Fspecs.openid.net%2Fextensions%2Fpape%2F1.0&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.com%2Fgp%2Faw%2Fpsi.html%3Fie%3DUTF8%26cartID%3D175-7126040-9723105&pageId=avl_us",
						RequestType.GET, "", "amazonPreLogin", false);
		parseFormData();
		formData.put("email", URLEncoder.encode(username, "UTF-8"));
		formData.put("password", URLEncoder.encode(pass, "UTF-8"));
		String post = "";
		for (Entry<String, String> entry : formData.entrySet()) {
			post = post + entry.getKey() + "=" + entry.getValue() + "&";
		}
		post = post.substring(0, post.length() - 1);
		webClient
				.setCookie(
						"amazon.com",
						"Referer",
						"	https://www.amazon.com/ap/signin?_encoding=UTF8&openid.assoc_handle=anywhere_v2_us&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.ns.pape=http%3A%2F%2Fspecs.openid.net%2Fextensions%2Fpape%2F1.0&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.com%2Fgp%2Faw%2Fpsi.html%3Fie%3DUTF8%26cartID%3D175-7126040-9723105&pageId=avl_us");
		body = webClient
				.sendRequest(
						"https://www.amazon.com/ap/signin?_encoding=UTF8&openid.assoc_handle=anywhere_v2_us&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.ns.pape=http%3A%2F%2Fspecs.openid.net%2Fextensions%2Fpape%2F1.0&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.com%2Fgp%2Faw%2Fpsi.html%3Fie%3DUTF8%26cartID%3D175-4935686-4928147&pageId=avl_us",
						RequestType.POST, post, "amazonLogin", false);
		validateAuthentification();
	}

	@Override
	public void changePassword(String newPass) throws Exception {
		passwordNew=newPass;
		body = webClient
				.sendRequest(
						"https://www.amazon.com/ap/cnep?appActionToken=91b6adJGjbfvoyA6XS29f9rABB0j3D&appAction=CNEP_PWD&openid.pape.max_auth_age=ape%3AMA%3D%3D&openid.ns=ape%3AaHR0cDovL3NwZWNzLm9wZW5pZC5uZXQvYXV0aC8yLjA%3D&openid.ns.pape=ape%3AaHR0cDovL3NwZWNzLm9wZW5pZC5uZXQvZXh0ZW5zaW9ucy9wYXBlLzEuMA%3D%3D&prevRID=ape%3AMVdSSlhRNkZYWTczRFI4UUgwTVQ%3D&email=ape%3ATWVpc2VnZWllckx1a2FzQGdteC5kZQ%3D%3D&pageId=ape%3AdXNmbGV4&openid.identity=ape%3AaHR0cDovL3NwZWNzLm9wZW5pZC5uZXQvYXV0aC8yLjAvaWRlbnRpZmllcl9zZWxlY3Q%3D&openid.claimed_id=ape%3AaHR0cDovL3NwZWNzLm9wZW5pZC5uZXQvYXV0aC8yLjAvaWRlbnRpZmllcl9zZWxlY3Q%3D&openid.mode=ape%3AY2hlY2tpZF9zZXR1cA%3D%3D&openid.assoc_handle=ape%3AdXNmbGV4&openid.return_to=ape%3AaHR0cHM6Ly93d3cuYW1hem9uLmNvbS9ncC9jc3MvaG9tZXBhZ2UuaHRtbD9pZT1VVEY4JnJlZl89eWFfY25lcA%3D%3D",
						RequestType.GET, "", "amazonPasswordChangePage", false);
		parsePasswordLink();
		formData.clear();
		parseFormData();
		String post = "";
		formData.put("appAction","CNEP_PWD");
		for (Entry<String, String> entry : formData.entrySet()) {
			post = post + entry.getKey() + "=" + entry.getValue() + "&";
		}
		post = post.substring(0, post.length() - 1);
		System.out.println(post);
		body=webClient.sendRequest(pwLink+"&"+post, RequestType.GET, "","editPwSite", false);
		formData.clear();
		parseSubmitPasswordLink();
		parseFormData();
		formData.put("password", URLEncoder.encode(pass,"UTF-8"));
		formData.put("passwordNew", URLEncoder.encode(newPass,"UTF-8"));
		formData.put("passwordNewCheck",URLEncoder.encode(newPass,"UTF-8"));
		post = "";
		for (Entry<String, String> entry : formData.entrySet()) {
			post = post + entry.getKey() + "=" + entry.getValue() + "&";
		}
		post = post.substring(0, post.length() - 1);
		body=webClient.sendRequest(pwLink, RequestType.POST, post,"changeAmazonPw", false);
		validatePasswordChange();
	}
	

	
	@Override
	public boolean validatePassword(String pass) {
		// TODO Auto-generated method stub
		return pass.length()>5;
	}

	@Override
	protected void validateAuthentification() throws Exception {
		body=webClient.sendRequest("http://www.amazon.com", RequestType.GET, "","checkAmazonLogin", false);
		if(body.contains("<span id='nav-signin-text' class='nav-button-em'>Sign in</span>")){
			throw new Exception("Login unsuccsessful please try again");
		}
		authenticated=true;
		

	}

	@Override
	protected void validatePasswordChange() throws Exception {
		String tempPass=pass;
		pass=passwordNew;
		initialize(username, passwordNew);
		try {
			authenticate();
		} catch (Exception e) {
			pass=tempPass;
			throw new Exception ("Change Password unsuccessful please try again");
		}
		succesful=true;


	}

	@Override
	public String getName() {
		return "Amazon";
	}

	@Override
	public String getTopic() {
		return "Shopping";
	}

	@Override
	public String toString() {
		return "Amazon";
	}

	private void parseFormData() {
		HashMap<String, String> tempFormMap = new HashMap<String, String>();
		Pattern inputPattern = Pattern.compile(
				"(<input[A-Za-z0-9-\\+\\._\"=: ]+\\/>)",
				Pattern.CASE_INSENSITIVE);
		Pattern namePattern = Pattern.compile("(name=\"[A-Za-z\\._]+\")",
				Pattern.MULTILINE);
		Pattern valuePattern = Pattern.compile(
				"(value=(\"|\')[A-Za-z0-9\\.=_\\-: ]+(\"|\'))",
				Pattern.MULTILINE);
		Matcher m = inputPattern.matcher(body);
		while (m.find()) {
			Matcher MatcherName = namePattern.matcher(m.group(1));
			Matcher MatcherValue = valuePattern.matcher(m.group(1));
			System.out.println("Input found:" + m.group(1));
			if (MatcherName.find()) {
				if (!MatcherName.group(0)
						.substring(6, MatcherName.group(0).length() - 1)
						.equals("signIn")) {

					if (MatcherValue.find()) {
						tempFormMap.put(
								MatcherName.group(0).substring(6,
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
		System.out.println(tempFormMap);
		formData = tempFormMap;
	}
	
	private void parsePasswordLink(){
		Pattern formPattern=Pattern.compile("(<form method=\"GET\" id=\"cnep_1a_password_form\"[\\&\\?\\/A-Za-z0-9-\\+\\._\"=: ]+>[ \n]+((<input[A-Za-z0-9-\\+\\._\\\"=: ]+\\/>)[\\t \\n]*)+)");
		Pattern linkPatter=Pattern.compile("(https:\\/\\/[A-Za-z0-9\\?=&\\/\\._-]+)");
		Matcher formMatcher=formPattern.matcher(body);
		if(formMatcher.find()){
			body=formMatcher.group(0);
			Matcher linkMatcher=linkPatter.matcher(formMatcher.group(0));
			if(linkMatcher.find()){
				pwLink=linkMatcher.group(0);
				System.out.println(pwLink);
			}
		}
	}
	
	private void parseSubmitPasswordLink(){
		Pattern formPattern=Pattern.compile("(<form[\\&\\?\\/A-Za-z0-9-\\+\\._\"=: ]+)");
		Pattern linkPatter=Pattern.compile("(\\/ap\\/[A-Za-z0-9\\?=&\\/\\._-]+)");
		Matcher formMatcher=formPattern.matcher(body);
		if(formMatcher.find()){
			Matcher linkMatcher=linkPatter.matcher(formMatcher.group(0));
			if(linkMatcher.find()){
				pwLink="https://www.amazon.com"+linkMatcher.group(0);
				System.out.println(pwLink);
			}
		}
		
	}

	@Override
	public Integer getId() {
		return 5;
	}

	@Override
	public String getPasswordCondition() {
		return "Password should be at least 6 characters long";
	}

	@Override
	public String getWebsiteUrl() {
		return "http://www.amazon.com/";
	}

	@Override
	public int getImageSource() {
		return R.drawable.amazon;
	}
}
