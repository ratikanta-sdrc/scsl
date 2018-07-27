package org.sdrc.scslmobile.model.realm;



import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SDRC_DEV on 24-04-2017.
 */

public class TXNEngagementScore extends RealmObject {
   @PrimaryKey
    private int txnEngagementScoreId;
    private Date createdDate;
    private double engagementScore;
    private int mstEngagementScoreId;
    private int facility;
    private int timePeriod;
    private boolean isSynced;

    public int getTxnEngagementScoreId() {
        return txnEngagementScoreId;
    }

    public void setTxnEngagementScoreId(int txnEngagementScoreId) {
        this.txnEngagementScoreId = txnEngagementScoreId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public double getEngagementScore() {
        return engagementScore;
    }

    public void setEngagementScore(double engagementScore) {
        this.engagementScore = engagementScore;
    }

    public int getMstEngagementScoreId() {
        return mstEngagementScoreId;
    }

    public void setMstEngagementScoreId(int mstEngagementScoreId) {
        this.mstEngagementScoreId = mstEngagementScoreId;
    }

    public int getFacility() {
        return facility;
    }

    public void setFacility(int facility) {
        this.facility = facility;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
