package com.shantanoo.know_your_government.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.shantanoo.know_your_government.R;
import com.shantanoo.know_your_government.model.Official;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";

    private Official official;

    private TextView tvCurrentLocation;
    private TextView tvOffice;
    private TextView tvName;
    private TextView tvParty;
    private ImageView ivOfficialPhoto;
    private ImageView ivPartyLogo;
    private TextView tvAddress;
    private TextView tvAddressText;
    private TextView tvPhone;
    private TextView tvPhoneText;
    private TextView tvEmail;
    private TextView tvEmailText;
    private TextView tvWebsite;
    private TextView tvWebsiteURL;
    private ImageView ivFacebook;
    private ImageView ivTwitter;
    private ImageView ivYoutube;

    private ConstraintLayout clOfficialActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        tvOffice = findViewById(R.id.tvOffice);
        tvName = findViewById(R.id.tvName);
        tvParty = findViewById(R.id.tvParty);
        ivOfficialPhoto = findViewById(R.id.ivOfficialPhoto);
        ivPartyLogo = findViewById(R.id.ivDetailPartyLogo);
        tvAddress = findViewById(R.id.tvAddress);
        tvAddressText = findViewById(R.id.tvAddressText);
        tvPhone = findViewById(R.id.tvPhone);
        tvPhoneText = findViewById(R.id.tvPhoneText);
        tvEmail = findViewById(R.id.tvEmail);
        tvEmailText = findViewById(R.id.tvEmailText);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvWebsiteURL = findViewById(R.id.tvWebsiteURL);
        ivFacebook = findViewById(R.id.ivFacebook);
        ivTwitter = findViewById(R.id.ivTwitter);
        ivYoutube = findViewById(R.id.ivYoutube);

        clOfficialActivity = findViewById(R.id.clOfficialActivity);

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.location)))
            tvCurrentLocation.setText(intent.getStringExtra(getString(R.string.location)));

        if (intent.hasExtra(Official.class.getName())) {
            official = (Official) intent.getSerializableExtra(Official.class.getName());

            tvOffice.setText(official.getOffice());
            tvName.setText(official.getOfficialName());

            if (!official.getOfficialParty().equalsIgnoreCase(getString(R.string.unknown)) && !official.getOfficialParty().equalsIgnoreCase(getString(R.string.data_not_found))) {
                String party = official.getOfficialParty();
                tvParty.setText(String.format("(%s)", party));

                if (party.toLowerCase().contains(getString(R.string.democrat))) {
                    clOfficialActivity.setBackgroundColor(Color.BLUE);
                    ivPartyLogo.setImageResource(R.drawable.dem_logo);
                } else if (party.toLowerCase().contains(getString(R.string.republican))) {
                    clOfficialActivity.setBackgroundColor(Color.RED);
                    ivPartyLogo.setImageResource(R.drawable.rep_logo);
                } else {
                    clOfficialActivity.setBackgroundColor(Color.BLACK);
                    ivPartyLogo.setVisibility(View.INVISIBLE);
                }
            }

            populateImage(official.getOfficialPhotoURL());

            // Update address if provided else don't display it
            if (official.getOfficialAddress().equalsIgnoreCase(getString(R.string.data_not_found))) {
                Group groupAddress = findViewById(R.id.groupAddress);
                groupAddress.setVisibility(View.GONE);
            } else {
                tvAddressText.setText(official.getOfficialAddress());
                tvAddressText.setPaintFlags(tvAddressText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            // Update phone number if provided else don't display it
            if (official.getOfficialPhone().equalsIgnoreCase(getString(R.string.data_not_found))) {
                Group groupPhone = findViewById(R.id.groupPhone);
                groupPhone.setVisibility(View.GONE);
            } else {
                tvPhoneText.setText(official.getOfficialPhone());
            }

            // Update email  if provided else don't display it
            if (official.getOfficialEmail().equalsIgnoreCase(getString(R.string.data_not_found))) {
                Group groupEmail = findViewById(R.id.groupEmail);
                groupEmail.setVisibility(View.GONE);
            } else {
                tvEmailText.setText(official.getOfficialEmail());
            }

            // Update website if provided else don't display it
            if (official.getOfficialWebURL().equalsIgnoreCase(getString(R.string.data_not_found))) {
                Group groupWebsite = findViewById(R.id.groupWebsite);
                groupWebsite.setVisibility(View.GONE);
            } else {
                tvWebsiteURL.setText(official.getOfficialWebURL());
            }

            // Update social media if provided else don't display it
            if (TextUtils.isEmpty(official.getOfficialFB()))
                ivFacebook.setVisibility(View.GONE);
            else
                ivFacebook.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(official.getOfficialYouTube()))
                ivYoutube.setVisibility(View.GONE);
            else
                ivYoutube.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(official.getOfficialTwitter()))
                ivTwitter.setVisibility(View.GONE);
            else
                ivTwitter.setVisibility(View.VISIBLE);
        }
    }

    private void populateImage(final String officialPhotoURL) {

        // If Photo URL is present
        if (officialPhotoURL == null) { // Image URL is not present for these officials
            Log.d(TAG, "populateImage: Official photo not available");
            ivOfficialPhoto.setImageResource(R.drawable.missing);
            return;
        }
        // To handle image load failures
        Picasso picasso = new Picasso.Builder(this).listener((picasso1, uri, exception) -> exception.printStackTrace()).build();
        picasso.setLoggingEnabled(true);
        // Load image from the URL, if error load the broken image
        picasso.load(officialPhotoURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(ivOfficialPhoto);

    }

    public void onClickPartyLogo(View view) {
        String party = official.getOfficialParty();
        String url = null;

        // If party is known Republican / Democrat*
        if (party.equalsIgnoreCase(getString(R.string.unknown)) || party.equalsIgnoreCase(getString(R.string.data_not_found)))
            return;

        if (party.toLowerCase().contains(getString(R.string.republican)))
            url = getString(R.string.republican_website);
        else if (party.toLowerCase().contains(getString(R.string.democrat)))
            url = getString(R.string.democrat_website);

        Intent partyWebSite = new Intent(Intent.ACTION_VIEW);
        partyWebSite.setData(Uri.parse(url));
        startActivity(partyWebSite);
    }

    public void onClickOfficialPhoto(View view) {
        // Check if the photo is available then only load it
        if (official.getOfficialPhotoURL() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.official_image_message), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(getString(R.string.location), tvCurrentLocation.getText());
        intent.putExtra(Official.class.getName(), official);
        startActivity(intent);
    }

    public void clickToOpenBrowser(View v) {
        String websiteURL = tvWebsiteURL.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "clickToOpenBrowser: Browser app not available");
            makeErrorAlert("No browser application found to open URL");
        }
    }

    public void clickCall(View v) {
        String number = tvPhoneText.getText().toString().trim();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(getString(R.string.tel) + number));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "clickCall: Phone call app not available");
            makeErrorAlert("No application found for placing calls");
        }
    }

    public void clickEmail(View v) {
        String emailTo = tvEmailText.getText().toString().trim();
        String[] addresses = new String[]{emailTo};

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.mailto)));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 111);
        } else {
            Log.e(TAG, "clickEmail: Email app not available");
            makeErrorAlert("No application found for sending email");
        }
    }

    public void clickMap(View v) {
        String address = tvAddressText.getText().toString().trim();
        Uri mapUri = Uri.parse(getString(R.string.google_map_geo_url) + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        intent.setPackage(getString(R.string.google_map_package));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "clickMap: Google Map not available");
            makeErrorAlert("No application found to open the Map");
        }
    }

    public void onClickTwitter(View v) {
        Intent intent;
        String name = official.getOfficialTwitter();
        try { // get the Twitter app if possible
            getPackageManager().getPackageInfo(getString(R.string.twitter_package), 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter_screen_url) + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            Log.e(TAG, "onClickTwitter: Twitter package not available. Loading web..", e);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter_url) + name));
        }
        startActivity(intent);
    }

    public void onClickFacebook(View v) {
        String urlToUse;
        String FACEBOOK_URL = getString(R.string.facebook_url) + official.getOfficialFB();
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo(getString(R.string.facebook_package), 0).versionCode;
            if (versionCode >= 3002850) {
                //newer versions of fb app
                urlToUse = getString(R.string.facebook_app_url) + FACEBOOK_URL;
            } else {
                //older versions of fb app
                urlToUse = getString(R.string.facebook_old_app_url) + official.getOfficialFB();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onClickFacebook: Facebook package not found. Loading web..", e);
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void onClickYouTube(View v) {
        Intent intent;
        String name = official.getOfficialYouTube();
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(getString(R.string.youtube_package));
            intent.setData(Uri.parse(getString(R.string.youtube_url) + name));
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "onClickYouTube: Activity not found. Loading web..", e);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_url) + name));
        }
        startActivity(intent);
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle(getString(R.string.no_app_found));

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}