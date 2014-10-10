package core;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class Website {
	protected String username;
	protected WebClient webClient;
	protected String pass;
	protected Activity activity;
	protected boolean succesful;
	protected boolean authenticated;


	public boolean isAuthenticated() {
		return authenticated;
	}

	public Website(Activity activity){
		this.activity=activity;
	}
	
	public void initialize(String username, String pass) {
		this.username = username;
		this.pass = pass;
	}
	
	public boolean isSuccesful(){
		return succesful;
	}
	
	public abstract void authenticate() throws Exception;

	public abstract void changePassword(String newPass) throws Exception;
	
	protected abstract void validateAuthentification() throws Exception;
	
	protected abstract void validatePasswordChange() throws Exception;

	public abstract String getName() ;
	
	public abstract String getTopic();
	
	public abstract String toString();
	
	public abstract Integer getId();

	public abstract boolean validatePassword(String pass);
	
	public void displayErrorMessage(String error){
		final LinearLayout layout = new LinearLayout(activity);	
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView errorView= new TextView(activity);
		layout.addView(errorView);
		errorView.setText(error);
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
						
						AlertDialog.Builder builder = 
						        new AlertDialog.Builder(activity).
						        setMessage("Error").
						        setPositiveButton("OK", new OnClickListener() {                     
						            @Override
						            public void onClick(DialogInterface dialog, int which) {
						            }
						        }).
						        setView(layout);
						builder.create().show();
				    
				
			}
		
		});	
	}

	public Map<String, Map<String, Map<String, String>>> getCookies(){
		return webClient.getCookies();
	}
	
	public abstract String getWebsiteUrl();
	
	public abstract String getPasswordCondition();

	public abstract int getImageSource();
}
