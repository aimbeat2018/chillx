package ott.chillx;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import ott.chillx.network.RetrofitClient;
import ott.chillx.network.apis.MovieRequestApi;
import ott.chillx.utils.RtlUtils;
import ott.chillx.utils.ToastMsg;
import ott.chillx.utils.Tools;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import ott.chillx.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchCompat, switchDarkMode;
    private LinearLayout tvTerms, shareLayout, movieRequestLayout;
    private ProgressBar progressBar;
    TextView txt_appVersion, txtGrievance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "settings_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        switchCompat = findViewById(R.id.notify_switch);
        tvTerms = findViewById(R.id.tv_term);
        shareLayout = findViewById(R.id.share_layout);
        movieRequestLayout = findViewById(R.id.movieRequestLayout);
//        progressBar = findViewById(R.id.code_progress);
        txt_appVersion = findViewById(R.id.txt_appVersion);
        txtGrievance = findViewById(R.id.txtGrievance);


        SharedPreferences preferences = getSharedPreferences("push", MODE_PRIVATE);
        switchCompat.setChecked(preferences.getBoolean("status", true));

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
            editor.putBoolean("status", isChecked);
            editor.apply();
            editor.commit();
        });

        tvTerms.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, TermsActivity.class);
            intent.putExtra("from", "terms");
            intent.putExtra("url", "https://chillx.in/term-condition.php");
            intent.putExtra("title", "Terms & Condition");
            startActivity(intent);
        });
        txtGrievance.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, GrievanceSupportActivity.class);
            startActivity(intent);
        });

        shareLayout.setOnClickListener(v -> Tools.share(SettingsActivity.this, ""));

        movieRequestLayout.setOnClickListener(v -> movieRequestDialog(isDark));

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txt_appVersion.setText("" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void movieRequestDialog(boolean isDark) {
        //open movie request dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.movie_request_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextInputEditText nameEt, emailEt, movieNameEt, messageEt;
        TextView title;
        Button sendButton, closeButton;
        nameEt = view.findViewById(R.id.nameEditText);
        emailEt = view.findViewById(R.id.emailEditText);
        movieNameEt = view.findViewById(R.id.movieNameEditText);
        messageEt = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        closeButton = view.findViewById(R.id.closeButton);
        title = view.findViewById(R.id.title);

        if (!isDark)
            title.setTextColor(getResources().getColor(R.color.colorPrimary));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                String movieName = movieNameEt.getText().toString().trim();
                String message = messageEt.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && !movieName.isEmpty() && !message.isEmpty()) {
                    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
                    MovieRequestApi api = retrofit.create(MovieRequestApi.class);
                    Call<ResponseBody> call = api.submitRequest(AppConfig.API_KEY, name, email, movieName, message);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                new ToastMsg(getApplicationContext()).toastIconSuccess("Request submitted");
                            } else {
                                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.something_went_text));
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.something_went_text));
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
