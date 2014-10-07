package account;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import core.Website;
import file.XmlParser;

public class AccountManager {
	private ArrayList<Account> accounts;
	private String accountFile;
	private String masterPass;
	private XmlParser xmlParser;
	private HashMap<String,Website> websites;
	//private MysqlManager mysqlManager;
	
	public AccountManager(String accountFile, String masterPass,HashMap<String,Website> websites){
		accounts=new ArrayList<Account>();
		this.accountFile=accountFile;
		this.masterPass=masterPass;
		this.websites=websites;
//		mysqlManager=new MysqlManager("", "", websites);
		xmlParser=new XmlParser(websites);
		
	}
	public AccountManager(String masterPass,HashMap<String,Website> websites){
		accounts=new ArrayList<Account>();
		this.masterPass=masterPass;
		this.websites=websites;
//		mysqlManager=new MysqlManager("", "", websites);
		xmlParser=new XmlParser(websites);
		
	}
	
	public HashMap<String, Website> getWebsites() {
		return websites;
	}
	public void loadFromFile() throws Exception{
		accounts=xmlParser.loadAccountsFromFile(accountFile,masterPass);
	}
	
	public void writeToFile() throws FileNotFoundException, Exception{
		xmlParser.saveAccountsToFile(accountFile,masterPass,this);
	}
	
	public void writeToFile(String pass) throws FileNotFoundException, Exception{
		xmlParser.saveAccountsToFile(accountFile,pass,this);
	}
	
	public void loadFromDatabase(){
		//accounts=mysqlManager.loadFromDatabase(getId(), masterPass);
	}
	
	public void saveToDabase(){
		//mysqlManager.saveToDatabase(this,masterPass);
	}
	
	public void saveToDabase(String pass){
		//mysqlManager.saveToDatabase(this,pass);
	}
	
	
	public void exportAccounts(String filename){
		
	}
	
	public Account addAccount(Account newAccount){
		accounts.add(newAccount);
		return newAccount;
	}
	
	public void removeAccount(String website,String accountName){
		int i=-1;
		for(Account account:accounts){
			if(account.getWebsite().toString().equals(website)&&account.getUserName().equals(accountName)){
				i=accounts.indexOf(account);
			}
		}
		accounts.remove(i);
	}
	
	public void removeAccount(Account account){
		accounts.remove(account);
	}
	
	public Account findAccount(String website,String accountName){
		int i=-1;
		for(Account account:accounts){
			if(account.getWebsite().toString().equals(website)&&account.getUserName().equals(accountName)){
				i=accounts.indexOf(account);
			}
		}
		return accounts.get(i);
	}
	
	public ArrayList<Account> getAccounts(){
		return accounts;
	}
	
	public Account getAccount(int index){
		return accounts.get(index);
	}
	
	public HashMap<String,ArrayList<Account>> getAccountMap(){
		HashMap<String,ArrayList<Account>> accountMap= new HashMap<String,ArrayList<Account>>();
		for(Account account:accounts){
			if(accountMap.containsKey(account.getWebsite().toString())){
				accountMap.get(account.getWebsite().toString()).add(account);
			}
			else {
				ArrayList<Account> temp = new ArrayList<Account>();
				temp.add(account);
				accountMap.put(account.getWebsite().toString(), temp);
				
			}
		}
		return accountMap;
	}

	public int getId() {
		return 1;
	}
	
	
}
