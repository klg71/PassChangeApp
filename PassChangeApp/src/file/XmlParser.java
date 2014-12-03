package file;

import generator.Crypt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ui.MainActivity;
import account.Account;
import account.AccountManager;
import android.util.Log;
import core.Configuration;
import core.Website;

public class XmlParser {
	private HashMap<String, Website> websites;
	private DateFormat simpleDateFormat;
	private String salt;

	public XmlParser(HashMap<String, Website> websites) {
		this.websites = websites;
		simpleDateFormat = SimpleDateFormat.getDateTimeInstance(
				SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
		salt = "1234567890ABCDEFGHIJKLMONPQRSTUVWXYZ";
	}

	public ArrayList<Account> loadAccountsFromFile(String filename,
			String password) throws Exception {
		ArrayList<Account> accounts = new ArrayList<Account>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		String content = null;
		content = new String(Crypt.decode(new FileInputStream(
				new File(filename)), Crypt.generateKey(password, salt)));
		Document document = null;
		document = builder.parse(new ByteArrayInputStream(content.getBytes()));
		NodeList nodeList = null;
		// Compatibility with old version of xml file
		if (document.getFirstChild().getNodeName().equals("file")) {
			nodeList = document.getFirstChild().getFirstChild().getChildNodes();
		} else {
			nodeList = document.getFirstChild().getChildNodes();
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals("website")) {
				NodeList accountList = nodeList.item(i).getChildNodes();
				for (int k = 0; k < accountList.getLength(); k++) {
					if (accountList.item(k).getNodeName().equals("account")) {
						Calendar tempCalendar = Calendar.getInstance();
						if (accountList.item(k).getAttributes()
								.getNamedItem("lastchanged").getNodeValue()
								.length() > 0) {
							Date tempDate = simpleDateFormat
									.parse(accountList.item(k).getAttributes()
											.getNamedItem("lastchanged")
											.getNodeValue());
							tempCalendar.setTime(tempDate);
						}

						if (MainActivity.DEBUG_ACTIVATED)
							System.out.println(nodeList.item(i).getAttributes()
									.getNamedItem("name").getNodeValue());
						
						NodeList cookieList = nodeList.item(i).getChildNodes();
						for (int cookieCount = 0; cookieCount < cookieList.getLength(); cookieCount++) {
							
						}
						
						accounts.add(new Account(accountList.item(k)
								.getAttributes().getNamedItem("name")
								.getNodeValue(), accountList.item(k)
								.getAttributes().getNamedItem("email")
								.getNodeValue(), accountList.item(k)
								.getAttributes().getNamedItem("pass")
								.getNodeValue(), tempCalendar, websites
								.get(nodeList.item(i).getAttributes()
										.getNamedItem("name").getNodeValue()),
								Integer.parseInt(accountList.item(k)
										.getAttributes().getNamedItem("expire")
										.getNodeValue())));
					}
				}
			}

		}
		return accounts;
	}

	public void saveAccountsToFile(String filename, String password,
			AccountManager accountManager, Configuration configuration)
			throws FileNotFoundException, Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElementTop = doc.createElement("file");
		Element rootElement = doc.createElement("websites");
		doc.appendChild(rootElementTop);
		rootElementTop.appendChild(rootElement);

		// staff elements
		for (Entry<String, ArrayList<Account>> entry : accountManager
				.getAccountMap().entrySet()) {
			Element website = doc.createElement("website");
			website.setAttribute("name", entry.getKey());
			for (Account account : entry.getValue()) {
				Element accountElement = doc.createElement("account");
				Date tempDate = account.getLastChangedCalendar().getTime();
				accountElement.setAttribute("name", account.getUserName());
				accountElement.setAttribute("email", account.getEmail());
				accountElement
						.setAttribute("pass", account.getActualPassword());
				accountElement.setAttribute("lastchanged",
						simpleDateFormat.format(tempDate));
				accountElement.setAttribute("expire",
						Integer.toString(account.getExpire()));
				for (Entry<String, Map<String, Map<String, String>>> entryCookieSite : account
						.getWebsite().getSaveCookies().entrySet()) {
					for (Entry<String, Map<String, String>> entryCookie : entryCookieSite
							.getValue().entrySet()) {
						Element cookieElement=doc.createElement("cookie");
						cookieElement.setAttribute("domain", entryCookieSite.getKey());
						cookieElement.setAttribute("key",entryCookie.getKey());
						cookieElement.setAttribute("value",entryCookie.getValue().get(entryCookie.getValue()));
						accountElement.appendChild(cookieElement);
					}
				}

				website.appendChild(accountElement);
			}
			rootElement.appendChild(website);
		}

