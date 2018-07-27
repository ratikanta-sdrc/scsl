package org.sdrc.scslmobile.service;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.activity.SplashScreenActivity;
import org.sdrc.scslmobile.customclass.SyncMessageData;
import org.sdrc.scslmobile.fragment.IntermediateFragment;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.Indicator;
import org.sdrc.scslmobile.model.realm.IndicatorFacilityTimeperiodMapping;
import org.sdrc.scslmobile.model.realm.MSTEngagementScore;
import org.sdrc.scslmobile.model.realm.SysConfig;
import org.sdrc.scslmobile.model.realm.TXNEngagementScore;
import org.sdrc.scslmobile.model.realm.TXNSNCUNICUData;
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
import org.sdrc.scslmobile.model.webservice.SyncModel;
import org.sdrc.scslmobile.model.webservice.SyncResult;
import org.sdrc.scslmobile.model.webservice.TXNEngagementScoreModel;
import org.sdrc.scslmobile.model.webservice.TXNSNCUDataModel;
import org.sdrc.scslmobile.model.webservice.TimePeriodModel;
import org.sdrc.scslmobile.model.webservice.TypeDetailModel;
import org.sdrc.scslmobile.model.webservice.TypeModel;
import org.sdrc.scslmobile.model.webservice.UserModel;
import org.sdrc.scslmobile.util.Constant;
import org.sdrc.scslmobile.util.PreferenceData;
import org.sdrc.scslmobile.util.SCSL;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Ratikanta Pradhan (ratikanta@sdrc.co.in) on 05-05-2017.
 * The impl class will help to gather data for syncing
 * @author Subhadarshani
 */

public class SyncServiceImpl {

    public SyncModel getSyncmodel(Realm realm) {
        SyncModel syncModel = new SyncModel();
        User user = realm.where(User.class).findFirst();
        SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
        LoginDataModel loginDataModel = new LoginDataModel();
        loginDataModel.setUsername(user.getUsername());
        loginDataModel.setPassword(user.getPassword());
        loginDataModel.setLastSyncDate(SCSL.getInstance().getFullDateString(sysConfig.getLastSyncDate()));
        syncModel.setLoginDataModel(loginDataModel);
        syncModel.setDeo(user.isDEO() ? 1 : 0);
        if (user.isDEO()) {

            List<IndicatorFacilityTimeperiodMappingModel> models = new ArrayList<>();

            RealmResults<IndicatorFacilityTimeperiodMapping> mappings = realm.where(
                    IndicatorFacilityTimeperiodMapping.class)
//                    .greaterThanOrEqualTo(Constant.Table.IndicatorFacilityTimeperiodMapping.CREATED_DATE, sysConfig.getLastSyncDate())


                    .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.IS_NEW, true)
                    .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.TIME_PERIOD_ID, SCSL.getInstance().getLastTimePeriodId())
                    .findAll();

            for (IndicatorFacilityTimeperiodMapping mapping : mappings) {
                IndicatorFacilityTimeperiodMappingModel model = new IndicatorFacilityTimeperiodMappingModel();
                model.setIndFacilityTpId(mapping.getIndFacilityTpId());
                model.setFacilityId(mapping.getFacility());
                model.setIndicatorId(mapping.getIndicator());
                model.setTimePeriodId(mapping.getTimePeriod());
                model.setCreatedDate(SCSL.getInstance().getFullDateString(mapping.getCreatedDate()));
                models.add(model);
            }

            syncModel.setMappingModels(models);

            List<TXNSNCUDataModel> txnsncuDataModels = new ArrayList<>();
            RealmQuery<TXNSNCUNICUData> query = realm.where(TXNSNCUNICUData.class);
            query = query.equalTo(Constant.Table.TXNSNCUNICUData.IS_SYNCED, false);
            query.beginGroup();
            for (int id : SCSL.getInstance().getLastIndicatorFacilityTimePeriodIds()) {
                query = query.or().equalTo(Constant.Table.TXNSNCUNICUData.IFTM, id);
            }
            query.endGroup();

