package plugins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.passchange.passchangeapp.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import core.PassChangeWebsite;
import core.RequestType;
import core.WebClient;
import core.Website;
import exceptions.AccountCredentialWrongException;

public class Facebook extends PassChangeWebsite {
	private String passwordNew;
	private String fb_dtsg;
	private String charset_test;
	private String PassForm;
	private String Login;

	public Facebook(String username, String pass,Activity activity) {
		super(activity);
		initialize(username, pass);
	}
	@Override
	public void initialize(String username, String pass) {
		super.initialize(username,pass);
		webClient = new WebClient();
		fb_dtsg = "";
		charset_test = "";
		succesful=false;
		authenticated=false;
	}

	public Facebook(Activity activity){
		super(activity);
	}

	@Override
	public void authenticate() throws Exception {
		webClient.sendRequest("https://m.facebook.com/", RequestType.GET, "",
				"home1", false);
		try {
			Login=webClient.sendRequest("https://m.facebook.com/login.php",
					RequestType.POST,
					"email=" + URLEncoder.encode(username, "UTF-8") + "&"
							+ "pass=" + URLEncoder.encode(pass, "UTF-8"),
					"login", false);
			Login=webClient.sendRequest(
					"https://m.facebook.com/settings/account/?password",
					RequestType.GET, "", "home2", false);
		} catch (UnsupportedEncodingException e) {
			Log.e("Debug",e.getMessage());
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

	}

	@Override
	protected void validateAuthentification() throws Exception {
		if(Login.indexOf("login_form")>0 || Login.length()==0){
			displayErrorMessage("Facebook: Login unsuccsessful please check username and password");
			throw new AccountCredentialWrongException();
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
			displayErrorMessage("Facebook: Change Password unsuccessful please try again");
			throw new Exception ("Change Password unsuccessful please try again");
		}
		succesful=true;
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
	@Override
	public boolean validatePassword(String pass) {
		return pass.length()>6;
		
	}
	@Override
	public String getPasswordCondition() {
		// TODO Auto-generated method stub
		return "Password should be at least 7 characters";
	}
	@Override
	public String getWebsiteUrl() {
		return "https://www.facebook.com";
	}
	@Override
	public int getImageSource() {
		return R.drawable.facebook;
	}


}
