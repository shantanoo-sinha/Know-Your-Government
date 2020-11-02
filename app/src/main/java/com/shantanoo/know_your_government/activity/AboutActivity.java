package com.shantanoo.know_your_government.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shantanoo.know_your_government.R;

public class AboutActivity extends AppCompatActivity {

    public static final String GOOGLE_CIVIC_INFORMATION_PAGE_URL = "https://developers.google.com/civic-information";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    // Open google civic information page
    public void openAPIPage(View v) {
        Uri uriURL = Uri.parse(GOOGLE_CIVIC_INFORMATION_PAGE_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriURL);
        startActivity(intent);
    }
}