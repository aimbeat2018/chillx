package ott.chillx;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ott.chillx.network.model.Package;
import ott.chillx.database.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import dev.android.oneupi.OneUPIPayment;
import dev.android.oneupi.model.PaymentApp;
import okhttp3.ResponseBody;
import ott.chillx.network.RetrofitClient;
import ott.chillx.network.apis.PaymentApi;
import ott.chillx.network.apis.PaymentGatewayApi;
import ott.chillx.network.apis.SubscriptionApi;
import ott.chillx.network.model.ActiveStatus;
import ott.chillx.network.model.SubscriptionHistory;
import ott.chillx.network.model.User;
import ott.chillx.utils.Constants;
import ott.chillx.utils.PreferenceUtils;
import ott.chillx.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SpeedlightActivity extends AppCompatActivity {

    String orderID = "ORDER_ID";
    String token = "TOKEN";
    String order_token = "order_token";
    String cf_order_id = "cf_order_id";
    String order_status = "order_status";

    ProgressDialog dialog;
    String subscription_end_date = "";

    private static final String TAG = "SpeedlightActivity";
    String uid = "", uname = "", mobile = "", email = "", order_id = "", orderIdstr = "";
    static int min, max, create_otp;
    String plantamount = "";

    private Package aPackage;
    private DatabaseHelper databaseHelper;
    TextView txt_txn_id,
            txt_falied_reason;
    LinearLayout lnr_success,
            lnr_failed;
    private ProgressBar progressBar;
    // CleverTapAPI clevertapChergedInstance;
    String transactionId;
    private OneUPIPayment easyUpiPayment;
    Float float_plan_amount;
    String str_user_age = "";
    PaymentApp paymentApp;
    String from = "";
    WebView webView1;
    Button pay;
    private Handler timeoutHandler = new Handler();
    private Runnable timeoutRunnable;
    private static final long TIMEOUT = 10 * 60 * 1000; // 10 minutes
    private static boolean ispayment = false;

    private Uri uri1;
    String currenturl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedlight);

        webView1 = findViewById(R.id.webviewlight);
        pay = findViewById(R.id.btn_paylight);


        try {
            //  Block of code to try
            SharedPreferences sharedPreferences = SpeedlightActivity.this.getSharedPreferences(Constants.USER_AGE, MODE_PRIVATE);
            str_user_age = sharedPreferences.getString("user_age", "20");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent() != null) {
            aPackage = (Package) getIntent().getSerializableExtra("package");

            databaseHelper = new DatabaseHelper(this);
        }

        float_plan_amount = Float.valueOf(aPackage.getPrice());
        float_plan_amount = Float.valueOf(aPackage.getPrice());
       // float_plan_amount = Float.valueOf(1);
       // float_plan_amount = Float.valueOf(5);

        lnr_success = findViewById(R.id.lnr_success);
        lnr_failed = findViewById(R.id.lnr_failed);
        txt_txn_id = findViewById(R.id.txt_txn_id);
        txt_falied_reason = findViewById(R.id.txt_falied_reason);
        progressBar = findViewById(R.id.progressBar);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        User user = databaseHelper.getUserData();
        uid = user.getUserId();
        uname = user.getName();
