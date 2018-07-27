package org.sdrc.scslmobile.util;

import android.content.Context;
import android.util.Log;

import org.sdrc.scslmobile.model.ProfileEntryModel;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.Type;
import org.sdrc.scslmobile.model.realm.TypeDetail;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta
 * This is a singletone class
 */

public class SCSL {
    private static final String TAG = SCSL.class.getName();

    private static SCSL scsl;
    private Realm realm;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat sdfFull = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss Z", Locale.getDefault());

    private ProfileEntryModel profileEntryModel;

    //Maps
    private HashMap<Integer, Type> typeMap;
    private HashMap<Integer, TypeDetail> typeDetailsMap;
    private TreeMap<Integer, TimePeriod> timePeriodMap;
    private HashMap<Integer, Indicator> indicatorMap;
    private HashMap<Integer, Area> areaMap;

    private Map<Integer, Float> indicatorValuesMapForValidation2 = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForValidation3 = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForValidation5 = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForValidation7 = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForNumberOfHighRiskDeliveries = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForNumberOfNeonatalDeaths = new HashMap<>();
    private Map<Integer, Float> indicatorValuesMapForNoOfNewBornLessThan2000gms = new HashMap<>();

    private List<Integer> indicatorIdsForValidation2;
    private List<Integer> indicatorIdsForValidation3;
    private List<Integer> indicatorIdsForValidation5;
    private List<Integer> indicatorIdsForValidation6;
    private List<Integer> indicatorIdsForValidation7;
    private List<Integer> indicatorIdsNumberOfHighRiskDeliveries;
    private List<Integer> indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU;
    private List<Integer> indicatorIdsNumberOfNeonatalDeathsNumerator;
    private List<Integer> indicatorIdsNumberOfNeonatalDeathsDinominator;
    //profile entry validation Indicators
    private List<Integer> inbornListOfIndicators;
    private List<Integer> totalAdmissionListOfIndicators;
    private List<Integer> liveBirthListOfIndicators;
    private List<Integer> noOfDeliveriesListOfIndicators;
    private List<Integer> noOfProcessIndicatorsCanotSelectedIfNoLr;
    private List<Integer> noOfIntermediateListOfIndicators;
    private List<Integer> noOfNewBornLessThan2000gmsInNumerators;
    private List<Integer> noOfNewBornLessThan2000gmsInDinominators;
    private List<Integer> noOfNewBornBabiesAdmittedToTheSNCUDinominators;
    private List<Integer> noOfNewBornBabiesAdmittedToTheSNCUNumerators;

    public List<Integer> getNoOfNewBornBabiesAdmittedToTheSNCUDinominators() {
        return noOfNewBornBabiesAdmittedToTheSNCUDinominators;
    }

    public void setNoOfNewBornBabiesAdmittedToTheSNCUDinominators() {
        noOfNewBornBabiesAdmittedToTheSNCUDinominators = new ArrayList<>();
        noOfNewBornBabiesAdmittedToTheSNCUDinominators.add(29);
    }

    public List<Integer> getNoOfNewBornBabiesAdmittedToTheSNCUNumerators() {
        return noOfNewBornBabiesAdmittedToTheSNCUNumerators;
    }

    public void setNoOfNewBornBabiesAdmittedToTheSNCUNumerators() {
        noOfNewBornBabiesAdmittedToTheSNCUNumerators = new ArrayList<>();
        noOfNewBornBabiesAdmittedToTheSNCUNumerators.add(30);
    }

    public List<Integer> getNoOfNewBornLessThan2000gmsInNumerators() {
        return noOfNewBornLessThan2000gmsInNumerators;
    }

    public void setNoOfNewBornLessThan2000gmsInNumerators() {
        noOfNewBornLessThan2000gmsInNumerators = new ArrayList<>();
        noOfNewBornLessThan2000gmsInNumerators.add(29);
    }

    public List<Integer> getNoOfNewBornLessThan2000gmsInDinominators() {
        return noOfNewBornLessThan2000gmsInDinominators;
    }

    public void setNoOfNewBornLessThan2000gmsInDinominators() {
        noOfNewBornLessThan2000gmsInDinominators = new ArrayList<>();
        noOfNewBornLessThan2000gmsInDinominators.add(31);
    }

    public List<Integer> getNoOfProcessIndicatorsCanotSelectedIfNoLr() {
        return noOfProcessIndicatorsCanotSelectedIfNoLr;
    }

    public void setNoOfProcessIndicatorsCanotSelectedIfNoLr() {
        noOfProcessIndicatorsCanotSelectedIfNoLr = new ArrayList<>();
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(1);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(2);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(3);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(4);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(5);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(7);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(8);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(11);
        noOfProcessIndicatorsCanotSelectedIfNoLr.add(30);
    }

