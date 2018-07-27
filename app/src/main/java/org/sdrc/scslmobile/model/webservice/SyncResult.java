package org.sdrc.scslmobile.model.webservice;

import java.util.List;

/**
 * This model class is going to take sync result to mobile phone from server.
 *
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in) on 24-Apr-2017 4:31:28 pm
 */
public class SyncResult {

    private int errorCode;
    private String errorMessage;
    private MasterDataModel masterDataModel;
    private int rejectedBySup;
    private String remarkSup;
    private String remarkMne;
    private int aprovedBySup;
    private int approveByMne;
    private String rejectedDate;
    private String submittedDate;
    private int rejectedByMne;
    private List<TXNSNCUDataModel> txnsncuDataModels;
    private List<TXNEngagementScoreModel> txnEngagementScoreModels;
    private List<IndicatorFacilityTimeperiodMappingModel> mappingModels;
    private int autoApproved;

    public int getAutoApproved() {
        return autoApproved;
    }

    public void setAutoApproved(int autoApproved) {
        this.autoApproved = autoApproved;
    }


    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }


    public int getAprovedBySup() {
        return aprovedBySup;
    }

    public void setAprovedBySup(int aprovedBySup) {
        this.aprovedBySup = aprovedBySup;
    }

    public int getApproveByMne() {
        return approveByMne;
    }

    public void setApproveByMne(int approveByMne) {
        this.approveByMne = approveByMne;
    }


    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
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

    public MasterDataModel getMasterDataModel() {
        return masterDataModel;
    }

    public void setMasterDataModel(MasterDataModel masterDataModel) {
        this.masterDataModel = masterDataModel;
    }

    public int getRejectedBySup() {
        return rejectedBySup;
    }

    public void setRejectedBySup(int rejectedBySup) {
        this.rejectedBySup = rejectedBySup;
    }

    public String getRemarkSup() {
        return remarkSup;
    }

    public void setRemarkSup(String remarkSup) {
        this.remarkSup = remarkSup;
    }

    public String getRemarkMne() {
        return remarkMne;
    }

    public void setRemarkMne(String remarkMne) {
        this.remarkMne = remarkMne;
    }

    public int getRejectedByMne() {
        return rejectedByMne;
    }

    public void setRejectedByMne(int rejectedByMne) {
        this.rejectedByMne = rejectedByMne;
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

}
