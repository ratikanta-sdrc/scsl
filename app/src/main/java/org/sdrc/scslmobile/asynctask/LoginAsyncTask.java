package org.sdrc.scslmobile.asynctask;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.sdrc.scslmobile.listener.LoginListener;
import org.sdrc.scslmobile.model.LoginDataModel;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.model.webservice.MasterDataModel;
import org.sdrc.scslmobile.service.LoginService;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.SCSL;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 26-04-2017.
 * This AsyncTask will help login process
 */

public class LoginAsyncTask extends AsyncTask<Object, Void, Object> {

    private LoginListener loginListener;
    private boolean hitServer;
    private boolean isNewUser;
    private String lastSyncDate;

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }
    private MasterDataModel masterDataModel;

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            LoginDataModel loginDataModel = new LoginDataModel();
            String username = (String) objects[0];
            String password = (String) objects[1];

            Realm realm = Realm.getDefaultInstance();
            User user = realm.where(User.class).findFirst();
            //Checking whether user data present in database or not
            if (user != null) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {

                    //login success
                    loginDataModel.setResult(Constant.Result.SUCCESS);
                    loginDataModel.setDEO(user.isDEO());
                    realm.close();
                    return loginDataModel;
                } else {
                    //User did not match, we have to hit server with given username and password
                    SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
                    lastSyncDate = SCSL.getInstance().getFullDateString(sysConfig.getLastSyncDate());
                    isNewUser = true;
                    hitServer = true;
                }
            } else {
                hitServer = true;
                isNewUser = false;
            }
            realm.close();
            if(hitServer){
                //There is no user in database, we have to hit the server
                //checking internet connection
                if ((Boolean) objects[4]) {

                    String url = (String) objects[2];
                    int login_timeout_in_second = (Integer) objects[3];
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    //We are using OkHttpClient for setting the timeout only
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(login_timeout_in_second, TimeUnit.SECONDS)
                            .connectTimeout(login_timeout_in_second, TimeUnit.SECONDS)
                            .addInterceptor(interceptor)
                            .build();
                   Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(okHttpClient)
                            .build();

                    LoginService service = retrofit.create(LoginService.class);
                    org.sdrc.scslmobile.model.webservice.LoginDataModel loginDataModel1 = new org.sdrc.scslmobile.model.webservice.LoginDataModel();
                    loginDataModel1.setUsername(username);
                    loginDataModel1.setPassword(password);
                    loginDataModel1.setLastSyncDate(lastSyncDate);
                    Call<MasterDataModel> call = service.MasterDataModel(loginDataModel1);

                    call.enqueue(new Callback<MasterDataModel>() {
                        @Override
                        public void onResponse(Call<MasterDataModel> call, retrofit2.Response<MasterDataModel> response) {
                            //handling the responce
                            if (response.code() == 200) {
                                masterDataModel = response.body();
                                if (masterDataModel != null && masterDataModel.getErrorCode() != 0) {
                                    LoginDataModel loginDataModelErr = new LoginDataModel();
                                    loginDataModelErr.setResult(Constant.Result.ERROR);
                                    loginDataModelErr.setMessage(masterDataModel.getErrorMessage());
                                    onPostExecute(loginDataModelErr);
                                } else {
                                    LoginDataModel loginDataModelSuccess = new LoginDataModel();
                                    loginDataModelSuccess.setResult(Constant.Result.SUCCESS_AFTER_SERVER_HIT);
                                    loginDataModelSuccess.setMasterDataModel(masterDataModel);
                                    loginDataModelSuccess.setNewUser(isNewUser);
                                    onPostExecute(loginDataModelSuccess);
                                }
                            } else {
                                LoginDataModel loginDataModelErr = new LoginDataModel();
                                switch (response.code()) {
                                    case 401:
                                        loginDataModelErr.setResult(Constant.Result.ERROR);
                                        loginDataModelErr.setMessage("Invalid Credentials");
                                        break;
                                    case 404:
                                        loginDataModelErr.setResult(Constant.Result.ERROR);
                                        loginDataModelErr.setMessage("Server not found, please contact admin.");
                                        break;
                                    case 500:
                                        loginDataModelErr.setMessage("Server error, please contact admin");
                                        loginDataModelErr.setResult(Constant.Result.SERVER_ERROR);
                                        break;
                                    default:
                                        loginDataModelErr.setResult(Constant.Result.ERROR);
                                        loginDataModelErr.setMessage(response.code() + ": " + response.message());
                                        break;
                                }
                                onPostExecute(loginDataModelErr);
                            }
                        }

                        @Override
                        public void onFailure(Call<MasterDataModel> call, Throwable t) {
                            //handling the error
                            LoginDataModel loginDataModelErr = new LoginDataModel();
                            loginDataModelErr.setResult(Constant.Result.SERVER_ERROR);

                            if (t instanceof SocketTimeoutException) {
                                loginDataModelErr.setMessage("Request timeout");
                            } else {
                                loginDataModelErr.setMessage(t.getMessage());
                            }
                            onPostExecute(loginDataModelErr);
                        }


                    });

                } else {
                    loginDataModel.setResult(Constant.Result.NO_INTERNET);
                    return loginDataModel;
                }
            }

            return loginDataModel;

        }catch (Exception e){
            e.printStackTrace();
            LoginDataModel loginDataModel = new LoginDataModel();
            loginDataModel.setResult(Constant.Result.ERROR);
            loginDataModel.setMessage(e.getMessage());
            return loginDataModel;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        if(this.loginListener != null){
            loginListener.loginComplete((LoginDataModel) o);
        }
        super.onPostExecute(o);
    }
}