            RealmResults<TXNSNCUNICUData> txnsncunicuDatas = query.findAll();
            for (TXNSNCUNICUData txnsncunicuData : txnsncunicuDatas) {
                TXNSNCUDataModel dataModel = new TXNSNCUDataModel();
                dataModel.setId(txnsncunicuData.getTxnIndicatorId());
                dataModel.setNumeratorValue(txnsncunicuData.getNumeratorValue() != null ?
                        txnsncunicuData.getNumeratorValue() : null);
                dataModel.setDenominatorValue(txnsncunicuData.getDenominatorValue() != null ?
                        txnsncunicuData.getDenominatorValue() : null);
                dataModel.setPercentage(txnsncunicuData.getPercentage() != null ?
                        txnsncunicuData.getPercentage() : null);
                dataModel.setIftid(txnsncunicuData.getIndicatorFacilityTimeperiodMapping());
                dataModel.setCreatedDate(SCSL.getInstance().getFullDateString(txnsncunicuData.getCreatedDate()));
                dataModel.setRejectedBySup(txnsncunicuData.isRejectedBySup() ? 1 : 0);
                dataModel.setRejectedByMNE(txnsncunicuData.isRejectedByMNE() ? 1 : 0);
                //new
                dataModel.setDescription(txnsncunicuData.getDescription());
                txnsncuDataModels.add(dataModel);

            }
            syncModel.setTxnsncuDataModels(txnsncuDataModels);
        } else {
            List<TXNEngagementScoreModel> txnEngagementScoreModels = new ArrayList<>();
            RealmResults<TXNEngagementScore> engagementScores = realm.where(TXNEngagementScore.class)
                    .equalTo(Constant.Table.TXNEngagementScore.IS_SYNCED, false)
                    .findAll();

            for (TXNEngagementScore txnEngagementScore : engagementScores) {
                TXNEngagementScoreModel txnEngagementScoreModel = new TXNEngagementScoreModel();
                txnEngagementScoreModel.setEngagementScoreId(txnEngagementScore.getMstEngagementScoreId());
                txnEngagementScoreModel.setAreaId(txnEngagementScore.getFacility());
                txnEngagementScoreModel.setTimePeriodId(txnEngagementScore.getTimePeriod());
                txnEngagementScoreModel.setCreatedDate(SCSL.getInstance().getFullDateString(txnEngagementScore.getCreatedDate()));
                txnEngagementScoreModels.add(txnEngagementScoreModel);
            }
            syncModel.setTxnEngagementScoreModels(txnEngagementScoreModels);
        }
       syncModel.setApiVersion(Constant.API_VERSION);
        return syncModel;
    }

    /**
     * This method is going to return the message which user is going to see after sync
     *
     * @param syncResult The input after sync
     * @return The message
     * Ratikanta
     */
    public List<SyncMessageData> getSyncMessage(SyncResult syncResult, SyncModel syncModel, Realm realm, Context context) {
        List<SyncMessageData> syncMessageDataList = new ArrayList<>();
        try {
            realm.beginTransaction();

            //Sync result could be null
            if (syncModel != null && syncResult != null) {
                //It is not null
                if (syncResult.getRejectedDate() != null) {
                    //SCSL.getInstance().setRejectedDate(syncResult.getRejectedDate());
                    new PreferenceData(context).setPreferenceData(syncResult.getRejectedDate(),"rejected_date");
                }
                if (syncResult.getSubmittedDate() != null) {
                    //SCSL.getInstance().setSubmittedDate(syncResult.getSubmittedDate());
                    new PreferenceData(context).setPreferenceData(syncResult.getSubmittedDate(),"submitted_date");
                }
                List<TXNSNCUDataModel> txnsncuDataModels = syncResult.getTxnsncuDataModels();
                List<TXNEngagementScoreModel> txnEngagementScoreModels = syncResult.getTxnEngagementScoreModels();
                List<IndicatorFacilityTimeperiodMappingModel> mappingModels = syncResult.getMappingModels();
                if ((syncModel.getTxnsncuDataModels() != null && syncModel.getTxnsncuDataModels().size() > 0) ||
                        syncResult.getRejectedBySup() == 1 || syncResult.getRejectedByMne() == 1) {
                    RealmQuery<TXNSNCUNICUData> query = realm.where(TXNSNCUNICUData.class);
                    setLastTimePeriodId(realm);
                    setLastIndicatorFacilityTimePeriodIds(realm);
                    for (int id : SCSL.getInstance().getLastIndicatorFacilityTimePeriodIds()) {
                        query = query.or().equalTo(Constant.Table.TXNSNCUNICUData.IFTM, id);
                    }
                    RealmResults<TXNSNCUNICUData> txnsncunicuDatas = query.findAll();
                    for (TXNSNCUNICUData txnsncunicuData : txnsncunicuDatas) {
                        if (syncResult.getRejectedBySup() == 1) {
                            txnsncunicuData.setRejectedBySup(true);
                            txnsncunicuData.setRemarkBySup(syncResult.getRemarkSup());
                        } else {
                            txnsncunicuData.setRejectedBySup(false);
                        }
                        if (syncResult.getRejectedByMne() == 1) {
                            txnsncunicuData.setRejectedByMNE(true);
                            txnsncunicuData.setRemarkByMNE(syncResult.getRemarkMne());

                        } else {
                            txnsncunicuData.setRejectedByMNE(false);

                        }
                        //new
                        if (syncResult.getAprovedBySup() == 1) {
                            txnsncunicuData.setApprovedBySup(true);
                            txnsncunicuData.setRemarkBySup(syncResult.getRemarkSup());
                        } else {
                            txnsncunicuData.setApprovedBySup(false);
                        }
                        if (syncResult.getApproveByMne() == 1) {
                            txnsncunicuData.setApprovedByMNE(true);
                            txnsncunicuData.setRemarkByMNE(syncResult.getRemarkMne());
                        } else {
                            txnsncunicuData.setApprovedByMNE(false);
                        }
                        if (syncResult.getAutoApproved() == 1) {
                            txnsncunicuData.setAutoApproved(true);
                        } else {
                            txnsncunicuData.setAutoApproved(false);
                        }
                        txnsncunicuData.setSynced(true);
                        txnsncunicuData.setHasError(false);
                        if (txnsncuDataModels != null && txnsncuDataModels.size() > 0) {
                            for (TXNSNCUDataModel dataModel : txnsncuDataModels) {
                                if (dataModel.getId() == txnsncunicuData.getTxnIndicatorId()) {
                                    txnsncunicuData.setHasError(true);
                                    break;
                                }
                            }
                        }
                    }
                } else if ((syncModel.getTxnsncuDataModels() != null && syncModel.getTxnsncuDataModels().size() > 0) ||
                        syncResult.getRejectedBySup() == 0 || syncResult.getRejectedByMne() == 0) {
                    RealmQuery<TXNSNCUNICUData> query = realm.where(TXNSNCUNICUData.class);
                    for (int id : SCSL.getInstance().getLastIndicatorFacilityTimePeriodIds()) {
                        query = query.or().equalTo(Constant.Table.TXNSNCUNICUData.IFTM, id);
                    }
                    RealmResults<TXNSNCUNICUData> txnsncunicuDatas = query.findAll();
                    for (TXNSNCUNICUData txnsncunicuData : txnsncunicuDatas) {
                        if (syncResult.getRejectedBySup() == 0) {
                            txnsncunicuData.setRejectedBySup(false);
                            txnsncunicuData.setRemarkBySup(syncResult.getRemarkSup());
                        } else {
                            txnsncunicuData.setRejectedBySup(true);
                        }
                        if (syncResult.getRejectedByMne() == 0) {
                            txnsncunicuData.setRejectedByMNE(false);
                            txnsncunicuData.setRemarkByMNE(syncResult.getRemarkMne());
                        } else {
                            txnsncunicuData.setRejectedByMNE(true);
                        }
                        //new
                        if (syncResult.getAprovedBySup() == 0) {
                            txnsncunicuData.setApprovedBySup(false);
                            txnsncunicuData.setRemarkBySup(syncResult.getRemarkSup());
                        } else {
                            txnsncunicuData.setApprovedBySup(true);
                        }
                        if (syncResult.getApproveByMne() == 0) {
                            txnsncunicuData.setApprovedByMNE(false);
                            txnsncunicuData.setRemarkByMNE(syncResult.getRemarkMne());
                        } else {
                            txnsncunicuData.setApprovedByMNE(true);
                        }
                        if (syncResult.getAutoApproved() == 1) {
                            txnsncunicuData.setAutoApproved(true);
                        } else {
                            txnsncunicuData.setAutoApproved(false);
                        }

                        txnsncunicuData.setSynced(true);
                        txnsncunicuData.setHasError(false);
                        if (txnsncuDataModels != null && txnsncuDataModels.size() > 0) {
                            for (TXNSNCUDataModel dataModel : txnsncuDataModels) {
                                if (dataModel.getId() == txnsncunicuData.getTxnIndicatorId()) {
                                    txnsncunicuData.setHasError(true);
                                    break;
                                }
                            }
                        }
                    }
                }

                //Make engagement score work
                if (syncModel.getTxnEngagementScoreModels() != null && syncModel.getTxnEngagementScoreModels().size() > 0) {
                    RealmResults<TXNEngagementScore> result = realm.where(TXNEngagementScore.class)
                            .equalTo(Constant.Table.TXNEngagementScore.IS_SYNCED, false).findAll();

                    for (TXNEngagementScore score : result) {
                        score.setSynced(true);
                    }
                }


                //IndicatorFacilityTimeperiodmapping data
                if (mappingModels != null) {
                    for (IndicatorFacilityTimeperiodMappingModel model : mappingModels) {
                        SyncMessageData syncMessageData = new SyncMessageData();
                        IndicatorFacilityTimeperiodMapping mapping = realm.where(
                                IndicatorFacilityTimeperiodMapping.class)
//                        .findFirst();
                                .equalTo(
                                        Constant.Table.IndicatorFacilityTimeperiodMapping.ID,
                                        model.getIndFacilityTpId())
                                .findFirst();

                        Indicator indicator = realm.where(Indicator.class)
                                .equalTo(Constant.Table.Indicator.ID, mapping.getIndicator())
                                .findFirst();

                        mapping.deleteFromRealm();
                        syncMessageData.setMsgType("Indicator time period mapping");
                        syncMessageData.setIndicatorName(indicator.getIndicatorName());
                        syncMessageData.setErrorMessage(model.getRejectMessage());
                        syncMessageDataList.add(syncMessageData);

                    }
                }

                //Get all indicator times period facility mappings which are created in mobile database and make isNew column false
                RealmResults<IndicatorFacilityTimeperiodMapping> result = realm.where(IndicatorFacilityTimeperiodMapping.class)
                        .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.IS_NEW, true)
                        .findAll();

                for(IndicatorFacilityTimeperiodMapping mapping : result){
                    mapping.setNew(false);
                }

                //SNCU data
                if (txnsncuDataModels != null) {
                    for (TXNSNCUDataModel txnsncuDataModel : txnsncuDataModels) {
                        SyncMessageData syncMessageData = new SyncMessageData();
                        IndicatorFacilityTimeperiodMapping mapping = realm.where(
                                IndicatorFacilityTimeperiodMapping.class)
                                .equalTo(
                                        Constant.Table.IndicatorFacilityTimeperiodMapping.ID,
                                        txnsncuDataModel.getIftid())
                                .findFirst();

                        Indicator indicator = realm.where(Indicator.class)
                                .equalTo(Constant.Table.Indicator.ID, mapping.getIndicator())
                                .findFirst();
                        syncMessageData.setMsgType("SNCU/ NICU data");
                        syncMessageData.setIndicatorName(indicator.getIndicatorName());
                        syncMessageData.setErrorMessage(txnsncuDataModel.getErrorMessage());
                        syncMessageDataList.add(syncMessageData);
                    }
                }

                //Engagement score data
                if (txnEngagementScoreModels != null) {
                    for (TXNEngagementScoreModel model : txnEngagementScoreModels) {
                        SyncMessageData syncMessageData = new SyncMessageData();
                        MSTEngagementScore score = realm.where(MSTEngagementScore.class)
                                .equalTo(Constant.Table.MSTEngagementScore.ID, model.getEngagementScoreId())
                                .findFirst();


                        syncMessageData.setMsgType("Definition");
                        syncMessageData.setIndicatorName(score.getDefinition());
                        syncMessageData.setErrorMessage(model.getRejectionMessage());
                        syncMessageDataList.add(syncMessageData);

                /*String msg = "\nDefinition: " + score.getDefinition();
                msg += "\n\nError message : " + model.getRejectionMessage();
                msg +="\n------------------------------";
                message += msg;*/

                    }
                }

                //Master data

                MasterDataModel masterDataModel = syncResult.getMasterDataModel();
                if (masterDataModel != null) {
                    if (masterDataModel.getTypeDetailModels() != null) {
                        for (TypeDetailModel iModel : masterDataModel.getTypeDetailModels()) {


                            TypeDetail object = realm.where(TypeDetail.class)
                                    .equalTo(Constant.Table.TypeDetail.ID, iModel.getTypeDetailId())
                                    .findFirst();
                            if (object != null) {
                                object.setTypeDetail(iModel.getTypeDetail());
                                object.setDescription(iModel.getDescription());
                                object.setTypeId(iModel.getTypeId());
                            } else {
                                TypeDetail iObject = realm.createObject(TypeDetail.class, iModel.getTypeDetailId());

                                iObject.setTypeDetail(iModel.getTypeDetail());
                                iObject.setDescription(iModel.getDescription());
                                iObject.setTypeId(iModel.getTypeId());
                            }
                        }
                    }


                    //Inserting/updating data in type and type details table
                    if (masterDataModel.getTypeModels() != null) {


                        for (TypeModel model : masterDataModel.getTypeModels()) {

                            Type object = realm.where(Type.class)
                                    .equalTo(Constant.Table.Type.ID, model.getTypeId())
                                    .findFirst();

                            if (object != null) {
                                object.setTypeName(model.getTypeName());
                                object.setDescription(model.getDescription());
                            } else {
                                object = realm.createObject(Type.class, model.getTypeId());
                                object.setTypeName(model.getTypeName());
                                object.setDescription(model.getDescription());
                            }


                        }

                    }


                    //Inserting into area table
                    if (masterDataModel.getAreaModels() != null) {
                        for (AreaModel model : masterDataModel.getAreaModels()) {
                            Area object = realm.where(Area.class)
                                    .equalTo(Constant.Table.Area.ID, model.getAreaId())
                                    .findFirst();
                            if (object != null) {
                                object.setAreaName(model.getAreaName());
                                object.setFacilitySize(model.getFacilitySize());
                                object.setFacilityType(model.getFacilityType());
                                object.setLevel(model.getLevel());
                                object.setParentAreaId(model.getParentAreaId());
                                object.setWave(model.getWave() == 0 ? null : model.getWave());
                            } else {
                                object = realm.createObject(Area.class, model.getAreaId());
                                object.setAreaName(model.getAreaName());
                                object.setFacilitySize(model.getFacilitySize());
                                object.setFacilityType(model.getFacilityType());
                                object.setLevel(model.getLevel());
                                object.setParentAreaId(model.getParentAreaId());
                                object.setWave(model.getWave() == 0 ? null : model.getWave());
                            }


                        }
                    }


                    //Inserting data into user table
                    if (masterDataModel.getUserModel() != null) {
                        UserModel model = masterDataModel.getUserModel();
                        User object = realm.where(User.class).findFirst();
                        object.setName(model.getName());
                        object.setDEO(model.getIsDEO());

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

                            Indicator object = realm.where(Indicator.class)
                                    .equalTo(Constant.Table.Indicator.ID, model.getIndicatorId())
                                    .findFirst();

                            if (object != null) {

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
                            } else {
                                object = realm.createObject(Indicator.class, model.getIndicatorId());
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
                    }

                    if (masterDataModel.getTimePeriodModels() != null) {
                        PreferenceData preferenceData = new PreferenceData(context);
                        for (TimePeriodModel model : masterDataModel.getTimePeriodModels()) {
                            TimePeriod object = realm.where(TimePeriod.class)
                                    .equalTo(Constant.Table.TimePeriod.ID, model.getTimePeriodId())
                                    .findFirst();

                            if (object != null) {
                                object.setEndDate(SCSL.getInstance().getDate(model.getEndDate()));
                                object.setPeriodicity(model.getPeriodicity());
                                object.setStartDate(SCSL.getInstance().getDate(model.getStartDate()));
                                object.setTimePeriod(model.getTimePeriod());
                                object.setWave(model.getWave());
                             //   preferenceData.setPreferenceData(false,"is_new_month");

                            } else {
                                object = realm.createObject(TimePeriod.class, model.getTimePeriodId());
                                object.setEndDate(SCSL.getInstance().getDate(model.getEndDate()));
                                object.setPeriodicity(model.getPeriodicity());
                                object.setStartDate(SCSL.getInstance().getDate(model.getStartDate()));
                                object.setTimePeriod(model.getTimePeriod());
                                object.setWave(model.getWave());
                               // preferenceData.setPreferenceData(true,"is_new_month");
                                //preferenceData.setPreferenceData(false,context.getString(R.string.profile_entry_is_dissable));
                                //preferenceData.setPreferenceData(false,context.getString(R.string.is_reset));
                            }


                        }
                    }

                    if (masterDataModel.getIndicatorFacilityTimeperiodMappingModels() != null) {
                        List<Integer> tempList = new ArrayList<>();
                        for (IndicatorFacilityTimeperiodMappingModel model : masterDataModel.getIndicatorFacilityTimeperiodMappingModels()) {

                            IndicatorFacilityTimeperiodMapping object = realm.where(IndicatorFacilityTimeperiodMapping.class)
                                    .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.INDICATOR_ID, model.getIndicatorId())
                                    .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.TIME_PERIOD_ID, model.getTimePeriodId())
                                    .findFirst();
                            //if there is any object
                            if (object != null) {
                                //we got record from indicatorfacilitymapping table
                                TXNSNCUNICUData data = realm.where(TXNSNCUNICUData.class)
                                        .equalTo(Constant.Table.TXNSNCUNICUData.IFTM, object.getIndFacilityTpId())
                                        .findFirst();
                                if (data != null) {
                                    data.setIndicatorFacilityTimeperiodMapping(model.getIndFacilityTpId());
                                }
                                //delete the existing indicator facility time perion mapping object, then we need to insert the new one
                                object.deleteFromRealm();

                                //insert the new object from server
                                object = realm.createObject(IndicatorFacilityTimeperiodMapping.class, model.getIndFacilityTpId());
                                object.setCreatedDate(SCSL.getInstance().getFullDate(model.getCreatedDate()));
                                object.setFacility(model.getFacilityId());
                                object.setIndicator(model.getIndicatorId());
                                object.setTimePeriod(model.getTimePeriodId());

                            } else {
                                //could not get from table so insert in mapping table
                                object = realm.createObject(IndicatorFacilityTimeperiodMapping.class, model.getIndFacilityTpId());
                                object.setCreatedDate(SCSL.getInstance().getFullDate(model.getCreatedDate()));
                                object.setFacility(model.getFacilityId());
                                object.setIndicator(model.getIndicatorId());
                                object.setTimePeriod(model.getTimePeriodId());
                            }

                        }


                    }

                    if (masterDataModel.getmSTEngagementScoreModels() != null) {
                        for (MSTEngagementScoreModel model : masterDataModel.getmSTEngagementScoreModels()) {

                            MSTEngagementScore object = realm.where(MSTEngagementScore.class)
                                    .equalTo(Constant.Table.MSTEngagementScore.ID, model.getMstEngagementScoreId())
                                    .findFirst();

                            if (object != null) {
                                object.setDefinition(model.getDefinition());
                                object.setProgress(model.getProgress());
                                object.setScore(model.getScore());
                            } else {
                                object = realm.createObject(MSTEngagementScore.class, model.getMstEngagementScoreId());
                                object.setDefinition(model.getDefinition());
                                object.setProgress(model.getProgress());
                                object.setScore(model.getScore());
                            }
                        }
                    }

                    //setting latest patient number
                    SysConfig sysConfig = realm.where(SysConfig.class).findFirst();
                    sysConfig.setLastSyncDate(SCSL.getInstance().getFullDate(masterDataModel.getLastSyncDate()));
                    sysConfig.setDeoDeadLine(masterDataModel.getDeoDeadLine());
                    sysConfig.setSubDeadLine(masterDataModel.getSubDeadLine());
                    sysConfig.setMneDeadLine(masterDataModel.getMneDeadLine());

                }
                realm.commitTransaction();

                // remove duplicate indicators
                //RealmResults<IndicatorFacilityTimeperiodMapping> results = realm.where(IndicatorFacilityTimeperiodMapping.class).findAll();
               /* List<Integer> ids = SCSL.getInstance().getExtraIndicatorFacilityTimeperiodIds();
                for (int id : ids) {
                    IndicatorFacilityTimeperiodMapping mapping = realm.where(IndicatorFacilityTimeperiodMapping.class).equalTo("indFacilityTpId", id).findFirst();
                    if (mapping != null) {
                        if (!realm.isInTransaction()) {
                            realm.beginTransaction();
                        }
                        mapping.deleteFromRealm();
                        realm.commitTransaction();
                    }
                }


                RealmResults<TXNSNCUNICUData> txnResults = realm.where(TXNSNCUNICUData.class).findAll();
                RealmResults<IndicatorFacilityTimeperiodMapping> indResults = realm.where(IndicatorFacilityTimeperiodMapping.class).findAll();
                Log.v("txnResults", "" + txnResults.size());
                Log.v("indResults", "" + indResults.size());*/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return syncMessageDataList;
    }

    /**
     * This following method is going to set last month indicator mappings for future usage
     * Ratikanta
     */
    private void setLastIndicatorFacilityTimePeriodIds(Realm realm) {
        RealmResults<IndicatorFacilityTimeperiodMapping> mappings = realm.where(IndicatorFacilityTimeperiodMapping.class)
                .equalTo(Constant.Table.IndicatorFacilityTimeperiodMapping.TIME_PERIOD_ID, SCSL.getInstance().getLastTimePeriodId())
                .findAll();
        List<Integer> ids = new ArrayList<>();
        for (IndicatorFacilityTimeperiodMapping mapping : mappings) {
            ids.add(mapping.getIndFacilityTpId());
        }
        SCSL.getInstance().setLastIndicatorFacilityTimePeriodIds(ids);
    }

    private void setLastTimePeriodId(Realm realm) {
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

}
