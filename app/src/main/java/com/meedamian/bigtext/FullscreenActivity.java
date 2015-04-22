package com.meedamian.bigtext;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.meeDamian.lib.AutoResizeTextView;

public class FullscreenActivity extends Activity {

    private AutoResizeTextView artv;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen);

        artv = (AutoResizeTextView) findViewById(R.id.text);

        int visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(visibility);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void fixRecentsAppearance() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        String name = String.format("%s (%s)", text, getString(R.string.app_name));
        setTaskDescription(new ActivityManager.TaskDescription(name, icon, Color.WHITE));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        text = i.getStringExtra(Intent.EXTRA_TEXT);
        artv.setText(text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fixRecentsAppearance();
        }
    }
}
