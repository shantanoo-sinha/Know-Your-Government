package com.shantanoo.know_your_government;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.shantanoo.know_your_government.activity.AboutActivity;
import com.shantanoo.know_your_government.adapter.RecyclerViewAdapter;
import com.shantanoo.know_your_government.model.Official;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String API_KEY = "AIzaSyCNrnzXAOycCyVGE6qxhaNAJhs9ZpJdDwg";
    private static final String TAG = "MainActivity";

    private TextView tvLocation;
    private RecyclerView recyclerView;
    private List<Official> officials;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tvLocation);
        recyclerView = findViewById(R.id.rvOfficials);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuAbout:
                Log.d(TAG, "onOptionsItemSelected: Loading About activity");
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.mnuSearchLocation:
                Log.d(TAG, "onOptionsItemSelected: Getting location from user");
                getLocation();
                break;
            default:
                Log.e(TAG, "onOptionsItemSelected: Unknown menu item");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocation() {
    }

    @Override
    public void onClick(View v) {

    }
}