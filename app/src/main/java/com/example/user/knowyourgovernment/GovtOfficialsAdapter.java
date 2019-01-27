package com.example.user.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by user on 01-04-2018.
 */

public class GovtOfficialsAdapter extends RecyclerView.Adapter<GovtOfficialsViewHolder> {

    private static final String TAG = "GovtOfficialsAdapter";
    private MainActivity mainActivity;
    private ArrayList<GovtOfficial> list_of_officies;

    public GovtOfficialsAdapter(MainActivity mainActi, ArrayList<GovtOfficial> list) {
        this.mainActivity = mainActi;
        this.list_of_officies = list;
    }

    @Override
    public GovtOfficialsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_official, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new GovtOfficialsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GovtOfficialsViewHolder holder, int position) {

        GovtOfficial officialItem = list_of_officies.get(position);
        holder.tvOffice.setText(officialItem.getOffice());
        holder.tvOfficialName.setText(officialItem.getOfficial_name());
        holder.tvParty.setText("("+ officialItem.getParty() +")");
    }

    @Override
    public int getItemCount() {
        return list_of_officies.size();
    }
}
