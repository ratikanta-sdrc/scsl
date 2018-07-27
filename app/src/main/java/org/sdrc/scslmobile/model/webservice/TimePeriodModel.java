package org.sdrc.scslmobile.model.webservice;

/**
 * Ratikanta Pradhan (ratikanta@sdrc.co.in) on 25-Apr-2017 8:11:39 pm
 */
public class TimePeriodModel{

	private int timePeriodId;	
	private String timePeriod;	
	private String startDate;	
	private String endDate;	
	private String periodicity;	
	private int wave;
	public int getTimePeriodId() {
		return timePeriodId;
	}
	public void setTimePeriodId(int timePeriodId) {
		this.timePeriodId = timePeriodId;
	}
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getPeriodicity() {
		return periodicity;
	}
	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}
	public int getWave() {
		return wave;
	}
	public void setWave(int wave) {
		this.wave = wave;
	}
	
}
