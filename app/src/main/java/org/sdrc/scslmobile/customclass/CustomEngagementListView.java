package org.sdrc.scslmobile.customclass;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.activity.EngagementActivity;
import org.sdrc.scslmobile.model.realm.Area;
import org.sdrc.scslmobile.model.realm.MSTEngagementScore;
import org.sdrc.scslmobile.model.realm.TXNEngagementScore;
import org.sdrc.scslmobile.model.realm.TimePeriod;
import org.sdrc.scslmobile.util.SCSL;

import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 */

public class CustomEngagementListView extends RecyclerView.Adapter<CustomEngagementListView.MyViewHolder> {

    private final Context mContext;
    private final RealmResults<MSTEngagementScore> engagementScoresResults;
    private int selectedPosition = -1;

    public CustomEngagementListView(Context mContext, RealmResults<MSTEngagementScore> engagementScoresResults) {
        super();
        //Getting all the superheroes
        this.mContext = mContext;
        this.engagementScoresResults = engagementScoresResults;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.engagement_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (position == 0){
            holder.serialTxt.setText("Select");
        }else{
            holder.serialTxt.setText("");
        }
        holder.serialNo.setText(position + 1 + "");
        holder.progress.setText(engagementScoresResults.get(position).getProgress());
        holder.definition.setText(engagementScoresResults.get(position).getDefinition());
        holder.score = engagementScoresResults.get(position).getScore();
        holder.engagementScoreId = engagementScoresResults.get(position).getMstEngagementScoreId();

        if (EngagementActivity.ckeckStatus.equalsIgnoreCase("true")) {
            holder.select.setEnabled(true);
            holder.headerArea.setBackgroundResource(R.color.darkBlue);
            holder.selectionArea.setBackgroundResource(R.color.yellow);
            holder.listSpace.setBackgroundResource(R.color.lighter_blue);
            holder.progress.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.serialNo.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.serialTxt.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.select.setEnabled(false);
            holder.headerArea.setBackgroundResource(R.color.grey);
            holder.selectionArea.setBackgroundResource(R.color.lightGrey);
            holder.progress.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.serialNo.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.serialTxt.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.listSpace.setBackgroundResource(R.color.lightGrey);
            if (EngagementActivity.engagementId != -1 && EngagementActivity.engagementId ==  holder.engagementScoreId){
                holder.select.setChecked(true);
                //dissabled but selected
                holder.select.setEnabled(false);
                holder.headerArea.setBackgroundResource(R.color.darkBlue);
                holder.selectionArea.setBackgroundResource(R.color.yellow);
                holder.listSpace.setBackgroundResource(R.color.lighter_blue);
                holder.progress.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.serialNo.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.serialTxt.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                holder.select.setChecked(false);
                //dissabled but not selected
                holder.headerArea.setBackgroundResource(R.color.grey);
                holder.selectionArea.setBackgroundResource(R.color.lightGrey);
                holder.progress.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.serialNo.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.serialTxt.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.listSpace.setBackgroundResource(R.color.lighter_grey);
            }
        }

        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = holder.getAdapterPosition();
                EngagementActivity.tempSelectionStart = true;
                callConfirmationDialog(holder.score, holder.engagementScoreId);
                notifyDataSetChanged();
            }
        });

        if (EngagementActivity.tempSelectionStart) {
            if (selectedPosition == position) {
                holder.select.setChecked(true);
                holder.headerArea.setBackgroundResource(R.color.darkBlue);
                holder.selectionArea.setBackgroundResource(R.color.yellow);
                holder.listSpace.setBackgroundResource(R.color.lighter_blue);
                holder.progress.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.serialNo.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.serialTxt.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                holder.select.setChecked(false);
                holder.headerArea.setBackgroundResource(R.color.grey);
                holder.selectionArea.setBackgroundResource(R.color.lightGrey);
                holder.progress.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.serialNo.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.serialTxt.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.listSpace.setBackgroundResource(R.color.lighter_grey);
            }
        }
    }

    @Override
    public int getItemCount() {
        return engagementScoresResults.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView serialTxt;
        private final TextView serialNo;
        private final TextView progress;
        private final TextView definition;
        private final CheckBox select;
        private final LinearLayout headerArea;
        private final LinearLayout selectionArea;
        private final TextView listSpace;
        private float score;
        private int engagementScoreId;

        public MyViewHolder(View itemView) {
            super(itemView);

            serialTxt = (TextView) itemView.findViewById(R.id.select_txt);
            serialNo = (TextView) itemView.findViewById(R.id.serial_no);
            progress = (TextView) itemView.findViewById(R.id.progress);
            definition = (TextView) itemView.findViewById(R.id.definition);
            select = (CheckBox) itemView.findViewById(R.id.select);
            headerArea = (LinearLayout) itemView.findViewById(R.id.header_area);
            selectionArea = (LinearLayout) itemView.findViewById(R.id.selection_area);
            listSpace = (TextView) itemView.findViewById(R.id.list_space);
        }
    }

    private void callConfirmationDialog(final float score, final int engagementScoreId) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confimation);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (dialog.getWindow().getAttributes() != null) {
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = 400;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }

        final TextView facility = (TextView) dialog.findViewById(R.id.facility);
        facility.setText(EngagementActivity.mFacilityTxt.getText().toString());
        TextView monthSelection = (TextView) dialog.findViewById(R.id.month_selection);
        monthSelection.setText(EngagementActivity.mMonthSelectionTxt.getText().toString());

        int selectedTimePeriodId = 0;
        for (Map.Entry<Integer, TimePeriod> entry : SCSL.getInstance().getTimePeriodMap().entrySet()) {
            String selectedTimnePeriodString = EngagementActivity.mMonthSelectionTxt.getText().toString().trim();
            if (entry.getValue().getTimePeriod().trim().equals(selectedTimnePeriodString)) {
                selectedTimePeriodId = entry.getValue().getTimePeriodId();
            }
        }

        int selectedFacilityId = 0;
        for (Map.Entry<Integer, Area> entry : SCSL.getInstance().getAreaMap().entrySet()) {
            String selectedFacilityString = facility.getText().toString().trim();
            if (entry.getValue().getAreaName().trim().equals(selectedFacilityString)) {
                selectedFacilityId = entry.getValue().getAreaId();
            }
        }

        TextView scoreValue = (TextView) dialog.findViewById(R.id.score);
        scoreValue.setText(score + "");
        Button cancel = (Button) dialog.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button submit = (Button) dialog.findViewById(R.id.submit_btn);

        final int finalSelectedFacilityId = selectedFacilityId;
        final int finalSelectedTimePeriodId = selectedTimePeriodId;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Realm realm = SCSL.getInstance().getRealm(mContext);

                int maxId =0;
                if (realm.where(TXNEngagementScore.class).max("txnEngagementScoreId") != null){
                    maxId = Integer.parseInt(realm.where(TXNEngagementScore.class).max("txnEngagementScoreId").toString());
                }
                realm.beginTransaction();
                TXNEngagementScore txnEngagementScore = new TXNEngagementScore();
                txnEngagementScore.setFacility(finalSelectedFacilityId);
                txnEngagementScore.setTimePeriod(finalSelectedTimePeriodId);
                txnEngagementScore.setCreatedDate((new Date()));
                txnEngagementScore.setTxnEngagementScoreId(++maxId);
                txnEngagementScore.setMstEngagementScoreId(engagementScoreId);
                txnEngagementScore.setEngagementScore(score);
                realm.copyToRealm(txnEngagementScore);
                realm.commitTransaction();
                realm.close();
                EngagementActivity.ckeckStatus = "false";
                notifyDataSetChanged();
                dialog.dismiss();
                Toast.makeText(mContext, "Submitted", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
