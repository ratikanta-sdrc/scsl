package org.sdrc.scslmobile.model;

import org.sdrc.scslmobile.model.webservice.SyncModel;
import org.sdrc.scslmobile.model.webservice.SyncResult;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 29-01-2017.
 */

public class AsyncTaskResultModel {
    private int result;
    private String message;
    private SyncResult syncResult;
    private SyncModel syncModel;

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

    public SyncResult getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(SyncResult syncResult) {
        this.syncResult = syncResult;
    }

    public SyncModel getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(SyncModel syncModel) {
        this.syncModel = syncModel;
    }
}
