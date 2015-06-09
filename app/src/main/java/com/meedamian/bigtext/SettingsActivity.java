package com.meedamian.bigtext;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.serchinastico.coolswitch.CoolSwitch;


public class SettingsActivity extends AppCompatActivity {

    public static final String MAP_SP_IDX = "coolMap";
    public static final String OTHER_SP_IDX = "coolOther";

    private SharedPreferences sp;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

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
                sp.edit()
                    .putBoolean(MAP_SP_IDX, true)
                    .apply();
            }

            @Override
            public void onUncheckedAnimationFinished() {
                sp.edit()
                    .putBoolean(MAP_SP_IDX, false)
                    .apply();
            }
        });

        otherSwitch.addAnimationListener(new CoolSwitch.AnimationListener() {
            @Override
            public void onCheckedAnimationFinished() {
                sp.edit()
                    .putBoolean(OTHER_SP_IDX, true)
                    .apply();
            }

            @Override
            public void onUncheckedAnimationFinished() {
                sp.edit()
                    .putBoolean(OTHER_SP_IDX, false)
                    .apply();
            }
        });
    }
}
