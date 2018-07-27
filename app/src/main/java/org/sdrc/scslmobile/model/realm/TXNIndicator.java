package org.sdrc.scslmobile.model.realm;



import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SDRC_DEV on 24-04-2017.
 * Ratikanta
 * Amit Kumar Sahoo
 */

public class TXNIndicator extends RealmObject {
  @PrimaryKey
    private int txnIndicatorId;
    private Integer numeratorValue;
    private Integer denominatorValue;
    private Double percentage;
    private Date createdDate;
    private boolean isSynced;

    public int getTxnIndicatorId() {
        return txnIndicatorId;
    }

    public void setTxnIndicatorId(int txnIndicatorId) {
        this.txnIndicatorId = txnIndicatorId;
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
}
