package com.meedamian.bigtext;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.meedamian.bigtext.util.IabHelper;
import com.meedamian.bigtext.util.IabResult;
import com.meedamian.bigtext.util.Inventory;
import com.meedamian.bigtext.util.Purchase;

import java.util.ArrayList;
import java.util.List;

public class IapHelper {

    private static final String TAG = "lalala";
    private static final String SKU_PREMIUM = "premium";
    private static final String SKU_TEST_PREMIUM = "test_premium";

    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtCKVX9dpA6JP8WmAtsEdwNEWtXeVZYsc4lfk8gfINLfgxXsMrWovnyovKtAioJIRn6880vEmxtn1sfh22/q7X0tD196C7g3MzvAorWn3eVh1FHqPu9pUBY/Uy4EPf2l25wnK+HVSwwMnKsFYu13SueyXBc0L2CMgutWbPMPP6XlQHWaKqLwBlQP4YJYBOxT1P6v5d1Rpkizp8s8kk/lDG4fPvFLTsn9PEVkuzocmpqX2YDj0M6QPkhMLAGZk6eYAfF3EGe/6WHA82qbsxSmmjsQDtTegfgxASakrnr96E/8zlLny3DW6MDLTB9/QT9HmdvvwBCX+e+d4uwoDo1+BQwIDAQAB";
    private IabHelper mHelper;

    private Context context;

    // Does the user have the premium upgrade?
    private boolean mIsPremium = false;

    private IapHelper(Context c) {
        context = c;

        mHelper = new IabHelper(context, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                List<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.add(SKU_PREMIUM);
                additionalSkuList.add(SKU_TEST_PREMIUM);
                mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
            }
        });
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "SKU_PREMIUM" : "NOT SKU_PREMIUM"));

//            Log.d(TAG, inventory.getSkuDetails(SKU_PREMIUM).toString());
        }
    };




    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    public boolean isPremium() {
        return mIsPremium;
    }
    public void buyPremium() {
        mHelper.launchPurchaseFlow((Activity) context, SKU_PREMIUM, 10001, mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isFailure()) {
            Log.d(TAG, "Error purchasing: " + result);

        } else if (purchase.getSku().equals(SKU_TEST_PREMIUM)) {
            // consume the gas and update the UI
            Log.d(TAG, "test-premium");

        } else if (purchase.getSku().equals(SKU_PREMIUM)) {
            // give user access to premium content and update the UI
            Log.d(TAG, "premium");

        }
        Log.d(TAG, result.toString());
        Log.d(TAG, purchase.toString());
        }
    };


    public void dispose() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }





    private static IapHelper instance;
    public static IapHelper getInstance(Context c) {
        if (instance == null)
            instance = new IapHelper(c);

        return instance;
    }
}
