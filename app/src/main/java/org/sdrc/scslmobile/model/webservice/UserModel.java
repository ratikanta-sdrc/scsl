package org.sdrc.scslmobile.model.webservice;

import java.util.List;

/**
 *  Ratikanta Pradhan (ratikanta@sdrc.co.in) on 24-Apr-2017 4:01:40 pm
 */
public class UserModel {

	private String name;
	private List<Integer> areaIds;
	private Boolean isDEO;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Integer> getAreaIds() {
		return areaIds;
	}
	public void setAreaIds(List<Integer> areaIds) {
		this.areaIds = areaIds;
	}
	public Boolean getIsDEO() {
		return isDEO;
	}
	public void setIsDEO(Boolean isDEO) {
		this.isDEO = isDEO;
	}
	
}
