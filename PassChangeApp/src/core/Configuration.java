package core;

public class Configuration {
	private boolean logoutWhenAppIsPaused;
	private int logoutTimeMinutes;
	private int rememberTimeMinmutes;
	public Configuration(boolean logoutWhenAppIsPaused, int logoutTimeMinutes,
			int rememberTimeMinmutes) {
		super();
		this.logoutWhenAppIsPaused = logoutWhenAppIsPaused;
		this.logoutTimeMinutes = logoutTimeMinutes;
		this.rememberTimeMinmutes = rememberTimeMinmutes;
	}
	public int getRememberTimeMinutes() {
		return rememberTimeMinmutes;
	}
	public void setRememberTimeMinmutes(int rememberTimeMinmutes) {
		this.rememberTimeMinmutes = rememberTimeMinmutes;
	}
	public boolean isLogoutWhenAppIsPaused() {
		return logoutWhenAppIsPaused;
	}
	public void setLogoutWhenAppIsPaused(boolean logoutWhenAppIsPaused) {
		this.logoutWhenAppIsPaused = logoutWhenAppIsPaused;
	}
	public int getLogoutTimeMinutes() {
		return logoutTimeMinutes;
	}
	public void setLogoutTimeMinutes(int logoutTimeMinutes) {
		this.logoutTimeMinutes = logoutTimeMinutes;
	}
}
