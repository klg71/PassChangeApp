package core;
//

/*
 * TODO: Make a new website for each account created
 * TODO: implement webview loading at runtime
 * TODO: implement saving cookies toxmlFile
 */
import android.app.Activity;

public abstract class PassChangeWebsite extends Website {

	public PassChangeWebsite(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	
	public abstract void changePassword(String newPass) throws Exception;
	protected abstract void validatePasswordChange() throws Exception;
	public abstract boolean validatePassword(String pass);

	
	

}
