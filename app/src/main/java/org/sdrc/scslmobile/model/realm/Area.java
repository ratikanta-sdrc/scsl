package org.sdrc.scslmobile.model.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta Pradhan
 * Amit kr. Sahoo
 */

public class Area extends RealmObject {

    @PrimaryKey
    private int areaId;
    private String areaName;
    private int parentAreaId;
    private int level;
    private Integer wave;
    private int facilityType;
    private int facilitySize;
    private boolean hasLR;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(int parentAreaId) {
        this.parentAreaId = parentAreaId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Integer getWave() {
        return wave;
    }

    public void setWave(Integer wave) {
        this.wave = wave;
    }

    public int getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(int facilityType) {
        this.facilityType = facilityType;
    }

    public int getFacilitySize() {
        return facilitySize;
    }

    public void setFacilitySize(int facilitySize) {
        this.facilitySize = facilitySize;
    }

    public boolean isHasLR() {
        return hasLR;
    }

    public void setHasLR(boolean hasLR) {
        this.hasLR = hasLR;
    }
}
