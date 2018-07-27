package org.sdrc.scslmobile.model.realm;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta pradhan
 */

public class IndicatorFacilityTimeperiodMapping extends RealmObject{
   @PrimaryKey
    private int indFacilityTpId;
    private Date createdDate;
    private int facility;
    private int indicator;
    private int timePeriod;
    private boolean isNew;

    public int getIndFacilityTpId() {
        return indFacilityTpId;
    }

    public void setIndFacilityTpId(int indFacilityTpId) {
        this.indFacilityTpId = indFacilityTpId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getFacility() {
        return facility;
    }

    public void setFacility(int facility) {
        this.facility = facility;
    }

    public int getIndicator() {
        return indicator;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
