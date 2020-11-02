
package com.shantanoo.know_your_government;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shantanoo.know_your_government.activity.AboutActivity;
import com.shantanoo.know_your_government.activity.OfficialActivity;
import com.shantanoo.know_your_government.adapter.RecyclerViewAdapter;
import com.shantanoo.know_your_government.model.Official;
import com.shantanoo.know_your_government.model.OfficialResult;
import com.shantanoo.know_your_government.service.OfficialMasterDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int LOCATION_REQUEST_CODE_ID = 111;

    private Criteria criteria;
    private TextView tvLocation;
    private RecyclerView recyclerView;
    private List<Official> officials;
    private RecyclerViewAdapter adapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        tvLocation = findViewById(R.id.tvLocation);
        recyclerView = findViewById(R.id.rvOfficials);

        officials = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, officials);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        if (isConnectedToNetwork()) {
            if (checkPermissions())
                getZipCode();
            else
                requestLocationPermission();
        } else {
            onNetworkDisconnected();
        }
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

    // Check if permissions are granted
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    // Request location permissions
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                LOCATION_REQUEST_CODE_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE_ID) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getZipCode();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: ACCESS_FINE_LOCATION permissions not granted");
                    }
                }
            }
        }
    }

    private void getLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText etLocationInput = new EditText(this);
        etLocationInput.setInputType(InputType.TYPE_CLASS_TEXT);
        etLocationInput.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(etLocationInput);

        // OK button
        builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
            // Check if connected to internet
            if (!isConnectedToNetwork()) // notify the user if no connectivity
                onNetworkDisconnected();
            else // Fetch the data for given location
                new Thread(new OfficialMasterDataService(MainActivity.this, etLocationInput.getText().toString())).start();
        });

        // CANCEL button
        builder.setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
            // Do Nothing
        });

        builder.setMessage(getString(R.string.get_location_message));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        Official official = officials.get(position);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra(getString(R.string.location), tvLocation.getText());
        intent.putExtra(Official.class.getName(), official);
        startActivity(intent);
    }

    // Check if internet connectivity is established
    private boolean isConnectedToNetwork() {
        Log.d(TAG, "isConnectedToNetwork: Checking network connectivity");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Log.d(TAG, "isConnectedToNetwork: Network not connected");
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "isConnectedToNetwork: Network connected");
            return true;
        }
        Log.d(TAG, "isConnectedToNetwork: Network not connected");
        return false;
    }

    public void onNetworkDisconnected() {
        Log.d(TAG, "onNetworkDisconnected: STARTED");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.no_network_connection_title));
        //builder.setIcon(R.drawable.outline_cloud_off_black_48);
        builder.setMessage(R.string.no_network_connection_message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d(TAG, "onNetworkDisconnected: COMPLETED");
    }

    private void getZipCode() {
        Log.d(TAG, "getZipCode: STARTED");
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

            if (locationManager != null) {
                // Checking if we can get location from NETWORK
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address address = addresses.get(0);
                    String zipCode = address.getPostalCode();
                    if (zipCode != null || zipCode.trim().length() > 0)
                        new Thread(new OfficialMasterDataService(MainActivity.this, address.getPostalCode())).start();
                } else {
                    onNetworkDisconnected();
                }
            } else {
                Log.d(TAG, "getZipCode: locationManger is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "getZipCode: Exception", e);
        }
    }

    public void populateOfficialData(OfficialResult jsonResult) {
        officials.clear();
        if (jsonResult == null) {
            tvLocation.setText(getString(R.string.no_data_for_location));
        } else {
            tvLocation.setText(TextUtils.isEmpty(jsonResult.getLocation()) ? getString(R.string.no_data_for_location) : jsonResult.getLocation());
            if (jsonResult.getOfficialList() != null) {
                officials.addAll(jsonResult.getOfficialList());
            }
        }
        adapter.notifyDataSetChanged();
    }
}