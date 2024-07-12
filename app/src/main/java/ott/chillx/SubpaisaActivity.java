package ott.chillx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ott.chillx.network.RetrofitClient;
import ott.chillx.network.apis.PaymentApi;
import ott.chillx.network.apis.SubscriptionApi;
import ott.chillx.network.model.ActiveStatus;
import ott.chillx.network.model.User;
import com.sabpaisa.gateway.android.sdk.SabPaisaGateway;
import com.sabpaisa.gateway.android.sdk.interfaces.IPaymentSuccessCallBack;
import com.sabpaisa.gateway.android.sdk.models.TransactionResponsesModel;
import ott.chillx.database.DatabaseHelper;
import java.util.Random;

import okhttp3.ResponseBody;
import ott.chillx.utils.PreferenceUtils;
import ott.chillx.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import ott.chillx.network.model.Package;

/*
import com.sabpaisa.gateway.android.sdk.SabPaisaGateway;
import com.sabpaisa.gateway.android.sdk.interfaces.IPaymentSuccessCallBack;
import com.sabpaisa.gateway.android.sdk.models.TransactionResponsesModel;
*/

//public class SubpaisaActivity extends AppCompatActivity {

public class SubpaisaActivity extends AppCompatActivity implements IPaymentSuccessCallBack<TransactionResponsesModel> {
    ProgressDialog dialog;

    String uid = "", uname = "", mobile = "", email = "", order_id = "";
    Float float_plan_amount;
    String trn_id="";
    private Package aPackage;
    DatabaseHelper databaseHelper;
    String from = "";
    String plantamount = "";

    LinearLayout lnr_success,
            lnr_failed;
    private ProgressBar progressBar;

    TextView txt_txn_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subpaisa);
        SabPaisaGateway.Companion.syncImages(this);


        if (getIntent() != null) {
            aPackage = (Package) getIntent().getSerializableExtra("package");
            from = getIntent().getStringExtra("from");
            databaseHelper = new DatabaseHelper(this);
        }

        lnr_success = findViewById(R.id.lnr_success);
        lnr_failed = findViewById(R.id.lnr_failed);
        txt_txn_id = findViewById(R.id.txt_txn_id);
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
        float_plan_amount = Float.valueOf(aPackage.getPrice());


        Random rnd = new Random();
        int trans_id = 100000 + rnd.nextInt(900000);
        trn_id = Integer.toString(trans_id);


      //  openSDK(float_plan_amount,uname,mobile,email);

    }


  //  public void openSDK(Float float_plan_amount, String uname, String mobile, String email) {
    public void openSDK(View view) {
        SabPaisaGateway sabPaisaGateway1 =
                SabPaisaGateway.Companion.builder()
                        .setAmount(float_plan_amount)   //Mandatory Parameter
                        //.setAmount(Double.parseDouble("1"))   //Mandatory Parameter
                        .setFirstName(uname) //Mandatory Parameter
                        .setLastName("Name") //Mandatory Parameter
                        .setMobileNumber(mobile) //Mandatory Parameter
                        .setEmailId(email)//Mandatory Parameter
                        .setClientCode("ANLG42")   // Please use the credentials shared by your Account Manager  If not, please contact your Account Manage
                        .setSabPaisaPaymentScreen(true)
                        .setAesApiIv("lolsanfG42e6Dtbt")   // Please use the credentials shared by your Account Manager  If not, please contact your Account Manage
                        .setAesApiKey("dw0MKYVeGt9A3NBH")   // Please use the credentials shared by your Account Manager  If not, please contact your Account Manage
                        .setTransUserName("gabruappindia_16632")   // Please use the credentials shared by your Account Manager  If not, please contact your Account Manage
                        .setTransUserPassword("ANLG42_SP16632")  // Please use the credentials shared by your Account Manager  If not, please contact your Account Manage
                        .build();


      //  SabPaisaGateway.Companion.setInitUrlSabpaisa("https://stage-securepay.sabpaisa.in/SabPaisa/sabPaisaInit?v=1");
        SabPaisaGateway.Companion.setInitUrlSabpaisa("https://securepay.sabpaisa.in/SabPaisa/sabPaisaInit?v=1");
        SabPaisaGateway.Companion.setEndPointBaseUrlSabpaisa("https://securepay.sabpaisa.in");
        SabPaisaGateway.Companion.setTxnEnquiryEndpointSabpaisa("https://txnenquiry.sabpaisa.in");

        sabPaisaGateway1.init(this, this);
    }

    @Override
    public void onPaymentFail(@Nullable TransactionResponsesModel transactionResponsesModel) {
        Log.d("SABPAISA", "Payment Fail");

      //  Toast.makeText(this, "pay failed"+transactionResponsesModel.getSabpaisaTxnId()+ " and " +transactionResponsesModel.getStatus(), Toast.LENGTH_SHORT).show();

       //  Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();

       // Toast.makeText(SubpaisaActivity.this,transactionResponsesModel.getStatus(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(SubpaisaActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onPaymentSuccess(@Nullable TransactionResponsesModel transactionResponsesModel) {
        Log.d("SABPAISA", "Payment Success");

        //Toast.makeText(SubpaisaActivity.this,transactionResponsesModel.getStatus() , Toast.LENGTH_SHORT).show();


       // Toast.makeText(this, "Payment successfully done", Toast.LENGTH_SHORT).show();
        lnr_failed.setVisibility(View.GONE);
        lnr_success.setVisibility(View.VISIBLE);

        txt_txn_id.setText("Transaction Id : " + transactionResponsesModel.getBankTxnId());

        saveChargeData(transactionResponsesModel.getBankTxnId(), "subpaisa");



    }



    public void saveChargeData(String bankTxnId, String subpaisa) {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(AppConfig.API_KEY, aPackage.getPlanId(),
                databaseHelper.getUserData().getUserId(),
                aPackage.getPrice(),
                // "1",
                bankTxnId, "19", subpaisa);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                    plantamount = aPackage.getPrice();
                    updateActiveStatus();

                    //   getSubscriptionHistory(plantamount);

                } else {
                    new ToastMsg(SubpaisaActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(SubpaisaActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(SubpaisaActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(SubpaisaActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    // progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(SubpaisaActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(SubpaisaActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                //  progressBar.setVisibility(View.GONE);
            }
        });

    }




}
