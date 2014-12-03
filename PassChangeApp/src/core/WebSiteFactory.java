package core;

import java.util.HashMap;
import java.util.Map;

import plugins.Amazon;
import plugins.Ebay;
import plugins.Facebook;
import plugins.Google;
import plugins.LeagueOfLegends;
import plugins.Steam;
import plugins.Twitter;
import android.app.Activity;

public class WebSiteFactory {
	public static Website createWebsite(String website, Activity activity) {
		switch (website) {
		case "Amazon":
			return new Amazon(activity);
		case "Ebay":
			return new Ebay(activity);
		case "Facebook":
			return new Facebook(activity);
		case "Google":
			return new Google(activity);
		case "LeagueOfLegends":
			return new LeagueOfLegends(activity);
		case "Steam":
			return new Steam(activity);
		case "Twitter":
			return new Twitter(activity);
		}
		return null;
	}
	public static Website createWebsite(String website, Activity activity,HashMap<String, Map<String, Map<String, String>>> cookies) {
		Website websiteInstance=null;
		switch (website) {
		case "Amazon":
			websiteInstance=new Amazon(activity);
			break;
		case "Ebay":
			websiteInstance=new Ebay(activity);
			break;
		case "Facebook":
			websiteInstance=new Facebook(activity);
			break;
		case "Google":
			websiteInstance=new Google(activity);
			break;
		case "LeagueOfLegends":
			websiteInstance=new LeagueOfLegends(activity);
			break;
		case "Steam":
			websiteInstance=new Steam(activity);
			break;
		case "Twitter":
			websiteInstance=new Twitter(activity);
			break;
		}
		websiteInstance.setCookies(cookies);
		return websiteInstance;
	}
}
