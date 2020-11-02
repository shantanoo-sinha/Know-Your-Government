package com.shantanoo.know_your_government.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.shantanoo.know_your_government.MainActivity;
import com.shantanoo.know_your_government.R;
import com.shantanoo.know_your_government.activity.OfficialActivity;
import com.shantanoo.know_your_government.model.Official;

/**
 * Created by Shantanoo on 10/25/2020.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    /*private Context context;*/
    private final TextView tvOfficialDesignation;
    private final TextView tvOfficialName;
    /*private final ConstraintLayout recyclerViewRow;*/

    public ViewHolder(@NonNull View itemView/*, Context context*/) {
        super(itemView);
        /*this.context = context;*/

        tvOfficialDesignation = itemView.findViewById(R.id.tvOfficialDesignation);
        tvOfficialName = itemView.findViewById(R.id.tvOfficialName);
        /*recyclerViewRow = itemView.findViewById(R.id.recyclerViewRow);*/
    }

    public void bind(Official official) {
        tvOfficialDesignation.setText(official.getOffice());

        if ("No data provided".equalsIgnoreCase(official.getOfficialParty()) ||
                "Unknown".equalsIgnoreCase(official.getOfficialParty())) {
            tvOfficialName.setText(official.getOfficialName());
        } else {
            tvOfficialName.setText(String.format("%s (%s)", official.getOfficialName(), official.getOfficialParty()));
        }

        /*recyclerViewRow.setOnClickListener((View.OnClickListener) v -> {
            Intent intent = new Intent(context, OfficialActivity.class);
            //intent.putExtra(context.getString(R.string.location), tvLocation.getText());
            intent.putExtra(Official.class.getName(), official);

            Pair<View, String> p1 = Pair.create((View) tvOfficialDesignation, context.getString(R.string.official_designation));
            Pair<View, String> p2 = Pair.create((View) tvOfficialName, context.getString(R.string.official_name));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2);
            context.startActivity(intent, options.toBundle());
        });*/
    }
}
