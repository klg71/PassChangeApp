package account;

public interface AccountExportListener {
	public void exportSuccessful(String hash);
	public void exportFailed();
}
