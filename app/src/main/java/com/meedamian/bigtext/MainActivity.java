package com.meedamian.bigtext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText et;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            fab.setLineMorphingState((fab.getLineMorphingState() + 1) % 2, true);
            showFullscreen();
            }
        });

        et = (EditText) findViewById(R.id.userText);

        if (savedInstanceState!=null) {
            et.setText(savedInstanceState.getString("text"));
            et.setSelection(et.getText().length());
        }

        handleIncomingTextShare();
    }

    private void handleIncomingTextShare() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type!=null && "text/plain".equals(type)) {

            String text = intent.getStringExtra(Intent.EXTRA_TEXT);

            if(sp.getBoolean(SettingsActivity.MAP_SP_IDX, false)) {
                Pattern p = Pattern.compile("(.*)\n\n(https?://goo.gl(/maps)?/[a-zA-Z0-9]*)");
                Matcher m = p.matcher(text);

                if(m.matches()) {
                    et.setText(m.group(1));
                    return;
                }
            }
            et.setText(text);
        }
    }

    private void showFullscreen() {
        Intent i = new Intent(this, FullscreenActivity.class);
        i.putExtra(Intent.EXTRA_TEXT, getUserText());
        startActivity(i);
    }

    protected void onSaveInstanceState(Bundle extra) {
        super.onSaveInstanceState(extra);
        extra.putString("text", getUserText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.buy_premium:
                return true;

            case R.id.action_print:
                doWebViewPrint();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getUserText() {
        return getUserText(false);
    }

    private String getUserText(boolean escaped) {
        String t = et.getText().toString().trim();
        return escaped
            ? t.replaceAll("'", "\\\\'")
            : t;
    }

    private WebView mWebView;

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void doWebViewPrint() {
        WebView webView = new WebView(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/bigtext.html");

        class JavaScriptInterface {
            @JavascriptInterface
            public void ready() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    Log.d("lalala", "callback");
                    createWebPrintJob(mWebView);
                    mWebView = null;
                    }
                });

            }
        }
        webView.addJavascriptInterface(new JavaScriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:setText('" + getUserText(true) + "')");
            }
        });

        mWebView = webView;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private PrintDocumentAdapter getPrintAdapter(WebView wv) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ? wv.createPrintDocumentAdapter("Let's get funky!")
            : wv.createPrintDocumentAdapter();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = getPrintAdapter(webView);

        printManager.print(
            getString(R.string.app_name) + " Document",
            printAdapter,
            new PrintAttributes
                .Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
                .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                .build()
        );
    }
}


