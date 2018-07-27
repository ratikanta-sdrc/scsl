package org.sdrc.scslmobile.model.webservice;


/**
 * Ratikanta Pradhan
 */
public class AreaModel {


	private int areaId;
	private String areaName;
	private int parentAreaId;
	private int level;
	private int wave;
	private int facilityType;
	private int facilitySize;

	private String facilitySizeName;
	private String facilityTypeName;

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
	public int getWave() {
		return wave;
	}
	public void setWave(int wave) {
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
	public String getFacilitySizeName() {
		return facilitySizeName;
	}
	public void setFacilitySizeName(String facilitySizeName) {
		this.facilitySizeName = facilitySizeName;
	}
	public String getFacilityTypeName() {
		return facilityTypeName;
	}
	public void setFacilityTypeName(String facilityTypeName) {
		this.facilityTypeName = facilityTypeName;
	}
	public boolean isHasLR() {
		return hasLR;
	}
	public void setHasLR(boolean hasLR) {
		this.hasLR = hasLR;
	}


}
