package plugins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import core.RequestType;
import core.WebClient;
import core.Website;

public class Facebook extends Website {
	private WebClient webClient;
	private String passwordNew;
	private String fb_dtsg;
	private String charset_test;
	private String PassForm;
	private String Login;

	public Facebook(String username, String pass) {
		initialize(username, pass);
		webClient = new WebClient();
		fb_dtsg = "";
		charset_test = "";
	}
	
	public Facebook(){
		super();
	}

	@Override
	public void authenticate() throws Exception {
		webClient.sendRequest("https://m.facebook.com/", RequestType.GET, "",
				"home1", false);
		System.out.println("");
		System.out.println("");
		try {
			Login=webClient.sendRequest("https://m.facebook.com/login.php",
					RequestType.POST,
					"email=" + URLEncoder.encode(username, "UTF-8") + "&"
							+ "pass=" + URLEncoder.encode(pass, "UTF-8"),
					"login", false);
			System.out.println("");
			System.out.println("");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PassForm=webClient.sendRequest(
				"https://m.facebook.com/settings/account/?password",
				RequestType.GET, "", "home2", false);
		validateAuthentification();
		parsingPassChangeForm();
		

	}

	@Override
	public void changePassword(String newPass) throws Exception {
		passwordNew=newPass;
		try {
			webClient.sendRequest(
					"https://m.facebook.com/password/change/",
					RequestType.POST,
					"confirm_password="
							+ URLEncoder.encode(passwordNew, "UTF-8") + "&"
							+ "new_password="
							+ URLEncoder.encode(passwordNew, "UTF-8") + "&"
							+ "old_password="
							+ URLEncoder.encode(pass, "UTF-8") + "&"
							+ "save="
							+ URLEncoder.encode("Passwort ändern", "UTF-8") +"&"
							+ "charset_test="
							+ charset_test +"&"
							+ "fb_dtsg="
							+ URLEncoder.encode(fb_dtsg, "UTF-8")
							+ "&change_password", "pwchange", false);
			System.out.println("");
			System.out.println("");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		validatePasswordChange();

	}

	public void parsingPassChangeForm() {
		String body = PassForm;
		
		fb_dtsg = body.substring(body.indexOf("fb_dtsg") + 16,
				body.indexOf("fb_dtsg") + 28);
		charset_test = body.substring(body.indexOf("charset_test") + 21);
		charset_test = charset_test.substring(0, charset_test.indexOf("\""));

		System.out.println(charset_test);
	}

	@Override
	protected void validateAuthentification() throws Exception {
		if(Login.indexOf("login_form")>0){
			throw new Exception("Login unsuccsessful please try again");
		}
		
	}

	@Override
	protected void validatePasswordChange() throws Exception {
		String tempPass=pass;
		pass=passwordNew;
		try {
			authenticate();
		} catch (Exception e) {
			pass=tempPass;
			throw new Exception ("Change Password unsuccessful please try again");
		}
		
	}

	@Override
	public String getName() {
		return "Facebook";
	}

	@Override
	public String getTopic() {
		return "Social";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Integer getId() {
		return 1;
	}


}
