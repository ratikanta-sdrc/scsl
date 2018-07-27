package org.sdrc.scslmobile.model;

import org.sdrc.scslmobile.model.webservice.MasterDataModel;

/**
 * This class will help us to get the data from asynctask to activity
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 26-01-2017.
 *
 */

public class LoginDataModel {

    private int result;
    private String message;
    private boolean isDEO;
    private boolean isNewUser;
    private MasterDataModel masterDataModel;



    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDEO() {
        return isDEO;
    }

    public void setDEO(boolean DEO) {
        isDEO = DEO;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    public MasterDataModel getMasterDataModel() {
        return masterDataModel;
    }

    public void setMasterDataModel(MasterDataModel masterDataModel) {
        this.masterDataModel = masterDataModel;
    }
}
