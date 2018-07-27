package org.sdrc.scslmobile.model.webservice;

/**
 * 
 * @since 1.0.0
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in) on 26-Apr-2017 10:32:52 am
 */
public class IndicatorFacilityTimeperiodMappingModel {
	
	private int indFacilityTpId;
	private String createdDate;
	private int facilityId;
	private int indicatorId;
	private int timePeriodId;
	private String rejectMessage;
	public int getIndFacilityTpId() {
		return indFacilityTpId;
	}
	public void setIndFacilityTpId(int indFacilityTpId) {
		this.indFacilityTpId = indFacilityTpId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public int getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}
	public int getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
	public int getTimePeriodId() {
		return timePeriodId;
	}
	public void setTimePeriodId(int timePeriodId) {
		this.timePeriodId = timePeriodId;
	}
	public String getRejectMessage() {
		return rejectMessage;
	}
	public void setRejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}
	
	
}
