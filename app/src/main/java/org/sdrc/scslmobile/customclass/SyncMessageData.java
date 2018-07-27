package org.sdrc.scslmobile.customclass;

/**
 * Created by SDRC_DEV on 11-05-2017.
 */

public class SyncMessageData {
    private String indicatorName;
    private String errorMessage;

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    private String msgType;
}
