package org.sdrc.scslmobile.service;



import org.sdrc.scslmobile.model.webservice.LoginDataModel;
import org.sdrc.scslmobile.model.webservice.MasterDataModel;

import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 29-01-2017.
 * This interface will help in login
 */

public interface LoginService {

    @POST("login")
    Call<MasterDataModel> MasterDataModel(@Body LoginDataModel loginDataModel);

    Map<String, Boolean> insertData(MasterDataModel masterDataModel, boolean isFirstUser, Realm realm, String username, String password);
}
