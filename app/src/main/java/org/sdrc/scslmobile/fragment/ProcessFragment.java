package org.sdrc.scslmobile.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.activity.SNCUActivity;
import org.sdrc.scslmobile.customclass.CustomComparator;
import org.sdrc.scslmobile.model.ProfileEntryModel;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.TXNSNCUNICUData;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.PreferenceData;
import org.sdrc.scslmobile.util.SCSL;
import org.sdrc.scslmobile.util.Validation;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import static org.sdrc.scslmobile.activity.SNCUActivity.remarkMessage;
import static org.sdrc.scslmobile.activity.SNCUActivity.showMore;

/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 * Edited by Amit Kumar Sahoo(amit@sdrc.co.in)
 */

public class ProcessFragment extends Fragment {

    public static LinkedHashMap<String, List<Indicator>> mIndicatorsMap;
    private Realm realm;
    public static List<Integer> listOfIndicatorIdsInProcessFragment;

    public static HashMap<Integer, TXNSNCUNICUData> mTemporaryData;
    private Validation validation;
    private TextView emptyTextView;
    private View view;
    boolean hasLR;
    ProfileEntryModel profileEntryModel;
    PreferenceData preferenceData;

    public static TableLayout mProcessTableLayout;
    //   public static List<IndicatorSaveData>
    public static Map<Integer, Integer> IndicatorDataMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferenceData = new PreferenceData(getActivity());
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_process, container, false);
        emptyTextView = (TextView) view.findViewById(R.id.empty_process_indicator);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
        Realm.init(getActivity());
        //    realm = SCSL.getInstance().getRealm(getActivity());
        realm = Realm.getDefaultInstance();

        validation = new Validation();

        IndicatorDataMap = new HashMap<>();

        mIndicatorsMap = new LinkedHashMap<>();
        //expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        // if(mProcessTableLayout == null){
        mProcessTableLayout = (TableLayout) view.findViewById(R.id.processTableLayout);
        //   }
        //get current user area has lr or not
        int currentUserFacilityId = Integer.parseInt(realm.where(User.class).findFirst().getAreasIds());

        hasLR = SCSL.getInstance().getAreaMap().get(currentUserFacilityId).isHasLR();
        mProcessTableLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View views = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(views.getWindowToken(), 0);
                }
                return false;
            }
        });
        getIndicatorListForProcess();
        return view;
    }

    private void getIndicatorListForProcess() {


        listOfIndicatorIdsInProcessFragment = new ArrayList<>();
        Map<Integer, String> coreAreas = new LinkedHashMap<>();
        //Adding adapter to recyclerview
        // Obtain a Realm instance

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
        int currentUserFacilityId = Integer.parseInt(realm.where(User.class).findFirst().getAreasIds());
        final List<Indicator> indicators = new ArrayList<>();
        RealmResults<IndicatorFacilityTimeperiodMapping> intermediateResults = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("timePeriod", lastMonth.getTimePeriodId()).equalTo("facility", currentUserFacilityId).findAll();

        for (IndicatorFacilityTimeperiodMapping results : intermediateResults) {
            int indicatorId = results.getIndicator();
            // results.getIndFacilityTpId()
            Indicator indicator = SCSL.getInstance().getIndicatorMap().get(indicatorId);
            if(indicator.getIndicatorType()!=null){
                if (indicator.getIndicatorType() == Constant.TypeDetail.INDICATOR_TYPE_PROCESS) {
                    indicators.add(indicator);
                    IndicatorDataMap.put(indicator.getIndicatorId(), results.getIndFacilityTpId());
                }
            }

        }

        Collections.sort(indicators, new CustomComparator());

        for (Indicator indicator : indicators) {
            if (SCSL.getInstance().getTypeDetailsMap().get(indicator.getCoreArea()) != null) {
                coreAreas.put(indicator.getCoreArea(), SCSL.getInstance().getTypeDetailsMap().get(indicator.getCoreArea()).getTypeDetail());
            }
        }
        for (Map.Entry<Integer, String> coreArea : coreAreas.entrySet()) {
            List<Indicator> list = new ArrayList<>();
            for (Indicator indicator : indicators) {

                if (coreArea.getKey() == indicator.getCoreArea()) {
                    list.add(indicator);
                    listOfIndicatorIdsInProcessFragment.add(indicator.getIndicatorId());
                }
            }
            Collections.sort(list, new CustomComparator());

            mIndicatorsMap.put(coreArea.getValue(), list);
        }
        if (mIndicatorsMap.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_indicators));
        } else {
            for (final Map.Entry<String, List<Indicator>> indicator : mIndicatorsMap.entrySet()) {
                TableRow headerTableRow = new TableRow(getActivity());
                headerTableRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                View headerView = View.inflate(getActivity(), R.layout.header_layout, null);
                headerTableRow.setTag("header");
                TextView coreArea_tv = (TextView) headerView.findViewById(R.id.core_area);
                coreArea_tv.setText(indicator.getKey());
                headerTableRow.addView(headerView);
                mProcessTableLayout.addView(headerTableRow);
                for (int i = 0; i < indicator.getValue().size(); i++) {
                    if (hasLR || !SCSL.getInstance().getIndicatorMap().get(indicator.getValue().get(i).getIndicatorId()).isLr()) {
                        final int mCount = i;
                        TableRow innerTableRow = new TableRow(getActivity());
                        innerTableRow.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        View innerView = View.inflate(getActivity(), R.layout.sncu_list, null);
                        TextView heading = (TextView) innerView.findViewById(R.id.heading);
                        final TextView percentage = (TextView) innerView.findViewById(R.id.percentage);
                        TextView nominatorTitle = (TextView) innerView.findViewById(R.id.nominator_title);
                        final EditText nominatorValue = (EditText) innerView.findViewById(R.id.nominator_value);
                        TextView denominatorTitle = (TextView) innerView.findViewById(R.id.denominator_title);
                        final EditText denominatorValue = (EditText) innerView.findViewById(R.id.denominator_value);
                        TextView slNo = (TextView) innerView.findViewById(R.id.serial_no);
                        slNo.setText(i + 1 + "");
                        heading.setTag(indicator.getValue().get(i).getIndicatorId());
                        //nominatorValue.setTag(indicator.getKey());
                        heading.setText(indicator.getValue().get(i).getIndicatorName());
                        nominatorTitle.setText(indicator.getValue().get(i).getNumerator());
                        denominatorTitle.setText(indicator.getValue().get(i).getDenominator());
                        innerTableRow.addView(innerView);
                        mProcessTableLayout.addView(innerTableRow);

                        //populate data as per profile entry
                        //ProfileEntryModel profileEntryModel =  SCSL.getInstance().getProfileEntryModel();
                        Gson gson = new Gson();
                        String json = new PreferenceData(getContext()).getPreferenceData(getString(R.string.profile_entry_json));
                        profileEntryModel = gson.fromJson(json, ProfileEntryModel.class);


                        if (profileEntryModel != null) {
                            if (SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(indicator.getValue().get(i).getIndicatorId())) {
                                if (profileEntryModel.getNoOfTotalDeliveries() != null) {
                                    denominatorValue.setText("" + profileEntryModel.getNoOfTotalDeliveries());
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                }

                            }
                            if (SCSL.getInstance().getLiveBirthListOfIndicators().contains(indicator.getValue().get(i).getIndicatorId())) {
                                if (profileEntryModel.getNoOfLiveBirth() != null) {
                                    denominatorValue.setText("" + profileEntryModel.getNoOfLiveBirth());
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);

                                }
                            }
                            if (SCSL.getInstance().getInbornListOfIndicators().contains(indicator.getValue().get(i).getIndicatorId())) {
                                if (profileEntryModel.getNoOfInbornAdmission() != null) {
                                    denominatorValue.setText("" + profileEntryModel.getNoOfInbornAdmission());
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);

                                }
                            }
                            if (SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(indicator.getValue().get(i).getIndicatorId())) {
                                if (profileEntryModel.getNoOfTotalAdmission() != null) {
                                    denominatorValue.setText("" + profileEntryModel.getNoOfTotalAdmission());
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                }
                            }

                        }

                        enableOrDisableIndicator(nominatorValue, denominatorValue, percentage, indicator.getValue().get(i).getIndicatorId());
                        nominatorValue.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (!nominatorValue.getText().toString().isEmpty()
                                        && !denominatorValue.getText().toString().isEmpty()) {

                                    float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                                    float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                                    if (denominatorValues == 0.0) {
                                        percentage.setText("NaN");
                                    } else if (denominatorValues != 0.0 && nominatorValues == 0.0) {
                                        percentage.setText("0.0" + getActivity().getString(R.string.percentage_symbol));
                                    } else {
                                        float percentageValues = (nominatorValues / denominatorValues) * 100;
                                        DecimalFormat percentageValueFront = new DecimalFormat(getActivity().getResources().getString(R.string.decimal_format));
                                        percentage.setText(percentageValueFront.format(percentageValues) + getActivity().getResources().getString(R.string.percentage_symbol));
                                    }

                                    if (!new Validation().validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                                        if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                            denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                        } else {
                                            nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_yellow));
                                            denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_yellow));
                                        }
                                    } else {
                                        nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                        denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                    }

                                } else {
                                    percentage.setText("");
                                    nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                    denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        nominatorValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if (!b) {
                                    String errorMessage = null;
                                    String warningMessage = null;
                                    //we can not take any common method for validation because if we delete any value from feild,
                                    // we also have to remove that value from map
                                    if (!nominatorValue.getText().toString().isEmpty()
                                            && !denominatorValue.getText().toString().isEmpty()) {
                                        float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                                        float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                                        if (!validation.validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                                            if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                                errorMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                            } else {
                                                //showWarningDialog(Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR);
                                                warningMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                            }
                                        }
                                    }

                                    //check for NumberOfNeonatalDeaths
                                    if (errorMessage == null) {
                                        if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsNumerator().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            errorMessage = validation.validateForValidationForNumberOfNeonatalDeaths(nominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                        }
                                    }

                                    if (errorMessage == null) {
                                        if (SCSL.getInstance().getNoOfNewBornLessThan2000gmsInNumerators().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            errorMessage = validation.validateForValidationForNoOfNewBornLessThan2000gms(nominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                        }
                                    }

                                    if (errorMessage == null) {
                                        if (SCSL.getInstance().getNoOfNewBornBabiesAdmittedToTheSNCUNumerators().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            errorMessage = validation.validateTotalNoOfAdmission(nominatorValue.getText().toString(), profileEntryModel.getNoOfTotalAdmission());
                                        }
                                    }

                                    if (errorMessage != null) {
                                        new Validation().removeIndicatorFromAllValidationMap(indicator.getValue().get(mCount).getIndicatorId());
                                        nominatorValue.setText("");
                                        showErrorDialog(errorMessage);
                                    } else if (warningMessage != null) {
                                        showWarningDialog(warningMessage);
                                    }

                                }
                            }
                        });
                        denominatorValue.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (!nominatorValue.getText().toString().isEmpty()
                                        && !denominatorValue.getText().toString().isEmpty()) {

                                    float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                                    float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                                    if (denominatorValues == 0.0) {
                                        percentage.setText("NaN");
                                    } else if (denominatorValues != 0.0 && nominatorValues == 0.0) {
                                        percentage.setText("0.0" + getActivity().getString(R.string.percentage_symbol));
                                    } else {
                                        float percentageValues = (nominatorValues / denominatorValues) * 100;
                                        DecimalFormat percentageValueFront = new DecimalFormat(getActivity().getResources().getString(R.string.decimal_format));
                                        percentage.setText(percentageValueFront.format(percentageValues) + getActivity().getResources().getString(R.string.percentage_symbol));
                                    }
                                    if (!new Validation().validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                                        if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                            denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                        } else {
                                            nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_yellow));
                                            denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_yellow));
                                        }
                                    } else {
                                        nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                        denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                    }

                                } else {
                                    percentage.setText("");
                                    nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                    denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        denominatorValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if (!b) {

                                    String errorMessage = null;
                                    String warningMessage = null;

                                    //we can not take any common method for validation because if we delete any value from feild,
                                    // we also have to remove that value from map

                                    if (!nominatorValue.getText().toString().isEmpty()
                                            && !denominatorValue.getText().toString().isEmpty()) {
                                        float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                                        float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                                        if (!validation.validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                                            if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                                errorMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                            } else {
                                                //showWarningDialog(Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR);
                                                warningMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                            }
                                        }
                                    }

                                    //check for Validation2
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidation2(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for Validation3
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidation3(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for Validation4
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidation4(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for Validation5
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidation5(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for Validation7
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidation7(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for NumberOfHighRiskDeliveries
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidationForNumberOfHighRiskDeliveries(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for NumberOfInbornNeonatesAdmittedToTheNICU
                                    if (errorMessage == null) {
                                        errorMessage = validation.validateForValidationForNumberOfInbornNeonatesAdmittedToTheNICU(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                    }

                                    //check for NumberOfNeonatalDeaths
                                    if (errorMessage == null) {
                                        if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsDinominator().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            errorMessage = validation.validateForValidationForNumberOfNeonatalDeaths(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                        }
                                    }

                                    //check for validate For Validation For No Of New BornLess Than 2000gms
                                    if (errorMessage == null) {
                                        if (SCSL.getInstance().getNoOfNewBornLessThan2000gmsInDinominators().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            errorMessage = validation.validateForValidationForNoOfNewBornLessThan2000gms(denominatorValue.getText().toString(), indicator.getValue().get(mCount).getIndicatorId());
                                        }
                                    }

                                    if (errorMessage == null) {
                                        if (SCSL.getInstance().getNoOfNewBornBabiesAdmittedToTheSNCUDinominators().contains(indicator.getValue().get(mCount).getIndicatorId())) {
                                            errorMessage = validation.validateTotalNoOfAdmission(denominatorValue.getText().toString(), profileEntryModel.getNoOfTotalAdmission());
                                        }
                                    }

                                    if (errorMessage != null) {
                                        denominatorValue.setText("");
                                        new Validation().removeIndicatorFromAllValidationMap(indicator.getValue().get(mCount).getIndicatorId());
                                        showErrorDialog(errorMessage);
                                    } else if (warningMessage != null) {
                                        showWarningDialog(warningMessage);
                                    }
                                }
                            }
                        });

                    }
                }

            }
        }
    }

    private void enableOrDisableIndicator(final EditText nominatorValue, final EditText denominatorValue, final TextView percentage, int old_indicator_id) {

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
            PreferenceData mPreferenceData = new PreferenceData(getActivity());
            if (data != null && data.size() > 0) {
                for (TXNSNCUNICUData txnsncunicuData : data) {
                    int indicatorFacilityTimeperiodMapping_id = txnsncunicuData.getIndicatorFacilityTimeperiodMapping();
                    IndicatorFacilityTimeperiodMapping indicatorFacilityTimeperiodMapping = realm.where(IndicatorFacilityTimeperiodMapping.class)
                            .equalTo("indFacilityTpId", indicatorFacilityTimeperiodMapping_id).findFirst();
                    int new_indicator_id = indicatorFacilityTimeperiodMapping.getIndicator();
                    if (old_indicator_id == new_indicator_id) {
                        if (txnsncunicuData != null) {

                            if (txnsncunicuData.getNumeratorValue() != null) {
                                nominatorValue.setText(txnsncunicuData.getNumeratorValue().toString());
                            } else {
                                nominatorValue.setText("");
                            }
                            if (txnsncunicuData.getDenominatorValue() != null) {
                                denominatorValue.setText("" + txnsncunicuData.getDenominatorValue());
                            } else {
                                denominatorValue.setText("");
                            }
                            if (txnsncunicuData.getPercentage() != null) {
                                percentage.setText(txnsncunicuData.getPercentage().toString() + "%");
                            } else {
                                if (txnsncunicuData.getNumeratorValue() != null && txnsncunicuData.getDenominatorValue() != null) {
                                    int numerator = txnsncunicuData.getNumeratorValue();
                                    int denominator = txnsncunicuData.getDenominatorValue();
                                    if (denominator != 0 && numerator == 0) {
                                        percentage.setText("0.0%");
                                    } else if (denominator == 0) {
                                        percentage.setText("NaN");
                                    } else {
                                        percentage.setText("");
                                    }
                                } else {
                                    percentage.setText("");
                                }
                            }
                            if (txnsncunicuData.getNumeratorValue() != null && txnsncunicuData.getDenominatorValue() != null) {
                                int numerator = txnsncunicuData.getNumeratorValue();
                                int denominator = txnsncunicuData.getDenominatorValue();
                                if (numerator > denominator) {
                                    if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(new_indicator_id)) {
                                        nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                        denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                    } else {
                                        nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_yellow));
                                        denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_yellow));
                                    }
                                } else {
                                    nominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                    denominatorValue.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                                }
                            }
                            if (!txnsncunicuData.isSynced() || txnsncunicuData.isHasError()) {
                                if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                                    SNCUActivity.mDueDateTextview.setVisibility(View.GONE);
                                    SNCUActivity.remarkMessage = "";
                                    if(SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                                            SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                                            SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                                            ||SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)){
                                        nominatorValue.setEnabled(true);
                                        denominatorValue.setEnabled(false);
                                        denominatorValue.setFocusable(false);
                                        denominatorValue.setFocusableInTouchMode(false);

                                    }else{
                                        nominatorValue.setEnabled(true);
                                        denominatorValue.setEnabled(true);
                                       // SNCUActivity.remarkMessage = "";
                                    }
                                } else {

                                    if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        SNCUActivity.mDueDateTextview.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                                        SNCUActivity.remarkMessage = "";
                                    }

                                    nominatorValue.setEnabled(false);
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                    
                                }
                            } else {

                                if (txnsncunicuData.isRejectedBySup()) {
                                    if (Integer.parseInt(date) <= sysConfig.getSubDeadLine()) {

                                        if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                            SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                            //  SNCUActivity.mDueDateTextview.setText("Submission rejected by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkBySup());
                                            SNCUActivity.mDueDateTextview.setText("Submission rejected by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                            SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                                            SNCUActivity.remarkMessage = txnsncunicuData.getRemarkBySup();
                                        }

                                        if(SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                                                SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                                                SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                                                ||SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)){
                                            nominatorValue.setEnabled(true);
                                            denominatorValue.setEnabled(false);
                                            denominatorValue.setFocusable(false);
                                            denominatorValue.setFocusableInTouchMode(false);

                                        }else{
                                            nominatorValue.setEnabled(true);
                                            denominatorValue.setEnabled(true);
                                          //  SNCUActivity.remarkMessage = "";
                                        }
                                    } else {

                                        if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                            SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                            SNCUActivity.mDueDateTextview.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                            SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                                            SNCUActivity.remarkMessage = "";
                                        }
                                        nominatorValue.setEnabled(false);
                                        denominatorValue.setEnabled(false);
                                        denominatorValue.setFocusable(false);
                                        denominatorValue.setFocusableInTouchMode(false);
                                    }

                                } else if (txnsncunicuData.isApprovedBySup()) {
                                    //set message
                                    if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        //SNCUActivity.mDueDateTextview.setText("Approved by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkBySup());
                                        SNCUActivity.mDueDateTextview.setText("Approved by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                        //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                                        SNCUActivity.remarkMessage = txnsncunicuData.getRemarkBySup();
                                    }
                                    nominatorValue.setEnabled(false);
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                } else if (txnsncunicuData.isRejectedByMNE()) {
                                    if (Integer.parseInt(date) <= sysConfig.getMneDeadLine()) {

                                        if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                            SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                            //   SNCUActivity.mDueDateTextview.setText("Submission rejected by MnE" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkByMNE());
                                            SNCUActivity.mDueDateTextview.setText("Submission rejected by MnE" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                            //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                            SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                                            SNCUActivity.remarkMessage = txnsncunicuData.getRemarkByMNE();
                                        }
                                        if(SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                                                SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                                                SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                                                ||SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)){
                                            nominatorValue.setEnabled(true);
                                            denominatorValue.setEnabled(false);
                                            denominatorValue.setFocusable(false);
                                            denominatorValue.setFocusableInTouchMode(false);

                                        }else{
                                            nominatorValue.setEnabled(true);
                                            denominatorValue.setEnabled(true);
                                           // SNCUActivity.remarkMessage = "";
                                        }
                                    } else {

                                        if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                            SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                            SNCUActivity.mDueDateTextview.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                            SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                                            SNCUActivity.remarkMessage = "";
                                        }
                                        nominatorValue.setEnabled(false);
                                        denominatorValue.setEnabled(false);
                                        denominatorValue.setFocusable(false);
                                        denominatorValue.setFocusableInTouchMode(false);
                                    }
                                } else if (txnsncunicuData.isApprovedByMNE()) {

                                    if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        // SNCUActivity.mDueDateTextview.setText("Approved by M&E" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkByMNE());
                                        SNCUActivity.mDueDateTextview.setText("Approved by M&E" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                        //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                                        SNCUActivity.remarkMessage = txnsncunicuData.getRemarkByMNE();


                                    }
                                    nominatorValue.setEnabled(false);
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                } else if (txnsncunicuData.isAutoApproved()) {
                                    if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        SNCUActivity.mDueDateTextview.setText("Auto approved by system on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                                        SNCUActivity.remarkMessage = "";
                                    }
                                    nominatorValue.setEnabled(false);
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                } else {

                                    if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        SNCUActivity.mDueDateTextview.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                                        SNCUActivity.remarkMessage = "";
                                    }
                                    nominatorValue.setEnabled(false);
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                }
                            }
                        }
                        break;
                    }
                }
            } else {
                if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                    SNCUActivity.mDueDateTextview.setVisibility(View.GONE);
                    if(SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                            SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                            SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                            ||SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)){
                        nominatorValue.setEnabled(true);
                        denominatorValue.setEnabled(false);
                        denominatorValue.setFocusable(false);
                        denominatorValue.setFocusableInTouchMode(false);
                    }else{
                        nominatorValue.setEnabled(true);
                        denominatorValue.setEnabled(true);
                        SNCUActivity.remarkMessage = "";
                    }

                } else {
                    SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                    SNCUActivity.mDueDateTextview.setText(getContext().getString(R.string.due_date_expired));
                    SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    nominatorValue.setEnabled(false);
                    denominatorValue.setEnabled(false);
                    denominatorValue.setFocusable(false);
                    denominatorValue.setFocusableInTouchMode(false);
                    SNCUActivity.remarkMessage = "";
                }

            }
            // }
        } catch (Exception e) {
            Log.i("", "" + e);
        }
        new Validation().validateForValidation2(denominatorValue.getText().toString(), old_indicator_id);
        new Validation().validateForValidation3(denominatorValue.getText().toString(), old_indicator_id);
        new Validation().validateForValidation4(denominatorValue.getText().toString(), old_indicator_id);
        new Validation().validateForValidation5(denominatorValue.getText().toString(), old_indicator_id);
        new Validation().validateForValidation7(denominatorValue.getText().toString(), old_indicator_id);
        new Validation().validateForValidationForNumberOfHighRiskDeliveries(denominatorValue.getText().toString(), old_indicator_id);
        new Validation().validateForValidationForNumberOfInbornNeonatesAdmittedToTheNICU(denominatorValue.getText().toString(), old_indicator_id);
        if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsNumerator().contains(old_indicator_id)) {
            new Validation().validateForValidationForNumberOfNeonatalDeaths(nominatorValue.getText().toString(), old_indicator_id);
        }
        if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsDinominator().contains(old_indicator_id)) {
            new Validation().validateForValidationForNumberOfNeonatalDeaths(denominatorValue.getText().toString(), old_indicator_id);
        }

        if(preferenceData.getPreferenceBooleanData(getString(R.string.is_reset))){
            if (preferenceData.getPreferenceBooleanData("isStatus")) {
                SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                SNCUActivity.mDueDateTextview.setText(preferenceData.getPreferenceData(getString(R.string.status_before_reset)));
                SNCUActivity.mDueDateTextview.setTextColor(preferenceData.getPreferenceIntData(getString(R.string.status_color)));
                if (preferenceData.getPreferenceBooleanData(getString(R.string.is_ramark))) {
                    showMore.setVisibility(View.VISIBLE);
                    //showMore.setText(preferenceData.getPreferenceData(getString(R.string.remark_before_reset)));
                    remarkMessage = preferenceData.getPreferenceData(getString(R.string.remark_before_reset));
                }
            }
        }
    }

    private void showErrorDialog(String errorMessage) {
        if (OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value) != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                            TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                            tabhost.getTabAt(0).select();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
        }
    }


    private void showWarningDialog(String warningMessage) {
        if (OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value) != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setTitle("Warning")
                    .setMessage(warningMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
        }
    }

}


