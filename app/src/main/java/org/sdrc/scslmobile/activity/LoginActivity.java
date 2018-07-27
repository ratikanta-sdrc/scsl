package org.sdrc.scslmobile.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.asynctask.LoginAsyncTask;
import org.sdrc.scslmobile.listener.LoginListener;
import org.sdrc.scslmobile.model.LoginDataModel;

import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.TXNEngagementScore;
import org.sdrc.scslmobile.model.realm.TXNSNCUNICUData;
import org.sdrc.scslmobile.model.realm.TypeDetail;

import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.Type;

import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.model.webservice.MasterDataModel;
import org.sdrc.scslmobile.service.LoginService;
import org.sdrc.scslmobile.service.LoginServiceImpl;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.PreferenceData;
import org.sdrc.scslmobile.util.SCSL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.RealmQuery;
import io.realm.Sort;


/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 * Ratikanta Pradhan
 * Amit Kumar Sahoo
 */

public class LoginActivity extends AppCompatActivity implements LoginListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();

    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private Realm realm;
    private MasterDataModel masterDataModel;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );
        // Get a Realm instance for this thread
        Realm.init(LoginActivity.this);
        realm = Realm.getDefaultInstance();
        Button loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        //progress dialog code
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle(getString(R.string.logging_in));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        //alert dialog code
        alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                View views = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(views.getWindowToken(), 0);
                }
                username = ((EditText) findViewById(R.id.username_et)).getText().toString();
                password = ((EditText) findViewById(R.id.password_et)).getText().toString();
                if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(getApplication(), getString(R.string.please_give_username_and_password), Toast.LENGTH_SHORT).show();
                } else if (username.isEmpty()) {
                    Toast.makeText(getApplication(), getString(R.string.please_give_username), Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(getApplication(), getString(R.string.please_give_password), Toast.LENGTH_SHORT).show();
                } else {

                    //taking default value
                    int login_timeout_in_second = 60;
                    //if somebody give string value in the strings.xml, we have to handle that
                    try {
                        login_timeout_in_second = Integer.parseInt(getString(R.string.login_timeout_in_second));
                    } catch (Exception e) {
                        Log.w(TAG, getString(R.string.invalid_timeout));
                    }
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                    loginAsyncTask.setLoginListener(LoginActivity.this);
                    User user = realm.where(User.class).findFirst();
                    //If already there is same user, we do not have to show the spinner, fetching from realm is fast
                    //If we do not have the user or different user is trying to login
                    // we have to hit the internet, we need the spinner at that moment
                    if (user == null || (user.getUsername() != null && user.getPassword() != null
                            && (!(user.getUsername().equals(username)) || !(user.getPassword().equals(password))))) {
                        progressDialog.show();
                    }

                    loginAsyncTask.execute(username
                            , password,
                            getString(R.string.server_url), login_timeout_in_second, isNetworkAvailable());

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void loginComplete(LoginDataModel loginDataModel) {
        if (loginDataModel != null) {
            switch (loginDataModel.getResult()) {
                case Constant.Result.SUCCESS_AFTER_SERVER_HIT:
                    final LoginService service = new LoginServiceImpl();
                    masterDataModel = loginDataModel.getMasterDataModel();
                    //If the user would be first user we just have to insert the data into database
                    //If the user is second user or after one user, we have to confirm that the user wants to delete
                    //Previous user data
                    if (loginDataModel.isNewUser()) {
                        //Not first user
                        //Getting unsynced records to show the warning
                        int count = 0;
                        RealmResults<TXNSNCUNICUData> indicatorData = realm.where(TXNSNCUNICUData.class)
                                .equalTo(Constant.Table.TXNSNCUNICUData.IS_SYNCED, false)
                                .findAll();
                        count += indicatorData.size();
                        RealmResults<TXNEngagementScore> engagementScoreData = realm.where(TXNEngagementScore.class)
                                .equalTo(Constant.Table.TXNEngagementScore.IS_SYNCED, false)
                                .findAll();

                        count += engagementScoreData.size();

                        String message = "";
                        if (count > 0) {
                            message += count + " " + getString(R.string.unsynced_record_found);
                        }

                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(message + getResources().getString(R.string.clear_data))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //delteting the isDissabled value for profileEntry activity
                                        PreferenceData preferenceData = new PreferenceData(getApplicationContext());
                                        preferenceData.setPreferenceData(false, getString(R.string.profile_entry_is_dissable));

                                        //reset the previous entered obj for profile entry page
                                        //SCSL.getInstance().setProfileEntryModel(null);
                                        new PreferenceData(LoginActivity.this).deletePreferenceData(getString(R.string.profile_entry_json));

                                        //Deleting transaction data
                                        realm.beginTransaction();
                                        RealmResults<TXNSNCUNICUData> txnsncunicuDatas = realm.where(TXNSNCUNICUData.class).findAll();
                                        txnsncunicuDatas.deleteAllFromRealm();
                                        RealmResults<TXNEngagementScore> tXNEngagementScoreDatas = realm.where(TXNEngagementScore.class).findAll();
                                        tXNEngagementScoreDatas.deleteAllFromRealm();
                                        RealmResults<IndicatorFacilityTimeperiodMapping> indicatorData = realm.where(IndicatorFacilityTimeperiodMapping.class).findAll();
                                        indicatorData.deleteAllFromRealm();
                                        //new
                                        /*RealmResults<Area> areaData = realm.where(Area.class).findAll();
                                        areaData.deleteAllFromRealm();*/
                                        // realm.delete(TXNSNCUNICUData.class);
                                        //  realm.delete(TXNEngagementScore.class);
                                        realm.commitTransaction();
                                        Map<String, Boolean> result = service.insertData(masterDataModel, false, realm, username, password);

                                        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                                            if (entry.getKey() != null) {
                                                dialog.cancel();
                                                alertDialog.setTitle(getString(R.string.error));
                                                alertDialog.setMessage(entry.getKey());
                                                alertDialog.show();
                                            } else {
                                                dialog.cancel();
                                                //set all data to maps
                                                realm.close();
                                                setDataToMap();

                                                //If the user is DEO, he/she will see SNCU data entry module
                                                //If the user id not DEO he/she will see engagement score module
                                                progressDialog.setTitle("Initializing.....");
                                                progressDialog.show();
                                                if (result.get(null)) {
                                                    Intent intent = new Intent(getApplicationContext(), ProfileEntryActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(getApplicationContext(), EngagementActivity.class);
                                                    startActivity(intent);
                                                }
                                                finish();
                                            }
                                        }

                                    }
                                })
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        alert.show();
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.green));
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));

                    } else {
                        //First user
                        Map<String, Boolean> result = service.insertData(masterDataModel, true, realm, username, password);
                        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                            if (entry.getKey() != null) {
                                progressDialog.dismiss();
                                alertDialog.setTitle(getString(R.string.error));
                                alertDialog.setMessage(entry.getKey());
                                alertDialog.show();
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.green));
                            } else {
                                //set all data to maps
                                setDataToMap();
                                progressDialog.dismiss();

                                //If the user is DEO, he/she will see SNCU data entry module
                                //If the user id not DEO he/she will see engagement score module
                                realm.close();
                                if (result.get(null)) {
                                    Intent intent = new Intent(getApplicationContext(), ProfileEntryActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), EngagementActivity.class);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        }
                    }
                    break;
                case Constant.Result.SUCCESS:
                    setDataToMap();
                    progressDialog.dismiss();
                    realm.close();
                    if (loginDataModel.isDEO()) {
                        Intent intent = new Intent(getApplicationContext(), ProfileEntryActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), EngagementActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    break;
                case Constant.Result.NO_INTERNET:
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.internet_check, Toast.LENGTH_SHORT).show();
                    break;
                case Constant.Result.ERROR:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(loginDataModel.getMessage());
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.green));
                    break;
                case Constant.Result.SERVER_ERROR:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(loginDataModel.getMessage());
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.green));
                    break;
                case Constant.Result.INVALID_CREDENTIALS:
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * This method will check whethere there is a network connectivity or not
     *
     * @return true if there is network connectivity, false if there is not network connectivity
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setDataToMap() {
        setTypeMap();
        setTypeDetailsMap();
        setTimePeriodMap();
        setIndicatorMap();
        setAreaMap();
        setLastTimePeriodId();
        setLastIndicatorFacilityTimePeriodIds();
        //set Indicators id List for different validations
        SCSL.getInstance().setIndicatorIdsForValidation2();
        SCSL.getInstance().setIndicatorIdsForValidation3();
        SCSL.getInstance().setIndicatorIdsForValidation5();
        SCSL.getInstance().setIndicatorIdsForValidation6();
        SCSL.getInstance().setIndicatorIdsForValidation7();
        SCSL.getInstance().setIndicatorIdsNumberOfHighRiskDeliveries();
        SCSL.getInstance().setIndicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU();
        SCSL.getInstance().setIndicatorIdsNumberOfNeonatalDeathsDinominator();
        SCSL.getInstance().setIndicatorIdsNumberOfNeonatalDeathsNumerator();
        SCSL.getInstance().setNoOfNewBornLessThan2000gmsInNumerators();
        SCSL.getInstance().setNoOfNewBornLessThan2000gmsInDinominators();
        SCSL.getInstance().setNoOfNewBornBabiesAdmittedToTheSNCUDinominators();
        SCSL.getInstance().setNoOfNewBornBabiesAdmittedToTheSNCUNumerators();
    }

    /**
     * This following method is going to set last month indicator mappings for future usage
     * Ratikanta
     */
    private void setLastIndicatorFacilityTimePeriodIds() {
        RealmResults<IndicatorFacilityTimeperiodMapping> mappings = realm.where(IndicatorFacilityTimeperiodMapping.class)
                .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.TIME_PERIOD_ID, SCSL.getInstance().getLastTimePeriodId())
                .findAll();
        List<Integer> ids = new ArrayList<>();
        for (IndicatorFacilityTimeperiodMapping mapping : mappings) {
            ids.add(mapping.getIndFacilityTpId());
        }
        SCSL.getInstance().setLastIndicatorFacilityTimePeriodIds(ids);
    }

    /**
     * This method is going to set last month time period id globaly, we need later
     * Ratikanta
     */
    private void setLastTimePeriodId() {
        RealmResults<TimePeriod> timePeriods = realm.where(TimePeriod.class)
                .findAllSorted(Constant.Table.TimePeriod.START_DATE, Sort.DESCENDING);
        boolean firstGone = false;
        int lastTimePeriodId = 0;
        for (TimePeriod timePeriod : timePeriods) {
            if (firstGone) {
                lastTimePeriodId = timePeriod.getTimePeriodId();
                break;
            }
            firstGone = true;
        }
        SCSL.getInstance().setLastTimePeriodId(lastTimePeriodId);

    }


    private void setTypeMap() {
        HashMap<Integer, Type> types = new HashMap<>();
        // Obtain a Realm instance
        RealmQuery<Type> query = realm.where(Type.class);
        RealmResults<Type> results = query.findAll();
        for (Type type : results) {
            types.put(type.getTypeId(), type);
        }
        SCSL.getInstance().setTypeMap(types);
    }

    private void setTypeDetailsMap() {
        HashMap<Integer, TypeDetail> typeDetails = new HashMap<>();
        // Obtain a Realm instance
        RealmQuery<TypeDetail> query = realm.where(TypeDetail.class);
        RealmResults<TypeDetail> results = query.findAll();
        for (TypeDetail typeDetail : results) {
            typeDetails.put(typeDetail.getTypeDetailId(), typeDetail);
        }
        SCSL.getInstance().setTypeDetailsMap(typeDetails);
    }

    private void setTimePeriodMap() {
        TreeMap<Integer, TimePeriod> timePeriods = new TreeMap<>();
        // Obtain a Realm instance
        //  RealmQuery<TimePeriod> query = realm.where(TimePeriod.class);
        //RealmResults<TimePeriod> results = query.findAll();

        RealmResults<TimePeriod> results = realm.where(TimePeriod.class).findAllSorted("startDate", Sort.ASCENDING);
        for (TimePeriod timePeriod : results) {
            timePeriods.put(timePeriod.getTimePeriodId(), timePeriod);
        }
        SCSL.getInstance().setTimePeriodMap(timePeriods);
    }

    private void setIndicatorMap() {
        HashMap<Integer, Indicator> indicators = new HashMap<>();
        // Obtain a Realm instance
        RealmQuery<Indicator> query = realm.where(Indicator.class);
        RealmResults<Indicator> results = query.findAll();
        for (Indicator indicator : results) {
            indicators.put(indicator.getIndicatorId(), indicator);
        }
        SCSL.getInstance().setIndicatorMap(indicators);
    }

    private void setAreaMap() {
        HashMap<Integer, Area> areas = new HashMap<>();
        // Obtain a Realm instance
        RealmQuery<Area> query = realm.where(Area.class);
        RealmResults<Area> results = query.findAll();
        for (Area area : results) {
            areas.put(area.getAreaId(), area);
        }
        SCSL.getInstance().setAreaMap(areas);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = SCSL.getInstance().getRealm(LoginActivity.this);
    }
}