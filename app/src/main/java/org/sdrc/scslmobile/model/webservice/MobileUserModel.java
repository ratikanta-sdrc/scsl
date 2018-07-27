package org.sdrc.scslmobile.model.webservice;

/**
 * Ratikanta Pradhan (ratikanta@sdrc.co.in) on 28-Jan-2017 4:34:13 pm
 */
public class MobileUserModel {
	
	private String username;
	private String password;
	private String imei;
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
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
}
