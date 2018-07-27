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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

public class OutcomeFragment extends Fragment implements View.OnClickListener {

    private Map<Integer, Integer> IndicatorDataMap;
    private Realm realm;
    public static TableLayout mOutcomeTableLayout;
    private Validation validation;
    private TextView emptyTextView;
    public static Button mSubmitButton;
    boolean hasLR;
    ProfileEntryModel profileEntryModel;
    PreferenceData preferenceData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferenceData = new PreferenceData(getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outcome, container, false);
        emptyTextView = (TextView) view.findViewById(R.id.empty_outcome_indicator);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
        validation = new Validation();
        realm = SCSL.getInstance().getRealm(getActivity());
        IndicatorDataMap = new HashMap<>();
        mOutcomeTableLayout = (TableLayout) view.findViewById(R.id.outcomeTableLayout);
        mOutcomeTableLayout.setOnTouchListener(new View.OnTouchListener() {
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

        //get current user area has lr or not
        int currentUserFacilityId = Integer.parseInt(realm.where(User.class).findFirst().getAreasIds());
        hasLR = SCSL.getInstance().getAreaMap().get(currentUserFacilityId).isHasLR();

        //Initializing Views
        mSubmitButton = (Button) view.findViewById(R.id.submit_button_for_sncu);
        mSubmitButton.setVisibility(View.VISIBLE);
        mSubmitButton.setOnClickListener(this);
        enableOrDisableButton();
        getOutComeList();
        return view;
    }


    private void getOutComeList() {

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
        List<Indicator> indicators = new ArrayList<>();
        RealmResults<IndicatorFacilityTimeperiodMapping> intermediateResults = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("timePeriod", lastMonth.getTimePeriodId())
                .equalTo("facility", currentUserFacilityId).findAll();
        for (IndicatorFacilityTimeperiodMapping results : intermediateResults) {
            int indicatorId = results.getIndicator();
            Indicator indicator = SCSL.getInstance().getIndicatorMap().get(indicatorId);
            if (indicator.getIndicatorType() != null) {
                if (indicator.getIndicatorType() == Constant.TypeDetail.INDICATOR_TYPE_OUTCOME) {
                    indicators.add(indicator);
                    IndicatorDataMap.put(indicator.getIndicatorId(), results.getIndFacilityTpId());
                }
            }

        }
        if (indicators.size() == 0) {
            mSubmitButton.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_indicators));
        } else {
            Collections.sort(indicators, new CustomComparator());
            populateOutComeLIst(indicators);
        }

    }

    private void populateOutComeLIst(List<Indicator> indicators) {
        int indicatorId;
        for (int i = 0; i < indicators.size(); i++) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableRow.LayoutParams(
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
            slNo.setText((i + 1) + "");
            indicatorId = indicators.get(i).getIndicatorId();
            heading.setTag(indicatorId);
            heading.setText(indicators.get(i).getIndicatorName());
            nominatorTitle.setText(indicators.get(i).getNumerator());
            denominatorTitle.setText(indicators.get(i).getDenominator());
            tableRow.addView(innerView);
            mOutcomeTableLayout.addView(tableRow);
            //ProfileEntryModel profileEntryModel =  SCSL.getInstance().getProfileEntryModel();
            Gson gson = new Gson();
            String json = new PreferenceData(getContext()).getPreferenceData(getString(R.string.profile_entry_json));
            profileEntryModel = gson.fromJson(json, ProfileEntryModel.class);

            if (profileEntryModel != null) {
                if (SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(indicators.get(i).getIndicatorId())) {
                    if (profileEntryModel.getNoOfTotalDeliveries() != null) {
                        denominatorValue.setText("" + profileEntryModel.getNoOfTotalDeliveries());
                        denominatorValue.setEnabled(false);
                        denominatorValue.setFocusable(false);
                        denominatorValue.setFocusableInTouchMode(false);
                    }

                }
                if (SCSL.getInstance().getLiveBirthListOfIndicators().contains(indicators.get(i).getIndicatorId())) {
                    if (profileEntryModel.getNoOfLiveBirth() != null) {
                        denominatorValue.setText("" + profileEntryModel.getNoOfLiveBirth());
                        denominatorValue.setEnabled(false);
                        denominatorValue.setFocusable(false);
                        denominatorValue.setFocusableInTouchMode(false);

                    }
                }
                if (SCSL.getInstance().getInbornListOfIndicators().contains(indicators.get(i).getIndicatorId())) {
                    if (profileEntryModel.getNoOfInbornAdmission() != null) {
                        denominatorValue.setText("" + profileEntryModel.getNoOfInbornAdmission());
                        denominatorValue.setEnabled(false);
                        denominatorValue.setFocusable(false);
                        denominatorValue.setFocusableInTouchMode(false);

                    }
                }
                if (SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(indicators.get(i).getIndicatorId())) {
                    if (profileEntryModel.getNoOfTotalAdmission() != null) {
                        denominatorValue.setText("" + profileEntryModel.getNoOfTotalAdmission());
                        denominatorValue.setEnabled(false);
                        denominatorValue.setFocusable(false);
                        denominatorValue.setFocusableInTouchMode(false);

                    }
                }

            }
            enableOrDisableIndicator(nominatorValue, denominatorValue, percentage, indicatorId);
            final int finalIndicatorId = indicatorId;
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
                            if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(finalIndicatorId)) {
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

                        if (!nominatorValue.getText().toString().isEmpty()
                                && !denominatorValue.getText().toString().isEmpty()) {
                            float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                            float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                            if (!validation.validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                                if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(finalIndicatorId)) {
                                    errorMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                } else {
                                    //showWarningDialog(Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR);
                                    warningMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                }
                            }
                        }

                        //check for NumberOfNeonatalDeaths
                        if (errorMessage == null) {
                            if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsNumerator().contains(finalIndicatorId)) {
                                errorMessage = validation.validateForValidationForNumberOfNeonatalDeaths(nominatorValue.getText().toString(), finalIndicatorId);
                            }
                        }

                        if (errorMessage == null) {
                            if (SCSL.getInstance().getNoOfNewBornLessThan2000gmsInNumerators().contains(finalIndicatorId)) {
                                errorMessage = validation.validateForValidationForNoOfNewBornLessThan2000gms(nominatorValue.getText().toString(), finalIndicatorId);
                            }
                        }

                        if (errorMessage == null) {
                            if (SCSL.getInstance().getNoOfNewBornBabiesAdmittedToTheSNCUNumerators().contains(finalIndicatorId)) {
                                errorMessage = validation.validateTotalNoOfAdmission(nominatorValue.getText().toString(), profileEntryModel.getNoOfTotalAdmission());
                            }
                        }

                        if (errorMessage != null) {
                            new Validation().removeIndicatorFromAllValidationMap(finalIndicatorId);
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
                            if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(finalIndicatorId)) {
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
            final int finalIndicatorId1 = indicatorId;
            denominatorValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (!b) {
                        String errorMessage = null;
                        String warningMessage = null;

                        if (!nominatorValue.getText().toString().isEmpty()
                                && !denominatorValue.getText().toString().isEmpty()) {
                            float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                            float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                            if (!validation.validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                                if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(finalIndicatorId1)) {
                                    errorMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                } else {
                                    //showWarningDialog(Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR);
                                    warningMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                                }
                            }
                        }

                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidation2(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidation3(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidation4(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidation5(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        //check for Validation7
                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidation7(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        //check for NumberOfHighRiskDeliveries
                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidationForNumberOfHighRiskDeliveries(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        //check for NumberOfInbornNeonatesAdmittedToTheNICU
                        if (errorMessage == null) {
                            errorMessage = validation.validateForValidationForNumberOfInbornNeonatesAdmittedToTheNICU(denominatorValue.getText().toString(), finalIndicatorId1);
                        }

                        //check for NumberOfNeonatalDeaths
                        if (errorMessage == null) {
                            if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsDinominator().contains(finalIndicatorId1)) {
                                errorMessage = validation.validateForValidationForNumberOfNeonatalDeaths(denominatorValue.getText().toString(), finalIndicatorId1);
                            }
                        }

                        //check for validate For Validation For No Of New BornLess Than 2000gms
                        if (errorMessage == null) {
                            if (SCSL.getInstance().getNoOfNewBornLessThan2000gmsInDinominators().contains(finalIndicatorId1)) {
                                errorMessage = validation.validateForValidationForNoOfNewBornLessThan2000gms(denominatorValue.getText().toString(), finalIndicatorId1);
                            }
                        }

                        if (errorMessage == null) {
                            if (SCSL.getInstance().getNoOfNewBornBabiesAdmittedToTheSNCUDinominators().contains(finalIndicatorId1)) {
                                errorMessage = validation.validateTotalNoOfAdmission(denominatorValue.getText().toString(), profileEntryModel.getNoOfTotalAdmission());
                            }
                        }

                        if (errorMessage != null) {
                            new Validation().removeIndicatorFromAllValidationMap(finalIndicatorId1);
                            denominatorValue.setText("");
                            showErrorDialog(errorMessage);

                        } else if (warningMessage != null) {
                            showWarningDialog(warningMessage);
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button_for_sncu:
                validateAllFeildsForError();
                break;
            default:
                break;
        }
    }

    private void saveSNCUData() {

        Realm realm = SCSL.getInstance().getRealm(getContext());

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
        //IndicatorFacilityTimeperiodMapping mapping = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("timePeriod", latestTimePeriod).findFirst();
        IndicatorFacilityTimeperiodMapping mapping = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("timePeriod", lastMonth.getTimePeriodId()).findFirst();
        TXNSNCUNICUData txnsncunicuData = null;
        if (mapping != null) {
            txnsncunicuData = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", mapping.getIndFacilityTpId()).findFirst();
        }

        Gson gson = new Gson();
        String json = new PreferenceData(getContext()).getPreferenceData(getString(R.string.profile_entry_json));
        ProfileEntryModel profileEntryModel = gson.fromJson(json, ProfileEntryModel.class);
        //this map will store all the Indicator TP Facility mapping ids  with the key indicator ids
        //So that it will help for storing tXNdata of the Profile Entry page
        HashMap<Integer, Integer> indicatorTpFacilityMappings = new HashMap<>();

        TimePeriod lastMonthTemp = null;
        RealmResults<TimePeriod> resultTP = realm.where(TimePeriod.class).findAllSorted("startDate", Sort.DESCENDING);
        int countTemp = 0;
        for (TimePeriod timePeriod : resultTP) {
            countTemp++;
            if (countTemp == 2) {
                lastMonthTemp = timePeriod;
                break;
            }
        }
        int currentUserFacilityId = Integer.parseInt(realm.where(User.class).findFirst().getAreasIds());
        RealmResults<IndicatorFacilityTimeperiodMapping> intermediateResults = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("timePeriod", lastMonthTemp.getTimePeriodId()).equalTo("facility", currentUserFacilityId).findAll();

        for (IndicatorFacilityTimeperiodMapping results : intermediateResults) {
            int indicatorId = results.getIndicator();
            // results.getIndFacilityTpId()
            Indicator indicator = SCSL.getInstance().getIndicatorMap().get(indicatorId);
            if (indicator.isProfile()) {
                indicatorTpFacilityMappings.put(indicator.getIndicatorId(), results.getIndFacilityTpId());
            }

        }

        if (txnsncunicuData != null) {
            //update

            //update the ProfilePage first
            if (realm.isInTransaction()) {
                realm.commitTransaction();
                realm.close();
            }


            realm.beginTransaction();
            int iftmId2 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_OUTBORN_ADMISSION);
            TXNSNCUNICUData data3 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId2).findFirst();
            data3.setIndicatorFacilityTimeperiodMapping(iftmId2);
            data3.setNumeratorValue(profileEntryModel.getNoOfOutbornAdmission());
            data3.setPercentage(Double.valueOf(profileEntryModel.getNoOfOutbornAdmission().toString()));
            data3.setCreatedDate(new Date());
            data3.setSynced(false);
            realm.copyToRealm(data3);
            realm.commitTransaction();

            realm.beginTransaction();
            int iftmId3 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_TOTAL_ADMISSION);
            TXNSNCUNICUData data4 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId3).findFirst();
            data4.setIndicatorFacilityTimeperiodMapping(iftmId3);
            data4.setNumeratorValue(profileEntryModel.getNoOfTotalAdmission());
            data4.setPercentage(Double.valueOf(profileEntryModel.getNoOfTotalAdmission().toString()));
            data4.setCreatedDate(new Date());
            data4.setSynced(false);
            realm.copyToRealm(data4);
            realm.commitTransaction();



            realm.beginTransaction();
            int iftmId5 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_OUTBORN_BABIES);
            TXNSNCUNICUData data6 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId5).findFirst();
            data6.setIndicatorFacilityTimeperiodMapping(iftmId5);
            data6.setNumeratorValue(profileEntryModel.getNoOfOutbornAdmission());
            data6.setDenominatorValue(profileEntryModel.getNoOfTotalAdmission());
            if (profileEntryModel.getPercentOfOutbornAdmission() == null){
                data6.setPercentage(null);
            } else {
                data6.setPercentage(Double.valueOf(profileEntryModel.getPercentOfOutbornAdmission().toString()));
            }
            data6.setCreatedDate(new Date());
            data6.setSynced(false);
            realm.copyToRealm(data6);
            realm.commitTransaction();

            if (hasLR) {
                realm.beginTransaction();
                int iftmId1 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_INBORN_ADMISSION);
                TXNSNCUNICUData data2 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId1).findFirst();
                data2.setIndicatorFacilityTimeperiodMapping(iftmId1);
                data2.setNumeratorValue(profileEntryModel.getNoOfInbornAdmission());
                data2.setPercentage(Double.valueOf(profileEntryModel.getNoOfInbornAdmission().toString()));
                data2.setCreatedDate(new Date());
                data2.setSynced(false);
                realm.copyToRealm(data2);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId4 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_INBORN_BABIES);
                TXNSNCUNICUData data5 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId4).findFirst();
                data5.setIndicatorFacilityTimeperiodMapping(iftmId4);
                data5.setNumeratorValue(profileEntryModel.getNoOfInbornAdmission());
                data5.setDenominatorValue(profileEntryModel.getNoOfTotalAdmission());
                if(profileEntryModel.getPercentOfInbornAdmission() == null){
                    data5.setPercentage(null);
                } else {
                    data5.setPercentage(Double.valueOf(profileEntryModel.getPercentOfInbornAdmission().toString()));
                }
                data5.setCreatedDate(new Date());
                data5.setSynced(false);
                realm.copyToRealm(data5);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId6 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_CSECTION_DELIVERY);
                TXNSNCUNICUData data7 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId6).findFirst();
                data7.setIndicatorFacilityTimeperiodMapping(iftmId6);
                data7.setNumeratorValue(profileEntryModel.getNoOfCSection());
                data7.setPercentage(Double.valueOf(profileEntryModel.getNoOfCSection().toString()));
                data7.setCreatedDate(new Date());
                data7.setSynced(false);
                realm.copyToRealm(data7);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId7 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_NORMAL_DELIVERY);
                TXNSNCUNICUData data8 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId7).findFirst();
                data8.setIndicatorFacilityTimeperiodMapping(iftmId7);
                data8.setNumeratorValue(profileEntryModel.getNoOfNormalDeliveries());
                data8.setPercentage(Double.valueOf(profileEntryModel.getNoOfNormalDeliveries().toString()));
                data8.setCreatedDate(new Date());
                data8.setSynced(false);
                realm.copyToRealm(data8);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId8 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_TOTAL_DELIVERY);
                TXNSNCUNICUData data9 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId8).findFirst();
                data9.setIndicatorFacilityTimeperiodMapping(iftmId8);
                data9.setNumeratorValue(profileEntryModel.getNoOfTotalDeliveries());
                data9.setPercentage(Double.valueOf(profileEntryModel.getNoOfTotalDeliveries().toString()));
                data9.setCreatedDate(new Date());
                data9.setSynced(false);
                realm.copyToRealm(data9);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId9 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_CSECTION_DELIVERIES);
                TXNSNCUNICUData data10 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId9).findFirst();
                data10.setIndicatorFacilityTimeperiodMapping(iftmId9);
                data10.setNumeratorValue(profileEntryModel.getNoOfCSection());
                data10.setDenominatorValue(profileEntryModel.getNoOfTotalDeliveries());
                if (profileEntryModel.getPercentOfCSection() == null){
                    data10.setPercentage(null);
                } else {
                    data10.setPercentage(Double.valueOf(profileEntryModel.getPercentOfCSection().toString()));
                }
                data10.setCreatedDate(new Date());
                data10.setSynced(false);
                realm.copyToRealm(data10);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId10 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_NORMAL_DELIVERIES);
                TXNSNCUNICUData data11 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId10).findFirst();
                data11.setIndicatorFacilityTimeperiodMapping(iftmId10);
                data11.setNumeratorValue(profileEntryModel.getNoOfNormalDeliveries());
                data11.setDenominatorValue(profileEntryModel.getNoOfTotalDeliveries());
                if (profileEntryModel.getPercentOfNormalDelivery() == null){
                    data11.setPercentage(null);
                } else {
                    data11.setPercentage(Double.valueOf(profileEntryModel.getPercentOfNormalDelivery().toString()));
                }
                data11.setCreatedDate(new Date());
                data11.setSynced(false);
                realm.copyToRealm(data11);
                realm.commitTransaction();

                realm.beginTransaction();
                int iftmId11 = indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_LIVE_BIRTH);
                TXNSNCUNICUData data12 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", iftmId11).findFirst();
                data12.setIndicatorFacilityTimeperiodMapping(iftmId11);
                data12.setNumeratorValue(profileEntryModel.getNoOfLiveBirth());
                data12.setPercentage(Double.valueOf(profileEntryModel.getNoOfLiveBirth().toString()));
                data12.setCreatedDate(new Date());
                data12.setSynced(false);
                realm.copyToRealm(data12);
                realm.commitTransaction();
            }
            //end

            for (int i = 0; i < ProcessFragment.mProcessTableLayout.getChildCount(); i++) {
                View view = ProcessFragment.mProcessTableLayout.getChildAt(i);
                if (view.getTag() == null) {
                    if (!realm.isInTransaction()) {
                        realm.beginTransaction();
                    }
                    int indicatorFacilityTimeperiodMappingId;
                    TextView heading = (TextView) view.findViewById(R.id.heading);
                    if (heading.getTag() != null) {
                        indicatorFacilityTimeperiodMappingId = ProcessFragment.IndicatorDataMap.get(heading.getTag());
                        TXNSNCUNICUData data = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", indicatorFacilityTimeperiodMappingId).findFirst();
                        //data.setTxnIndicatorId(data.getTxnIndicatorId());

                        //If data is null then a new indicator is added in process
                        if (data == null) {
                            data = new TXNSNCUNICUData();
                            View view1 = OutcomeFragment.mOutcomeTableLayout.getChildAt(0);
                            int indicatorFacilityTimeperiodMappingId1;
                            TextView heading1 = (TextView) view1.findViewById(R.id.heading);
                            indicatorFacilityTimeperiodMappingId1 = IndicatorDataMap.get(heading1.getTag());
                            TXNSNCUNICUData data1 = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", indicatorFacilityTimeperiodMappingId1).findFirst();
                            data.setRejectedBySup(data1.isRejectedBySup());
                            data.setRejectedByMNE(data1.isRejectedByMNE());
                            data.setApprovedBySup(data1.isApprovedBySup());
                            data.setApprovedByMNE(data1.isApprovedByMNE());
                            data.setRemarkBySup(data1.getRemarkBySup());
                            data.setRemarkByMNE(data1.getRemarkByMNE());
                            //insert
                            int latestId;
                            if (realm.where(TXNSNCUNICUData.class).max("txnIndicatorId") != null) {
                                latestId = Integer.parseInt("" + realm.where(TXNSNCUNICUData.class).max("txnIndicatorId"));
                            } else {
                                latestId = 0;
                            }
                            data.setTxnIndicatorId(++latestId);
                        }

                        if (heading.getTag() != null) {
                            data.setIndicatorFacilityTimeperiodMapping(ProcessFragment.IndicatorDataMap.get(heading.getTag()));
                        }
                        EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                        if (!nominatorValue.getText().toString().isEmpty()) {
                            data.setNumeratorValue(Integer.parseInt(nominatorValue.getText().toString()));
                        } else {
                            data.setNumeratorValue(null);
                        }
                        EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
                        if (!denominatorValue.getText().toString().isEmpty()) {
                            data.setDenominatorValue(Integer.parseInt(denominatorValue.getText().toString()));
                        } else {
                            data.setDenominatorValue(null);
                        }
                        //new
                        if (data.getNumeratorValue() != null && data.getDenominatorValue() != null) {
                            if (data.getNumeratorValue() > data.getDenominatorValue()) {
                                data.setDescription(55);
                            } else {
                                data.setDescription(null);
                            }

                        } else {
                            data.setDescription(null);
                        }
                        TextView percentage = (TextView) view.findViewById(R.id.percentage);
                        if (!percentage.getText().toString().isEmpty() && !percentage.getText().toString().equals("NaN")) {
                            data.setPercentage(Double.valueOf(percentage.getText().toString().substring(0, percentage.getText().toString().length() - 1)));
                        } else {
                            data.setPercentage(null);
                        }
                        data.setCreatedDate(new Date());
                        data.setSynced(false);
                        realm.copyToRealm(data);
                        realm.commitTransaction();
                    }
                }

            }

            for (int i = 0; i < IntermediateFragment.mIntermediateTableLayout.getChildCount(); i++) {
                View view = IntermediateFragment.mIntermediateTableLayout.getChildAt(i);
                if (view.getTag() == null) {
                    realm.beginTransaction();
                    int indicatorFacilityTimeperiodMappingId;
                    TextView heading = (TextView) view.findViewById(R.id.heading);
                    if (heading.getTag() != null) {
                        indicatorFacilityTimeperiodMappingId = IntermediateFragment.IndicatorDataMap.get(heading.getTag());
                        TXNSNCUNICUData data = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", indicatorFacilityTimeperiodMappingId).findFirst();
                        //data.setTxnIndicatorId(data.getTxnIndicatorId());
                        if (heading.getTag() != null) {
                            data.setIndicatorFacilityTimeperiodMapping(IntermediateFragment.IndicatorDataMap.get(heading.getTag()));
                        }
                        EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                        if (!nominatorValue.getText().toString().isEmpty()) {
                            data.setNumeratorValue(Integer.parseInt(nominatorValue.getText().toString()));
                        } else {
                            data.setNumeratorValue(null);
                        }
                        EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
                        if (!denominatorValue.getText().toString().isEmpty()) {
                            data.setDenominatorValue(Integer.parseInt(denominatorValue.getText().toString()));
                        } else {
                            data.setDenominatorValue(null);
                        }
                        //new
                        if (data.getNumeratorValue() != null && data.getDenominatorValue() != null) {
                            if (data.getNumeratorValue() > data.getDenominatorValue()) {
                                data.setDescription(55);
                            } else {
                                data.setDescription(null);
                            }

                        } else {
                            data.setDescription(null);
                        }
                        TextView percentage = (TextView) view.findViewById(R.id.percentage);
                        if (!percentage.getText().toString().isEmpty() && !percentage.getText().toString().equals("NaN")) {
                            data.setPercentage(Double.valueOf(percentage.getText().toString().substring(0, percentage.getText().toString().length() - 1)));
                        } else {
                            data.setPercentage(null);
                        }
                        data.setCreatedDate(new Date());
                        data.setSynced(false);
                        realm.copyToRealm(data);
                        realm.commitTransaction();
                    }
                }


            }

            for (int i = 0; i < mOutcomeTableLayout.getChildCount(); i++) {
                View view = mOutcomeTableLayout.getChildAt(i);
                realm.beginTransaction();
                int indicatorFacilityTimeperiodMappingId;
                TextView heading = (TextView) view.findViewById(R.id.heading);
                if (heading.getTag() != null) {
                    indicatorFacilityTimeperiodMappingId = IndicatorDataMap.get(heading.getTag());
                    TXNSNCUNICUData data = realm.where(TXNSNCUNICUData.class).equalTo("indicatorFacilityTimeperiodMapping", indicatorFacilityTimeperiodMappingId).findFirst();
                    //data.setTxnIndicatorId(data.getTxnIndicatorId());
                    if (heading.getTag() != null) {
                        data.setIndicatorFacilityTimeperiodMapping(IndicatorDataMap.get(heading.getTag()));
                    }
                    EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                    if (!nominatorValue.getText().toString().isEmpty()) {
                        data.setNumeratorValue(Integer.parseInt(nominatorValue.getText().toString()));
                    } else {
                        data.setNumeratorValue(null);
                    }
                    EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
                    if (!denominatorValue.getText().toString().isEmpty()) {
                        data.setDenominatorValue(Integer.parseInt(denominatorValue.getText().toString()));
                    } else {
                        data.setDenominatorValue(null);
                    }
                    //new
                    if (data.getNumeratorValue() != null && data.getDenominatorValue() != null) {
                        if (data.getNumeratorValue() > data.getDenominatorValue()) {
                            data.setDescription(55);
                        } else {
                            data.setDescription(null);
                        }

                    } else {
                        data.setDescription(null);
                    }
                    TextView percentage = (TextView) view.findViewById(R.id.percentage);
                    if (!percentage.getText().toString().isEmpty() && !percentage.getText().toString().equals("NaN")) {
                        data.setPercentage(Double.valueOf(percentage.getText().toString().substring(0, percentage.getText().toString().length() - 1)));
                    } else {
                        data.setPercentage(null);
                    }
                    data.setCreatedDate(new Date());
                    data.setSynced(false);
                    realm.copyToRealm(data);
                    realm.commitTransaction();
                }
            }
            Toast.makeText(getContext(), Constant.ErrorMessage.DATA_SUBMITTED_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
            realm.close();

        } else {
            //insert
            int latestId;
            if (realm.where(TXNSNCUNICUData.class).max("txnIndicatorId") != null) {
                latestId = Integer.parseInt("" + realm.where(TXNSNCUNICUData.class).max("txnIndicatorId"));
            } else {
                latestId = 0;
            }

            //insert Profile page first


            realm.beginTransaction();
            TXNSNCUNICUData data3 = new TXNSNCUNICUData();
            data3.setTxnIndicatorId(++latestId);
            data3.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_OUTBORN_ADMISSION));
            data3.setNumeratorValue(profileEntryModel.getNoOfOutbornAdmission());
            data3.setPercentage(Double.valueOf(profileEntryModel.getNoOfOutbornAdmission().toString()));
            data3.setCreatedDate(new Date());
            data3.setSynced(false);
            realm.copyToRealm(data3);
            realm.commitTransaction();

            realm.beginTransaction();
            TXNSNCUNICUData data4 = new TXNSNCUNICUData();
            data4.setTxnIndicatorId(++latestId);
            data4.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_TOTAL_ADMISSION));
            data4.setNumeratorValue(profileEntryModel.getNoOfTotalAdmission());
            data4.setPercentage(Double.valueOf(profileEntryModel.getNoOfTotalAdmission().toString()));
            data4.setCreatedDate(new Date());
            data4.setSynced(false);
            realm.copyToRealm(data4);
            realm.commitTransaction();



            realm.beginTransaction();
            TXNSNCUNICUData data6 = new TXNSNCUNICUData();
            data6.setTxnIndicatorId(++latestId);
            data6.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_OUTBORN_BABIES));
            data6.setNumeratorValue(profileEntryModel.getNoOfOutbornAdmission());
            data6.setDenominatorValue(profileEntryModel.getNoOfTotalAdmission());
            if (profileEntryModel.getPercentOfOutbornAdmission() != null){
                data6.setPercentage(Double.valueOf(profileEntryModel.getPercentOfOutbornAdmission().toString()));

            } else {
                data6.setPercentage(null);
            }
            data6.setCreatedDate(new Date());
            data6.setSynced(false);
            realm.copyToRealm(data6);
            realm.commitTransaction();

            if (hasLR) {
                //new code added if labor room for inborn admission and percentage of admission

                realm.beginTransaction();
                TXNSNCUNICUData data2 = new TXNSNCUNICUData();
                data2.setTxnIndicatorId(++latestId);

                data2.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_INBORN_ADMISSION));
                data2.setNumeratorValue(profileEntryModel.getNoOfInbornAdmission());
                data2.setPercentage(Double.valueOf(profileEntryModel.getNoOfInbornAdmission().toString()));
                data2.setCreatedDate(new Date());
                data2.setSynced(false);
                realm.copyToRealm(data2);
                realm.commitTransaction();

                realm.beginTransaction();
                TXNSNCUNICUData data5 = new TXNSNCUNICUData();
                data5.setTxnIndicatorId(++latestId);
                data5.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_INBORN_BABIES));
                data5.setNumeratorValue(profileEntryModel.getNoOfInbornAdmission());
                data5.setDenominatorValue(profileEntryModel.getNoOfTotalAdmission());
                if(profileEntryModel.getPercentOfInbornAdmission() != null){
                    data5.setPercentage(Double.valueOf(profileEntryModel.getPercentOfInbornAdmission().toString()));

                } else {
                    data5.setPercentage(null);
                }
                data5.setCreatedDate(new Date());
                data5.setSynced(false);
                realm.copyToRealm(data5);
                realm.commitTransaction();


                realm.beginTransaction();
                TXNSNCUNICUData data7 = new TXNSNCUNICUData();
                data7.setTxnIndicatorId(++latestId);
                data7.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_CSECTION_DELIVERY));
                data7.setNumeratorValue(profileEntryModel.getNoOfCSection());
                data7.setPercentage(Double.valueOf(profileEntryModel.getNoOfCSection().toString()));
                data7.setCreatedDate(new Date());
                data7.setSynced(false);
                realm.copyToRealm(data7);
                realm.commitTransaction();

                realm.beginTransaction();
                TXNSNCUNICUData data8 = new TXNSNCUNICUData();
                data8.setTxnIndicatorId(++latestId);
                data8.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_NORMAL_DELIVERY));
                data8.setNumeratorValue(profileEntryModel.getNoOfNormalDeliveries());
                data8.setPercentage(Double.valueOf(profileEntryModel.getNoOfNormalDeliveries().toString()));
                data8.setCreatedDate(new Date());
                data8.setSynced(false);
                realm.copyToRealm(data8);
                realm.commitTransaction();

                realm.beginTransaction();
                TXNSNCUNICUData data9 = new TXNSNCUNICUData();
                data9.setTxnIndicatorId(++latestId);
                data9.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_TOTAL_DELIVERY));
                data9.setNumeratorValue(profileEntryModel.getNoOfTotalDeliveries());
                data9.setPercentage(Double.valueOf(profileEntryModel.getNoOfTotalDeliveries().toString()));
                data9.setCreatedDate(new Date());
                data9.setSynced(false);
                realm.copyToRealm(data9);
                realm.commitTransaction();

                realm.beginTransaction();
                TXNSNCUNICUData data10 = new TXNSNCUNICUData();
                data10.setTxnIndicatorId(++latestId);
                data10.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_CSECTION_DELIVERIES));
                data10.setNumeratorValue(profileEntryModel.getNoOfCSection());
                data10.setDenominatorValue(profileEntryModel.getNoOfTotalDeliveries());
                if (profileEntryModel.getPercentOfCSection() != null){
                    data10.setPercentage(Double.valueOf(profileEntryModel.getPercentOfCSection().toString()));
                } else {
                    data10.setPercentage(null);
                }
                data10.setCreatedDate(new Date());
                data10.setSynced(false);
                realm.copyToRealm(data10);
                realm.commitTransaction();

                realm.beginTransaction();
                TXNSNCUNICUData data11 = new TXNSNCUNICUData();
                data11.setTxnIndicatorId(++latestId);
                data11.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.PERCENT_OF_NORMAL_DELIVERIES));
                data11.setNumeratorValue(profileEntryModel.getNoOfNormalDeliveries());
                data11.setDenominatorValue(profileEntryModel.getNoOfTotalDeliveries());
                if (profileEntryModel.getPercentOfNormalDelivery() != null){
                    data11.setPercentage(Double.valueOf(profileEntryModel.getPercentOfNormalDelivery().toString()));

                } else {
                    data11.setPercentage(null);
                }
                data11.setCreatedDate(new Date());
                data11.setSynced(false);
                realm.copyToRealm(data11);
                realm.commitTransaction();

                realm.beginTransaction();
                TXNSNCUNICUData data12 = new TXNSNCUNICUData();
                data12.setTxnIndicatorId(++latestId);
                data12.setIndicatorFacilityTimeperiodMapping(indicatorTpFacilityMappings.get(Constant.ProfilePageIndicator.NO_OF_LIVE_BIRTH));
                data12.setNumeratorValue(profileEntryModel.getNoOfLiveBirth());
                data12.setPercentage(Double.valueOf(profileEntryModel.getNoOfLiveBirth().toString()));
                data12.setCreatedDate(new Date());
                data12.setSynced(false);
                realm.copyToRealm(data12);
                realm.commitTransaction();
            }

            //int latestId = (latestIdNumber == null) ? 0 : (int)latestIdNumber;

            for (int i = 0; i < ProcessFragment.mProcessTableLayout.getChildCount(); i++) {
                View view = ProcessFragment.mProcessTableLayout.getChildAt(i);
                if (view.getTag() == null) {
                    realm.beginTransaction();
                    TXNSNCUNICUData data = new TXNSNCUNICUData();
                    TextView heading = (TextView) view.findViewById(R.id.heading);
                    data.setTxnIndicatorId(++latestId);
                    if (heading.getTag() != null) {
                        data.setIndicatorFacilityTimeperiodMapping(ProcessFragment.IndicatorDataMap.get(heading.getTag()));
                    }
                    EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                    if (!nominatorValue.getText().toString().isEmpty()) {
                        data.setNumeratorValue(Integer.parseInt(nominatorValue.getText().toString()));
                    } else {
                        data.setNumeratorValue(null);
                    }
                    EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
                    if (!denominatorValue.getText().toString().isEmpty()) {
                        data.setDenominatorValue(Integer.parseInt(denominatorValue.getText().toString()));
                    } else {
                        data.setDenominatorValue(null);
                    }
                    //new
                    if (data.getNumeratorValue() != null && data.getDenominatorValue() != null) {
                        if (data.getNumeratorValue() > data.getDenominatorValue()) {
                            data.setDescription(55);
                        } else {
                            data.setDescription(null);
                        }

                    } else {
                        data.setDescription(null);
                    }
                    TextView percentage = (TextView) view.findViewById(R.id.percentage);
                    if (!percentage.getText().toString().isEmpty() && !percentage.getText().toString().equals("NaN")) {
                        data.setPercentage(Double.valueOf(percentage.getText().toString().substring(0, percentage.getText().toString().length() - 1)));
                    } else {
                        data.setPercentage(null);
                    }
                    data.setCreatedDate(new Date());
                    data.setSynced(false);
                    realm.copyToRealm(data);
                    realm.commitTransaction();
                }

            }

            for (int i = 0; i < IntermediateFragment.mIntermediateTableLayout.getChildCount(); i++) {
                View view = IntermediateFragment.mIntermediateTableLayout.getChildAt(i);
                if (view.getTag() == null) {
                    realm.beginTransaction();
                    TXNSNCUNICUData data = new TXNSNCUNICUData();
                    TextView heading = (TextView) view.findViewById(R.id.heading);
                    data.setTxnIndicatorId(++latestId);
                    if (heading.getTag() != null) {
                        data.setIndicatorFacilityTimeperiodMapping(IntermediateFragment.IndicatorDataMap.get(heading.getTag()));
                    }
                    EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                    if (!nominatorValue.getText().toString().isEmpty()) {
                        data.setNumeratorValue(Integer.parseInt(nominatorValue.getText().toString()));
                    } else {
                        data.setNumeratorValue(null);
                    }
                    EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
                    if (!denominatorValue.getText().toString().isEmpty()) {
                        data.setDenominatorValue(Integer.parseInt(denominatorValue.getText().toString()));
                    } else {
                        data.setDenominatorValue(null);
                    }
                    //new
                    if (data.getNumeratorValue() != null && data.getDenominatorValue() != null) {
                        if (data.getNumeratorValue() > data.getDenominatorValue()) {
                            data.setDescription(55);
                        } else {
                            data.setDescription(null);
                        }

                    } else {
                        data.setDescription(null);
                    }
                    TextView percentage = (TextView) view.findViewById(R.id.percentage);
                    if (!percentage.getText().toString().isEmpty() && !percentage.getText().toString().equals("NaN")) {
                        data.setPercentage(Double.valueOf(percentage.getText().toString().substring(0, percentage.getText().toString().length() - 1)));
                    } else {
                        data.setPercentage(null);
                    }
                    data.setCreatedDate(new Date());
                    data.setSynced(false);
                    realm.copyToRealm(data);
                    realm.commitTransaction();
                }


            }

            for (int i = 0; i < mOutcomeTableLayout.getChildCount(); i++) {
                View view = mOutcomeTableLayout.getChildAt(i);
                realm.beginTransaction();
                TXNSNCUNICUData data = new TXNSNCUNICUData();
                TextView heading = (TextView) view.findViewById(R.id.heading);
                data.setTxnIndicatorId(++latestId);
                if (heading.getTag() != null) {
                    data.setIndicatorFacilityTimeperiodMapping(IndicatorDataMap.get(heading.getTag()));
                }
                EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                if (!nominatorValue.getText().toString().isEmpty()) {
                    data.setNumeratorValue(Integer.parseInt(nominatorValue.getText().toString()));
                } else {
                    data.setNumeratorValue(null);
                }
                EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
                if (!denominatorValue.getText().toString().isEmpty()) {
                    data.setDenominatorValue(Integer.parseInt(denominatorValue.getText().toString()));
                } else {
                    data.setDenominatorValue(null);
                }
                //new
                if (data.getNumeratorValue() != null && data.getDenominatorValue() != null) {
                    if (data.getNumeratorValue() > data.getDenominatorValue()) {
                        data.setDescription(55);
                    } else {
                        data.setDescription(null);
                    }

                } else {
                    data.setDescription(null);
                }
                TextView percentage = (TextView) view.findViewById(R.id.percentage);
                if (!percentage.getText().toString().isEmpty() && !percentage.getText().toString().equals("NaN")) {
                    data.setPercentage(Double.valueOf(percentage.getText().toString().substring(0, percentage.getText().toString().length() - 1)));
                } else {
                    data.setPercentage(null);
                }
                data.setCreatedDate(new Date());
                data.setSynced(false);
                realm.copyToRealm(data);
                realm.commitTransaction();

            }
            Toast.makeText(getContext(), Constant.ErrorMessage.DATA_SUBMITTED_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
            realm.close();
        }

    }


    private void validateAllFeildsForWarning() {

        String warningMessage = null;

        for (int i = 0; i < mOutcomeTableLayout.getChildCount(); i++) {

            View view = mOutcomeTableLayout.getChildAt(i);
            EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
            EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
            TextView header = (TextView) view.findViewById(R.id.heading);

            Validation validation = new Validation();

            //we can not take any common method for validation because if we delete any value from feild,
            // we also have to remove that value from map

            if (!nominatorValue.getText().toString().isEmpty()
                    && !denominatorValue.getText().toString().isEmpty()) {
                float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                if (!validation.validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                    //this condition is checked
                    //If LR room is not prestent then dont show warning message for those indicator
                    if (!(SCSL.getInstance().getIndicatorIdsForValidation6().contains(Integer.parseInt(header.getTag().toString())))) {
                        warningMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                    }
                }
            }
        }
        for (int i = 0; i < IntermediateFragment.mIntermediateTableLayout.getChildCount(); i++) {
            View view = IntermediateFragment.mIntermediateTableLayout.getChildAt(i);
            if (view.getTag() == null) {
                EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
                EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);

                if (nominatorValue.getText().toString().isEmpty()
                        || denominatorValue.getText().toString().isEmpty()) {
                    if (nominatorValue.isEnabled() || denominatorValue.isEnabled()) {
                        warningMessage = getString(R.string.No_feilds_in_intermediate_page_can_be_empty);
                    }

                }
            }

        }
        if (warningMessage == null) {
            saveSNCUData();
        } else {
            showWarningDialog(warningMessage);
        }
    }

    private void validateAllFeildsForError() {

        String errorMessage = null;

        for (int i = 0; i < mOutcomeTableLayout.getChildCount(); i++) {

            View view = mOutcomeTableLayout.getChildAt(i);
            EditText nominatorValue = (EditText) view.findViewById(R.id.nominator_value);
            EditText denominatorValue = (EditText) view.findViewById(R.id.denominator_value);
            TextView header = (TextView) view.findViewById(R.id.heading);

            Validation validation = new Validation();

            if (nominatorValue.getText().toString().isEmpty()
                    || denominatorValue.getText().toString().isEmpty()) {
                errorMessage = getString(R.string.No_feilds_in_outcome_page_can_be_empty);

            }

            if (errorMessage == null) {
                if (!nominatorValue.getText().toString().isEmpty()
                        && !denominatorValue.getText().toString().isEmpty()) {
                    float denominatorValues = Float.parseFloat(denominatorValue.getText().toString());
                    float nominatorValues = Float.parseFloat(nominatorValue.getText().toString());
                    if (!validation.validateIsNominatorSmallerThanDenominator(nominatorValues, denominatorValues)) {
                        if (SCSL.getInstance().getIndicatorIdsForValidation6().contains(Integer.parseInt(header.getTag().toString()))) {
                            errorMessage = Constant.ErrorMessage.NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR;
                        }
                    }
                }
            } else {
                break;
            }

            //check for Validation2
            if (errorMessage == null) {
                errorMessage = validation.validateForValidation2(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for Validation3
            if (errorMessage == null) {
                errorMessage = validation.validateForValidation3(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for Validation4
            if (errorMessage == null) {
                errorMessage = validation.validateForValidation4(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for Validation5
            if (errorMessage == null) {
                errorMessage = validation.validateForValidation5(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for Validation7
            if (errorMessage == null) {
                errorMessage = validation.validateForValidation7(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for NumberOfHighRiskDeliveries
            if (errorMessage == null) {
                errorMessage = validation.validateForValidationForNumberOfHighRiskDeliveries(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for NumberOfInbornNeonatesAdmittedToTheNICU
            if (errorMessage == null) {
                errorMessage = validation.validateForValidationForNumberOfInbornNeonatesAdmittedToTheNICU(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for NumberOfNeonatalDeaths for numerators
            if (errorMessage == null) {
                if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsNumerator().contains(Integer.parseInt(header.getTag().toString()))) {
                    errorMessage = validation.validateForValidationForNumberOfNeonatalDeaths(nominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
                }
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for NumberOfNeonatalDeaths for denominators
            if (errorMessage == null) {
                if (SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsDinominator().contains(Integer.parseInt(header.getTag().toString()))) {
                    errorMessage = validation.validateForValidationForNumberOfNeonatalDeaths(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
                }
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for Total number of new born less than 2000 gms in Numerator
            if (errorMessage == null) {
                if (SCSL.getInstance().getNoOfNewBornLessThan2000gmsInNumerators().contains(Integer.parseInt(header.getTag().toString()))) {
                    errorMessage = validation.validateForValidationForNoOfNewBornLessThan2000gms(nominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
                }
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

            //check for Total number of new born less than 2000 gms in Dinominator
            if (errorMessage == null) {
                if (SCSL.getInstance().getNoOfNewBornLessThan2000gmsInDinominators().contains(Integer.parseInt(header.getTag().toString()))) {
                    errorMessage = validation.validateForValidationForNoOfNewBornLessThan2000gms(denominatorValue.getText().toString(), Integer.parseInt(header.getTag().toString()));
                }
            } else {
                new Validation().removeIndicatorFromAllValidationMap(Integer.parseInt(header.getTag().toString()));
                nominatorValue.setText("");
                break;
            }

        }

        if (errorMessage != null) {
            showErrorDialog(errorMessage);
        } else {
            validateAllFeildsForWarning();
        }
    }

    private void showErrorDialog(String errorMessage) {
        if (OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value) != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                            TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                            tabhost.getTabAt(2).select();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                }
            });
            alert.show();
        }
    }

    private void showWarningDialog(final String warningMessage) {
        if (OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value) != null && OutcomeFragment.mOutcomeTableLayout.getChildAt(1).findViewById(R.id.nominator_value).isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle("Warning")
                    .setMessage(warningMessage)
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (warningMessage.equals(getString(R.string.No_feilds_in_intermediate_page_can_be_empty))) {
                            /*TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                            tabhost.getTabAt(1).select();*/
                            }
                            // continue with save
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton("Proceed To Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            saveSNCUData();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                }
            });
            alert.show();
        }
    }

    private void enableOrDisableIndicator(EditText nominatorValue, EditText denominatorValue, TextView percentage, int old_indicator_id) {
        SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
        PreferenceData mPreferenceData = new PreferenceData(getActivity());
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
            query = query.or().equalTo("indicatorFacilityTimeperiodMapping", indicatorFacilityTimeperiodMapping.getIndFacilityTpId());
        }

        RealmResults<TXNSNCUNICUData> data = query.findAll();
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
                              /*  nominatorValue.setEnabled(true);
                                denominatorValue.setEnabled(true);
                                SNCUActivity.remarkMessage = "";*/
                                if (SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                                        SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                                        SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                                        || SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)) {
                                    nominatorValue.setEnabled(true);
                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);

                                } else {
                                    nominatorValue.setEnabled(true);
                                    denominatorValue.setEnabled(true);
                                    SNCUActivity.remarkMessage = "";
                                }
                            } else {

                                if (mPreferenceData.getPreferenceData("submitted_date") != null) {
                                    SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                    SNCUActivity.mDueDateTextview.setText("Data is submitted on " + new Validation().formatDate(mPreferenceData.getPreferenceData("submitted_date")));
                                    SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                                    SNCUActivity.remarkMessage = "";
                                }
                                nominatorValue.setEnabled(false);
                                nominatorValue.setFocusable(false);
                                nominatorValue.setFocusableInTouchMode(false);

                                denominatorValue.setEnabled(false);
                                denominatorValue.setFocusable(false);
                                denominatorValue.setFocusableInTouchMode(false);
                            }
                        } else {
                            if (txnsncunicuData.isRejectedBySup()) {

                                if (Integer.parseInt(date) <= sysConfig.getSubDeadLine()) {

                                    if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        // SNCUActivity.mDueDateTextview.setText("Submission rejected by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkBySup());
                                        SNCUActivity.mDueDateTextview.setText("Submission rejected by Superintendent" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                        //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                                        SNCUActivity.remarkMessage = txnsncunicuData.getRemarkBySup();
                                    }
                                    //nominatorValue.setEnabled(true);
                                    //denominatorValue.setEnabled(true);
                                    if (SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                                            SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                                            SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                                            || SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)) {
                                        nominatorValue.setEnabled(true);
                                        denominatorValue.setEnabled(false);
                                        denominatorValue.setFocusable(false);
                                        denominatorValue.setFocusableInTouchMode(false);

                                    } else {
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
                                    nominatorValue.setFocusable(false);
                                    nominatorValue.setFocusableInTouchMode(false);

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
                                nominatorValue.setFocusable(false);
                                nominatorValue.setFocusableInTouchMode(false);

                                denominatorValue.setEnabled(false);
                                denominatorValue.setFocusable(false);
                                denominatorValue.setFocusableInTouchMode(false);
                            } else if (txnsncunicuData.isRejectedByMNE()) {
                                if (Integer.parseInt(date) <= sysConfig.getMneDeadLine()) {

                                    if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                        SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                        //  SNCUActivity.mDueDateTextview.setText("Submission rejected by MnE" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkByMNE());
                                        SNCUActivity.mDueDateTextview.setText("Submission rejected by MnE" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                        //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                        SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                                        SNCUActivity.remarkMessage = txnsncunicuData.getRemarkByMNE();
                                    }
                                    //nominatorValue.setEnabled(true);
                                    //denominatorValue.setEnabled(true);
                                    if (SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                                            SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                                            SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                                            || SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)) {
                                        nominatorValue.setEnabled(true);
                                        denominatorValue.setEnabled(false);
                                        denominatorValue.setFocusable(false);
                                        denominatorValue.setFocusableInTouchMode(false);

                                    } else {
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
                                    nominatorValue.setFocusable(false);
                                    nominatorValue.setFocusableInTouchMode(false);

                                    denominatorValue.setEnabled(false);
                                    denominatorValue.setFocusable(false);
                                    denominatorValue.setFocusableInTouchMode(false);
                                }
                            } else if (txnsncunicuData.isApprovedByMNE()) {

                                if (mPreferenceData.getPreferenceData("rejected_date") != null) {
                                    SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                                    //  SNCUActivity.mDueDateTextview.setText("Approved by M&E" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")) + "\n" + "Remarks : " + txnsncunicuData.getRemarkByMNE());
                                    SNCUActivity.mDueDateTextview.setText("Approved by M&E" + " on " + new Validation().formatDate(mPreferenceData.getPreferenceData("rejected_date")));
                                    //ResizableCustomView.doResizeTextView(SNCUActivity.mDueDateTextview, 2, "View More", true);
                                    SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                                    SNCUActivity.remarkMessage = txnsncunicuData.getRemarkByMNE();

                                }
                                nominatorValue.setEnabled(false);
                                nominatorValue.setFocusable(false);
                                nominatorValue.setFocusableInTouchMode(false);

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
                                nominatorValue.setFocusable(false);
                                nominatorValue.setFocusableInTouchMode(false);

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
                                nominatorValue.setFocusable(false);
                                nominatorValue.setFocusableInTouchMode(false);

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
                if (SCSL.getInstance().getNoOfDeliveriesListOfIndicators().contains(old_indicator_id) ||
                        SCSL.getInstance().getLiveBirthListOfIndicators().contains(old_indicator_id) ||
                        SCSL.getInstance().getInbornListOfIndicators().contains(old_indicator_id)
                        || SCSL.getInstance().getTotalAdmissionListOfIndicators().contains(old_indicator_id)) {
                    nominatorValue.setEnabled(true);
                    denominatorValue.setEnabled(false);
                    denominatorValue.setFocusable(false);
                    denominatorValue.setFocusableInTouchMode(false);

                } else {
                    nominatorValue.setEnabled(true);
                    denominatorValue.setEnabled(true);
                    SNCUActivity.remarkMessage = "";
                }
            } else {
                SNCUActivity.mDueDateTextview.setVisibility(View.VISIBLE);
                SNCUActivity.mDueDateTextview.setText(getContext().getString(R.string.due_date_expired));
                SNCUActivity.mDueDateTextview.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                SNCUActivity.remarkMessage = "";
                nominatorValue.setEnabled(false);
                nominatorValue.setFocusable(false);
                nominatorValue.setFocusableInTouchMode(false);

                denominatorValue.setEnabled(false);
                denominatorValue.setFocusable(false);
                denominatorValue.setFocusableInTouchMode(false);
            }
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

        if (preferenceData.getPreferenceBooleanData(getString(R.string.is_reset))) {
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


    private void enableOrDisableButton() {
        SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
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
                            mSubmitButton.setEnabled(true);
                            mSubmitButton.setBackgroundResource(R.color.colorAccent);
                        } else {
                            mSubmitButton.setEnabled(false);
                            mSubmitButton.setBackgroundResource(R.color.lighter_grey);
                        }
                    } else {

                        if (txnsncunicuData.isRejectedBySup()) {
                            if (Integer.parseInt(date) <= sysConfig.getSubDeadLine()) {
                                mSubmitButton.setEnabled(true);
                                mSubmitButton.setBackgroundResource(R.color.colorAccent);
                            } else {
                                mSubmitButton.setEnabled(false);
                                mSubmitButton.setBackgroundResource(R.color.lighter_grey);
                            }

                        } else if (txnsncunicuData.isRejectedByMNE()) {
                            if (Integer.parseInt(date) <= sysConfig.getMneDeadLine()) {
                                mSubmitButton.setEnabled(true);
                                mSubmitButton.setBackgroundResource(R.color.colorAccent);
                            } else {
                                mSubmitButton.setEnabled(false);
                                mSubmitButton.setBackgroundResource(R.color.lighter_grey);
                            }
                        } else {
                            mSubmitButton.setEnabled(false);
                            mSubmitButton.setBackgroundResource(R.color.lighter_grey);
                        }

                    }

                    break;
                }
            } else {
                if (Integer.parseInt(date) <= sysConfig.getDeoDeadLine()) {
                    mSubmitButton.setEnabled(true);
                    mSubmitButton.setBackgroundResource(R.color.colorAccent);
                } else {
                    mSubmitButton.setEnabled(false);
                    mSubmitButton.setBackgroundResource(R.color.lightGrey);
                }
            }


        } catch (Exception e) {
            Log.i("", "" + e);
        }

    }

}
