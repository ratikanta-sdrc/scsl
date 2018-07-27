package org.sdrc.scslmobile.service;

import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.MSTEngagementScore;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.model.realm.Type;
import org.sdrc.scslmobile.model.realm.TypeDetail;
import org.sdrc.scslmobile.model.realm.User;
import org.sdrc.scslmobile.model.webservice.AreaModel;
import org.sdrc.scslmobile.model.webservice.IndicatorFacilityTimeperiodMappingModel;
import org.sdrc.scslmobile.model.webservice.IndicatorModel;
import org.sdrc.scslmobile.model.webservice.LoginDataModel;
import org.sdrc.scslmobile.model.webservice.MSTEngagementScoreModel;
import org.sdrc.scslmobile.model.webservice.MasterDataModel;
import org.sdrc.scslmobile.model.webservice.TimePeriodModel;
import org.sdrc.scslmobile.model.webservice.TypeDetailModel;
import org.sdrc.scslmobile.model.webservice.TypeModel;
import org.sdrc.scslmobile.model.webservice.UserModel;
import org.sdrc.scslmobile.util.SCSL;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.http.Body;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in), created on 05-05-2017.
 * This class will implement some methods of LoginService
 */

public class LoginServiceImpl implements LoginService {

    private Realm realm;
    private MasterDataModel masterDataModel;
    private String username;
    private String password;
    private boolean isDEO;

    @Override
    public Call<MasterDataModel> MasterDataModel(@Body LoginDataModel loginDataModel) {
        return null;
    }

    @Override
    public Map<String, Boolean> insertData(MasterDataModel masterDataModel, boolean isFirstUser, Realm realm, String username, String password) {
        this.realm = realm;
        this.masterDataModel = masterDataModel;
        this.username = username;
        this.password = password;
        if(isFirstUser){
            return insertData();
        }else{
            return insertDataForSecondUser();
        }
    }

