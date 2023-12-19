package ott.spices;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ott.spices.database.DatabaseHelper;
import ott.spices.more.HelpActivity;
import ott.spices.network.RetrofitClient;
import ott.spices.network.apis.LoginApi;
import ott.spices.network.apis.SubscriptionApi;
import ott.spices.network.apis.UserDataApi;
import ott.spices.network.model.ActiveStatus;
import ott.spices.network.model.ActiveSubscription;
import ott.spices.network.model.SubscriptionHistory;
import ott.spices.network.model.User;
import ott.spices.utils.Constants;
import ott.spices.utils.HelperUtils;
import ott.spices.utils.PreferenceUtils;
import ott.spices.utils.RtlUtils;
import ott.spices.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoreActivity extends AppCompatActivity {
    private boolean status = false;
    Switch switchfamilycontent;

    private FirebaseAnalytics mFirebaseAnalytics;
    public boolean isDark = true;


    private DatabaseHelper db;
    private boolean vpnStatus;
    private HelperUtils helperUtils;
    private String id;
    private ProgressBar progressBar;
    ImageView back_iv;
    private String userId;
    private List<ActiveSubscription> activeSubscriptions = new ArrayList<>();
    CardView cv;
    LinearLayout ll_profiledetails, lnrSignout;
    public static boolean familycontent = false;
    /*
        CircularImageView imageAvtar;
    */
    ImageView imageAvtar, editProfile;
    TextView textName, textEmail, planstatus, planname, txtmobile;
    private Toolbar mToolbar;
    RelativeLayout relLogin, relNotLogin;

    String str_register_age = "";
    CardView cardfamilyzone;

    /*Navigation layout*/
    ImageView imgProfile, imgWatchLater, imgSubscription, imgSupport, imgSettings, imgHelp, imgRateUs, imgPrivacyPolicy, imgTermsCondition, imgRefundPolicy, imgSignout;
    TextView txtProfile, txtWatchLater, txtSubscription, txtSupport, txtSettings, txtHelp, txtRateUs, txtPrivacyPolicy, txtTermsCondition, txtRefundPolicy, txtSignout;

    private RewardedInterstitialAd rewardedInterstitialAd;
    private String TAG = "MoreActivity";

    LinearLayoutCompat lnrPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        db = new DatabaseHelper(MoreActivity.this);


        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_more);
        //check vpn connection
        helperUtils = new HelperUtils(MoreActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();

        userId = PreferenceUtils.getUserId(MoreActivity.this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAd();
            }
        });

