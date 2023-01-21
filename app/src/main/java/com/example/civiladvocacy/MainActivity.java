package com.example.civiladvocacy;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    private RecyclerView recyclerView;
    private OfficeAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ArrayList<Official> officialArrayList = new ArrayList<>();
    private TextView locationTV;
    private View noNetworkView;
    private static String locationString = "Unspecified Location";
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public String mainActivityAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noNetworkView = findViewById(R.id.noNetworkCL);
        noNetworkView.setVisibility(View.GONE);
        locationTV = findViewById(R.id.MAAddressTV);
        recyclerView = findViewById(R.id.myRV);
        adapter = new OfficeAdapter(this, officialArrayList);
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);


        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (hasNetworkConnection() && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            determineLocation();

        } else if (hasNetworkConnection()) {
            locationTV.setText("Enable location to get current location");
            Toast.makeText(this, "Please Enable location!", Toast.LENGTH_LONG).show();
        } else{
            downloadFailed();
            noNetworkView.setVisibility(View.VISIBLE);
        }

    }


    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }


    private void handleResult(ActivityResult result) {

    }

    public void downloadFailed() {
        //Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show();
        locationTV.setText("No Data for Location");
    }

    public void updateData(ArrayList<Official> officialArrayList) {
        this.officialArrayList.addAll(officialArrayList);
        adapter.notifyItemRangeInserted(0, officialArrayList.size());
    }

    public void updateMainActivityAddress(String nICity, String nIState, String nIZip) {
        //locationTV.setText(mainActivityAddress);
        if (nIZip != "" || nIState != "" || nICity != "") {
            mainActivityAddress = nICity + ", " + nIState + ", " + nIZip;
            locationTV.setText(String.format("%s, %s %s", nICity, nIState, nIZip));
        }
    }


    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official official = officialArrayList.get(pos);
        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("OFFICIAL", official);
        intent.putExtra("mainActivityAddress", mainActivityAddress);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.loc_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    void onInfoClicked() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_menu_btn) {
            onSearchClicked();
            return true;
        }
        if (item.getItemId() == R.id.about_menu_btn) {
            onInfoClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void onSearchClicked() {
        if(!hasNetworkConnection()){
            return;
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);
        // lambda can be used here (as is below)
        builder.setPositiveButton("OK", (dialog, id) -> {
            officialArrayList.clear();
            infoDownloader.getAPIData(et.getText().toString(), this);
            //locationTV.setText(et.getText().toString());

        });

        // lambda can be used here (as is below)
        builder.setNegativeButton("CANCEL", (dialog, id) -> {
        });

        builder.setTitle("EnterAddress: ");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void determineLocation() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        locationString = getPlace(location);
                        infoDownloader.getAPIData(locationString, this);
                        //textView.setText(locationString);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, "determineLocation: FAILED");
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    locationTV.setText("Acces Denied");
                }
            }
        }
    }

    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
//                    Locale.getDefault(),
//                    "%s, %s%n%nProvider: %s%n%n%.5f, %.5f",
//                    city, state, loc.getProvider(), loc.getLatitude(), loc.getLongitude()));
                    Locale.getDefault(), "%.5f, %.5f", loc.getLatitude(), loc.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}