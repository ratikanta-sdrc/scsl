package org.sdrc.scslmobile.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.adapter.SNCUHomePageAdapter;
import org.sdrc.scslmobile.adapter.SyncErrorMessageAdapter;
import org.sdrc.scslmobile.asynctask.SyncAsyncTask;
import org.sdrc.scslmobile.customclass.IndicatorRow;
import org.sdrc.scslmobile.customclass.SyncMessageData;
import org.sdrc.scslmobile.fragment.IntermediateFragment;
import org.sdrc.scslmobile.fragment.OutcomeFragment;
import org.sdrc.scslmobile.fragment.ProcessFragment;
import org.sdrc.scslmobile.listener.SyncListener;
import org.sdrc.scslmobile.model.AsyncTaskResultModel;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.TXNSNCUNICUData;
import org.sdrc.scslmobile.model.realm.Type;
import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.service.SyncServiceImpl;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.TypeDetail;
import org.sdrc.scslmobile.util.PreferenceData;
import org.sdrc.scslmobile.util.SCSL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 * Ratikanta Pradhan
 * Edited by Amit Kumar Sahoo(amit@sdrc.co.in)
 */

public class SNCUActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener,
        ViewPager.OnPageChangeListener, View.OnClickListener, SyncListener {
    private static final String TAG = SNCUActivity.class.getName();
    private ViewPager mViewPager;
    private FloatingActionButton mFab;
    private CheckBox mSelectAllIndicators;
    private int indicatorSelectedCount = 0;
    private TableLayout mIndicatorsTable;
    private List<IndicatorRow> indicatorRows;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private Map<Integer, String> coreAreaMap;
    private int selectedCoreAreaId;
    private Dialog addIndicatorDialog;
    private Realm realm;
    private Handler handler;
    private SyncServiceImpl syncService;
    private TextView mTimePeriodTextView;
    private Button submitButton;
    public static TextView mDueDateTextview, showMore;
    private boolean mFirstTime;
    public static String remarkMessage;
    boolean hasLR;
    PreferenceData preferenceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sncu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Safe Care, Saving Lives</font>"));
        realm = SCSL.getInstance().getRealm(SNCUActivity.this);
        mDueDateTextview = (TextView) findViewById(R.id.due_date_msg);
        showMore = (TextView) findViewById(R.id.show_more);
        preferenceData = new PreferenceData(SNCUActivity.this);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.bringToFront();
        //enableOrDisableButton();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFirstTime = true;
                addIndicatorDialog = new Dialog(SNCUActivity.this);
                addIndicatorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addIndicatorDialog.setContentView(R.layout.activity_add_indicator);
                ImageView closeImage = (ImageView) addIndicatorDialog.findViewById(R.id.close_dialog);
                closeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addIndicatorDialog.dismiss();
                    }
                });
               //get the core areas
                RealmResults<TypeDetail> indicatorRealmResults = realm.where(TypeDetail.class).equalTo("typeId", Constant.Type.COREAREA_TYPE_ID).findAllSorted("typeDetail");
                coreAreaMap = new LinkedHashMap<>();

                for (TypeDetail typeDetails : indicatorRealmResults) {
                    coreAreaMap.put(typeDetails.getTypeDetailId(),
                            typeDetails.getTypeDetail());
                }
                //set the first coreArea default selected core area at the time of dialog box show
                selectedCoreAreaId = -1;
                final TextView coreAreaText = (TextView) addIndicatorDialog.findViewById(R.id.core_area_textbox);
                LinearLayout coreAreaDialog = (LinearLayout) addIndicatorDialog.findViewById(R.id.select_core_area_layout);
                coreAreaDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence[] coreAreas = new CharSequence[coreAreaMap.size()];
                        int i = 0;
                        for (Map.Entry<Integer, String> coreArea : coreAreaMap.entrySet()) {
                            coreAreas[i] = coreArea.getValue();
                            i++;
                        }
                        chooseSelection(coreAreas, coreAreaText, coreAreaText.getText().toString());
                    }
                });

                submitButton = (Button) addIndicatorDialog.findViewById(R.id.submit_button);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedIndicatorsForAdd();
                        //addIndicatorDialog.dismiss();
                    }
                });
                //select all indicators in the dialog box
                mSelectAllIndicators = (CheckBox) addIndicatorDialog.findViewById(R.id.select_all_indicators);
                mSelectAllIndicators.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectOrDeselectAllIndicators(mSelectAllIndicators.isChecked());
                        if (!mSelectAllIndicators.isChecked()) {
                            indicatorSelectedCount = 0;
                        } else {
                            indicatorSelectedCount = mIndicatorsTable.getChildCount();
                        }
                    }
                });
                showDialogContent(addIndicatorDialog, selectedCoreAreaId);
                addIndicatorDialog.show();
                addIndicatorDialog.setCanceledOnTouchOutside(false);
            }
        });

        //get current user area has lr or not
        int currentUserFacilityId = Integer.parseInt(realm.where(User.class).findFirst().getAreasIds());
        hasLR = SCSL.getInstance().getAreaMap().get(currentUserFacilityId).isHasLR();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sncu_drawer_layout);
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
        mTimePeriodTextView = (TextView) findViewById(R.id.time_period);
        //get 2nd latest time period
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

        progressDialog = new ProgressDialog(SNCUActivity.this);
        progressDialog.setTitle(getString(R.string.syncing));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        NavigationView navigationView = (NavigationView) findViewById(R.id.sncu_nav_view);
        View header = navigationView.getHeaderView(0);
        TextView sync = (TextView) header.findViewById(R.id.sncu_sync);
        sync.setOnClickListener(this);
        TextView logout = (TextView) header.findViewById(R.id.sncu_logout);
        logout.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        setupViewPager();

        //alert dialog code
        alertDialog = new AlertDialog.Builder(SNCUActivity.this).create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        User user = realm.where(User.class).findFirst();

        //We know that there will be only one area assigned to one deo so we are going to get that area id in string.
        //Still we have to take precaution
        int areaId;
        String facility_name = "";
        String district_name = "";
        String state_name = "";
        try {
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


        } catch (NumberFormatException e) {
            alertDialog.setTitle(getString(R.string.error));
            alertDialog.setMessage(getString(R.string.invalid_user_area_mapping));
        }

        TextView deoName = (TextView) header.findViewById(R.id.deo_name);
        deoName.setText(getString(R.string.deo_name) + " " + user.getName());
        TextView state = (TextView) header.findViewById(R.id.state);
        state.setText(getString(R.string.state) + " " + state_name);
        TextView district = (TextView) header.findViewById(R.id.district);
        district.setText(getString(R.string.district) + " " + district_name);
        TextView facility = (TextView) header.findViewById(R.id.facility);
        facility.setText(getString(R.string.facility) + " " + facility_name);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ((View) findViewById(R.id.view)).setVisibility(View.VISIBLE);
                progressDialog.dismiss();

            }
        };

    }

    private void showDialogContent(Dialog dialog, int coreAreaId) {
        indicatorRows = new ArrayList<>();
        RealmResults<Indicator> indicatorRealmResults = realm.where(Indicator.class).equalTo("coreArea", coreAreaId).findAll();
        int slNo = 1;
        for (Indicator indicator : indicatorRealmResults) {

            if (hasLR || !SCSL.getInstance().getIndicatorMap().get(indicator.getIndicatorId()).isLr()) {

                if (indicator.getIndicatorType() == Constant.TypeDetail.INDICATOR_TYPE_PROCESS) {
                    if (!ProcessFragment.listOfIndicatorIdsInProcessFragment.contains(indicator.getIndicatorId())) {
                        IndicatorRow row = new IndicatorRow();
                        row.setSlNo(slNo++);
                        row.setIndicatorName(indicator.getIndicatorName());
                        row.setIndicatorId(indicator.getIndicatorId());
                        indicatorRows.add(row);
                    }
                }
            }

        }
        mIndicatorsTable = (TableLayout) dialog.findViewById(R.id.indicators);

        LinearLayout headerLayout = (LinearLayout) dialog.findViewById(R.id.linear_header_layout);
        TextView emptyIndicators_tv = (TextView) dialog.findViewById(R.id.no_indicators_tv);
        if (indicatorRows.size() == 0) {
            if (mFirstTime) {
                headerLayout.setVisibility(View.GONE);
                emptyIndicators_tv.setVisibility(View.VISIBLE);
                emptyIndicators_tv.setText(getString(R.string.select_core_area_to_add_indicator));
                submitButton.setEnabled(false);
            } else {
                headerLayout.setVisibility(View.GONE);
                emptyIndicators_tv.setVisibility(View.VISIBLE);
                emptyIndicators_tv.setText(getString(R.string.all_indicators_mapped));
                submitButton.setEnabled(false);
            }

        } else {
            emptyIndicators_tv.setVisibility(View.GONE);
            headerLayout.setVisibility(View.VISIBLE);

            submitButton.setEnabled(true);
        }
        mIndicatorsTable.removeAllViews();
        for (int i = 0; i < indicatorRows.size(); i++) {
            final IndicatorRow indicatorRow = indicatorRows.get(i);
            TableRow tableRow = new TableRow(SNCUActivity.this);
            View mTableView = View.inflate(this, R.layout.indicator_list_row, null);
            LinearLayout layout = (LinearLayout) mTableView.findViewById(R.id.list_row);
            TextView slNoTv = (TextView) mTableView.findViewById(R.id.sl_no_tv);
            TextView indicatorTv = (TextView) mTableView.findViewById(R.id.indicator_name_tv);
            CheckBox selected = (CheckBox) mTableView.findViewById(R.id.select_indicator);

            if (i % 2 != 0) {
                layout.setBackgroundColor(Color.rgb(222, 222, 222));
            }

            slNoTv.setText("" + indicatorRow.getSlNo());
            slNoTv.setTag(indicatorRow.getIndicatorId());
            indicatorTv.setText(indicatorRow.getIndicatorName());
            tableRow.addView(mTableView);
            mIndicatorsTable.addView(tableRow);

            selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b) {
                        indicatorSelectedCount--;
                        if (mSelectAllIndicators.isChecked()) {
                            mSelectAllIndicators.setChecked(false);
                        }
                    } else if (b && !mSelectAllIndicators.isChecked()) {
                        indicatorSelectedCount++;
                        if (indicatorSelectedCount == mIndicatorsTable.getChildCount()) {
                            mSelectAllIndicators.setChecked(true);
                        }
                    }

                }
            });

        }
    }

    //at the time of submit add all the selected indicators tothe selelcted Indicator list
    private void selectedIndicatorsForAdd() {
        int count = 0;
        //get latest id
        int latestId;
        if (realm.where(IndicatorFacilityTimeperiodMapping.class).max("indFacilityTpId") != null) {
            latestId = Integer.parseInt("" + realm.where(IndicatorFacilityTimeperiodMapping.class).max("indFacilityTpId"));
        } else {
            latestId = 0;
        }
        //get latest time period
        TimePeriod lastMonth = null;
        TimePeriod latestMonth = null;
        RealmResults<TimePeriod> result = realm.where(TimePeriod.class).findAllSorted("startDate", Sort.DESCENDING);
        int mCount = 0;
        for (TimePeriod timePeriod : result) {
            mCount++;
            if (mCount == 1) {
                latestMonth = timePeriod;
            }
            if (mCount == 2) {
                lastMonth = timePeriod;
                break;
            }
        }

        int idToBeInsert;

        if (OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value) != null && !OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
            idToBeInsert = latestMonth.getTimePeriodId();
        } else {
            idToBeInsert = lastMonth.getTimePeriodId();
        }

        List<Integer> idsPresent = new ArrayList<>();
        RealmResults<IndicatorFacilityTimeperiodMapping> results = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("timePeriod", idToBeInsert).findAll();
        for (IndicatorFacilityTimeperiodMapping mapping : results) {
            idsPresent.add(mapping.getIndicator());
        }

        List<Integer> idsAdded = new ArrayList<>();

        //getting the current user
        User user = realm.where(User.class).findFirst();
        try {

            int userAreaId = Integer.parseInt(user.getAreasIds());

            List<Integer> ids = SCSL.getInstance().getLastIndicatorFacilityTimePeriodIds();

            for (int i = 0; i < mIndicatorsTable.getChildCount(); i++) {
                View child = mIndicatorsTable.getChildAt(i);
                CheckBox indicatorSelect = (CheckBox) child.findViewById(R.id.select_indicator);
                if (indicatorSelect.isChecked()) {
                    TextView selectedIndicatorSlNo = (TextView) child.findViewById(R.id.sl_no_tv);
                    for (IndicatorRow row : indicatorRows) {
                        if (Integer.parseInt(selectedIndicatorSlNo.getText().toString()) == row.getSlNo()) {
                            if (!idsPresent.contains(row.getIndicatorId())) {
                                realm.beginTransaction();
                                IndicatorFacilityTimeperiodMapping data = new IndicatorFacilityTimeperiodMapping();
                                data.setIndFacilityTpId(++latestId);
                                data.setCreatedDate(new Date());
                                data.setFacility(userAreaId);
                                data.setIndicator(row.getIndicatorId());
                                data.setTimePeriod(idToBeInsert);
                                data.setNew(true);
                                ids.add(latestId);
                                realm.copyToRealm(data);
                                realm.commitTransaction();
                                idsAdded.add(latestId);

                            }
                        }
                    }
                    count++;
                }
            }
            SCSL.getInstance().setExtraIndicatorFacilityTimeperiodIds(idsAdded);
            SCSL.getInstance().setLastIndicatorFacilityTimePeriodIds(ids);
            if (count > 0) {
                Log.v("", "" + ProcessFragment.mProcessTableLayout.getChildCount());
                int child_count = ProcessFragment.mProcessTableLayout.getChildCount();
                for (int i = 0; i < child_count; i++) {
                    View child = ProcessFragment.mProcessTableLayout.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                Log.v("", "" + ProcessFragment.mProcessTableLayout.getChildCount());
                ProcessFragment.mProcessTableLayout.removeAllViews();
                IntermediateFragment.mIntermediateTableLayout.removeAllViews();
                OutcomeFragment.mOutcomeTableLayout.removeAllViews();
                addIndicatorDialog.dismiss();
                Toast.makeText(this, count + " Indicators added successfully.", Toast.LENGTH_SHORT).show();
                populateFragments();

            } else {
                Toast.makeText(this, "Please select Indicator(s) to add.", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(getString(R.string.invalid_user_area_mapping))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

    }

    private void selectOrDeselectAllIndicators(boolean isChecked) {

        for (int i = 0; i < mIndicatorsTable.getChildCount(); i++) {
            View child = mIndicatorsTable.getChildAt(i);
            CheckBox indicatorSelect = (CheckBox) child.findViewById(R.id.select_indicator);
            indicatorSelect.setChecked(isChecked);
        }
    }

    private void setupViewPager() {
        progressDialog.setTitle(getString(R.string.initializing));
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateFragments();
                handler.sendEmptyMessage(0);

            }
        }, 3000);


    }

    private void populateFragments() {
        //   mViewPager.setAdapter(null);
        //resetAllPreviousValidationData();
        SNCUHomePageAdapter adapter = new SNCUHomePageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProcessFragment(), this.getResources().getString(R.string.process));
        adapter.addFragment(new IntermediateFragment(), this.getResources().getString(R.string.intermediate));
        adapter.addFragment(new OutcomeFragment(), this.getResources().getString(R.string.outcome));
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        callShowMore();
    }

    public void callShowMore(){
      if(remarkMessage !=null && remarkMessage.length()>0){
          showMore.setVisibility(View.VISIBLE);
          showMore.setPaintFlags(showMore.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
          showMore.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SNCUActivity.this);
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
              }
          });
      }else{
          showMore.setVisibility(View.GONE);
      }
    }

    private void resetAllPreviousValidationData() {
        SCSL.getInstance().setIndicatorValuesMapForValidation2(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForValidation3(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForValidation5(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForValidation7(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(new HashMap<Integer, Float>());
        SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(new HashMap<Integer, Float>());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sncu_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sncu_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                hidekeyboard();
                mFab.show();
                break;
            case 1:
                hidekeyboard();
                mFab.hide();
                break;
            case 2:
                hidekeyboard();
                mFab.hide();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                hidekeyboard();
                mFab.show();
                if (ProcessFragment.mProcessTableLayout != null && ProcessFragment.mProcessTableLayout.getChildAt(1) == null) {
                    if (getWindow().getCurrentFocus() != null) {
                        getWindow().getCurrentFocus().clearFocus();
                    }
                }
                break;
            case 1:
                hidekeyboard();
                mFab.hide();
                /*if (getWindow().getCurrentFocus() != null) {
                    getWindow().getCurrentFocus().clearFocus();
                }*/
                break;
            case 2:
                hidekeyboard();
                mFab.hide();
                /*if (getWindow().getCurrentFocus() != null) {
                    getWindow().getCurrentFocus().clearFocus();
                }*/
                break;
            default:
                break;
        }
    }

    private void hidekeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }

    private void hiddenKeyboard(View v) {
     /*   InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);*/
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
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
    public void onClick(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sncu_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (view.getId()) {
            case R.id.sncu_sync:
                //show alert dialog before syncing
                //first check that for feild is editable or not
                //if yes(Editable) then only show the message other wise not
                if (OutcomeFragment.mOutcomeTableLayout != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1) != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SNCUActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Warning")
                            .setMessage("All unsaved data will be cleared. Do you want to Sync?")
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
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SNCUActivity.this, R.color.red));
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SNCUActivity.this, R.color.green));
                        }
                    });
                    alert.show();
                } else {
                    proceedToSync();
                }

                break;
            case R.id.sncu_logout:
                showLogoutAlert();
                break;
            default:
                break;
        }
    }

    private void proceedToSync(){

        setTypeMap();
        setTypeDetailsMap();
        setTimePeriodMap();
        setIndicatorMap();
        setAreaMap();
        setLastTimePeriodId();
        setLastIndicatorFacilityTimePeriodIds();

        int sync_timeout_in_second = 60;

        //if somebody give string value in the strings.xml, we have to handle that
        try {
            sync_timeout_in_second = Integer.parseInt(getString(R.string.sync_timeout_in_second));
        } catch (Exception e) {
            Log.w(TAG, getString(R.string.invalid_timeout));
        }
        progressDialog.setTitle(getString(R.string.syncing));
        progressDialog.show();

        SyncAsyncTask syncAsyncTask = new SyncAsyncTask();
        syncAsyncTask.setSyncListener(SNCUActivity.this);
        syncService = new SyncServiceImpl();

        syncAsyncTask.execute(getString(R.string.server_url), isNetworkAvailable(), sync_timeout_in_second, syncService.getSyncmodel(realm));
    }

    private void chooseSelection(final CharSequence[] items, final TextView txtView, String text) {

        int selected = 0;
        for (int i = 0; i < items.length; i++) {
            if (text.trim().equals(items[i])) {
                selected = i;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_core_area_title));
        builder.setSingleChoiceItems(items, selected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                    }
                })
                .setPositiveButton(" ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mFirstTime = false;
                        int selectedPosition = ((AlertDialog) dialog)
                                .getListView().getCheckedItemPosition();
                        String value = items[selectedPosition].toString();
                        txtView.setText(value);
                        for (Map.Entry<Integer, String> entry : coreAreaMap.entrySet()) {
                            if (entry.getValue().trim().equals(value)) {
                                selectedCoreAreaId = entry.getKey();
                            }
                        }
                        // set the select all to default
                        if (mSelectAllIndicators != null) {
                            mSelectAllIndicators.setChecked(false);
                        }
                        indicatorSelectedCount = 0;

                        showDialogContent(addIndicatorDialog, selectedCoreAreaId);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

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
                            asyncTaskResultModel.getSyncResult(), asyncTaskResultModel.getSyncModel(), realm, SNCUActivity.this);

                    //remove all table views before add any text to it, from 3 fragments
                    if (syncMessageDataList.size() == 0) {

                        OutcomeFragment.mSubmitButton.setVisibility(View.GONE);
                        showMore.setVisibility(View.GONE);
                        mDueDateTextview.setVisibility(View.GONE);
                        ProcessFragment.mProcessTableLayout.removeAllViews();
                        IntermediateFragment.mIntermediateTableLayout.removeAllViews();
                        OutcomeFragment.mOutcomeTableLayout.removeAllViews();

                        final AlertDialog alert = new AlertDialog.Builder(SNCUActivity.this).create();
                        alert.setTitle(getString(R.string.sync_result));
                        alert.setMessage(getString(R.string.sync_success));
                        alert.setCanceledOnTouchOutside(false);
                        alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                                preferenceData.setPreferenceData(false, getString(R.string.is_reset));
                                ProcessFragment.mTemporaryData = new HashMap<>();
                                finish();
                                //setupViewPager();
                            }
                        });
                        alert.show();
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                        // populateFragments();

                    } else {
                        final Dialog dialog = new Dialog(SNCUActivity.this);
                        dialog.setContentView(R.layout.sync_error_listview);
                        dialog.setTitle(getString(R.string.sync_result));
                        RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.sync_recycle_view);
                        Button okButton = (Button) dialog.findViewById(R.id.ok_btn);
                        mRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        SyncErrorMessageAdapter adapter = new SyncErrorMessageAdapter(SNCUActivity.this, syncMessageDataList);
                        //Adding adapter to recyclerview
                        mRecyclerView.setAdapter(adapter);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();

                            }
                        });
                        dialog.show();
                        preferenceData.setPreferenceData(false,getString(R.string.is_reset));

                    }

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

    public void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SNCUActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = SCSL.getInstance().getRealm(SNCUActivity.this);

    }

    private void enableOrDisableButton() {
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
            if (data != null && data.size() > 0) {
                for (TXNSNCUNICUData txnsncunicuData : data) {
                    int indicatorFacilityTimeperiodMapping_id = txnsncunicuData.getIndicatorFacilityTimeperiodMapping();
                    IndicatorFacilityTimeperiodMapping indicatorFacilityTimeperiodMapping = realm.where(IndicatorFacilityTimeperiodMapping.class)
                            .equalTo("indFacilityTpId", indicatorFacilityTimeperiodMapping_id).findFirst();
                    if (txnsncunicuData != null) {
                    }
                    if (!txnsncunicuData.isSynced() || txnsncunicuData.isHasError()) {
                        if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                            mFab.setEnabled(true);
                        } else {
                            mFab.setEnabled(false);
                        }
                    } else {

                        if (txnsncunicuData.isRejectedBySup()) {
                            if (Integer.parseInt(date) <= sysConfig.getSubDeadLine()) {
                                mFab.setEnabled(true);
                            } else {
                                mFab.setEnabled(false);
                            }

                        } else if (txnsncunicuData.isRejectedByMNE()) {
                            if (Integer.parseInt(date) <= sysConfig.getMneDeadLine()) {
                                mFab.setEnabled(true);
                            } else {
                                mFab.setEnabled(false);
                            }
                        } else {

                            mFab.setEnabled(false);

                        }

                    }

                    break;
                }
            } else {
                if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                    mFab.setEnabled(true);
                } else {
                    mFab.setEnabled(false);
                }
            }


        } catch (Exception e) {
            Log.i("", "" + e);
        }

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
}