/*

        Intent intent = getIntent();
        Uri data = intent.getData();


        if (data != null) {
            String param1 = data.getQueryParameter(R.string.admob_application_id);
            String param2 = data.getQueryParameter("param2");

        }
*/


        if (userId != null) {
            getSubscriptionHistory();
        }


        if (vpnStatus) {
            helperUtils.showWarningDialog(MoreActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }
        // To resolve cast button visibility problem. Check Cast State when app is open.
//        CastContext castContext = CastContext.getSharedInstance(this);
//        castContext.getCastState();

//        navMenuStyle = db.getConfigurationData().getAppConfig().getMenu();

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "more_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //update subscription
       /* if (PreferenceUtils.isLoggedIn(MoreActivity.this)) {
            PreferenceUtils.updateSubscriptionStatus(MoreActivity.this);
        }

        // checking storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()) {
                createDownloadDir();
            } else {
                requestPermission();
            }
        } else {
            createDownloadDir();
        }*/

        //----init---------------------------
        /*navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);*/
//        navHeaderLayout = findViewById(R.id.nav_head_layout);
        progressBar = findViewById(R.id.progress_bar);
//        themeSwitch = findViewById(R.id.theme_switch);
        cv = findViewById(R.id.cv);
        imageAvtar = findViewById(R.id.imageAvtar);
        textName = findViewById(R.id.textName);
        lnrSignout = findViewById(R.id.lnrSignout);
        ll_profiledetails = findViewById(R.id.ll_profiledetails);
        planstatus = findViewById(R.id.planstatus);
        planname = findViewById(R.id.planname);
        txtmobile = findViewById(R.id.txtmobile);
        editProfile = findViewById(R.id.editProfile);
        textEmail = findViewById(R.id.textEmail);
        back_iv = findViewById(R.id.back_iv);
        mToolbar = findViewById(R.id.subscription_toolbar);
        switchfamilycontent = findViewById(R.id.simpleSwitch);
        lnrPremium = findViewById(R.id.lnrPremium);

        /*Navigation Layout*/
        imgProfile = findViewById(R.id.imgProfile);
        imgWatchLater = findViewById(R.id.imgWatchLater);
        imgSubscription = findViewById(R.id.imgSubscription);
        imgSupport = findViewById(R.id.imgSupport);
        imgSettings = findViewById(R.id.imgSettings);
        imgHelp = findViewById(R.id.imgHelp);
        imgRateUs = findViewById(R.id.imgRateUs);
        imgPrivacyPolicy = findViewById(R.id.imgPrivacyPolicy);
        imgTermsCondition = findViewById(R.id.imgTermsCondition);
        imgRefundPolicy = findViewById(R.id.imgRefundPolicy);
        imgSignout = findViewById(R.id.imgSignout);

        txtProfile = findViewById(R.id.txtProfile);
        txtWatchLater = findViewById(R.id.txtWatchLater);
        txtSubscription = findViewById(R.id.txtSubscription);
        txtSupport = findViewById(R.id.txtSupport);
        txtSettings = findViewById(R.id.txtSettings);
        txtHelp = findViewById(R.id.txtHelp);
        txtRateUs = findViewById(R.id.txtRateUs);
        txtPrivacyPolicy = findViewById(R.id.txtPrivacyPolicy);
        txtTermsCondition = findViewById(R.id.txtTermsCondition);
        txtRefundPolicy = findViewById(R.id.txtRefundPolicy);
        txtSignout = findViewById(R.id.txtSignout);
        relLogin = findViewById(R.id.relLogin);
        relNotLogin = findViewById(R.id.relNotLogin);
        cardfamilyzone = findViewById(R.id.cardfamilyzone);


        try {
            //  Block of code to try
            SharedPreferences sharedPreferences_userage = getSharedPreferences(Constants.USER_REGISTER_AGE, MODE_PRIVATE);
            str_register_age = sharedPreferences_userage.getString("user_register_age", "20");

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            int user_age = Integer.parseInt(str_register_age);
            if (user_age > 18) {
                cardfamilyzone.setVisibility(View.GONE);

            }

        } catch (Exception e) {

        }


//        themeSwitch.setChecked(isDark);

        if (isDark) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));

        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (familycontent) {

            switchfamilycontent.setChecked(true);
        }


        switchfamilycontent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    familycontent = true;

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.FAMILYCONTENTSTATUS, MODE_PRIVATE).edit();
                    editor.putBoolean("familycontent", familycontent);
                    editor.apply();

                    Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    familycontent = false;
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.FAMILYCONTENTSTATUS, MODE_PRIVATE).edit();
                    editor.putBoolean("familycontent", familycontent);
                    editor.apply();

                    Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });


        status = PreferenceUtils.isLoggedIn(this);
        if (status) {
            PreferenceUtils.updateSubscriptionStatus(MoreActivity.this);

//            imgProfile.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgSubscription.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgWatchLater.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgHelp.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgSignout.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
//
//            txtProfile.setTextColor(getResources().getColor(R.color.white));
//            txtSubscription.setTextColor(getResources().getColor(R.color.white));
//            txtWatchLater.setTextColor(getResources().getColor(R.color.white));
//            txtHelp.setTextColor(getResources().getColor(R.color.white));
//            txtSignout.setTextColor(getResources().getColor(R.color.white));

            relLogin.setVisibility(View.VISIBLE);
            relNotLogin.setVisibility(View.GONE);
            lnrSignout.setVisibility(View.VISIBLE);
        } else {

//            imgProfile.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgSubscription.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgWatchLater.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgHelp.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
//            imgSignout.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
//
//            txtProfile.setTextColor(getResources().getColor(R.color.default_text));
//            txtSubscription.setTextColor(getResources().getColor(R.color.default_text));
//            txtWatchLater.setTextColor(getResources().getColor(R.color.default_text));
//            txtHelp.setTextColor(getResources().getColor(R.color.default_text));
//            txtSignout.setTextColor(getResources().getColor(R.color.default_text));

            relLogin.setVisibility(View.GONE);
            relNotLogin.setVisibility(View.VISIBLE);
            lnrSignout.setVisibility(View.GONE);
        }


        getActiveSubscriptionFromDatabase();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
                intent.putExtra("from", "more");
                startActivity(intent);
            }
        });

        planstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, SubscriptionActivity.class);
                startActivity(intent);
            }
        });

        lnrPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status) {
                    Intent intent = new Intent(MoreActivity.this, PurchasePlanActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
                    startActivity(intent);

                }

            }
        });

        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.putExtra("login_status", "user_login");
                startActivity(intent);
                // finish();
            }
        });

    }


    public void loadAd() {
        // Use the test ad unit ID to load an ad.
        //  RewardedInterstitialAd.load(MoreActivity.this, "ca-app-pub-1505000717930669/4487899176",
        RewardedInterstitialAd.load(MoreActivity.this, "ca-app-pub-3940256099942544/5354046379",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        Log.d(TAG, "Ad was loaded.");
                        rewardedInterstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;
                    }
                });
    }

    public void onProfileClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
            intent.putExtra("from", "more");
            startActivity(intent);
        } else {
            Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
            startActivity(intent);

        }
    }

    public void onNotLoginClick(View view) {
        Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void onSubscriptionClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, SubscriptionActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
            startActivity(intent);

        }
    }

    public void onWatchLaterClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, FavActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
            startActivity(intent);

        }
    }

    public void onOnSupportClick(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "8976395879"));
        startActivity(intent);

    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(MoreActivity.this, SettingsActivity.class);
        //Intent intent = new Intent(MoreActivity.this, Home.class);
        startActivity(intent);

    }

    public void onHelpClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, HelpActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
            startActivity(intent);

        }

    }

    public void onRateUsClick(View view) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    public void onPrivacyClick(View view) {
        Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
        intent.putExtra("from", "privacy");
        intent.putExtra("url", "https://bigshots.co.in/privacy-policy.php");
        intent.putExtra("title", "Privacy Policy");
        startActivity(intent);

    }

    public void onTermsClick(View view) {
        Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
        intent.putExtra("from", "terms");
        intent.putExtra("url", "https://bigshots.co.in//terms-condition.php");
        intent.putExtra("title", "Terms & Condition");
        startActivity(intent);

    }

    public void onRefundClick(View view) {
        Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
        intent.putExtra("from", "refund");
        intent.putExtra("url", "https://bigshots.co.in//refund-policy.php");
        intent.putExtra("title", "Refund Policy");
        startActivity(intent);

    }

    public void onSignOutClick(View view) {
        if (status) {
            new MaterialAlertDialogBuilder(MoreActivity.this)
                    .setMessage("Are you sure to logout ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logoutUser();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
        }

    }


    private void getActiveSubscriptionFromDatabase() {
        DatabaseHelper db = new DatabaseHelper(MoreActivity.this);
        if (db.getActiveStatusCount() > 0 && db.getUserDataCount() > 0) {
            //   activePlanLayout.setVisibility(View.VISIBLE);
            ActiveStatus activeStatus = db.getActiveStatusData();
            User user = db.getUserData();
           /* activeUserName.setText(user.getName());
            activeEmail.setText(user.getEmail());*/
            planname.setText(activeStatus.getPackageTitle() + " " + activeStatus.getExpireDate());
            //    activeExpireDate.setText(activeStatus.getExpireDate());

        } else {
            // activePlanLayout.setVisibility(View.GONE);
        }

    }


    private void getUserProfileData(String uid) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        User user = response.body();
                        String userProfileStatus = user.getProfile_status();

                        if (userProfileStatus != null) {
                            if (userProfileStatus.equals("0")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MoreActivity.this);
//                            builder.setMessage("Update your profile for better service.")
                                builder.setMessage("Verify your mobile number for better service.")
                                        .setCancelable(false)
                                        .setNegativeButton("Cancel", (dialog, id) -> {
                                            finishAffinity();
                                            dialog.dismiss();
                                        })
                                        .setPositiveButton("OK", (dialog1, id) -> {
                                            dialog1.dismiss();
                                            Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
                                            intent.putExtra("from", "main");
                                            startActivity(intent);
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }


    private void logoutUser() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<User> call = api.postLogoutStatus(AppConfig.API_KEY, id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut();
                        }

                        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                        editor.apply();
                        editor.commit();

                        DatabaseHelper databaseHelper = new DatabaseHelper(MoreActivity.this);
                        databaseHelper.deleteUserData();

                        PreferenceUtils.clearSubscriptionSavedData(MoreActivity.this);

                        Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        new ToastMsg(MoreActivity.this).toastIconError(response.body().getData());

                    }
                } else {

                    new ToastMsg(MoreActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                new ToastMsg(MoreActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }


    private void getSubscriptionHistory() {


        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<SubscriptionHistory> call = subscriptionApi.getSubscriptionHistory(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, Response<SubscriptionHistory> response) {
                SubscriptionHistory subscriptionHistory = response.body();
                if (response.code() == 200) {


                  /*  shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);*/

                    activeSubscriptions = subscriptionHistory.getActiveSubscription();
                    if (subscriptionHistory.getActiveSubscription().size() > 0) {

                        planstatus.setText("Subscribed");
                        lnrPremium.setVisibility(View.GONE);
                        // planname.setText(subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                        planstatus.setBackgroundColor(getResources().getColor(R.color.green));

                    } else {
                        planstatus.setText("Subscribe Now");
                        lnrPremium.setVisibility(View.VISIBLE);
                        // planname.setText(subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                        planstatus.setBackgroundColor(getResources().getColor(R.color.black));
                        // planname.setVisibility(View.GONE);

                    }

                    /*if (subscriptionHistory.getInactiveSubscription().size() > 0) {
                        mNoHistoryTv.setVisibility(View.GONE);
                        mSubHistoryLayout.setVisibility(View.VISIBLE);
                        inactiveSubscriptionAdapter = new InactiveSubscriptionAdapter(subscriptionHistory.getInactiveSubscription(),
                                SubscriptionActivity.this);
                        mInactiveRv.setAdapter(inactiveSubscriptionAdapter);

                    } else {
                        mNoHistoryTv.setVisibility(View.VISIBLE);
                        mSubHistoryLayout.setVisibility(View.GONE);
                    }*/

                /*    if (subscriptionHistory.getActiveSubscription().size() > 0) {
                        mNoHistoryTv.setVisibility(View.GONE);
                        mSubHistoryLayout.setVisibility(View.VISIBLE);
                        inactiveSubscriptionAdapter = new InactiveSubscriptionAdapter(subscriptionHistory.getActiveSubscription(),
                                SubscriptionActivity.this);
                        mInactiveRv.setAdapter(inactiveSubscriptionAdapter);

                    } else {
                        mNoHistoryTv.setVisibility(View.VISIBLE);
                        mSubHistoryLayout.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);*/
                }
            }

            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        id = PreferenceUtils.getUserId(MoreActivity.this);
        if (id != null) {
            getProfile();
            getUserProfileData(id);
        }
    }

    private void getProfile() {

        showLoader();

        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        progressBar.setVisibility(View.GONE);
                        User user = response.body();
                       /* Picasso.get()
                                .load(user.getImageUrl())
                                .placeholder(R.drawable.ic_account_circle_black)
                                .error(R.drawable.ic_account_circle_black)
                                .into(userIv);*/

                     /*   Glide.with(MoreActivity.this)
                                .load(user.getImageUrl())
                                .into(imageAvtar);
*/


                        Glide.with(MoreActivity.this)
                                //.load(getResources().getDrawable(R.drawable.ppapplogo))
//                                .load(getResources().getDrawable(R.drawable.useraccount))
                                .load(getResources().getDrawable(R.drawable.ic_baseline_account_circle_24))
                                .into(imageAvtar);

                        textName.setText(user.getName());
                        if (user.getCountry_code() != null) {
                            txtmobile.setText(String.format("%s%s", user.getCountry_code(), user.getPhone()));
                        } else {
                            txtmobile.setText(user.getPhone());
                        }
                        textEmail.setText(user.getEmail());

                       /* cv.setVisibility(View.VISIBLE);
                        editProfile.setVisibility(View.VISIBLE);*/
                        ll_profiledetails.setVisibility(View.VISIBLE);
                        planstatus.setVisibility(View.VISIBLE);
                        hideLoader();
                    }
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }


    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}