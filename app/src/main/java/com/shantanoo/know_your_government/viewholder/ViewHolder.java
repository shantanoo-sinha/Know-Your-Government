package com.shantanoo.know_your_government.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shantanoo.know_your_government.R;
import com.shantanoo.know_your_government.model.Official;

/**
 * Created by Shantanoo on 10/25/2020.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvOfficialDesignation;
    private final TextView tvOfficialName;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        tvOfficialDesignation = itemView.findViewById(R.id.tvOfficialDesignation);
        tvOfficialName = itemView.findViewById(R.id.tvOfficialName);
    }

    public void bind(Official official) {
        tvOfficialDesignation.setText(official.getOffice());

        if ("No data provided".equalsIgnoreCase(official.getOfficialParty()) ||
                "Unknown".equalsIgnoreCase(official.getOfficialParty())) {
            tvOfficialName.setText(official.getOfficialName());
        } else {
            tvOfficialName.setText(String.format("%s (%s)", official.getOfficialName(), official.getOfficialParty()));
        }
    }
}
