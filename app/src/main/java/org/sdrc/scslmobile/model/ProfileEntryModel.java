package org.sdrc.scslmobile.model;

import java.io.Serializable;

/**
 * Created by Amit Kumar Sahoo(amit@sdrc.co.in) on 07-08-2017.
 */

public class ProfileEntryModel implements Serializable {

    private Integer noOfInbornAdmission;
    private Integer noOfOutbornAdmission;
    private Integer noOfTotalAdmission;
    private Float percentOfInbornAdmission;
    private Float percentOfOutbornAdmission;
    private Integer noOfCSection;
    private Integer noOfNormalDeliveries;
    private Integer noOfTotalDeliveries;
    private Float percentOfCSection;
    private Float percentOfNormalDelivery;
    private Integer noOfLiveBirth;

    public Integer getNoOfInbornAdmission() {
        return noOfInbornAdmission;
    }

    public void setNoOfInbornAdmission(Integer noOfInbornAdmission) {
        this.noOfInbornAdmission = noOfInbornAdmission;
    }

    public Integer getNoOfOutbornAdmission() {
        return noOfOutbornAdmission;
    }

    public void setNoOfOutbornAdmission(Integer noOfOutbornAdmission) {
        this.noOfOutbornAdmission = noOfOutbornAdmission;
    }

    public Integer getNoOfTotalAdmission() {
        return noOfTotalAdmission;
    }

    public void setNoOfTotalAdmission(Integer noOfTotalAdmission) {
        this.noOfTotalAdmission = noOfTotalAdmission;
    }

    public Integer getNoOfCSection() {
        return noOfCSection;
    }

    public void setNoOfCSection(Integer noOfCSection) {
        this.noOfCSection = noOfCSection;
    }

    public Integer getNoOfNormalDeliveries() {
        return noOfNormalDeliveries;
    }

    public void setNoOfNormalDeliveries(Integer noOfNormalDeliveries) {
        this.noOfNormalDeliveries = noOfNormalDeliveries;
    }

    public Integer getNoOfTotalDeliveries() {
        return noOfTotalDeliveries;
    }

    public void setNoOfTotalDeliveries(Integer noOfTotalDeliveries) {
        this.noOfTotalDeliveries = noOfTotalDeliveries;
    }

    public Integer getNoOfLiveBirth() {
        return noOfLiveBirth;
    }

    public void setNoOfLiveBirth(Integer noOfLiveBirth) {
        this.noOfLiveBirth = noOfLiveBirth;
    }

    public Float getPercentOfInbornAdmission() {
        return percentOfInbornAdmission;
    }

    public void setPercentOfInbornAdmission(Float percentOfInbornAdmission) {
        this.percentOfInbornAdmission = percentOfInbornAdmission;
    }

    public Float getPercentOfOutbornAdmission() {
        return percentOfOutbornAdmission;
    }

    public void setPercentOfOutbornAdmission(Float percentOfOutbornAdmission) {
        this.percentOfOutbornAdmission = percentOfOutbornAdmission;
    }

    public Float getPercentOfCSection() {
        return percentOfCSection;
    }

    public void setPercentOfCSection(Float percentOfCSection) {
        this.percentOfCSection = percentOfCSection;
    }

    public Float getPercentOfNormalDelivery() {
        return percentOfNormalDelivery;
    }

    public void setPercentOfNormalDelivery(Float percentOfNormalDelivery) {
        this.percentOfNormalDelivery = percentOfNormalDelivery;
    }
}
