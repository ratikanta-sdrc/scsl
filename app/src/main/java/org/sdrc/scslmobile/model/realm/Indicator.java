package org.sdrc.scslmobile.model.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta Pradhan
 */

public class Indicator extends RealmObject {
   @PrimaryKey
    private int indicatorId;
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
    private boolean isLr;
    private boolean isProfile;

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
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

    public Boolean getReqired() {
        return isReqired;
    }

    public void setReqired(Boolean reqired) {
        isReqired = reqired;
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

    public boolean isLr() {
        return isLr;
    }

    public void setLr(boolean lr) {
        isLr = lr;
    }

    public boolean isProfile() {
        return isProfile;
    }

    public void setProfile(boolean profile) {
        isProfile = profile;
    }
}
