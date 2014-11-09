package account;

import java.util.ArrayList;

public interface AccountExpiredListener {
	public void accountsExpired(ArrayList<Account> accounts);
}
