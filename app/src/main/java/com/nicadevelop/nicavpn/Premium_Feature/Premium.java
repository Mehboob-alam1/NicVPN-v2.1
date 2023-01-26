package com.nicadevelop.nicavpn.Premium_Feature;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.nicadevelop.nicavpn.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Premium extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = Premium.class.getSimpleName().concat(":SUBS");
    private BillingClient mBillingClient;

    private SkuDetails weeklysub;
    private SkuDetails monthlysub;
    private SkuDetails yearlysub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.premium);
        initUI();

        mBillingClient = BillingClient.newBuilder(this)
                .setListener((billingResult, purchases) -> {
                    // To be implemented in a later section.
                    if (billingResult.getResponseCode() ==
                            BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    } else if (billingResult.getResponseCode() ==
                            BillingClient.BillingResponseCode.USER_CANCELED) {
                        // Handle an error caused by a user cancelling the purchase flow.
                    } else {
                        // Handle any other error codes.
                    }

                })
                .enablePendingPurchases()
                .build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {

                // once the client has been successfully initialized we will retrieve the products
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                    getProducts();
            }

            @Override
            public void onBillingServiceDisconnected() {

                Log.d(TAG, "onBillingServiceDisconnected");
            }
        });
    }

    private void initUI() {

        findViewById(R.id.premium_low).setOnClickListener(this);
        findViewById(R.id.premium_mid).setOnClickListener(this);
        findViewById(R.id.premium_high).setOnClickListener(this);
    }

    private void handlePurchase(Purchase purchase) {

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener =
                    billingResult -> {

                        // the user's purchase has been successful
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)

                            //TODO set the user's premium status to true
                            Log.d(TAG, "successfully acknowledged product");
                    };

            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams,
                        acknowledgePurchaseResponseListener);
            }
        }
    }

    /**
     * We retrieve the products by giving the SKUs
     */
    private void getProducts() {

        Log.d(TAG, "getProducts");
        final List<String> skuList = new ArrayList<>();
        skuList.add("1week");
        skuList.add("1month");
        skuList.add("1year");

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        mBillingClient.querySkuDetailsAsync(params,
                (billingResult, skuDetailsList) -> {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && skuDetailsList != null) {

                        if (!skuDetailsList.isEmpty()) {

                            for (SkuDetails skuDetails : skuDetailsList) {

                                switch (skuDetails.getSku()) {

                                    case "1week":
                                        weeklysub = skuDetails;
                                        break;

                                    case "1month":
                                        monthlysub = skuDetails;
                                        break;

                                    case "1year":
                                        yearlysub = skuDetails;
                                        break;
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error in retrieving " +
                                "products from store", Toast.LENGTH_SHORT).show();
                    }

                });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.premium_low:

                if (weeklysub != null) {

                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(weeklysub)
                            .build();

                    int responseCode = mBillingClient.launchBillingFlow(this,
                            billingFlowParams).getResponseCode();

                    if (responseCode != BillingClient.BillingResponseCode.OK)
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                break;

            case R.id.premium_mid:

                if (monthlysub != null) {

                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(monthlysub)
                            .build();

                    int responseCode = mBillingClient.launchBillingFlow(this,
                            billingFlowParams).getResponseCode();

                    if (responseCode != BillingClient.BillingResponseCode.OK)
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                break;

            case R.id.premium_high:

                if (yearlysub != null) {

                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(yearlysub)
                            .build();

                    int responseCode = mBillingClient.launchBillingFlow(this,
                            billingFlowParams).getResponseCode();

                    if (responseCode != BillingClient.BillingResponseCode.OK)
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                break;

        }
    }
}