		Element rootElement2 = doc.createElement("config");
		rootElementTop.appendChild(rootElement2);
		Element logOutWhenPausedElement = doc.createElement("attribute");
		logOutWhenPausedElement.setAttribute("name", "logOutWhenPaused");
		logOutWhenPausedElement.setAttribute("value",
				Boolean.toString(configuration.isLogoutWhenAppIsPaused()));
		rootElement2.appendChild(logOutWhenPausedElement);

		Element logOutTimeElement = doc.createElement("attribute");
		logOutTimeElement.setAttribute("name", "logOutTime");
		logOutTimeElement.setAttribute("value",
				Integer.toString(configuration.getLogoutTimeMinutes()));
		rootElement2.appendChild(logOutTimeElement);

		Element rememberTimeElement = doc.createElement("attribute");
		rememberTimeElement.setAttribute("name", "rememberTime");
		rememberTimeElement.setAttribute("value",
				Integer.toString(configuration.getRememberTimeMinmutes()));
		rootElement2.appendChild(rememberTimeElement);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		if (MainActivity.DEBUG_ACTIVATED)
			System.out.println(source.toString());
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
		if (MainActivity.DEBUG_ACTIVATED)
			System.out.println(stringWriter.toString());
		Crypt.encode(stringWriter.toString().getBytes(), new FileOutputStream(
				new File(filename)), Crypt.generateKey(password, salt));

	}

	public Configuration loadConfigurationFromFile(String filename,
			String password) throws FileNotFoundException, Exception {
		Configuration configuration = new Configuration(true, 0, 10);

		ArrayList<Account> accounts = new ArrayList<Account>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		String content = null;
		content = new String(Crypt.decode(new FileInputStream(
				new File(filename)), Crypt.generateKey(password, salt)));
		if (MainActivity.DEBUG_ACTIVATED)
			System.out.println(content);
		Document document = null;
		document = builder.parse(new ByteArrayInputStream(content.getBytes()));
		NodeList nodeList = document.getFirstChild().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals("config")) {
				NodeList configList = nodeList.item(i).getChildNodes();
				for (int k = 0; k < configList.getLength(); k++) {

					if (configList.item(k).getNodeName().equals("attribute")) {

						if (MainActivity.DEBUG_ACTIVATED)
							System.out.println(configList.item(k)
									.getAttributes().getNamedItem("name")
									.getNodeValue());

						if (configList.item(k).getAttributes()
								.getNamedItem("name").getNodeValue()
								.equals("logOutWhenPaused")) {
							configuration.setLogoutWhenAppIsPaused(Boolean
									.parseBoolean(configList.item(k)
											.getAttributes()
											.getNamedItem("value")
											.getNodeValue()));
						}
						if (configList.item(k).getAttributes()
								.getNamedItem("name").getNodeValue()
								.equals("logOutTime")) {
							configuration.setLogoutTimeMinutes(Integer
									.parseInt(configList.item(k)
											.getAttributes()
											.getNamedItem("value")
											.getNodeValue()));
						}
						if (configList.item(k).getAttributes()
								.getNamedItem("name").getNodeValue()
								.equals("rememberTime")) {
							configuration.setRememberTimeMinmutes(Integer
									.parseInt(configList.item(k)
											.getAttributes()
											.getNamedItem("value")
											.getNodeValue()));
						}

					}
				}
			}

		}

		return configuration;
	}

}
