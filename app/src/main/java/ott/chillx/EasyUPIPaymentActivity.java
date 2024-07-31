package ott.chillx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.clevertap.android.sdk.CleverTapAPI;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.PaymentApp;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.util.Random;

import okhttp3.ResponseBody;
import ott.chillx.network.RetrofitClient;
import ott.chillx.network.apis.PaymentApi;
import ott.chillx.network.apis.SubscriptionApi;
import ott.chillx.network.model.ActiveStatus;
import ott.chillx.network.model.Package;
import ott.chillx.network.model.SubscriptionHistory;
import ott.chillx.network.model.User;
import ott.chillx.utils.Constants;
import ott.chillx.utils.PreferenceUtils;
import ott.chillx.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class EasyUPIPaymentActivity extends AppCompatActivity implements PaymentStatusListener {
    String orderID = "ORDER_ID";
    String token = "TOKEN";
    String order_token = "order_token";
    String cf_order_id = "cf_order_id";
    String order_status = "order_status";

    ProgressDialog dialog;

    String subscription_end_date = "";

    private static final String TAG = "OneUPIPaymentActivity";
    String uid = "", uname = "", mobile = "", email = "", order_id = "", orderIdstr = "";
    static int min, max, create_otp;
    String plantamount = "";

    private Package aPackage;
    private ott.chillx.database.DatabaseHelper databaseHelper;

    TextView txt_txn_id,
            txt_falied_reason;
    LinearLayout lnr_success,
            lnr_failed;
    private ProgressBar progressBar;
//    CleverTapAPI clevertapChergedInstance;

    String transactionId;
    Float float_plan_amount;
    String str_user_age = "";
    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_upipayment);


        try {
            //  Block of code to try
            SharedPreferences sharedPreferences = EasyUPIPaymentActivity.this.getSharedPreferences(Constants.USER_AGE, MODE_PRIVATE);
            str_user_age = sharedPreferences.getString("user_age", "20");

        } catch (Exception e) {
            e.printStackTrace();
        }

        from = getIntent().getStringExtra("from");

        if (getIntent() != null) {
            aPackage = (Package) getIntent().getSerializableExtra("package");
            databaseHelper = new ott.chillx.database.DatabaseHelper(this);
        }

        float_plan_amount = Float.valueOf(aPackage.getPrice());

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

        payWithUpi();

    }


    private void payWithUpi() {

        transactionId = "TID" + System.currentTimeMillis();


        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa("bigshotsmoviesandweb.39772315@hdfcbank")
                .setPayeeName("Bigshots movies and web series")
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionId)
                .setDescription("For Subscription")
                .setAmount(String.valueOf(float_plan_amount))
                .build();

        try {

            switch (from) {
                case "google":
                    easyUpiPayment.setDefaultPaymentApp(PaymentApp.GOOGLE_PAY);
                    break;
                case "phonepe":
                    easyUpiPayment.setDefaultPaymentApp(PaymentApp.PHONE_PE);
                    break;
                case "paytm":
                    easyUpiPayment.setDefaultPaymentApp(PaymentApp.PAYTM);
                    break;
            }

            easyUpiPayment.setPaymentStatusListener(this);
            easyUpiPayment.startPayment();

        } catch (Exception exception) {
            exception.printStackTrace();
            toast("Error: " + exception.getMessage());

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onTransactionCancelled() {

        // Payment Cancelled by User
        toast("Cancelled by user");

    }

    @Override
    public void onAppNotFound() {

    }


    @Override
    public void onTransactionCompleted(@NonNull TransactionDetails transactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString());

//        switch (transactionDetails.getStatus()) {
//            case SUCCESS:
//                onTransactionSuccess();
//                break;
//            case FAILURE:
//                onTransactionFailed();
//                break;
//            case SUBMITTED:
//                onTransactionSubmitted();
//                break;
//        }

    }


    public void onTransactionSuccess() {
        // Payment Success
        lnr_failed.setVisibility(View.GONE);
        lnr_success.setVisibility(View.VISIBLE);

        txt_txn_id.setText("Transaction Id : " + transactionId);

        saveChargeData(transactionId, "oneupi");
        //  saveChargeData(orderID, "oneupi");

        toast("Success");
        Log.d("UPI", "responseStr: " + transactionId);
//  new Transaction(SelectPlanActivity.this).purchasedItem(planId, transactionId, "UPI");
    }


    public void onTransactionSubmitted() {
        // Payment Pending
        toast("Pending | Submitted");
    }

    public void onTransactionFailed() {
        // Payment Failed
        toast("Failed");

        lnr_failed.setVisibility(View.VISIBLE);
        lnr_success.setVisibility(View.GONE);

        /*   txt_txn_id.setText(cfErrorResponse.getMessage());*/


        new Handler().postDelayed(() -> finish(), 1000);

    }


    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void saveChargeData(String token, String from) {
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
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                    plantamount = aPackage.getPrice();
                    updateActiveStatus();

                    getSubscriptionHistory(plantamount);

                } else {
                    new ToastMsg(EasyUPIPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(EasyUPIPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(EasyUPIPaymentActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                    ott.chillx.database.DatabaseHelper db = new ott.chillx.database.DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(EasyUPIPaymentActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    progressBar.setVisibility(View.GONE);

                    /*firebase purchase event*/
                    FirebaseAnalytics mFirebaseAnalytics;
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(EasyUPIPaymentActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, aPackage.getPlanId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, aPackage.getName());
                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
                    bundle.putString(FirebaseAnalytics.Param.VALUE, aPackage.getPrice());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);

                    AppEventsLogger logger = AppEventsLogger.newLogger(EasyUPIPaymentActivity.this);
                    Bundle params = new Bundle();
                    params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Entertainment");
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, aPackage.getPrice());

                    logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED,
                            54.23,
                            params);

                    Intent intent = new Intent(EasyUPIPaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(EasyUPIPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
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
            public void onResponse(Call<SubscriptionHistory> call, retrofit2.Response<SubscriptionHistory> response) {
                SubscriptionHistory subscriptionHistory = response.body();
                if (response.code() == 200) {

                    try {
//
//                        if (subscriptionHistory.getActiveSubscription().size() > 0) {
//
//                            HashMap<String, Object> paymentAction = new HashMap<String, Object>();
//                            paymentAction.put("payment mode", "oneupi");
//                            paymentAction.put("amount", plantamount);
//                            paymentAction.put("subscription plan", subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
//                            paymentAction.put("Payment ID", subscriptionHistory.getActiveSubscription().get(0).getPaymentInfo());
//                            paymentAction.put("Subscription ID", subscriptionHistory.getActiveSubscription().get(0).getSubscriptionId());
//                            paymentAction.put("subscription plan id", subscriptionHistory.getActiveSubscription().get(0).getPlanId());
//                            paymentAction.put("subscription Start date", subscriptionHistory.getActiveSubscription().get(0).getStartDate());
//                            paymentAction.put("subscription End date", subscriptionHistory.getActiveSubscription().get(0).getExpireDate());
//                            clevertapChergedInstance.pushEvent("Charged", paymentAction);
//
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }


}