    /**
     * This method will insert data into database and send the control to respective facility
     */
    private Map<String, Boolean> insertData() {

        try {
            realm.beginTransaction();
            if (masterDataModel != null) {
                if (masterDataModel.getTypeDetailModels() != null) {

                    for (TypeDetailModel iModel : masterDataModel.getTypeDetailModels()) {

                        TypeDetail iObject = realm.createObject(TypeDetail.class, iModel.getTypeDetailId());

                        iObject.setTypeDetail(iModel.getTypeDetail());
                        iObject.setDescription(iModel.getDescription());
                        iObject.setTypeId(iModel.getTypeId());

                    }
                }

                //Inserting data in type and type details table
                if (masterDataModel.getTypeModels() != null) {

                    for (TypeModel model : masterDataModel.getTypeModels()) {
                        Type object = realm.createObject(Type.class, model.getTypeId());
                        object.setTypeName(model.getTypeName());
                        object.setDescription(model.getDescription());
                    }

                }


                //Inserting into area table
                if (masterDataModel.getAreaModels() != null) {
                    for (AreaModel model : masterDataModel.getAreaModels()) {
                        Area object = realm.createObject(Area.class, model.getAreaId());
                        object.setAreaName(model.getAreaName());
                        object.setFacilitySize(model.getFacilitySize());
                        object.setFacilityType(model.getFacilityType());
                        object.setLevel(model.getLevel());
                        object.setParentAreaId(model.getParentAreaId());
                        object.setWave(model.getWave() == 0 ? null : model.getWave());
                        object.setHasLR(model.isHasLR());
                    }
                }


                //Inserting data into user table
                if (masterDataModel.getUserModel() != null) {
                    UserModel model = masterDataModel.getUserModel();
                    User object = new User();
                    object.setName(model.getName());
                    object.setUsername(username);
                    object.setPassword(password);
                    object.setDEO(model.getIsDEO());
                    isDEO = model.getIsDEO();
                    String areaIds = "";
                    int count = 0;
                    for (int i : model.getAreaIds()) {
                        areaIds += i;
                        count++;
                        if (count != model.getAreaIds().size()) {
                            areaIds += ",";
                        }
                    }
                    object.setAreasIds(areaIds);
                    realm.copyToRealm(object);

                }


                if (masterDataModel.getIndicatorModels() != null) {
                    for (IndicatorModel model : masterDataModel.getIndicatorModels()) {
                        Indicator object = realm.createObject(Indicator.class, model.getIndicatorId());
                        object.setCoreArea(model.getCoreArea());
                        object.setDenominator(model.getDenominator());
                        object.setExceptionRule(model.getExceptionRule());
                        object.setIndicatorName(model.getIndicatorName());
                        object.setIndicatorType(model.getIndicatorType());
                        object.setIntermediateIndicatorId(model.getIntermediateIndicatorId());
                        object.setNumerator(model.getNumerator());
                        object.setOutcomeIndicatorId(model.getOutcomeIndicatorId());
                        object.setProcessIndicatorId(model.getProcessIndicatorId());
                        object.setReqired(model.getIsReqired());
                        object.setIndicatorOrder(model.getIndicatorOrder());
                        object.setLr(model.getIsLr());
                        object.setProfile(model.getIsProfile());
                    }
                }

                if (masterDataModel.getTimePeriodModels() != null) {
                    for (TimePeriodModel model : masterDataModel.getTimePeriodModels()) {
                        TimePeriod object = realm.createObject(TimePeriod.class, model.getTimePeriodId());
                        object.setEndDate(SCSL.getInstance().getDate(model.getEndDate()));
                        object.setPeriodicity(model.getPeriodicity());
                        object.setStartDate(SCSL.getInstance().getDate(model.getStartDate()));
                        object.setTimePeriod(model.getTimePeriod());
                        object.setWave(model.getWave());
                    }
                }

                if (masterDataModel.getIndicatorFacilityTimeperiodMappingModels() != null) {
                    for (IndicatorFacilityTimeperiodMappingModel model : masterDataModel.getIndicatorFacilityTimeperiodMappingModels()) {
                        IndicatorFacilityTimeperiodMapping object = realm.createObject(IndicatorFacilityTimeperiodMapping.class, model.getIndFacilityTpId());
                        object.setCreatedDate(SCSL.getInstance().getFullDate(model.getCreatedDate()));
                        object.setFacility(model.getFacilityId());
                        object.setIndicator(model.getIndicatorId());
                        object.setTimePeriod(model.getTimePeriodId());
                    }
                }

                if (masterDataModel.getmSTEngagementScoreModels() != null) {
                    for (MSTEngagementScoreModel model : masterDataModel.getmSTEngagementScoreModels()) {
                        MSTEngagementScore object = realm.createObject(MSTEngagementScore.class, model.getMstEngagementScoreId());
                        object.setDefinition(model.getDefinition());
                        object.setProgress(model.getProgress());
                        object.setScore(model.getScore());
                    }
                }

                //setting latest patient number
                SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
                if (sysConfig == null) {
                    SysConfig sysConfigNewRecord = realm.createObject(SysConfig.class);
                    sysConfigNewRecord.setLastSyncDate(SCSL.getInstance().getFullDate(masterDataModel.getLastSyncDate()));
                    sysConfigNewRecord.setDeoDeadLine(masterDataModel.getDeoDeadLine());
                    sysConfigNewRecord.setSubDeadLine(masterDataModel.getSubDeadLine());
                    sysConfigNewRecord.setMneDeadLine(masterDataModel.getMneDeadLine());
                }else{
                    sysConfig.setLastSyncDate(SCSL.getInstance().getFullDate(masterDataModel.getLastSyncDate()));
                    sysConfig.setDeoDeadLine(masterDataModel.getDeoDeadLine());
                    sysConfig.setSubDeadLine(masterDataModel.getSubDeadLine());
                    sysConfig.setMneDeadLine(masterDataModel.getMneDeadLine());
                }
            }
            realm.commitTransaction();
            Map<String, Boolean> map = new HashMap<>();
            map.put(null, isDEO);
            return map;
        }catch (Exception e){
            realm.commitTransaction();
            Map<String, Boolean> map = new HashMap<>();
            map.put("Exception in inserting data in database, message: " + e.getMessage(), null);
            return map;
        }
            
    }



