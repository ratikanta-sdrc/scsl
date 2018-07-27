package org.sdrc.scslmobile.model.webservice;
/**
 * Ratikanta Pradhan (ratikanta@sdrc.co.in) on 24-Apr-2017 4:12:31 pm
 */
public class LoginDataModel {

	private String username;
	private String password;
	private String lastSyncDate;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLastSyncDate() {
		return lastSyncDate;
	}
	public void setLastSyncDate(String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
}
