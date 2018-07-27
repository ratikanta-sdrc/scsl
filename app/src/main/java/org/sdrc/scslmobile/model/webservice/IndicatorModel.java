package org.sdrc.scslmobile.model.webservice;


/**
 * Ratikanta Pradhan (ratikanta@sdrc.co.in) on 25-Apr-2017 1:58:33 pm
 */
public class IndicatorModel {

    private Integer indicatorId;
    private String indicatorName;
    private String numerator;
    private String denominator;
    private Boolean isReqired;
    private Integer processIndicatorId;
    private Integer intermediateIndicatorId;
    private Integer outcomeIndicatorId;
    private String exceptionRule;
    private Integer coreArea;
    private Integer indicatorType;
    private Integer indicatorOrder;
    private Boolean isLr;
    private Boolean isProfile;
    public Integer getIndicatorId() {
        return indicatorId;
    }
    public void setIndicatorId(Integer indicatorId) {
        this.indicatorId = indicatorId;
    }
    public String getIndicatorName() {
        return indicatorName;
    }
    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }
    public String getNumerator() {
        return numerator;
    }
    public void setNumerator(String numerator) {
        this.numerator = numerator;
    }
    public String getDenominator() {
        return denominator;
    }
    public void setDenominator(String denominator) {
        this.denominator = denominator;
    }
    public Boolean getIsReqired() {
        return isReqired;
    }
    public void setIsReqired(Boolean isReqired) {
        this.isReqired = isReqired;
    }
    public Integer getProcessIndicatorId() {
        return processIndicatorId;
    }
    public void setProcessIndicatorId(Integer processIndicatorId) {
        this.processIndicatorId = processIndicatorId;
    }
    public Integer getIntermediateIndicatorId() {
        return intermediateIndicatorId;
    }
    public void setIntermediateIndicatorId(Integer intermediateIndicatorId) {
        this.intermediateIndicatorId = intermediateIndicatorId;
    }
    public Integer getOutcomeIndicatorId() {
        return outcomeIndicatorId;
    }
    public void setOutcomeIndicatorId(Integer outcomeIndicatorId) {
        this.outcomeIndicatorId = outcomeIndicatorId;
    }
    public String getExceptionRule() {
        return exceptionRule;
    }
    public void setExceptionRule(String exceptionRule) {
        this.exceptionRule = exceptionRule;
    }
    public Integer getCoreArea() {
        return coreArea;
    }
    public void setCoreArea(Integer coreArea) {
        this.coreArea = coreArea;
    }
    public Integer getIndicatorType() {
        return indicatorType;
    }
    public void setIndicatorType(Integer indicatorType) {
        this.indicatorType = indicatorType;
    }

    public Integer getIndicatorOrder() {
        return indicatorOrder;
    }
    public void setIndicatorOrder(Integer indicatorOrder) {
        this.indicatorOrder = indicatorOrder;
    }
    public Boolean getIsLr() {
        return isLr;
    }
    public void setIsLr(Boolean isLr) {
        this.isLr = isLr;
    }
    public Boolean getIsProfile() {
        return isProfile;
    }
    public void setIsProfile(Boolean isProfile) {
        this.isProfile = isProfile;
    }

}
