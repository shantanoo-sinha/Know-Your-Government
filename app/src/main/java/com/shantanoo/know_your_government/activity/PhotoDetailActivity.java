package com.shantanoo.know_your_government.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.shantanoo.know_your_government.R;
import com.shantanoo.know_your_government.model.Official;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = "PhotoDetailActivity";

    private Official official;

    private TextView tvDetailCurrentLocation;
    private TextView tvDetailOffice;
    private TextView tvDetailName;
    private ImageView ivDetailOfficialPhoto;
    private ImageView ivDetailPartyLogo;

    private ConstraintLayout clDetailOfficialActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        clDetailOfficialActivity = findViewById(R.id.clDetailOfficialActivity);

        tvDetailCurrentLocation = findViewById(R.id.tvDetailCurrentLocation);
        tvDetailOffice = findViewById(R.id.tvDetailOffice);
        tvDetailName = findViewById(R.id.tvDetailName);
        ivDetailOfficialPhoto = findViewById(R.id.ivDetailOfficialPhoto);
        ivDetailPartyLogo = findViewById(R.id.ivDetailPartyLogo);

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.location)))
            tvDetailCurrentLocation.setText(intent.getStringExtra(getString(R.string.location)));

        if (intent.hasExtra(Official.class.getName())) {

            official = (Official) intent.getSerializableExtra(Official.class.getName());

            tvDetailOffice.setText(official.getOffice());
            tvDetailName.setText(official.getOfficialName());

            if (!official.getOfficialParty().equalsIgnoreCase(getString(R.string.unknown)) && !official.getOfficialParty().equalsIgnoreCase(getString(R.string.data_not_found))) {
                String party = official.getOfficialParty();

                if (party.toLowerCase().contains(getString(R.string.democrat))) {
                    clDetailOfficialActivity.setBackgroundColor(Color.BLUE);
                    ivDetailPartyLogo.setImageResource(R.drawable.dem_logo);
                } else if (party.toLowerCase().contains(getString(R.string.republican))) {
                    clDetailOfficialActivity.setBackgroundColor(Color.RED);
                    ivDetailPartyLogo.setImageResource(R.drawable.rep_logo);
                } else {
                    clDetailOfficialActivity.setBackgroundColor(Color.BLACK);
                    ivDetailPartyLogo.setVisibility(View.INVISIBLE);
                }
            }

            populateImage(official.getOfficialPhotoURL());
        }
    }

    private void populateImage(final String officialPhotoURL) {

        // If Photo URL is present
        if (officialPhotoURL != null) {
            // To handle image load failures
            Picasso picasso = new Picasso.Builder(this).listener((picasso1, uri, exception) -> exception.printStackTrace()).build();
            picasso.setLoggingEnabled(true);
            // Load image from the URL, if error load the broken image
            picasso.load(officialPhotoURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(ivDetailOfficialPhoto);
        } else { // Image URL is not present for these officials
            Log.d(TAG, "populateImage: Official photo not available");
            ivDetailOfficialPhoto.setImageResource(R.drawable.missing);
        }
    }
}