    public List<Integer> getNoOfIntermediateListOfIndicators() {
        return noOfIntermediateListOfIndicators;
    }

    public void setNoOfIntermediateListOfIndicators() {
        noOfIntermediateListOfIndicators= new ArrayList<>();
        noOfIntermediateListOfIndicators.add(23);
        noOfIntermediateListOfIndicators.add(24);
        noOfIntermediateListOfIndicators.add(25);
    }

    public List<Integer> getInbornListOfIndicators() {
        return inbornListOfIndicators;
    }

    public void setInbornListOfIndicators() {
        inbornListOfIndicators = new ArrayList<>();
        inbornListOfIndicators.add(13);
    }

    public List<Integer> getTotalAdmissionListOfIndicators() {
        return totalAdmissionListOfIndicators;
    }

    public void setTotalAdmissionListOfIndicators() {
        totalAdmissionListOfIndicators = new ArrayList<>();
        totalAdmissionListOfIndicators.add(15);
     //   totalAdmissionListOfIndicators.add(17);
        totalAdmissionListOfIndicators.add(20);
        totalAdmissionListOfIndicators.add(26);
        totalAdmissionListOfIndicators.add(27);
        totalAdmissionListOfIndicators.add(28);
        totalAdmissionListOfIndicators.add(30);
    }

    public List<Integer> getLiveBirthListOfIndicators() {
        return liveBirthListOfIndicators;
    }

    public void setLiveBirthListOfIndicators() {
        liveBirthListOfIndicators = new ArrayList<>();
        liveBirthListOfIndicators.add(23);
        liveBirthListOfIndicators.add(24);
    }

    public List<Integer> getNoOfDeliveriesListOfIndicators() {
        return noOfDeliveriesListOfIndicators;
    }

    public void setNoOfDeliveriesListOfIndicators() {
        noOfDeliveriesListOfIndicators = new ArrayList<>();
        noOfDeliveriesListOfIndicators.add(2);
        noOfDeliveriesListOfIndicators.add(3);
        noOfDeliveriesListOfIndicators.add(7);
        noOfDeliveriesListOfIndicators.add(25);
    }



    //list of indicatorTimePeriodFacility ids to remove from db at after sync
    List<Integer> extraIndicatorFacilityTimeperiodIds;

    private int lastTimePeriodId;
    private List<Integer> lastIndicatorFacilityTimePeriodIds;
    public static Validation validator;

