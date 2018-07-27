package org.sdrc.scslmobile.model.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * This class will keep transactional data for SNCU/NICU module filled up by DEO
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in), created on 02-05-2017.
 */

public class TXNSNCUNICUData extends RealmObject {
    @PrimaryKey
    private int txnIndicatorId;
    private int indicatorFacilityTimeperiodMapping;
    private Integer numeratorValue;
    private Integer denominatorValue;
    private Double percentage;
    private Date createdDate;
    private boolean isSynced;
    private boolean hasError;
    private boolean isRejectedBySup;
    private boolean isRejectedByMNE;
    private boolean isApprovedBySup;
    private boolean isApprovedByMNE;
    private boolean isAutoApproved;
    private String remarkBySup;
    private String remarkByMNE;
    private Integer description;

    public boolean isAutoApproved() {
        return isAutoApproved;
    }

    public void setAutoApproved(boolean autoApproved) {
        isAutoApproved = autoApproved;
    }


    public boolean isApprovedBySup() {
        return isApprovedBySup;
    }

    public void setApprovedBySup(boolean approvedBySup) {
        isApprovedBySup = approvedBySup;
    }

    public boolean isApprovedByMNE() {
        return isApprovedByMNE;
    }

    public void setApprovedByMNE(boolean approvedByMNE) {
        isApprovedByMNE = approvedByMNE;
    }

    public int getTxnIndicatorId() {
        return txnIndicatorId;
    }

    public void setTxnIndicatorId(int txnIndicatorId) {
        this.txnIndicatorId = txnIndicatorId;
    }

    public int getIndicatorFacilityTimeperiodMapping() {
        return indicatorFacilityTimeperiodMapping;
    }

    public void setIndicatorFacilityTimeperiodMapping(int indicatorFacilityTimeperiodMapping) {
        this.indicatorFacilityTimeperiodMapping = indicatorFacilityTimeperiodMapping;
    }

    public Integer getNumeratorValue() {
        return numeratorValue;
    }

    public void setNumeratorValue(Integer numeratorValue) {
        this.numeratorValue = numeratorValue;
    }

    public Integer getDenominatorValue() {
        return denominatorValue;
    }

    public void setDenominatorValue(Integer denominatorValue) {
        this.denominatorValue = denominatorValue;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean isRejectedBySup() {
        return isRejectedBySup;
    }

    public void setRejectedBySup(boolean rejectedBySup) {
        isRejectedBySup = rejectedBySup;
    }

    public boolean isRejectedByMNE() {
        return isRejectedByMNE;
    }

    public void setRejectedByMNE(boolean rejectedByMNE) {
        isRejectedByMNE = rejectedByMNE;
    }

    public String getRemarkBySup() {
        return remarkBySup;
    }

    public void setRemarkBySup(String remarkBySup) {
        this.remarkBySup = remarkBySup;
    }

    public String getRemarkByMNE() {
        return remarkByMNE;
    }

    public void setRemarkByMNE(String remarkByMNE) {
        this.remarkByMNE = remarkByMNE;
    }

    public Integer getDescription() {
        return description;
    }

    public void setDescription(Integer description) {
        this.description = description;
    }
}
