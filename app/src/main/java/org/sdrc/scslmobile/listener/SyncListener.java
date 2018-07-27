package org.sdrc.scslmobile.listener;


import org.sdrc.scslmobile.model.AsyncTaskResultModel;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 29-01-2017.
 */

public interface SyncListener {

    void syncComplete(AsyncTaskResultModel asyncTaskResultModel);
}
