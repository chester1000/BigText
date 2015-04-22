package com.meedamian.bigtext;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.serchinastico.coolswitch.CoolSwitch;


public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String MAP_SP_IDX = "coolMap";
    public static final String OTHER_SP_IDX = "coolOther";

    private MapView mapView;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    private SharedPreferences sp;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        initMapStuff(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        CoolSwitch smartMap = (CoolSwitch) findViewById(R.id.smart_maps);
        CoolSwitch otherSwitch = (CoolSwitch) findViewById(R.id.cool_switch_foo);

        smartMap.setChecked(sp.getBoolean(MAP_SP_IDX, false));
        otherSwitch.setChecked(sp.getBoolean(OTHER_SP_IDX, false));

        smartMap.addAnimationListener(new CoolSwitch.AnimationListener() {
            @Override
            public void onCheckedAnimationFinished() {
                updateLocation();
                sp.edit()
                    .putBoolean(MAP_SP_IDX, false)
                    .apply();
            }

            @Override
            public void onUncheckedAnimationFinished() {
                sp.edit()
                    .putBoolean(MAP_SP_IDX, true)
                    .apply();
            }
        });

        otherSwitch.addAnimationListener(new CoolSwitch.AnimationListener() {
            @Override
            public void onCheckedAnimationFinished() {
                updateLocation();
                sp.edit()
                    .putBoolean(OTHER_SP_IDX, false)
                    .apply();
            }

            @Override
            public void onUncheckedAnimationFinished() {
                sp.edit()
                    .putBoolean(OTHER_SP_IDX, true)
                    .apply();
            }
        });
    }

    private void initMapStuff(Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.setMyLocationEnabled(false);

        MapsInitializer.initialize(this);
    }

    private void updateLocation() {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
            ), 15);

            map.animateCamera(cameraUpdate);
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}
