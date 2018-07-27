package org.sdrc.scslmobile.model.webservice;

import java.util.List;

/**
 * This model class is going to get transaction data from mobile device to server 
 *
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in) on 24-Apr-2017 4:32:51 pm
 *  @author subhadarshani Patra (subhadarshani@sdrc.co.in)
 */
public class SyncModel {

	private LoginDataModel loginDataModel;
	private List<TXNSNCUDataModel> txnsncuDataModels;
	private List<TXNEngagementScoreModel> txnEngagementScoreModels;
	private List<IndicatorFacilityTimeperiodMappingModel> mappingModels;
	private int deo;
	private String apiVersion;
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	public LoginDataModel getLoginDataModel() {
		return loginDataModel;
	}
	public void setLoginDataModel(LoginDataModel loginDataModel) {
		this.loginDataModel = loginDataModel;
	}
	public List<TXNSNCUDataModel> getTxnsncuDataModels() {
		return txnsncuDataModels;
	}
	public void setTxnsncuDataModels(List<TXNSNCUDataModel> txnsncuDataModels) {
		this.txnsncuDataModels = txnsncuDataModels;
	}
	public List<TXNEngagementScoreModel> getTxnEngagementScoreModels() {
		return txnEngagementScoreModels;
	}
	public void setTxnEngagementScoreModels(
			List<TXNEngagementScoreModel> txnEngagementScoreModels) {
		this.txnEngagementScoreModels = txnEngagementScoreModels;
	}
	public List<IndicatorFacilityTimeperiodMappingModel> getMappingModels() {
		return mappingModels;
	}
	public void setMappingModels(
			List<IndicatorFacilityTimeperiodMappingModel> mappingModels) {
		this.mappingModels = mappingModels;
	}
	public int getDeo() {
		return deo;
	}
	public void setDeo(int deo) {
		this.deo = deo;
	}
	
}