    private String submittedDate;
    private String rejectedDate;

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }


    public static SCSL getInstance(){
        if(scsl == null){
            scsl = new SCSL();
            validator = new Validation();
        }
        return scsl;
    }

    /**
     * This following is going to return the realm object.
     * This method contains migration code too
     * @param context Application context from Activities
     * @return The realm object
     */
    public Realm getRealm(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        //If any changes in objects or properties will occur control will through exception
        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e) {
            Log.e(TAG, e.getMessage());
        }
        return realm;
    }

    /**
     * This method will close the realm object
     */
    public void closeRealm(){
        try {
            if (realm != null) {
                realm.close();
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * The folowing method will return a date from a string variable
     * Ratikanta
     * @param dateString The string variable
     * @return Date
     */
    public Date getDate(String dateString){
        if(dateString != null){
            try {
                return sdf.parse(dateString);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;

    }

    /**
     * The folowing method will return a date from a string variable
     * Ratikanta
     * @param dateString The string variable
     * @return Date
     */
    public Date getFullDate(String dateString){
        if(dateString != null){
            try {
                return sdfFull.parse(dateString);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;

    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Map<Integer, Float> getIndicatorValuesMapForValidation2() {
        return indicatorValuesMapForValidation2;
    }

    public void setIndicatorValuesMapForValidation2(Map<Integer, Float> indicatorValuesMapForValidation2) {
        this.indicatorValuesMapForValidation2 = indicatorValuesMapForValidation2;
    }

    public Map<Integer, Float> getIndicatorValuesMapForValidation3() {
        return indicatorValuesMapForValidation3;
    }

    public void setIndicatorValuesMapForValidation3(Map<Integer, Float> indicatorValuesMapForValidation3) {
        this.indicatorValuesMapForValidation3 = indicatorValuesMapForValidation3;
    }

    public Map<Integer, Float> getIndicatorValuesMapForValidation5() {
        return indicatorValuesMapForValidation5;
    }

    public void setIndicatorValuesMapForValidation5(Map<Integer, Float> indicatorValuesMapForValidation5) {
        this.indicatorValuesMapForValidation5 = indicatorValuesMapForValidation5;
    }

    public Map<Integer, Float> getIndicatorValuesMapForValidation7() {
        return indicatorValuesMapForValidation7;
    }

    public void setIndicatorValuesMapForValidation7(Map<Integer, Float> indicatorValuesMapForValidation7) {
        this.indicatorValuesMapForValidation7 = indicatorValuesMapForValidation7;
    }

    public Map<Integer, Float> getIndicatorValuesMapForNumberOfHighRiskDeliveries() {
        return indicatorValuesMapForNumberOfHighRiskDeliveries;
    }

    public void setIndicatorValuesMapForNumberOfHighRiskDeliveries(Map<Integer, Float> indicatorValuesMapForNumberOfHighRiskDeliveries) {
        this.indicatorValuesMapForNumberOfHighRiskDeliveries = indicatorValuesMapForNumberOfHighRiskDeliveries;
    }

    public Map<Integer, Float> getIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU() {
        return indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU;
    }

    public void setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(Map<Integer, Float> indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU) {
        this.indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU = indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU;
    }

    public Map<Integer, Float> getIndicatorValuesMapForNumberOfNeonatalDeaths() {
        return indicatorValuesMapForNumberOfNeonatalDeaths;
    }

    public Map<Integer, Float> getIndicatorValuesMapForNoOfNewBornLessThan2000gms() {
        return indicatorValuesMapForNoOfNewBornLessThan2000gms;
    }

    public void setIndicatorValuesMapForNoOfNewBornLessThan2000gms(Map<Integer, Float> indicatorValuesMapForNoOfNewBornLessThan2000gms) {
        this.indicatorValuesMapForNoOfNewBornLessThan2000gms = indicatorValuesMapForNoOfNewBornLessThan2000gms;
    }

    public void setIndicatorValuesMapForNumberOfNeonatalDeaths(Map<Integer, Float> indicatorValuesMapForNumberOfNeonatalDeaths) {
        this.indicatorValuesMapForNumberOfNeonatalDeaths = indicatorValuesMapForNumberOfNeonatalDeaths;
    }

    public List<Integer> getIndicatorIdsForValidation2() {
        return indicatorIdsForValidation2;
    }

    public void setIndicatorIdsForValidation2() {
        indicatorIdsForValidation2 = new ArrayList<>();
        indicatorIdsForValidation2.add(2);
        indicatorIdsForValidation2.add(3);
        indicatorIdsForValidation2.add(7);
        indicatorIdsForValidation2.add(25);
    }

    public List<Integer> getIndicatorIdsForValidation3() {
        return indicatorIdsForValidation3;
    }

    public void setIndicatorIdsForValidation3() {
        indicatorIdsForValidation3 = new ArrayList<>();
        indicatorIdsForValidation3.add(23);
        indicatorIdsForValidation3.add(24);
    }

    public List<Integer> getIndicatorIdsForValidation5() {
        return indicatorIdsForValidation5;
    }

    public void setIndicatorIdsForValidation5() {
        indicatorIdsForValidation5 = new ArrayList<>();
        indicatorIdsForValidation5.add(26);
        indicatorIdsForValidation5.add(27);
        indicatorIdsForValidation5.add(28);
    }

    public List<Integer> getIndicatorIdsForValidation6() {
        return indicatorIdsForValidation6;
    }

    //where numerator can not be greater than denominator
    public void setIndicatorIdsForValidation6() {
        indicatorIdsForValidation6 = new ArrayList<>();
        indicatorIdsForValidation6.add(2);
        indicatorIdsForValidation6.add(3);
        indicatorIdsForValidation6.add(4);
        indicatorIdsForValidation6.add(7);
        indicatorIdsForValidation6.add(8);
        indicatorIdsForValidation6.add(9);
        indicatorIdsForValidation6.add(10);
        indicatorIdsForValidation6.add(12);
        indicatorIdsForValidation6.add(13);
        indicatorIdsForValidation6.add(15);
        indicatorIdsForValidation6.add(16);
        indicatorIdsForValidation6.add(17);
        indicatorIdsForValidation6.add(18);
        indicatorIdsForValidation6.add(19);
        indicatorIdsForValidation6.add(20);
        indicatorIdsForValidation6.add(21);
        indicatorIdsForValidation6.add(22);
        indicatorIdsForValidation6.add(24);
        indicatorIdsForValidation6.add(25);
        indicatorIdsForValidation6.add(26);
        indicatorIdsForValidation6.add(27);
        indicatorIdsForValidation6.add(28);
        indicatorIdsForValidation6.add(29);
        indicatorIdsForValidation6.add(30);
        indicatorIdsForValidation6.add(31);
        indicatorIdsForValidation6.add(32);
        indicatorIdsForValidation6.add(33);
    }

    public List<Integer> getIndicatorIdsForValidation7() {
        return indicatorIdsForValidation7;
    }

    public void setIndicatorIdsForValidation7() {
        indicatorIdsForValidation7 = new ArrayList<>();
        indicatorIdsForValidation7.add(13);
        indicatorIdsForValidation7.add(15);
      //  indicatorIdsForValidation7.add(17);
        indicatorIdsForValidation7.add(20);
    }

    public List<Integer> getIndicatorIdsNumberOfHighRiskDeliveries() {
        return indicatorIdsNumberOfHighRiskDeliveries;
    }

    public void setIndicatorIdsNumberOfHighRiskDeliveries() {
        indicatorIdsNumberOfHighRiskDeliveries = new ArrayList<>();
        indicatorIdsNumberOfHighRiskDeliveries.add(4);
        indicatorIdsNumberOfHighRiskDeliveries.add(5);
    }

    public List<Integer> getIndicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU() {
        return indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU;
    }

    public void setIndicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU() {
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU = new ArrayList<>();
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(15);
      //  indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(17);
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(20);
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(26);
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(27);
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(28);
        indicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU.add(30);
    }

    public List<Integer> getIndicatorIdsNumberOfNeonatalDeathsNumerator() {
        return indicatorIdsNumberOfNeonatalDeathsNumerator;
    }

    public void setIndicatorIdsNumberOfNeonatalDeathsNumerator() {
        indicatorIdsNumberOfNeonatalDeathsNumerator = new ArrayList<>();
        indicatorIdsNumberOfNeonatalDeathsNumerator.add(23);
        indicatorIdsNumberOfNeonatalDeathsNumerator.add(26);
    }

    public List<Integer> getIndicatorIdsNumberOfNeonatalDeathsDinominator() {
        return indicatorIdsNumberOfNeonatalDeathsDinominator;
    }

    public void setIndicatorIdsNumberOfNeonatalDeathsDinominator() {
        indicatorIdsNumberOfNeonatalDeathsDinominator = new ArrayList<>();
        indicatorIdsNumberOfNeonatalDeathsDinominator.add(16);
        indicatorIdsNumberOfNeonatalDeathsDinominator.add(18);
        indicatorIdsNumberOfNeonatalDeathsDinominator.add(21);
        //indicatorIdsNumberOfNeonatalDeathsDinominator.add(22);
    }

    public List<Integer> getExtraIndicatorFacilityTimeperiodIds() {
        return extraIndicatorFacilityTimeperiodIds;
    }

    public void setExtraIndicatorFacilityTimeperiodIds(List<Integer> extraIndicatorFacilityTimeperiodIds) {
        this.extraIndicatorFacilityTimeperiodIds = extraIndicatorFacilityTimeperiodIds;
    }

    /**
     * The folowing method will return a string from a date variable
     * Ratikanta
     * @param date The given date
     * @return String format of a date
     */
    public String getFullDateString(Date date){
        if(date != null){
            return sdfFull.format(date);
        }
        return null;

    }

    public HashMap<Integer, Type> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(HashMap<Integer, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public HashMap<Integer, TypeDetail> getTypeDetailsMap() {
        return typeDetailsMap;
    }

    public void setTypeDetailsMap(HashMap<Integer, TypeDetail> typeDetailsMap) {
        this.typeDetailsMap = typeDetailsMap;
    }

    public TreeMap<Integer, TimePeriod> getTimePeriodMap() {
        return timePeriodMap;
    }

    public void setTimePeriodMap(TreeMap<Integer, TimePeriod> timePeriodMap) {
        this.timePeriodMap = timePeriodMap;
    }

    public HashMap<Integer, Indicator> getIndicatorMap() {
        return indicatorMap;
    }

    public void setIndicatorMap(HashMap<Integer, Indicator> indicatorMap) {
        this.indicatorMap = indicatorMap;
    }

    public HashMap<Integer, Area> getAreaMap() {
        return areaMap;
    }

    public void setAreaMap(HashMap<Integer, Area> areaMap) {
        this.areaMap = areaMap;
    }

    public int getLastTimePeriodId() {
        return lastTimePeriodId;
    }

    public void setLastTimePeriodId(int lastTimePeriodId) {
        this.lastTimePeriodId = lastTimePeriodId;
    }

    public List<Integer> getLastIndicatorFacilityTimePeriodIds() {
        return lastIndicatorFacilityTimePeriodIds;
    }

    public void setLastIndicatorFacilityTimePeriodIds(List<Integer> lastIndicatorFacilityTimePeriodIds) {
        this.lastIndicatorFacilityTimePeriodIds = lastIndicatorFacilityTimePeriodIds;
    }

    public ProfileEntryModel getProfileEntryModel() {
        return profileEntryModel;
    }

    public void setProfileEntryModel(ProfileEntryModel profileEntryModel) {
        this.profileEntryModel = profileEntryModel;
    }
}
