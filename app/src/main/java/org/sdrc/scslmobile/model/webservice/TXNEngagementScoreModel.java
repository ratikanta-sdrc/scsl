package org.sdrc.scslmobile.model.webservice;

/**
 * 
 * This model class will bring data from mobile
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in) on 06-May-2017 11:13:18 am
 */
public class TXNEngagementScoreModel {

	
	private int engagementScoreId;
	private int areaId;
	private int timePeriodId;
	private String createdDate;
	private String rejectionMessage;
	public int getEngagementScoreId() {
		return engagementScoreId;
	}
	public void setEngagementScoreId(int engagementScoreId) {
		this.engagementScoreId = engagementScoreId;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getTimePeriodId() {
		return timePeriodId;
	}
	public void setTimePeriodId(int timePeriodId) {
		this.timePeriodId = timePeriodId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getRejectionMessage() {
		return rejectionMessage;
	}
	public void setRejectionMessage(String rejectionMessage) {
		this.rejectionMessage = rejectionMessage;
	}
	
	
	
}