    /**
     * This method will insert data into database and send the control to respective facility
     */
    private Map<String, Boolean> insertDataForSecondUser() {

        try {
            realm.beginTransaction();
            if (masterDataModel != null) {
                if (masterDataModel.getTypeDetailModels() != null) {
                    for (TypeDetailModel iModel : masterDataModel.getTypeDetailModels()) {

                        TypeDetail iObject = realm.createObject(TypeDetail.class, iModel.getTypeDetailId());

                        iObject.setTypeDetail(iModel.getTypeDetail());
                        iObject.setDescription(iModel.getDescription());
                        iObject.setTypeId(iModel.getTypeId());

                    }
                }

                //Inserting data in type and type details table
                if (masterDataModel.getTypeModels() != null) {

                    for (TypeModel model : masterDataModel.getTypeModels()) {
                        Type object = realm.createObject(Type.class, model.getTypeId());
                        object.setTypeName(model.getTypeName());
                        object.setDescription(model.getDescription());
                    }

                }


                //Inserting into area table
                if (masterDataModel.getAreaModels() != null) {
                    for (AreaModel model : masterDataModel.getAreaModels()) {
                        Area object = realm.createObject(Area.class, model.getAreaId());
                        object.setAreaName(model.getAreaName());
                        object.setFacilitySize(model.getFacilitySize());
                        object.setFacilityType(model.getFacilityType());
                        object.setLevel(model.getLevel());
                        object.setParentAreaId(model.getParentAreaId());
                        object.setWave(model.getWave() == 0 ? null : model.getWave());
                    }
                }


                //Inserting data into user table
                if (masterDataModel.getUserModel() != null) {
                    UserModel model = masterDataModel.getUserModel();
                    User object = realm.where(User.class).findFirst();
                    object.setName(model.getName());
                    object.setUsername(username);
                    object.setPassword(password);
                    object.setDEO(model.getIsDEO());
                    isDEO = model.getIsDEO();
                    String areaIds = "";
                    int count = 0;
                    for (int i : model.getAreaIds()) {
                        areaIds += i;
                        count++;
                        if (count != model.getAreaIds().size()) {
                            areaIds += ",";
                        }
                    }
                    object.setAreasIds(areaIds);

                }


                if (masterDataModel.getIndicatorModels() != null) {
                    for (IndicatorModel model : masterDataModel.getIndicatorModels()) {
                        Indicator object = realm.createObject(Indicator.class, model.getIndicatorId());
                        object.setCoreArea(model.getCoreArea());
                        object.setDenominator(model.getDenominator());
                        object.setExceptionRule(model.getExceptionRule());
                        object.setIndicatorName(model.getIndicatorName());
                        object.setIndicatorType(model.getIndicatorType());
                        object.setIntermediateIndicatorId(model.getIntermediateIndicatorId());
                        object.setNumerator(model.getNumerator());
                        object.setOutcomeIndicatorId(model.getOutcomeIndicatorId());
                        object.setProcessIndicatorId(model.getProcessIndicatorId());
                        object.setReqired(model.getIsReqired());
                    }
                }

                if (masterDataModel.getTimePeriodModels() != null) {
                    for (TimePeriodModel model : masterDataModel.getTimePeriodModels()) {
                        TimePeriod object = realm.createObject(TimePeriod.class, model.getTimePeriodId());
                        object.setEndDate(SCSL.getInstance().getFullDate(model.getEndDate()));
                        object.setPeriodicity(model.getPeriodicity());
                        object.setStartDate(SCSL.getInstance().getFullDate(model.getStartDate()));
                        object.setTimePeriod(model.getTimePeriod());
                        object.setWave(model.getWave());
                    }
                }

                if (masterDataModel.getIndicatorFacilityTimeperiodMappingModels() != null) {
                    for (IndicatorFacilityTimeperiodMappingModel model : masterDataModel.getIndicatorFacilityTimeperiodMappingModels()) {
                        IndicatorFacilityTimeperiodMapping object = realm.createObject(IndicatorFacilityTimeperiodMapping.class, model.getIndFacilityTpId());
                        object.setCreatedDate(SCSL.getInstance().getFullDate(model.getCreatedDate()));
                        object.setFacility(model.getFacilityId());
                        object.setIndicator(model.getIndicatorId());
                        object.setTimePeriod(model.getTimePeriodId());
                    }
                }

                if (masterDataModel.getmSTEngagementScoreModels() != null) {
                    for (MSTEngagementScoreModel model : masterDataModel.getmSTEngagementScoreModels()) {
                        MSTEngagementScore object = realm.createObject(MSTEngagementScore.class, model.getMstEngagementScoreId());
                        object.setDefinition(model.getDefinition());
                        object.setProgress(model.getProgress());
                        object.setScore(model.getScore());
                    }
                }

                //setting latest patient number
                SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
                if (sysConfig == null) {
                    SysConfig sysConfigNewRecord = realm.createObject(SysConfig.class);
                    sysConfigNewRecord.setLastSyncDate(SCSL.getInstance().getFullDate(masterDataModel.getLastSyncDate()));
                    sysConfigNewRecord.setDeoDeadLine(masterDataModel.getDeoDeadLine());
                    sysConfigNewRecord.setSubDeadLine(masterDataModel.getSubDeadLine());
                    sysConfigNewRecord.setMneDeadLine(masterDataModel.getMneDeadLine());
                }else{
                    sysConfig.setLastSyncDate(SCSL.getInstance().getFullDate(masterDataModel.getLastSyncDate()));
                    sysConfig.setDeoDeadLine(masterDataModel.getDeoDeadLine());
                    sysConfig.setSubDeadLine(masterDataModel.getSubDeadLine());
                    sysConfig.setMneDeadLine(masterDataModel.getMneDeadLine());
                }
            }
            realm.commitTransaction();
            Map<String, Boolean> map = new HashMap<>();
            map.put(null, isDEO);
            return map;
        }catch (Exception e){
            realm.commitTransaction();
            Map<String, Boolean> map = new HashMap<>();
            map.put("Exception in inserting data in database, message: " + e.getMessage(), null);
            return map;
        }
    }


}