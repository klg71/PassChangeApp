package core;

import java.util.HashMap;
import java.util.Map;

import ui.MainFragmentActivity;
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
	protected HashMap<String, Map<String, Map<String, String>>> safeCookieStore;


	public boolean isAuthenticated() {
		return authenticated;
	}

	public Website(Activity activity){
		this.activity=activity;
	}
	
	
	
	public void setCookies(HashMap<String, Map<String, Map<String, String>>> cookies){
		this.safeCookieStore=cookies;
	}
	
	public void initialize(String username,String pass){
		this.username = username;
		this.pass = pass;
		webClient = new WebClient();
		if(safeCookieStore==null){
			safeCookieStore=new HashMap<String, Map<String, Map<String, String>>>();
		}
		for(Map.Entry<String,  Map<String, Map<String, String>>> entryWebSite:safeCookieStore.entrySet()){
			for(Map.Entry<String,  Map<String, String>> entryCookie:entryWebSite.getValue().entrySet()){
				webClient.setCookie(entryWebSite.getKey(),entryCookie.getKey(), entryCookie.getValue().get(entryCookie.getKey()));
			}
		}
		if(MainFragmentActivity.DEBUG_ACTIVATED)
			System.out.println("Preset Cookies: "+webClient.getCookies().toString());
	}
	
	public boolean isSuccesful(){
		return succesful;
	}
	
	public abstract void authenticate() throws Exception;

	
	protected abstract void validateAuthentification() throws Exception;
	
	public Map<String, Map<String, Map<String, String>>> getSaveCookies(){
		return safeCookieStore;
	}
	
	
	public abstract String getName() ;
	
	public abstract String getTopic();
	
	public abstract String toString();
	
	public abstract Integer getId();

	
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

	public abstract int getImageSource();

}
