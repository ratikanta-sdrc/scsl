package org.sdrc.scslmobile.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sdrc.scslmobile.R;
import org.sdrc.scslmobile.customclass.SyncMessageData;

import java.util.List;

/**
 * Created by SDRC_DEV on 11-05-2017.
 */

public class SyncErrorMessageAdapter extends RecyclerView.Adapter<SyncErrorMessageAdapter.MyViewHolder> {
    private Context mContext;
    private List<SyncMessageData> syncMessageDataList;

    public SyncErrorMessageAdapter(Context context, List<SyncMessageData> list) {
        super();
        //Getting all the superheroes
        this.mContext = context;
        this.syncMessageDataList = list;
    }


    @Override
    public SyncErrorMessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_syn_error_listview, parent, false);
        return new SyncErrorMessageAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SyncErrorMessageAdapter.MyViewHolder holder, int position) {
        holder.heading.setText(syncMessageDataList.get(position).getMsgType());
        holder.indicator_name.setText(mContext.getResources().getString(R.string.indicator_name) + syncMessageDataList.get(position).getIndicatorName());

        if(syncMessageDataList.get(position).getErrorMessage().equals("Success")){
            holder.heading.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            holder.errorMessage.setText("Success message:" + syncMessageDataList.get(position).getErrorMessage());
        }else{
            holder.errorMessage.setText(mContext.getResources().getString(R.string.error_message) + syncMessageDataList.get(position).getErrorMessage());
        }
    }

    @Override
    public int getItemCount() {
        return syncMessageDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView heading;
        private final TextView indicator_name;
        private final TextView errorMessage;

        public MyViewHolder(View itemView) {
            super(itemView);

            heading = (TextView) itemView.findViewById(R.id.heading);
            indicator_name = (TextView) itemView.findViewById(R.id.indicator);
            errorMessage = (TextView) itemView.findViewById(R.id.error_message);

        }
    }

}
