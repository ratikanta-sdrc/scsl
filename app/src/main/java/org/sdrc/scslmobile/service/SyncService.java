package org.sdrc.scslmobile.service;


import org.sdrc.scslmobile.model.webservice.SyncModel;
import org.sdrc.scslmobile.model.webservice.SyncResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 29-01-2017.
 * This service will help post sync
 */

public interface SyncService {

    @POST("sync")
    Call<SyncResult> SyncResult(@Body SyncModel syncModel);
}
