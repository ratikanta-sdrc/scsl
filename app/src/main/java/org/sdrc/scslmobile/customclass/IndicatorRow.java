package org.sdrc.scslmobile.customclass;

/**
 * Created by Amit Kumar Sahoo(amit@sdrc.co.in) on 24-04-2017.
 */

public class IndicatorRow {
    private int slNo;
    private String indicatorName;
    private int indicatorId;
    private boolean select;

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
