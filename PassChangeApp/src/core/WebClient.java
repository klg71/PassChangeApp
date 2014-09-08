package core;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class WebClient {
	private RequestType type;
	private String body;
	private HttpsURLConnection connection;
	private CookieManager cookieManager;
	private CookieStore cookieStore;
	private URL url;
	boolean ref;

	private Map store;

	private static final String SET_COOKIE = "Set-Cookie";
	private static final String COOKIE_VALUE_DELIMITER = ";";
	private static final String PATH = "path";
	private static final String EXPIRES = "expires";
	private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
	private static final String SET_COOKIE_SEPARATOR = "; ";
	private static final String COOKIE = "Cookie";

	private static final char NAME_VALUE_SEPARATOR = '=';
	private static final char DOT = '.';

	private DateFormat dateFormat;

	public WebClient() {
		super();
		ref = false;
		cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cookieManager);
		cookieStore = cookieManager.getCookieStore();

		store = new HashMap();
		dateFormat = new SimpleDateFormat(DATE_FORMAT);

	}

	private void initConnection() {
		try {
			connection = (HttpsURLConnection) url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		if (type == RequestType.POST) {
			connection.setRequestProperty("Content-Length",
					String.valueOf(body.length()));
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
		}
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("DNT", "1");
		connection.setRequestProperty("Accept-Language",
				"de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
		connection
				.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:20.0) Gecko/20100101 Firefox/20.0");
		connection
				.setRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");

//		try {
//			setCookies(connection);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (type == RequestType.GET) {
			try {
				connection.setRequestMethod("GET");
				connection.setDoOutput(false);
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		connection.setDoInput(true);

		connection.setUseCaches(true);
		connection.setRequestProperty("Host", url.getHost());
		if (ref) {
			connection.setDoInput(false);
		}
		// try {
		// connection.setRequestProperty("Cookie",
		// URLEncoder.encode(cookies, "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public String sendRequest(String url, RequestType type, String body,
			String filename, Boolean ref) {
		String ret = "";
		System.out.println(filename);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename + ".html");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.type = type;
		this.body = body;
		this.ref = ref;
		initConnection();

		OutputStreamWriter writer = null;

		try {
			if (type == RequestType.POST) {
				writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(body);
				System.out.println(body);
				writer.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			storeCookies(connection);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 
		 System.out.println();
		// List<HttpCookie> cks = cookieStore.getCookies();
		// for (HttpCookie ck : cks) {
		// try {
		// System.out.print(URLDecoder.decode(ck.getName(), "UTF-8")
		// + ": ");
		// System.out.println(URLDecoder.decode(ck.getValue(), "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		System.out.println(store);
		BufferedReader reader = null;
		if(!ref){
		if (connection.getHeaderField("Content-Encoding") != null) {
			try {
				reader = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(connection.getInputStream())));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			try {
				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		try {
			for (String line; (line = reader.readLine()) != null;) {
				ret += line;
				fileWriter.write(line+System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		try {
			if(!ref)
			reader.close();
			if (writer != null)
				writer.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;

	}

	public void storeCookies(URLConnection conn) throws IOException {

		// let's determine the domain from where these cookies are being sent
		String domain = getDomainFromHost(conn.getURL().getHost());

		Map domainStore; // this is where we will store cookies for this domain

		// now let's check the store to see if we have an entry for this domain
		if (store.containsKey(domain)) {
			// we do, so lets retrieve it from the store
			domainStore = (Map) store.get(domain);
		} else {
			// we don't, so let's create it and put it in the store
			domainStore = new HashMap();
			store.put(domain, domainStore);
		}

		// OK, now we are ready to get the cookies out of the URLConnection

		String headerName = null;
		for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
			if (headerName.equalsIgnoreCase(SET_COOKIE)) {
				Map<String, String> cookie = new HashMap();
				StringTokenizer st = new StringTokenizer(
						conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);

				// the specification dictates that the first name/value pair
				// in the string is the cookie name and value, so let's handle
				// them as a special case:

				if (st.hasMoreTokens()) {
					String token = st.nextToken();
					String name = token.substring(0,
							token.indexOf(NAME_VALUE_SEPARATOR));
					String value = token.substring(
							token.indexOf(NAME_VALUE_SEPARATOR) + 1,
							token.length());
					domainStore.put(name, cookie);
					cookie.put(name, value);
				}

				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (token.indexOf(NAME_VALUE_SEPARATOR) > 0) {
						cookie.put(
								token.substring(0,
										token.indexOf(NAME_VALUE_SEPARATOR))
										.toLowerCase(),
								token.substring(
										token.indexOf(NAME_VALUE_SEPARATOR) + 1,
										token.length()));
					}
				}
			}
		}
	}

	public void setCookies(URLConnection conn) throws IOException {

		// let's determine the domain and path to retrieve the appropriate
		// cookies
		URL url = conn.getURL();
		String domain = getDomainFromHost(url.getHost());
		String path = url.getPath();

		Map domainStore = (Map) store.get(domain);
		if (domainStore == null)
			return;
		StringBuffer cookieStringBuffer = new StringBuffer();

		Iterator cookieNames = domainStore.keySet().iterator();
		while (cookieNames.hasNext()) {
			String cookieName = (String) cookieNames.next();
			Map cookie = (Map) domainStore.get(cookieName);
			// check cookie to ensure path matches and cookie is not expired
			// if all is cool, add cookie to header string
			if (comparePaths((String) cookie.get(PATH), path)
					&& isNotExpired((String) cookie.get(EXPIRES))) {
				cookieStringBuffer.append(cookieName);
				cookieStringBuffer.append("=");
				cookieStringBuffer.append((String) cookie.get(cookieName));
				if (cookieNames.hasNext())
					cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
			}
		}
		try {
			conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
		} catch (java.lang.IllegalStateException ise) {
			IOException ioe = new IOException(
					"Illegal State! Cookies cannot be set on a URLConnection that is already connected. "
							+ "Only call setCookies(java.net.URLConnection) AFTER calling java.net.URLConnection.connect().");
			throw ioe;
		}
	}

	private String getDomainFromHost(String host) {
		if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
			return host.substring(host.indexOf(DOT) + 1);
		} else {
			return host;
		}
	}

	private boolean isNotExpired(String cookieExpires) {
		if (cookieExpires == null)
			return true;
		Date now = new Date();
		try {
			return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
		} catch (java.text.ParseException pe) {
			pe.printStackTrace();
			return false;
		}
	}

	private boolean comparePaths(String cookiePath, String targetPath) {
		if (cookiePath == null) {
			return true;
		} else if (cookiePath.equals("/")) {
			return true;
		} else if (targetPath.regionMatches(0, cookiePath, 0,
				cookiePath.length())) {
			return true;
		} else {
			return false;
		}

	}
	
	public String getCookie(String domain,String name){
		return (String) ((Map)((Map) store.get(domain)).get(name)).get(name);
	}

}
