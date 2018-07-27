package org.sdrc.scslmobile.asynctask;

import android.os.AsyncTask;
import org.sdrc.scslmobile.listener.SyncListener;
import org.sdrc.scslmobile.model.AsyncTaskResultModel;
import org.sdrc.scslmobile.model.webservice.SyncModel;
import org.sdrc.scslmobile.model.webservice.SyncResult;
import org.sdrc.scslmobile.service.SyncService;
import org.sdrc.scslmobile.util.Constant;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 29-01-2017.
 * This class will help in transfering value to the activity
 */

public class SyncAsyncTask extends AsyncTask<Object, Void, Object> {

    private SyncListener syncListener;
    private SyncModel syncModel;

    public void setSyncListener(SyncListener syncListener) {
        this.syncListener = syncListener;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        //This following functionality will send and recieve data from server(sync)
        AsyncTaskResultModel asyncTaskResultModel = new AsyncTaskResultModel();
        //internet check
        if((Boolean)objects[1]){
            syncModel = (SyncModel) objects[3];

            if(syncModel != null) {

                String url = (String) objects[0];
                int sync_timeout_in_second = (Integer) objects[2];
                //We are using OkHttpClient for setting the timeout only
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(sync_timeout_in_second, TimeUnit.SECONDS)
                        .connectTimeout(sync_timeout_in_second, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

                SyncService service = retrofit.create(SyncService.class);

                Call<SyncResult> call = service.SyncResult(syncModel);

                call.enqueue(new Callback<SyncResult>() {
                    @Override
                    public void onResponse(Call<SyncResult> call, retrofit2.Response<SyncResult> response) {

                        if (response.code() == 200) {
                            //Request success
                            SyncResult syncResult = response.body();
                            if (syncResult != null) {
                                if (syncResult.getErrorCode() != 0) {
                                    //error from server
                                    AsyncTaskResultModel asyncTaskResultModel1 = new AsyncTaskResultModel();
                                    asyncTaskResultModel1.setResult(Constant.Result.ERROR);
                                    asyncTaskResultModel1.setMessage(syncResult.getErrorMessage());
                                    onPostExecute(asyncTaskResultModel1);
                                }
                                else {
                                    //No error happened
                                    AsyncTaskResultModel asyncTaskResultModel1 = new AsyncTaskResultModel();
                                    asyncTaskResultModel1.setResult(Constant.Result.SUCCESS);
                                    asyncTaskResultModel1.setSyncResult(syncResult);
                                    asyncTaskResultModel1.setSyncModel(syncModel);
                                    onPostExecute(asyncTaskResultModel1);
                                }
                            } else {
                                AsyncTaskResultModel asyncTaskResultModel1 = new AsyncTaskResultModel();
                                asyncTaskResultModel1.setResult(Constant.Result.SERVER_ERROR);
                                asyncTaskResultModel1.setMessage("Server returned null");
                                onPostExecute(asyncTaskResultModel1);
                            }
                        } else {
                            AsyncTaskResultModel asyncTaskResultModel1 = new AsyncTaskResultModel();
                            switch (response.code()) {
                                case 401:
                                    asyncTaskResultModel1.setResult(Constant.Result.ERROR);
                                    asyncTaskResultModel1.setMessage("Invalid credentials");
                                    break;
                                case 404:
                                    asyncTaskResultModel1.setResult(Constant.Result.ERROR);
                                    asyncTaskResultModel1.setMessage("Server not found");
                                    break;
                                case 500:
                                    asyncTaskResultModel1.setMessage("Server error");
                                    asyncTaskResultModel1.setResult(Constant.Result.SERVER_ERROR);
                                    break;
                                default:
                                    asyncTaskResultModel1.setResult(Constant.Result.ERROR);
                                    asyncTaskResultModel1.setMessage(response.code() + ": " + response.message());
                                    break;
                            }
                            onPostExecute(asyncTaskResultModel1);
                        }

                    }

                    @Override
                    public void onFailure(Call<SyncResult> call, Throwable t) {
                        AsyncTaskResultModel asyncTaskResultModel = new AsyncTaskResultModel();


                        if (t instanceof SocketTimeoutException) {
                            asyncTaskResultModel.setResult(Constant.Result.REQUEST_TIMEOUT);
                        } else {
                            asyncTaskResultModel.setResult(Constant.Result.SERVER_ERROR);
                            asyncTaskResultModel.setMessage(t.getMessage());
                        }
                        onPostExecute(asyncTaskResultModel);
                    }

                });
            }else{
                asyncTaskResultModel.setResult(Constant.Result.ERROR);
                asyncTaskResultModel.setMessage("Bad database data");
            }

        }else{
            asyncTaskResultModel.setResult(Constant.Result.NO_INTERNET);
        }
        return asyncTaskResultModel;
    }

    @Override
    protected void onPostExecute(Object result) {
        if(syncListener != null && result != null){
            syncListener.syncComplete((AsyncTaskResultModel)result);
        }
        super.onPostExecute(result);
    }
}
