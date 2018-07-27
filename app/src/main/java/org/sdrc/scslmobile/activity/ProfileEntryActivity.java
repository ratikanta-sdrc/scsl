package org.sdrc.scslmobile.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.adapter.SyncErrorMessageAdapter;
import org.sdrc.scslmobile.asynctask.SyncAsyncTask;
import org.sdrc.scslmobile.customclass.SyncMessageData;
import org.sdrc.scslmobile.fragment.IntermediateFragment;
import org.sdrc.scslmobile.fragment.OutcomeFragment;
import org.sdrc.scslmobile.fragment.ProcessFragment;
import org.sdrc.scslmobile.listener.SyncListener;
import org.sdrc.scslmobile.model.AsyncTaskResultModel;
import org.sdrc.scslmobile.model.ProfileEntryModel;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.TXNSNCUNICUData;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.service.SyncServiceImpl;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.PreferenceData;
import org.sdrc.scslmobile.util.SCSL;
import org.sdrc.scslmobile.util.Validation;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static org.sdrc.scslmobile.activity.SNCUActivity.remarkMessage;

public class ProfileEntryActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, SyncListener {

    EditText noOfInbornAddmission, noOfOutbornAddmission, noOfCsection, noOfNormalDelivery, noOfLiveBirth;
    TextView noOfAddmission, percentOfInborn, percentOfOutborn, totalDelivery, percentOfCsection, percentOfNormalDeliveries, isLaborRoomAvailable;
    private TextView mTimePeriodTextView;
    //Text views for setting the indicator names
    TextView noOfInbornAddmissionTV, noOfOutbornAddmissionTV, noOfCsectionTV, noOfNormalDeliveryTV, noOfLiveBirthTV,
            noOfAddmissionTV, percentOfInbornTV, percentOfOutbornTV, totalDeliveryTV, percentOfCsectionTV, percentOfNormalDeliveriesTV;
    RelativeLayout labourRoomRelatedLayout;
    private Button resetBtn, saveBtn;
    DecimalFormat percentageValueFont;
    PreferenceData preferenceData;
    private Realm realm;
    private Context context = this;
    private TextView profileEntryStatusTv, showMore;
    private String validationMsg = "", status = "";
    private RelativeLayout parentLayout;
    private ProgressDialog progressDialog;
    private static final String TAG = ProfileEntryActivity.class.getName();
    private AlertDialog alertDialog;
    private SyncServiceImpl syncService;
    private String remarkMessage;
    private LinearLayout inborenLyout,percentageInbornLayout,outbornLayout,noOfAdmission;
    boolean hasLR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_entry_sidemenu);
        mTimePeriodTextView = (TextView) findViewById(R.id.profile_time_period);
        Realm.init(getApplicationContext());
        //    realm = SCSL.getInstance().getRealm(getActivity());
        realm = Realm.getDefaultInstance();

        progressDialog = new ProgressDialog(ProfileEntryActivity.this);
        progressDialog.setTitle(getString(R.string.syncing));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        //alert dialog code
        alertDialog = new AlertDialog.Builder(ProfileEntryActivity.this).create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_sncuativity);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                Log.v("opened", "opened");
                hidekeyboard();
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        User user = realm.where(User.class).findFirst();

        //We know that there will be only one area assigned to one deo so we are going to get that area id in string.
        //Still we have to take precaution
        int areaId;
        String facility_name = "";
        String district_name = "";
        String state_name = "";
        areaId = Integer.parseInt(user.getAreasIds());
        Area area = realm.where(Area.class).equalTo(Constant.Table.Area.ID, areaId).findFirst();
        facility_name = area.getAreaName();
        area = realm.where(Area.class).equalTo(Constant.Table.Area.ID, area.getParentAreaId()).findFirst();
        if (area.getLevel() == Constant.AreaLevel.BLOCK_AREA_LEVEL) {
            area = realm.where(Area.class).equalTo(Constant.Table.Area.ID, area.getParentAreaId()).findFirst();
        }
        district_name = area.getAreaName();
        area = realm.where(Area.class).equalTo(Constant.Table.Area.ID, area.getParentAreaId()).findFirst();
        state_name = area.getAreaName();
        NavigationView navigationView = (NavigationView) findViewById(R.id.profile_nav_view);
        View header = navigationView.getHeaderView(0);
        TextView sync = (TextView) header.findViewById(R.id.sync_profile);
        sync.setOnClickListener(this);
        TextView logout = (TextView) header.findViewById(R.id.logout_profile);
        logout.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        TextView deoName = (TextView) header.findViewById(R.id.deo_name_profile);
        deoName.setText(getString(R.string.deo_name) + " " + user.getName());
        TextView state = (TextView) header.findViewById(R.id.state_profile);
        state.setText(getString(R.string.state) + " " + state_name);
        TextView district = (TextView) header.findViewById(R.id.district_profile);
        district.setText(getString(R.string.district) + " " + district_name);
        TextView facility = (TextView) header.findViewById(R.id.facility_profile);
        facility.setText(getString(R.string.facility) + " " + facility_name);

        SCSL.getInstance().setInbornListOfIndicators();
        SCSL.getInstance().setTotalAdmissionListOfIndicators();
        SCSL.getInstance().setLiveBirthListOfIndicators();
        SCSL.getInstance().setNoOfDeliveriesListOfIndicators();
        SCSL.getInstance().setNoOfIntermediateListOfIndicators();
        SCSL.getInstance().setNoOfProcessIndicatorsCanotSelectedIfNoLr();

        inborenLyout = (LinearLayout) findViewById(R.id.profile_layout1);
        percentageInbornLayout = (LinearLayout) findViewById(R.id.profile_layout4);
        outbornLayout = (LinearLayout) findViewById(R.id.profile_layout2);
        noOfAdmission = (LinearLayout) findViewById(R.id.profile_layout3);


        preferenceData = new PreferenceData(getApplicationContext());
        noOfInbornAddmission = (EditText) findViewById(R.id.no_of_inborn_addmission);
        noOfOutbornAddmission = (EditText) findViewById(R.id.no_of_outborn_addmission);
        noOfAddmission = (TextView) findViewById(R.id.no_of_addmission);
        percentOfInborn = (TextView) findViewById(R.id.percent_of_inborn);
        percentOfOutborn = (TextView) findViewById(R.id.percent_of_outborn);
        profileEntryStatusTv = (TextView) findViewById(R.id.profileEntryStatus_tv);
        showMore = (TextView) findViewById(R.id.show_more_profile);
        //related to labour room
        labourRoomRelatedLayout = (RelativeLayout) findViewById(R.id.labour_room_related_layout);
        noOfCsection = (EditText) findViewById(R.id.no_of_csection);
        noOfNormalDelivery = (EditText) findViewById(R.id.no_of_normal_delivery);
        totalDelivery = (TextView) findViewById(R.id.no_of_total_delivery);
        percentOfCsection = (TextView) findViewById(R.id.percent_of_csection);
        percentOfNormalDeliveries = (TextView) findViewById(R.id.percent_of_normal_deleveries);
        noOfLiveBirth = (EditText) findViewById(R.id.no_of_livebirth);
        isLaborRoomAvailable = (TextView) findViewById(R.id.is_lr_edt);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        percentageValueFont = new DecimalFormat(getResources().getString(R.string.decimal_format));

        //all text views
        Map<Integer, Indicator> indicatorMap = SCSL.getInstance().getIndicatorMap();

        noOfInbornAddmissionTV = (TextView) findViewById(R.id.no_of_inborn_addmission_tv);
        noOfOutbornAddmissionTV = (TextView) findViewById(R.id.no_of_outborn_addmission_tv);
        noOfAddmissionTV = (TextView) findViewById(R.id.no_of_addmission_tv);
        percentOfInbornTV = (TextView) findViewById(R.id.percent_of_inborn_tv);
        percentOfOutbornTV = (TextView) findViewById(R.id.percent_of_outborn_tv);
        noOfCsectionTV = (TextView) findViewById(R.id.no_of_csection_tv);
        noOfNormalDeliveryTV = (TextView) findViewById(R.id.no_of_normal_delivery_tv);
        totalDeliveryTV = (TextView) findViewById(R.id.no_of_total_delivery_tv);
        percentOfCsectionTV = (TextView) findViewById(R.id.percent_of_csection_tv);
        percentOfNormalDeliveriesTV = (TextView) findViewById(R.id.percent_of_normal_deleveries_tv);
        noOfLiveBirthTV = (TextView) findViewById(R.id.no_of_livebirth_tv);

        noOfInbornAddmissionTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_INBORN_ADMISSION).getIndicatorName());
        noOfOutbornAddmissionTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_OUTBORN_ADMISSION).getIndicatorName());
        noOfAddmissionTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_TOTAL_ADMISSION).getIndicatorName());
        percentOfInbornTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.PERCENT_OF_INBORN_BABIES).getIndicatorName());
        percentOfOutbornTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.PERCENT_OF_OUTBORN_BABIES).getIndicatorName());
        noOfCsectionTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_CSECTION_DELIVERY).getIndicatorName());
        noOfNormalDeliveryTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_NORMAL_DELIVERY).getIndicatorName());
        totalDeliveryTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_TOTAL_DELIVERY).getIndicatorName());
        percentOfCsectionTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.PERCENT_OF_CSECTION_DELIVERIES).getIndicatorName());
        percentOfNormalDeliveriesTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.PERCENT_OF_NORMAL_DELIVERIES).getIndicatorName());
        noOfLiveBirthTV.setText(indicatorMap.get(Constant.ProfilePageIndicator.NO_OF_LIVE_BIRTH).getIndicatorName());

        resetBtn = (Button) findViewById(R.id.resetBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        resetBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        showMore.setOnClickListener(this);
        //get current user area has lr or not
        int currentUserFacilityId = Integer.parseInt(realm.where(User.class).findFirst().getAreasIds());

         hasLR = SCSL.getInstance().getAreaMap().get(currentUserFacilityId).isHasLR();
        if (hasLR) {
            labourRoomRelatedLayout.setVisibility(View.VISIBLE);
            isLaborRoomAvailable.setText(getString(R.string.yes));
            isLaborRoomAvailable.setEnabled(false);
        } else {


            labourRoomRelatedLayout.setVisibility(View.GONE);
              //New code added if no labor room then the no of  inborn admission and percentage of inborn admission will not appear.
            inborenLyout.setVisibility(View.GONE);
            percentageInbornLayout.setVisibility(View.GONE);
            outbornLayout.setBackgroundResource(R.color.white);
            TextView outBornSlNoTv = (TextView) findViewById(R.id.outBornSlNoTv);
            outBornSlNoTv.setText("2.");
            noOfAdmission.setBackgroundResource(R.color.grey);
            TextView noOfAdmissionSlNoTv = (TextView) findViewById(R.id.noOfAdmissionSlNoTv);
            noOfAdmissionSlNoTv.setText("3.");
            TextView perOfOutBornBabiesTv = (TextView) findViewById(R.id.perOfOutBornBabiesTv);
            perOfOutBornBabiesTv.setText("4.");

            ///code end
            isLaborRoomAvailable.setText(R.string.no);
            isLaborRoomAvailable.setEnabled(false);
        }

        noOfInbornAddmission.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populateTotalAddmission();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noOfInbornAddmission.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                status = "";
            }
        });

        noOfOutbornAddmission.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populateTotalAddmission();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noOfOutbornAddmission.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                status = "";
            }
        });

        noOfCsection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populateTotalDeliveries();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noOfCsection.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                status = "";
            }
        });

        noOfNormalDelivery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populateTotalDeliveries();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noOfNormalDelivery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                status = "";
               /* validationMsg = new Validation().checkForLiveBirthAndDelivery(noOfLiveBirth.getText().toString(), noOfNormalDelivery.getText().toString());

                if (validationMsg != null) {
                    showWarningDialog(validationMsg);
                }*/


            }
        });

        noOfLiveBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                validationMsg = new Validation().checkForLiveBirthAndDelivery(noOfLiveBirth.getText().toString(), totalDelivery.getText().toString());
                if (validationMsg != null) {
                    showWarningDialog(validationMsg);
                }
                status = "";
            }
        });
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View views = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(views.getWindowToken(), 0);
                }
                return false;
            }
        });

    }

    private void showWarningDialog(String warningMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setCancelable(false);
        builder.setTitle(getString(R.string.warning_dialog_title))
                .setMessage(warningMessage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        dialog.dismiss();
                        if (status.equalsIgnoreCase("save")) {
                            saveAndPopulate();
                        }
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.green));

    }

    private void enableDisableResetButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        String date = sdf.format(new Date());
        TimePeriod lastMonth = null;
        RealmResults<TimePeriod> result = realm.where(TimePeriod.class).findAllSorted("startDate", Sort.DESCENDING);
        int count = 0;
        for (TimePeriod timePeriod : result) {
            count++;
            if (count == 2) {
                lastMonth = timePeriod;
                break;
            }
        }
        RealmResults<IndicatorFacilityTimeperiodMapping> results = realm.where(IndicatorFacilityTimeperiodMapping.class)
                .equalTo("timePeriod", lastMonth.getTimePeriodId())
                .findAll();

        RealmQuery<TXNSNCUNICUData> query = realm.where(TXNSNCUNICUData.class);
        for (IndicatorFacilityTimeperiodMapping indicatorFacilityTimeperiodMapping : results) {
            if (indicatorFacilityTimeperiodMapping != null) {
                query = query.or().equalTo("indicatorFacilityTimeperiodMapping", indicatorFacilityTimeperiodMapping.getIndFacilityTpId());
            }
        }
        try {
            SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
            RealmResults<TXNSNCUNICUData> data = query.findAll();
            PreferenceData mPreferenceData = new PreferenceData(context);
            if (data != null && data.size() > 0) {
                for (TXNSNCUNICUData txnsncunicuData : data) {
                    int indicatorFacilityTimeperiodMapping_id = txnsncunicuData.getIndicatorFacilityTimeperiodMapping();
                    IndicatorFacilityTimeperiodMapping indicatorFacilityTimeperiodMapping = realm.where(IndicatorFacilityTimeperiodMapping.class)
                            .equalTo("indFacilityTpId", indicatorFacilityTimeperiodMapping_id).findFirst();
                    if (!txnsncunicuData.isSynced() || txnsncunicuData.isHasError()) {
                        if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                            resetBtn.setEnabled(true);
                            resetBtn.setBackgroundResource(R.color.darkBlue);
                            remarkMessage = "";
                        } else {
                            resetBtn.setEnabled(false);
                            resetBtn.setBackgroundResource(R.color.lighter_grey);

                            if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                profileEntryStatusTv.setVisibility(View.VISIBLE);
                                profileEntryStatusTv.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue));
                                remarkMessage = "";
                            }
                        }
                    } else {
                        if (txnsncunicuData.isRejectedBySup()) {
                            if (Integer.parseInt(date) <= sysConfig.getSubDeadLine()) {
                                resetBtn.setEnabled(true);
                                resetBtn.setBackgroundResource(R.color.darkBlue);
                                saveBtn.setEnabled(true);
                                saveBtn.setBackgroundResource(R.color.darkBlue);
                                if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                    profileEntryStatusTv.setVisibility(View.VISIBLE);
                                    profileEntryStatusTv.setText("Submission rejected by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                    profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.red));
                                    remarkMessage = txnsncunicuData.getRemarkBySup();

                                }
                            } else {
                                resetBtn.setEnabled(true);
                                resetBtn.setBackgroundResource(R.color.darkBlue);
                                saveBtn.setEnabled(true);
                                saveBtn.setBackgroundResource(R.color.darkBlue);
                                if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                    profileEntryStatusTv.setVisibility(View.VISIBLE);
                                    profileEntryStatusTv.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                    profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue));
                                    remarkMessage = "";
                                }
                            }

                        } else if (txnsncunicuData.isApprovedBySup()) {
                            resetBtn.setEnabled(false);
                            resetBtn.setBackgroundResource(R.color.lighter_grey);
                            if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                profileEntryStatusTv.setVisibility(View.VISIBLE);
                                //SNCUActivity.mDueDateTextview.setText("Approved by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkBySup());
                                profileEntryStatusTv.setText("Approved by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.green));
                                remarkMessage = txnsncunicuData.getRemarkBySup();
                            }
                        } else if (txnsncunicuData.isRejectedByMNE()) {
                            if (Integer.parseInt(date) <= sysConfig.getMneDeadLine()) {
                                resetBtn.setEnabled(true);
                                resetBtn.setBackgroundResource(R.color.darkBlue);
                                saveBtn.setEnabled(true);
                                saveBtn.setBackgroundResource(R.color.darkBlue);
                                if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                    profileEntryStatusTv.setVisibility(View.VISIBLE);
                                    //   SNCUActivity.mDueDateTextview.setText("Submission rejected by MnE" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkByMNE());
                                    profileEntryStatusTv.setText("Submission rejected by MnE" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                    //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                    profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.red));
                                    remarkMessage = txnsncunicuData.getRemarkByMNE();

                                }

                            } else {
                                resetBtn.setEnabled(false);
                                resetBtn.setBackgroundResource(R.color.lighter_grey);
                                if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                    profileEntryStatusTv.setVisibility(View.VISIBLE);
                                    profileEntryStatusTv.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                    profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue));
                                    remarkMessage = "";
                                }
                            }
                        } else if (txnsncunicuData.isApprovedByMNE()) {
                            resetBtn.setEnabled(false);
                            resetBtn.setBackgroundResource(R.color.lighter_grey);
                            if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                profileEntryStatusTv.setVisibility(View.VISIBLE);
                                // SNCUActivity.mDueDateTextview.setText("Approved by M&E" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkByMNE());
                                profileEntryStatusTv.setText("Approved by M&E" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.green));
                                remarkMessage = txnsncunicuData.getRemarkByMNE();
                            }
                        } else if (txnsncunicuData.isAutoApproved()) {
                            resetBtn.setEnabled(false);
                            resetBtn.setBackgroundResource(R.color.lighter_grey);
                            if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                profileEntryStatusTv.setVisibility(View.VISIBLE);
                                profileEntryStatusTv.setText("Auto approved by system on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.green));
                                remarkMessage = "";
                            }
                        } else {
                            resetBtn.setEnabled(false);
                            resetBtn.setBackgroundResource(R.color.lighter_grey);
                            if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                profileEntryStatusTv.setVisibility(View.VISIBLE);
                                profileEntryStatusTv.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                profileEntryStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue));
                                remarkMessage = "";
                            }

                        }
                    }


                }
            } else {
                if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                    profileEntryStatusTv.setVisibility(View.GONE);
                    resetBtn.setEnabled(true);
                    resetBtn.setBackgroundResource(R.color.darkBlue);
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundResource(R.color.darkBlue);
                    //First check if its a new month or not if it is then resent the data , otherwise not
                    String monthNumber = (String) DateFormat.format("MM", new Date()); // 06
                    String year = (String) DateFormat.format("yyyy", new Date()); // 2013
                    String key = year + monthNumber; //201708

                    if (!preferenceData.getPreferenceBooleanData(key)) {
                        preferenceData.setPreferenceData(true, key);
                        resetAllField();
                    }

                } else {
                    profileEntryStatusTv.setVisibility(View.VISIBLE);
                    profileEntryStatusTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                    dissableAllField();
                    resetBtn.setEnabled(false);
                    resetBtn.setBackgroundResource(R.color.lighter_grey);
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundResource(R.color.lighter_grey);
                }
                remarkMessage = "";
                //saveBtn.setText(getString(R.string.button_save_and_next));
            }
        } catch (Exception e) {
            Log.i("", "" + e);
        }
    }

    public void populateTotalAddmission() {
        if (!noOfInbornAddmission.getText().toString().isEmpty()
                && !noOfOutbornAddmission.getText().toString().isEmpty()) {

            int noOfInbornAddmissionValue = Integer.parseInt(noOfInbornAddmission.getText().toString());
            int noOfOutbornAddmissionValue = Integer.parseInt(noOfOutbornAddmission.getText().toString());
            noOfAddmission.setText(String.valueOf(noOfInbornAddmissionValue + noOfOutbornAddmissionValue));
            if (noOfInbornAddmissionValue == 0 && noOfOutbornAddmissionValue != 0) {
                float percentageOfOutbornValues = ((float) noOfOutbornAddmissionValue / ((float) noOfInbornAddmissionValue + (float) noOfOutbornAddmissionValue)) * 100;
                percentOfInborn.setText("0.0" + getResources().getString(R.string.percentage_symbol));
                percentOfOutborn.setText(percentageValueFont.format(percentageOfOutbornValues) + getResources().getString(R.string.percentage_symbol));
            } else if (noOfInbornAddmissionValue != 0 && noOfOutbornAddmissionValue == 0){
                float percentageOfInbornValues = ((float) noOfInbornAddmissionValue / ((float) noOfInbornAddmissionValue + (float) noOfOutbornAddmissionValue)) * 100;
                percentOfInborn.setText(percentageValueFont.format(percentageOfInbornValues) + getResources().getString(R.string.percentage_symbol));
                percentOfOutborn.setText("0.0" + getResources().getString(R.string.percentage_symbol));
            } else if (noOfInbornAddmissionValue != 0 && noOfOutbornAddmissionValue != 0){
                float percentageOfInbornValues = ((float) noOfInbornAddmissionValue / ((float) noOfInbornAddmissionValue + (float) noOfOutbornAddmissionValue)) * 100;
                float percentageOfOutbornValues = ((float) noOfOutbornAddmissionValue / ((float) noOfInbornAddmissionValue + (float) noOfOutbornAddmissionValue)) * 100;
                percentOfInborn.setText(percentageValueFont.format(percentageOfInbornValues) + getResources().getString(R.string.percentage_symbol));
                percentOfOutborn.setText(percentageValueFont.format(percentageOfOutbornValues) + getResources().getString(R.string.percentage_symbol));
            } else {
                percentOfInborn.setText("NaN");
                percentOfOutborn.setText("NaN");
            }
        } else if (!noOfInbornAddmission.getText().toString().isEmpty()) {
            int noOfInbornAddmissionValue = Integer.parseInt(noOfInbornAddmission.getText().toString());
            noOfAddmission.setText(String.valueOf(noOfInbornAddmissionValue));
            if (noOfInbornAddmissionValue != 0) {
                float percentageOfInbornValues = ((float) noOfInbornAddmissionValue / (float) noOfInbornAddmissionValue) * 100;
                percentOfInborn.setText(percentageValueFont.format(percentageOfInbornValues) + getResources().getString(R.string.percentage_symbol));
            } else {
                percentOfInborn.setText("NaN");
            }
            percentOfOutborn.setText("");
        } else if (!noOfOutbornAddmission.getText().toString().isEmpty()) {
            int noOfOutbornAddmissionValue = Integer.parseInt(noOfOutbornAddmission.getText().toString());
            noOfAddmission.setText(String.valueOf(noOfOutbornAddmissionValue));
            if (noOfOutbornAddmissionValue != 0) {
                float percentageOfOutbornValues = ((float) noOfOutbornAddmissionValue / (float) noOfOutbornAddmissionValue) * 100;
                percentOfOutborn.setText(percentageValueFont.format(percentageOfOutbornValues) + getResources().getString(R.string.percentage_symbol));
            } else {
                percentOfOutborn.setText("NaN");
            }
            percentOfInborn.setText("");
        } else {
            noOfAddmission.setText("");
            percentOfInborn.setText("");
            percentOfOutborn.setText("");
        }
    }

    public void populateTotalDeliveries() {
        if (!noOfCsection.getText().toString().isEmpty()
                && !noOfNormalDelivery.getText().toString().isEmpty()) {
            int noOfCSectionValue = Integer.parseInt(noOfCsection.getText().toString());
            int noOfNormalDeliveryValue = Integer.parseInt(noOfNormalDelivery.getText().toString());
            totalDelivery.setText(String.valueOf(noOfCSectionValue + noOfNormalDeliveryValue));
            if (noOfCSectionValue == 0 && noOfNormalDeliveryValue != 0){
                float percentOfNormalDeliveriesValue = ((float) noOfNormalDeliveryValue / ((float) noOfCSectionValue + (float) noOfNormalDeliveryValue)) * 100;
                percentOfNormalDeliveries.setText(percentageValueFont.format(percentOfNormalDeliveriesValue) + getResources().getString(R.string.percentage_symbol));
                percentOfCsection.setText("NaN");
            } else if (noOfCSectionValue != 0 && noOfNormalDeliveryValue == 0){
                float percentageOfCSectionValue = ((float) noOfCSectionValue / ((float) noOfCSectionValue + (float) noOfNormalDeliveryValue)) * 100;
                percentOfCsection.setText(percentageValueFont.format(percentageOfCSectionValue) + getResources().getString(R.string.percentage_symbol));
                percentOfNormalDeliveries.setText("NaN");
            } else if (noOfCSectionValue != 0 && noOfNormalDeliveryValue != 0) {
                float percentageOfCSectionValue = ((float) noOfCSectionValue / ((float) noOfCSectionValue + (float) noOfNormalDeliveryValue)) * 100;
                float percentOfNormalDeliveriesValue = ((float) noOfNormalDeliveryValue / ((float) noOfCSectionValue + (float) noOfNormalDeliveryValue)) * 100;
                percentOfCsection.setText(percentageValueFont.format(percentageOfCSectionValue) + getResources().getString(R.string.percentage_symbol));
                percentOfNormalDeliveries.setText(percentageValueFont.format(percentOfNormalDeliveriesValue) + getResources().getString(R.string.percentage_symbol));
            } else {
                percentOfCsection.setText("NaN");
                percentOfNormalDeliveries.setText("NaN");
            }

        } else if (!noOfCsection.getText().toString().isEmpty()) {
            int noOfCSectionValue = Integer.parseInt(noOfCsection.getText().toString());
            totalDelivery.setText(String.valueOf(noOfCSectionValue));
            if (noOfCSectionValue != 0) {
                float percentageOfCSectionValue = ((float) noOfCSectionValue / ((float) noOfCSectionValue)) * 100;
                percentOfCsection.setText(percentageValueFont.format(percentageOfCSectionValue) + getResources().getString(R.string.percentage_symbol));
            } else {
                percentOfCsection.setText("NaN");
            }
            percentOfNormalDeliveries.setText("");
        } else if (!noOfNormalDelivery.getText().toString().isEmpty()) {
            int noOfNormalDeliveryValue = Integer.parseInt(noOfNormalDelivery.getText().toString());
            totalDelivery.setText(String.valueOf(noOfNormalDeliveryValue));
            if (noOfNormalDeliveryValue != 0) {
                float percentOfNormalDeliveriesValue = ((float) noOfNormalDeliveryValue / (float) noOfNormalDeliveryValue) * 100;
                percentOfNormalDeliveries.setText(percentageValueFont.format(percentOfNormalDeliveriesValue) + getResources().getString(R.string.percentage_symbol));
            } else {
                percentOfNormalDeliveries.setText("NaN");
            }
            percentOfCsection.setText("");
        } else {
            totalDelivery.setText("");
            percentOfCsection.setText("");
            percentOfNormalDeliveries.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (view.getId()) {
            case R.id.resetBtn:
                resetAllField();
                break;
            case R.id.saveBtn:
                validateAllField();
                break;
            case R.id.logout_profile:
                showLogoutAlert();
                break;
            case R.id.show_more_profile:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileEntryActivity.this);
                alertDialogBuilder
                        .setTitle("Remark");
                alertDialogBuilder.setMessage(remarkMessage);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialog.dismiss();
                            }
                        });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    }
                });
                alertDialog.show();
                break;
            case R.id.sync_profile:
                //show alert dialog before syncing
                //first check that for feild is editable or not
                //if yes(Editable) then only show the message other wise not
                /*if (OutcomeFragment.mOutcomeTableLayout != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1) != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEntryActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Warning")
                            .setMessage("All unsaved data will be cleared. Do you want to Sync ?")
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })
                            .setNegativeButton("Proceed To Sync", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    proceedToSync();

                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ProfileEntryActivity.this, R.color.red));
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ProfileEntryActivity.this, R.color.green));
                        }
                    });
                    alert.show();
                } else {
                    proceedToSync();
                }*/
                proceedToSync();
                break;
        }
        /*if (view.getId() == R.id.resetBtn) {
            resetAllField();
        } else if (view.getId() == R.id.saveBtn) {
            validateAllField();
        }*/
    }

    private void validateAllField() {


        if (hasLR &&noOfInbornAddmission.getText().toString().length() == 0) {
            Toast.makeText(context, getString(R.string.please_enter_inborn_admission), Toast.LENGTH_LONG).show();
        } else if (noOfOutbornAddmission.getText().toString().length() == 0) {
            Toast.makeText(context, getString(R.string.please_enter_outborn_admission), Toast.LENGTH_LONG).show();
        } else if (labourRoomRelatedLayout.getVisibility() == View.VISIBLE) {
            if (noOfCsection.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.please_enter_c_section_deleveries), Toast.LENGTH_LONG).show();
            } else if (noOfNormalDelivery.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.please_enter_normal_deleveries), Toast.LENGTH_LONG).show();
            } else if (noOfLiveBirth.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.please_enter_live_birth), Toast.LENGTH_LONG).show();
            } else {
                status = "save";
                validationMsg = new Validation().checkForLiveBirthAndDelivery(noOfLiveBirth.getText().toString(), totalDelivery.getText().toString());
                if (validationMsg != null) {
                    showWarningDialog(validationMsg);
                } else {
                    saveAndPopulate();
                }


            }
        } else {
            saveAndPopulate();
        }

    }

    private void saveAndPopulate() {
        if (preferenceData.getPreferenceBooleanData(getString(R.string.profile_entry_is_dissable))) {
            Intent intent = new Intent(ProfileEntryActivity.this, SNCUActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEntryActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Warning")
                    .setMessage(getString(R.string.save_profile_entry_msg))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ProfileEntryModel entryModel = saveAllDataForNextPage();
                            Gson gson = new Gson();
                            String json = gson.toJson(entryModel);
                            new PreferenceData(getApplicationContext()).setPreferenceData(json, getString(R.string.profile_entry_json));

                            //SCSL.getInstance().setProfileEntryModel(entryModel);
                            dissableAllField();
                            Intent intent = new Intent(ProfileEntryActivity.this, SNCUActivity.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            //saveSNCUData();
                            dialog.dismiss();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ProfileEntryActivity.this, R.color.green));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ProfileEntryActivity.this, R.color.red));
                }
            });
            alert.show();
        }

    }

    private void resetAllField() {
        PreferenceData preferenceData = new PreferenceData(getApplicationContext());
        //save all status and remark and resetOrNot in shared preference
        preferenceData.setPreferenceData(profileEntryStatusTv.getText().toString(), getString(R.string.status_before_reset));
        preferenceData.setPreferenceData(remarkMessage, getString(R.string.remark_before_reset));
        preferenceData.setPreferenceData(true, getString(R.string.is_reset));
        preferenceData.setPreferenceData(profileEntryStatusTv.getCurrentTextColor(), getString(R.string.status_color));
        if (showMore.getVisibility() == View.VISIBLE) {
            preferenceData.setPreferenceData(true, getString(R.string.is_ramark));
        } else {
            preferenceData.setPreferenceData(false, getString(R.string.is_ramark));
        }
        if (profileEntryStatusTv.getVisibility() == View.VISIBLE) {
            preferenceData.setPreferenceData(true, "isStatus");
        } else {
            preferenceData.setPreferenceData(false, "isStatus");
        }

        //delteting the isDissabled value for profileEntry activity
        status = "";
        preferenceData.setPreferenceData(false, getString(R.string.profile_entry_is_dissable));

        //reset the previous entered obj for profile entry page
        //SCSL.getInstance().setProfileEntryModel(null);
        new PreferenceData(ProfileEntryActivity.this).deletePreferenceData(getString(R.string.profile_entry_json));

        //also delte all transaction data
        realm.beginTransaction();
        realm.where(TXNSNCUNICUData.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        //clear all fields
        noOfInbornAddmission.setText("");
        noOfOutbornAddmission.setText("");
        noOfAddmission.setText("");
        percentOfInborn.setText("");
        percentOfOutborn.setText("");
        noOfCsection.setText("");
        noOfNormalDelivery.setText("");
        totalDelivery.setText("");
        percentOfCsection.setText("");
        percentOfNormalDeliveries.setText("");
        noOfLiveBirth.setText("");
        //enable all fields again
        noOfInbornAddmission.setEnabled(true);
        noOfOutbornAddmission.setEnabled(true);
        noOfCsection.setEnabled(true);
        noOfNormalDelivery.setEnabled(true);
        noOfLiveBirth.setEnabled(true);

        //change button name next to save and next
        //saveBtn.setText(getString(R.string.button_save_and_next));

        //reset all validation maps because if we change in profile page by comming back from snsu activity
        //It can possible that it stored the previous value and validating and showing errors that "value didnt match with previous input"
        SCSL.getInstance().setIndicatorValuesMapForValidation2(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForValidation3(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForValidation5(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForValidation7(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(new HashMap<Integer, Float>());
    }

    public ProfileEntryModel saveAllDataForNextPage() {
        ProfileEntryModel entryModel = new ProfileEntryModel();
        Integer noOfInbornAddmissionValue = noOfInbornAddmission.getText().toString().isEmpty() ? null : Integer.valueOf(noOfInbornAddmission.getText().toString());
        Integer noOfOutbornAddmissionValue = noOfOutbornAddmission.getText().toString().isEmpty() ? null : Integer.valueOf(noOfOutbornAddmission.getText().toString());
        Integer noOfAddmissionValue = noOfAddmission.getText().toString().isEmpty() ? null : Integer.valueOf(noOfAddmission.getText().toString());
        Float percentOfInbornValue = (percentOfInborn.getText().toString().isEmpty() || percentOfInborn.getText().toString().equals("NaN")) ? null : Float.valueOf(percentOfInborn.getText().toString().substring(0, percentOfInborn.getText().toString().length() - 1));
        Float percentOfOutbornValue = (percentOfOutborn.getText().toString().isEmpty() || percentOfOutborn.getText().toString().equals("NaN")) ? null : Float.valueOf(percentOfOutborn.getText().toString().substring(0, percentOfOutborn.getText().toString().length() - 1));
        Integer noOfCsectionValue = noOfCsection.getText().toString().isEmpty() ? null : Integer.valueOf(noOfCsection.getText().toString());
        Integer noOfNormalDeliveryValue = noOfNormalDelivery.getText().toString().isEmpty() ? null : Integer.valueOf(noOfNormalDelivery.getText().toString());
        Integer totalDeliveryValue = totalDelivery.getText().toString().isEmpty() ? null : Integer.valueOf(totalDelivery.getText().toString());
        Float percentOfCsectionValue = (percentOfCsection.getText().toString().isEmpty() || percentOfCsection.getText().toString().equals("NaN")) ? null : Float.valueOf(percentOfCsection.getText().toString().substring(0, percentOfCsection.getText().toString().length() - 1));
        Float percentOfNormalDeliveriesValue = (percentOfNormalDeliveries.getText().toString().isEmpty() || percentOfNormalDeliveries.getText().toString().equals("NaN")) ? null : Float.valueOf(percentOfNormalDeliveries.getText().toString().substring(0, percentOfNormalDeliveries.getText().toString().length() - 1));
        Integer noOfLiveBirthValue = noOfLiveBirth.getText().toString().isEmpty() ? null : Integer.valueOf(noOfLiveBirth.getText().toString());
        entryModel.setNoOfInbornAdmission(noOfInbornAddmissionValue);
        entryModel.setNoOfOutbornAdmission(noOfOutbornAddmissionValue);
        entryModel.setNoOfTotalAdmission(noOfAddmissionValue);
        entryModel.setPercentOfInbornAdmission(percentOfInbornValue);
        entryModel.setPercentOfOutbornAdmission(percentOfOutbornValue);
        entryModel.setNoOfCSection(noOfCsectionValue);
        entryModel.setNoOfNormalDeliveries(noOfNormalDeliveryValue);
        entryModel.setNoOfTotalDeliveries(totalDeliveryValue);
        entryModel.setPercentOfCSection(percentOfCsectionValue);
        entryModel.setPercentOfNormalDelivery(percentOfNormalDeliveriesValue);
        entryModel.setNoOfLiveBirth(noOfLiveBirthValue);
        return entryModel;
    }

    public void dissableAllField() {
        preferenceData.setPreferenceData(true, getString(R.string.profile_entry_is_dissable));
        noOfInbornAddmission.setEnabled(false);
        noOfOutbornAddmission.setEnabled(false);
        noOfAddmission.setEnabled(false);
        noOfCsection.setEnabled(false);
        noOfNormalDelivery.setEnabled(false);
        totalDelivery.setEnabled(false);
        noOfLiveBirth.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if(preferenceData.getPreferenceBooleanData("is_new_month")){
            resetAllField();
        }*/
        TimePeriod lastMonth = null;
        RealmResults<TimePeriod> result = realm.where(TimePeriod.class).findAllSorted("startDate", Sort.DESCENDING);
        int count = 0;
        for (TimePeriod timePeriod : result) {
            count++;
            if (count == 2) {
                lastMonth = timePeriod;
                break;
            }
        }
        mTimePeriodTextView.setText(lastMonth.getTimePeriod());
        if (preferenceData.getPreferenceBooleanData(getString(R.string.profile_entry_is_dissable))) {
            dissableAllField();
            Gson gson = new Gson();
            String json = new PreferenceData(ProfileEntryActivity.this).getPreferenceData(getString(R.string.profile_entry_json));
            ProfileEntryModel model = gson.fromJson(json, ProfileEntryModel.class);

            if (model != null) {
                if(hasLR){
                    noOfInbornAddmission.setText(model.getNoOfInbornAdmission().toString());
                    if(model.getPercentOfInbornAdmission()!=null){
                        percentOfInborn.setText(model.getPercentOfInbornAdmission().toString() + getResources().getString(R.string.percentage_symbol));
                    }
                }

                noOfOutbornAddmission.setText(model.getNoOfOutbornAdmission().toString());
                noOfAddmission.setText(model.getNoOfTotalAdmission().toString());

                if(model.getPercentOfOutbornAdmission()!=null){
                    percentOfOutborn.setText(model.getPercentOfOutbornAdmission().toString() + getResources().getString(R.string.percentage_symbol));
                }



                if (labourRoomRelatedLayout.getVisibility() == View.VISIBLE) {
                    noOfCsection.setText(model.getNoOfCSection().toString());
                    noOfNormalDelivery.setText(model.getNoOfNormalDeliveries().toString());
                    totalDelivery.setText(model.getNoOfTotalDeliveries().toString());
                    if(model.getPercentOfCSection()!=null){
                        percentOfCsection.setText(model.getPercentOfCSection().toString() + getResources().getString(R.string.percentage_symbol));
                    }
                    if(model.getPercentOfNormalDelivery()!=null){
                        percentOfNormalDeliveries.setText(model.getPercentOfNormalDelivery().toString() + getResources().getString(R.string.percentage_symbol));
                    }
                    if(model.getNoOfLiveBirth()!=null){
                        noOfLiveBirth.setText(model.getNoOfLiveBirth().toString());
                    }



                }
            }
            //saveBtn.setText(getString(R.string.button_next));
        }
        enableDisableResetButton();

        if (preferenceData.getPreferenceBooleanData(getString(R.string.is_reset))) {
            if (preferenceData.getPreferenceBooleanData("isStatus")) {

                profileEntryStatusTv.setVisibility(View.VISIBLE);
                profileEntryStatusTv.setText(preferenceData.getPreferenceData(getString(R.string.status_before_reset)));
                profileEntryStatusTv.setTextColor(preferenceData.getPreferenceIntData(getString(R.string.status_color)));
                if (preferenceData.getPreferenceBooleanData(getString(R.string.is_ramark))) {
                    showMore.setVisibility(View.VISIBLE);
                    //showMore.setText(preferenceData.getPreferenceData(getString(R.string.remark_before_reset)));
                    remarkMessage = preferenceData.getPreferenceData(getString(R.string.remark_before_reset));
                }
            }
        }
        //  callShowMore();
        if (remarkMessage != null && remarkMessage.length() > 0) {
            showMore.setVisibility(View.VISIBLE);
            showMore.setPaintFlags(showMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        } else {
            showMore.setVisibility(View.GONE);
        }

    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void proceedToSync() {
        int sync_timeout_in_second = 600;

        //if somebody give string value in the strings.xml, we have to handle that
        try {
            sync_timeout_in_second = Integer.parseInt(getString(R.string.sync_timeout_in_second));
        } catch (Exception e) {
            Log.w(TAG, getString(R.string.invalid_timeout));
        }
        progressDialog.setTitle(getString(R.string.syncing));
        progressDialog.show();

        SyncAsyncTask syncAsyncTask = new SyncAsyncTask();
        syncAsyncTask.setSyncListener(ProfileEntryActivity.this);
        syncService = new SyncServiceImpl();

        syncAsyncTask.execute(getString(R.string.server_url), isNetworkAvailable(), sync_timeout_in_second, syncService.getSyncmodel(realm));
    }

    public void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEntryActivity.this);
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

    @Override
    public void syncComplete(AsyncTaskResultModel asyncTaskResultModel) {
        if (asyncTaskResultModel != null) {
            switch (asyncTaskResultModel.getResult()) {
                case Constant.Result.SUCCESS:
                    progressDialog.dismiss();
                    Date cDate = new Date();
                    List<SyncMessageData> syncMessageDataList = syncService.getSyncMessage(
                            asyncTaskResultModel.getSyncResult(), asyncTaskResultModel.getSyncModel(), realm, ProfileEntryActivity.this);

                    //remove all table views before add any text to it, from 3 fragments
                    if (syncMessageDataList.size() == 0) {

                        //OutcomeFragment.mSubmitButton.setVisibility(View.GONE);
                        //PreferenceData pd = new PreferenceData(ProfileEntryActivity.this);
                        //pd.setPreferenceData(true, "isSubmitButtonDisabled");
                        showMore.setVisibility(View.GONE);
                        profileEntryStatusTv.setVisibility(View.GONE);
                        //ProcessFragment.mProcessTableLayout.removeAllViews();
                        //IntermediateFragment.mIntermediateTableLayout.removeAllViews();
                        //OutcomeFragment.mOutcomeTableLayout.removeAllViews();
                        //onResume();

                        final AlertDialog alert = new AlertDialog.Builder(ProfileEntryActivity.this).create();
                        alert.setTitle(getString(R.string.sync_result));
                        alert.setMessage(getString(R.string.sync_success));
                        alert.setCanceledOnTouchOutside(false);
                        alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
//                                ProcessFragment.mTemporaryData = new HashMap<>();
//                                setupViewPager();
                            }
                        });
                        alert.show();
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                        preferenceData.setPreferenceData(false, getString(R.string.is_reset));

                    } else {
                        final Dialog dialog = new Dialog(ProfileEntryActivity.this);
                        dialog.setContentView(R.layout.sync_error_listview);
                        dialog.setTitle(getString(R.string.sync_result));
                        RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.sync_recycle_view);
                        Button okButton = (Button) dialog.findViewById(R.id.ok_btn);
                        mRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        SyncErrorMessageAdapter adapter = new SyncErrorMessageAdapter(ProfileEntryActivity.this, syncMessageDataList);
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
                    onResume();
                    break;
                case Constant.Result.ERROR:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(asyncTaskResultModel.getMessage());
                    alertDialog.show();
                    break;
                case Constant.Result.SERVER_ERROR:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.server_error));
                    alertDialog.setMessage(asyncTaskResultModel.getMessage());
                    alertDialog.show();
                    break;
                case Constant.Result.REQUEST_TIMEOUT:
                    progressDialog.dismiss();
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(getString(R.string.request_timeout));
                    alertDialog.show();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void callShowMore() {
        if (remarkMessage != null && remarkMessage.length() > 0) {
            showMore.setVisibility(View.VISIBLE);
            showMore.setPaintFlags(showMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        } else {
            showMore.setVisibility(View.GONE);
        }
    }

    private void hidekeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
