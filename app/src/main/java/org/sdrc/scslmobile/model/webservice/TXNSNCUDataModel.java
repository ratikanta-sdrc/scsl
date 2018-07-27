package org.sdrc.scslmobile.model.webservice;

/**
 * 
 * This model class will help to bring data from mobile
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in) on 06-May-2017 11:15:02 am
 */
public class TXNSNCUDataModel {

	private int id;
	private int iftid;
	private Integer numeratorValue;
	private Integer denominatorValue;
	private Double percentage;
	private String createdDate;
	private String errorMessage;
	private int rejectedBySup;
	private int rejectedByMNE;
	private Integer description;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIftid() {
		return iftid;
	}
	public void setIftid(int iftid) {
		this.iftid = iftid;
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
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int getRejectedBySup() {
		return rejectedBySup;
	}
	public void setRejectedBySup(int rejectedBySup) {
		this.rejectedBySup = rejectedBySup;
	}
	public int getRejectedByMNE() {
		return rejectedByMNE;
	}
	public void setRejectedByMNE(int rejectedByMNE) {
		this.rejectedByMNE = rejectedByMNE;
	}
	public Integer getDescription() {
		return description;
	}
	public void setDescription(Integer description) {
		this.description = description;
	}

}
