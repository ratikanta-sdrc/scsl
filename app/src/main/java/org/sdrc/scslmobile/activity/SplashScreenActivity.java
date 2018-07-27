package org.sdrc.scslmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.DisplayMetrics;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.DataVisibility;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.MSTEngagementScore;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.TXNEngagementScore;
import org.sdrc.scslmobile.model.realm.TXNIndicator;
import org.sdrc.scslmobile.model.realm.TXNSNCUNICUData;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.Type;
import org.sdrc.scslmobile.model.realm.TypeDetail;
import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.PreferenceData;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {
    PreferenceData preferenceData;
    private Context context =this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        preferenceData = new PreferenceData(context);
        getVersionCode();

        String versionCodeFromLocal = preferenceData.getPreferenceData(getString(R.string.version_code));

        if(versionCodeFromLocal == null && preferenceData.getPreferenceData(getString(R.string.profile_entry_json))!=null ){
            deleteAllApplicationData();
        }else if((getVersionCode()!=null) && (versionCodeFromLocal!=null) && ( Integer.parseInt(getVersionCode())> Integer.parseInt(versionCodeFromLocal))){
            deleteAllApplicationData();
        }else{
             navigateToLogin();
        }
      // navigateToLogin();

    }
    public String getVersionCode(){
        PackageInfo pinfo = null;
        String versioncode = null;
        try {
            pinfo = SplashScreenActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            versioncode = ""+versionNumber;
            //String versionName = pinfo.versionName;
            //preferenceData.setPreferenceData(versionName,getString(R.string.version_name));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }
    public void deleteAllApplicationData(){
        new AlertDialog.Builder(SplashScreenActivity.this)
                .setTitle("Alert")
                .setMessage(getString(R.string.alert_msg_version_change))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                        delete();
                        setVersionCode();

                    }
                }).show();
    }

    public void delete(){
        //delete all shared Preference
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        //deleteAll realm instance
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        if(realm!=null){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Area> areas = realm.where(Area.class).findAll();
                    areas.deleteAllFromRealm();
                    RealmResults<DataVisibility> dataVisibility = realm.where(DataVisibility.class).findAll();
                    dataVisibility.deleteAllFromRealm();

                    RealmResults<Indicator> indicator = realm.where(Indicator.class).findAll();
                    indicator.deleteAllFromRealm();

                    RealmResults<IndicatorFacilityTimeperiodMapping> indicatorFacilityTimeperiodMapping = realm.where(IndicatorFacilityTimeperiodMapping.class).findAll();
                    indicatorFacilityTimeperiodMapping.deleteAllFromRealm();

                    RealmResults<MSTEngagementScore> mSTEngagementScore = realm.where(MSTEngagementScore.class).findAll();
                    mSTEngagementScore.deleteAllFromRealm();

                    RealmResults<SysConfig> sysConfig = realm.where(SysConfig.class).findAll();
                    sysConfig.deleteAllFromRealm();

                    RealmResults<TimePeriod> timePeriod = realm.where(TimePeriod.class).findAll();
                    timePeriod.deleteAllFromRealm();

                    RealmResults<TXNEngagementScore> tXNEngagementScore = realm.where(TXNEngagementScore.class).findAll();
                    tXNEngagementScore.deleteAllFromRealm();

                    RealmResults<TXNIndicator> tXNIndicator = realm.where(TXNIndicator.class).findAll();
                    tXNIndicator.deleteAllFromRealm();

                    RealmResults<TXNSNCUNICUData> tXNSNCUNICUData = realm.where(TXNSNCUNICUData.class).findAll();
                    tXNSNCUNICUData.deleteAllFromRealm();

                    RealmResults<Type> type = realm.where(Type.class).findAll();
                    type.deleteAllFromRealm();

                    RealmResults<TypeDetail> typeDetail = realm.where(TypeDetail.class).findAll();
                    typeDetail.deleteAllFromRealm();

                    RealmResults<User> user = realm.where(User.class).findAll();
                    user.deleteAllFromRealm();
                }
            });
        }

    }
    public void setVersionCode(){
        preferenceData.setPreferenceData(getVersionCode(),getString(R.string.version_code));
        navigateToLogin();
    }
    public void navigateToLogin(){
        preferenceData.setPreferenceData(getVersionCode(),getString(R.string.version_code));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        final double screenInches = Math.sqrt(x + y);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (screenInches > 4.3) {
                    // This method will be executed once the timer is over
                    // Start your app main activity


                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);

                    // close this activity
                    finish();
                } else {
                    new AlertDialog.Builder(SplashScreenActivity.this)
                            .setTitle("Error")
                            .setMessage(getString(R.string.this_application_can_not_be_run_on_this_device))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    dialog.dismiss();
                                    finishAffinity();
                                }
                            }).show();
                }
            }
        }, 2000);
    }
}
