package org.sdrc.scslmobile.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.adapter.SyncErrorMessageAdapter;
import org.sdrc.scslmobile.asynctask.SyncAsyncTask;
import org.sdrc.scslmobile.customclass.CustomEngagementListView;
import org.sdrc.scslmobile.customclass.SyncMessageData;
import org.sdrc.scslmobile.listener.SyncListener;
import org.sdrc.scslmobile.model.AsyncTaskResultModel;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.MSTEngagementScore;
import org.sdrc.scslmobile.model.realm.TXNEngagementScore;
import org.sdrc.scslmobile.model.realm.TimePeriod;

import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.service.SyncServiceImpl;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.SCSL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 * Edit by Ratikanta
 */

public class EngagementActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SyncListener {
    private static final String TAG = EngagementActivity.class.getName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    public static TextView mFacilityTxt, mMonthSelectionTxt;
    private HashMap<Integer, Area> facilityMap;
    private TreeMap<Integer, TimePeriod> timePeriodMap;
    private static int mSelectedFacility;
    private static int mSelectedTimePeriod;
    public static String ckeckStatus = "";
    public static Boolean tempSelectionStart = false;
    public static int engagementId = -1;
    private Realm realm;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private SyncServiceImpl syncService;
    int waveId = 0;
    List<String> monthList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engagement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_title_login));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Safe Care, Saving Lives</font>"));
        ImageView mFacilityIcon = (ImageView) findViewById(R.id.facility_icon);
        ImageView mMonthSelectionIcon = (ImageView) findViewById(R.id.month_selection_icon);
        mFacilityIcon.setOnClickListener(this);
        mMonthSelectionIcon.setOnClickListener(this);

        mFacilityTxt = (TextView) findViewById(R.id.facility_txt);
        mMonthSelectionTxt = (TextView) findViewById(R.id.month_selection_txt);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.engagement_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.engagement_nav_view);
        View header = navigationView.getHeaderView(0);
        TextView sync = (TextView) header.findViewById(R.id.engagement_sync);
        sync.setOnClickListener(this);
        TextView logout = (TextView) header.findViewById(R.id.engagement_logout);
        logout.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        mSelectedFacility = -1;
        mSelectedTimePeriod = -1;

        //Initializing Views
        mRecyclerView = (RecyclerView) findViewById(R.id.engagement_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        realm = SCSL.getInstance().getRealm(this);

        getEngagementList();
        progressDialog = new ProgressDialog(EngagementActivity.this);
        progressDialog.setTitle(getString(R.string.syncing));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        //alert dialog code
        alertDialog = new AlertDialog.Builder(EngagementActivity.this).create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
    }

    private void getEngagementList() {
        RealmResults<MSTEngagementScore> engagementScoresResults = realm.where(MSTEngagementScore.class).findAll();
        mRecyclerView.setAdapter(null);
        adapter = new CustomEngagementListView(this, engagementScoresResults);
        //Adding adapter to recyclerview
        mRecyclerView.setAdapter(adapter);
    }

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.engagement_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.engagement_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.engagement_drawer_layout);
        drawer.closeDrawer(Gravity.START);
        switch (view.getId()) {
            case R.id.engagement_sync:
                int sync_timeout_in_second = 60;

                //if somebody give string value in the strings.xml, we have to handle that
                try {
                    sync_timeout_in_second = Integer.parseInt(getString(R.string.sync_timeout_in_second));
                } catch (Exception e) {
                    Log.w(TAG, getString(R.string.invalid_timeout));
                }

                progressDialog.show();
                SyncAsyncTask syncAsyncTask = new SyncAsyncTask();
                syncAsyncTask.setSyncListener(EngagementActivity.this);
                syncService = new SyncServiceImpl();
                syncAsyncTask.execute(getString(R.string.server_url), isNetworkAvailable(), sync_timeout_in_second, syncService.getSyncmodel(realm));
                break;
            case R.id.engagement_logout:
                ckeckStatus = "";
                showLogoutAlert();
                break;
            case R.id.facility_icon:
                facilityMap = SCSL.getInstance().getAreaMap();
                int i = 0;
                User user = realm.where(User.class).findFirst();
                String[] facilityIds = user.getAreasIds().split(",");
                CharSequence[] facilities = new CharSequence[facilityIds.length];
                for (String id : facilityIds) {
                    facilities[i] = SCSL.getInstance().getAreaMap().get(Integer.parseInt(id)).getAreaName();
                    i++;
                }
                chooseSelection(facilities, getString(R.string.choose_facility), mFacilityTxt, "Facility");
                break;
            case R.id.month_selection_icon:
               /* timePeriodMap = SCSL.getInstance().getTimePeriodMap();

                final CharSequence[] month_items = new CharSequence[timePeriodMap.size()];
                int j = 0;
                for (HashMap.Entry<Integer, TimePeriod> entry : timePeriodMap.entrySet()) {
                    month_items[j] = entry.getValue().getTimePeriod();
                    j++;
                }
                chooseSelection(month_items, getString(R.string.choose_month), mMonthSelectionTxt, "Month");*/
                if (monthList != null) {
                    final CharSequence[] month_items = new CharSequence[monthList.size()];
                    int j = 0;
                    for (int k = 0; k < monthList.size(); k++) {
                        month_items[j] = monthList.get(k);
                        j++;
                    }
                    chooseSelection(month_items, getString(R.string.choose_month), mMonthSelectionTxt, "Month");

                } else {
                    Toast.makeText(getApplicationContext(), "You have not choose any facility", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    private void chooseSelection(final CharSequence[] items, final String title, final TextView txtView, final String warningMsg) {

        String text = (String) txtView.getText();
        int position = 0;
        int count = 0;
        if (text != null && !text.isEmpty()) {
            for (CharSequence str : items) {
                count++;
                if (text.equals(str)) {
                    position = count;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(items, position - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int selectedPosition = ((AlertDialog) dialog)
                                .getListView().getCheckedItemPosition();
                        String value = "";
                        if (selectedPosition != -1) {
                            if (items[selectedPosition] != null) {
                                value = items[selectedPosition].toString();
                                txtView.setText(value);
                            }

                        }

                        if (value.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "You have not choose any " + warningMsg, Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                        //set ids for both time period and facility on selection
                        if (title.equals(getString(R.string.choose_facility))) {
                            facilityMap = SCSL.getInstance().getAreaMap();

                            //clear the time period selection table and reset the value
                            mMonthSelectionTxt.setText("");
                            mSelectedTimePeriod = -1;
                            engagementId = -1;
                            timePeriodMap = SCSL.getInstance().getTimePeriodMap();
                            for (HashMap.Entry<Integer, Area> entry : facilityMap.entrySet()) {
                                if (entry.getValue().getAreaName().equals(value)) {
                                    mSelectedFacility = entry.getValue().getAreaId();
                                    waveId = entry.getValue().getWave();
                                    //}
                                }

                            }
                            if (waveId == 2) {
                                monthList = new ArrayList<>();
                                int j = 0;
                                for (HashMap.Entry<Integer, TimePeriod> entry1 : timePeriodMap.entrySet()) {
                                    if (entry1.getValue().getWave() == waveId) {
                                        monthList.add(entry1.getValue().getTimePeriod());
                                    }
                                    j++;
                                }
                            }else if(waveId == 1){
                                monthList = new ArrayList<>();
                                int j = 0;
                                for (HashMap.Entry<Integer, TimePeriod> entry1 : timePeriodMap.entrySet()) {
                                    if(entry1.getValue().getTimePeriodId() >= Constant.ENGAGEMENTSCORE_STARTING_TIME_PERIOD_ID ){
                                        monthList.add(entry1.getValue().getTimePeriod());
                                    }
                                    j++;
                                }
                            }

                            //disable all the list again because we clear the data of timeperiod
                            ckeckStatus = "false";
                            tempSelectionStart = false;
                            mRecyclerView.removeAllViews();
                            mRecyclerView.getAdapter().notifyDataSetChanged();

                        } else {
                            for (HashMap.Entry<Integer, TimePeriod> entry : timePeriodMap.entrySet()) {
                                if (entry.getValue().getTimePeriod().equals(value))
                                    mSelectedTimePeriod = entry.getValue().getTimePeriodId();
                            }
                            ckeckStatus = "false";
                            tempSelectionStart = false;
                            engagementId = -1;
                            mRecyclerView.removeAllViews();
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }

                        if (!mFacilityTxt.getText().toString().isEmpty() && !mMonthSelectionTxt.getText().toString().isEmpty()) {
                            TXNEngagementScore score = realm.where(TXNEngagementScore.class).equalTo("timePeriod", mSelectedTimePeriod)
                                    .equalTo("facility", mSelectedFacility).findFirst();
                            if (isPreviousMonthDataAvailable(mSelectedTimePeriod,waveId)) {
                                if (score == null) {
                                    ckeckStatus = "true";
                                    tempSelectionStart = false;
                                    mRecyclerView.removeAllViews();
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                } else {
                                    engagementId = score.getMstEngagementScoreId();
                                    ckeckStatus = "false";
                                    tempSelectionStart = false;
                                    mRecyclerView.removeAllViews();
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            } else {
                                ckeckStatus = "false";
                                tempSelectionStart = false;
                                mRecyclerView.removeAllViews();
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                                Toast.makeText(getApplication(), getString(R.string.please_fill_last_month_data), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.green));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean isPreviousMonthDataAvailable(int monthId,int waveId) {
        TimePeriod lastMonth = null;
        RealmResults<TimePeriod> result = null;
        if(waveId == 2){
            RealmQuery<TimePeriod> query = realm.where(TimePeriod.class);
            query.equalTo("wave", 2);
            result = query.findAllSorted("startDate", Sort.ASCENDING);

        }else if(waveId == 1){
            RealmQuery<TimePeriod> query = realm.where(TimePeriod.class);
            query.greaterThanOrEqualTo("timePeriodId",Constant.ENGAGEMENTSCORE_STARTING_TIME_PERIOD_ID);
            result = query.findAllSorted("startDate", Sort.ASCENDING);
        }
       // RealmResults<TimePeriod> result = realm.where(TimePeriod.class).findAllSorted("startDate", Sort.ASCENDING);
        int firstMonthId = result.first().getTimePeriodId();
        if (firstMonthId != monthId) {
            for (TimePeriod timePeriod : result) {
                if (timePeriod.getTimePeriodId() == monthId) {
                    break;
                }
                lastMonth = timePeriod;
            }
            TXNEngagementScore score = realm.where(TXNEngagementScore.class).equalTo("timePeriod", lastMonth.getTimePeriodId())
                    .equalTo("facility", mSelectedFacility).findFirst();
            return score != null;
        } else {
            return true;
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


    @Override
    public void syncComplete(AsyncTaskResultModel asyncTaskResultModel) {
        if (asyncTaskResultModel != null) {
            switch (asyncTaskResultModel.getResult()) {
                case Constant.Result.SUCCESS:
                    progressDialog.dismiss();
                    List<SyncMessageData> syncMessageDataList = syncService.getSyncMessage(
                            asyncTaskResultModel.getSyncResult(), asyncTaskResultModel.getSyncModel(), realm, EngagementActivity.this);
                    if (syncMessageDataList.size() == 0) {
                        alertDialog.setTitle(getString(R.string.sync_result));
                        alertDialog.setMessage(getString(R.string.sync_success));
                        alertDialog.show();
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    } else {
                        final Dialog dialog = new Dialog(EngagementActivity.this);
                        dialog.setContentView(R.layout.sync_error_listview);
                        dialog.setTitle(getString(R.string.sync_result));
                        RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.sync_recycle_view);
                        Button okButton = (Button) dialog.findViewById(R.id.ok_btn);
                        mRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        SyncErrorMessageAdapter adapter = new SyncErrorMessageAdapter(EngagementActivity.this, syncMessageDataList);
                        //Adding adapter to recyclerview
                        mRecyclerView.setAdapter(adapter);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                    break;
                case Constant.Result.ERROR:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(asyncTaskResultModel.getMessage());
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    break;
                case Constant.Result.SERVER_ERROR:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.server_error));
                    alertDialog.setMessage(asyncTaskResultModel.getMessage());
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    break;
                case Constant.Result.REQUEST_TIMEOUT:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(getString(R.string.request_timeout));
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    break;
                case Constant.Result.NO_DATA_TO_SYNC:
                    progressDialog.dismiss();
                    Toast.makeText(this, getString(R.string.no_data_to_sync), Toast.LENGTH_SHORT).show();
                    break;
                case Constant.Result.NO_INTERNET:
                    progressDialog.dismiss();
                    Toast.makeText(this, getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, getString(R.string.error) + " code 3: sync error", Toast.LENGTH_SHORT).show();
        }
    }

    public void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.confirm_layout))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            }
        });
        alert.show();
    }


    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.engagement_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }


    }

}
