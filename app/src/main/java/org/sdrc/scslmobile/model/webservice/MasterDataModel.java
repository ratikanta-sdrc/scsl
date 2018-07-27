package org.sdrc.scslmobile.model.webservice;

import java.util.List;

/**
 * This following model class will take data from server to mobile device, after successful login
 * @since 1.0.0
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in) on 24-Apr-2017 3:54:17 pm
 */
public class MasterDataModel {
	
	private UserModel userModel;
	private int errorCode;
	private String errorMessage;
	private List<AreaModel> areaModels;
	private List<IndicatorModel> indicatorModels;
	private List<TypeModel> typeModels;
	private List<TypeDetailModel> typeDetailModels;
	private List<TimePeriodModel> timePeriodModels;
	private List<MSTEngagementScoreModel> mSTEngagementScoreModels;
	private List<IndicatorFacilityTimeperiodMappingModel> indicatorFacilityTimeperiodMappingModels;
	private String lastSyncDate;
	private int deoDeadLine;
    private int subDeadLine;
    private int mneDeadLine;
	public UserModel getUserModel() {
		return userModel;
	}
	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<AreaModel> getAreaModels() {
		return areaModels;
	}
	public void setAreaModels(List<AreaModel> areaModels) {
		this.areaModels = areaModels;
	}
	public List<IndicatorModel> getIndicatorModels() {
		return indicatorModels;
	}
	public void setIndicatorModels(List<IndicatorModel> indicatorModels) {
		this.indicatorModels = indicatorModels;
	}
	public List<TypeModel> getTypeModels() {
		return typeModels;
	}
	public void setTypeModels(List<TypeModel> typeModels) {
		this.typeModels = typeModels;
	}
	public List<TypeDetailModel> getTypeDetailModels() {
		return typeDetailModels;
	}
	public void setTypeDetailModels(List<TypeDetailModel> typeDetailModels) {
		this.typeDetailModels = typeDetailModels;
	}
	public List<TimePeriodModel> getTimePeriodModels() {
		return timePeriodModels;
	}
	public void setTimePeriodModels(List<TimePeriodModel> timePeriodModels) {
		this.timePeriodModels = timePeriodModels;
	}
	public List<MSTEngagementScoreModel> getmSTEngagementScoreModels() {
		return mSTEngagementScoreModels;
	}
	public void setmSTEngagementScoreModels(
			List<MSTEngagementScoreModel> mSTEngagementScoreModels) {
		this.mSTEngagementScoreModels = mSTEngagementScoreModels;
	}
	public List<IndicatorFacilityTimeperiodMappingModel> getIndicatorFacilityTimeperiodMappingModels() {
		return indicatorFacilityTimeperiodMappingModels;
	}
	public void setIndicatorFacilityTimeperiodMappingModels(
			List<IndicatorFacilityTimeperiodMappingModel> indicatorFacilityTimeperiodMappingModels) {
		this.indicatorFacilityTimeperiodMappingModels = indicatorFacilityTimeperiodMappingModels;
	}
	public String getLastSyncDate() {
		return lastSyncDate;
	}
	public void setLastSyncDate(String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	public int getDeoDeadLine() {
		return deoDeadLine;
	}
	public void setDeoDeadLine(int deoDeadLine) {
		this.deoDeadLine = deoDeadLine;
	}
	public int getSubDeadLine() {
		return subDeadLine;
	}
	public void setSubDeadLine(int subDeadLine) {
		this.subDeadLine = subDeadLine;
	}
	public int getMneDeadLine() {
		return mneDeadLine;
	}
	public void setMneDeadLine(int mneDeadLine) {
		this.mneDeadLine = mneDeadLine;
	}
    
    
}