//        mobile = "0000000000";
        mobile = user.getPhone();
        email = user.getEmail();

        min = 100000;
        max = 999999;
        Random r = new Random();
        create_otp = r.nextInt(max - min + 1) + min;
        order_id = String.valueOf(create_otp);
        orderID = order_id;



        //getToken(order_id, "1");

        //  payWithUpi();

      /*  pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // lightspeed_initiatetransaction(uname, uid, float_plan_amount, "desc",aPackage.getPlanId());
                lightspeed_initiatetransaction(uname, uid, float_plan_amount, "desc");

            }
        });*/

        lightspeed_initiatetransaction(uname, uid, float_plan_amount, "desc");


        // fetch_subpaisa_Payment_data(trn_id,float_plan_amount,uname,mobile,email);
        //  lightspeed_initiatetransaction(uname, uid, float_plan_amount, "desc",aPackage.getPlanId());


    }

    private void lightspeed_initiatetransaction(String uname, String uid, Float float_plan_amount, String test) {
        // private void lightspeed_initiatetransaction(String uname, String uid, Float float_plan_amount, String test, String planId) {
        // dialog.show();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentGatewayApi apiInterface = retrofit.create(PaymentGatewayApi.class);
        Call<ResponseBody> call = apiInterface.payment_initiate(AppConfig.API_KEY, uname, uid, String.valueOf(float_plan_amount), test);
        //Call<ResponseBody> call = apiInterface.payment_initiate(AppConfig.API_KEY, uname, uid, String.valueOf(float_plan_amount), test,planId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String trnid = jsonObject.getString("txn");
                        String apikey = jsonObject.getString("apiKey");
                        String apisecrete = jsonObject.getString("apiSecret");

                        openPaymentWebView(trnid, apikey, apisecrete);


                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ToastMsg(SpeedlightActivity.this).toastIconError("Something went wrong." + t.getMessage());
                // dialog.cancel();
                t.printStackTrace();
            }
        });

    }

    private void openPaymentWebView(String trnid, String apikey, String apisecrete) {


        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setDomStorageEnabled(true);

        //String webUrl = webView1.getUrl();

        WebSettings webSettings = webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);


        try {
            if( ispayment==false){

                webView1.loadUrl("https://dashboard.lightspeedpay.in/payment?txn=" + trnid + "&key=" + apikey + "&secret=" + apisecrete);

                Toast.makeText(getApplicationContext(), "Do not close app after payment completed", Toast.LENGTH_LONG).show();

            }
        }catch (Exception e){
            e.printStackTrace();
        }



        webView1.setWebViewClient(new WebViewClient() {


            public boolean shouldOverrideUrlLoading(WebView wv, String url) {

                try {


                    if (url.contains("pay?") && ispayment==false) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Log.d(TAG, "transaction url" + url);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);

                        return true;
                    } else if ((url.contains("status=COMPLETED") && ispayment==false)) {


                        Log.d(TAG, "transaction_status" + url);
                        try {
                            if (ispayment==false) {

                                saveChargeData(trnid, "Speedlight");

                                ispayment = true;

                            }
                        } catch (Exception e) {
                        }

                        return true;
                    }

                }catch (Exception e){}
                return false;
            }
        });

    }


    private void saveChargeData(String token, String from) {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(AppConfig.API_KEY, aPackage.getPlanId(),
                databaseHelper.getUserData().getUserId(),
                aPackage.getPrice(),
                // "1",
                token, str_user_age, from);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    plantamount = aPackage.getPrice();
                    updateActiveStatus();

                    //  getSubscriptionHistory(plantamount);

                } else {
                    new ToastMsg(SpeedlightActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(SpeedlightActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(SpeedlightActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                if (response.code() == 200) {

                    // ispayment = true;
                    ActiveStatus activeStatus = response.body();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(SpeedlightActivity.this).toastIconSuccess("Payment successfully done.");
                    progressBar.setVisibility(View.GONE);
                    // currenturl="paid";

                    /*firebase purchase event*/
                    FirebaseAnalytics mFirebaseAnalytics;
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(SpeedlightActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, aPackage.getPlanId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, aPackage.getName());
                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
                    bundle.putString(FirebaseAnalytics.Param.VALUE, aPackage.getPrice());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);

                    AppEventsLogger logger = AppEventsLogger.newLogger(SpeedlightActivity.this);
                    Bundle params = new Bundle();
                    params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Entertainment");
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, aPackage.getPrice());

                    logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED,
                            Double.parseDouble(aPackage.getPrice()),
                            params);

                    Intent intent = new Intent(SpeedlightActivity.this, MainActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                    webView1.clearHistory();
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(SpeedlightActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void getSubscriptionHistory(String plantamount) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<SubscriptionHistory> call = subscriptionApi.getSubscriptionHistory(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, Response<SubscriptionHistory> response) {
                SubscriptionHistory subscriptionHistory = response.body();
                if (response.code() == 200) {
/*
                    try {

                        if (subscriptionHistory.getActiveSubscription().size() > 0) {

                            HashMap<String, Object> paymentAction = new HashMap<String, Object>();
                            paymentAction.put("payment mode", "oneupi");
                            paymentAction.put("amount", plantamount);
                            paymentAction.put("subscription plan", subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                            paymentAction.put("Payment ID", subscriptionHistory.getActiveSubscription().get(0).getPaymentInfo());
                            paymentAction.put("Subscription ID", subscriptionHistory.getActiveSubscription().get(0).getSubscriptionId());
                            paymentAction.put("subscription plan id", subscriptionHistory.getActiveSubscription().get(0).getPlanId());
                            paymentAction.put("subscription Start date", subscriptionHistory.getActiveSubscription().get(0).getStartDate());
                            paymentAction.put("subscription End date", subscriptionHistory.getActiveSubscription().get(0).getExpireDate());
                            clevertapChergedInstance.pushEvent("Charged", paymentAction);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }
            }

            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            //Calling a javascript function in html page
            view.loadUrl("javascript:alert(showVersion('called by Android'))");
        }
    }


    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("LogTag", message);
            result.confirm();
            return true;
        }
    }


}