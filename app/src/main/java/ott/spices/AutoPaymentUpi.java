package ott.spices;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cashfree.pg.core.api.CFSession;
//import com.clevertap.android.sdk.CleverTapAPI;

import ott.spices.R;
import ott.spices.database.DatabaseHelper;
import ott.spices.network.model.Package;

public class AutoPaymentUpi extends AppCompatActivity {

    String orderID = "ORDER_ID";
    String token = "TOKEN";
    String order_token = "order_token";
    String cf_order_id = "cf_order_id";
    String order_status = "order_status";
    CFSession.Environment cfEnvironment = CFSession.Environment.PRODUCTION;

    ProgressDialog dialog;
    String subscription_end_date = "";

    private static final String TAG = "CashFreePaymentActivity";
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
//    CleverTapAPI clevertapChergedInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_payment_upi);
        creat_plan();
    }

    private void creat_plan() {

    }

}