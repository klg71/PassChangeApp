package core;

public class Configuration {
	private boolean logoutWhenAppIsPaused;
	private int logoutTimeMinutes;
	public Configuration(boolean logoutWhenAppIsPaused, int logoutTimeMinutes) {
		super();
		this.logoutWhenAppIsPaused = logoutWhenAppIsPaused;
		this.logoutTimeMinutes = logoutTimeMinutes;
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
