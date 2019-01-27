package com.example.user.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by user on 01-04-2018.
 */

public class GovtOfficialsViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOffice;
    public TextView tvOfficialName;
    public TextView tvParty;

    public GovtOfficialsViewHolder(View itemView) {
        super(itemView);

        tvOffice = (TextView) itemView.findViewById(R.id.tv_office);
        tvOfficialName = (TextView) itemView.findViewById(R.id.tv_name);
        tvParty = (TextView) itemView.findViewById(R.id.tv_party);
    }
}
