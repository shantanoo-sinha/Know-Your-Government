package com.shantanoo.know_your_government;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shantanoo.know_your_government.activity.AboutActivity;
import com.shantanoo.know_your_government.adapter.RecyclerViewAdapter;
import com.shantanoo.know_your_government.model.Official;
import com.shantanoo.know_your_government.model.OfficialResult;
import com.shantanoo.know_your_government.service.OfficialMasterDataService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static int LOCATION_REQUEST_CODE_ID = 111;

    private Criteria criteria;
    private TextView tvLocation;
    private RecyclerView recyclerView;
    private List<Official> officials;
    private RecyclerViewAdapter adapter;
    private LocationManager locationManager;
    private LocationListener locationListener;

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

    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);
        ((TextView) findViewById(R.id.bestText)).setText(bestProvider);

        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {
            ((TextView) findViewById(R.id.locText)).setText(
                    String.format(Locale.getDefault(),
                            "%.4f, %.4f", currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else {
            ((TextView) findViewById(R.id.locText)).setText(R.string.no_locs);
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
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

            if (locationManager != null) {
                // Checking if we can get location from NETWORK
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    List<Address> addresses = null;
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address ad = addresses.get(0);
                    String zipCode = ad.getPostalCode();
                    if (zipCode != null || zipCode.trim().length() > 0)
                        new OfficialMasterList(MainActivity.this).execute(ad.getPostalCode());
                    return;
                } else {
                    dataNotAvailable();
                }
            } else {
                Log.d(TAG, "getZipCode: locationManger is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateOfficialData(OfficialResult jsonResult) {
        officials.clear();
        if (jsonResult == null) {
            tvLocation.setText("No Data for location");
        } else {
            tvLocation.setText(TextUtils.isEmpty(jsonResult.getLocation()) ? "No Data for location" : jsonResult.getLocation());
            if (jsonResult.getOfficialList() != null) {
                officials.addAll(jsonResult.getOfficialList());
            }
        }
        adapter.notifyDataSetChanged();
    }
}