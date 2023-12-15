package ott.bigshots.nav_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.volcaniccoder.bottomify.BottomifyNavigationView;
import com.volcaniccoder.bottomify.OnNavigationItemChangeListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import ott.bigshots.AppConfig;
import ott.bigshots.CpGoldFragment;
import ott.bigshots.HomeFragment;
import ott.bigshots.MainActivity;
import ott.bigshots.MoreActivity;
import ott.bigshots.OtpActivity;
import ott.bigshots.R;
import ott.bigshots.SearchActivity;
import ott.bigshots.TvSeriesFragment;
import ott.bigshots.database.DatabaseHelper;
import ott.bigshots.network.RetrofitClient;
import ott.bigshots.network.apis.SubscriptionApi;
import ott.bigshots.network.model.ActiveStatus;
import ott.bigshots.reels.ReelsListActivity;
import ott.bigshots.utils.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainHomeFragment extends Fragment {
    private MainActivity activity;
    //    private MoreActivity activity1;
    private BottomifyNavigationView bottomifyNavigationViewDark, bottomifyNavigationViewLight;
    LinearLayout searchRootLayout;
    String from = "";
    SharedPreferences sharedPreferences;
    LinearLayoutCompat lnr_home, lnr_gold, lnr_watchlist, lnr_download, lnr_account, lnr_search;
    AppCompatImageView img_home, img_gold, img_watchlist, img_download, img_account, img_search;
    TextView txt_home, txt_gold, txt_watchlist, txt_download, txt_account, txt_search;
    FloatingActionButton fab_goals;
//    CleverTapAPI clevertapscreenviewd;
    private AdView mAdView;
    ChipNavigationBar bottomNav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            from = bundle.getString("from");
        }

//        if (from.equals("main")) {
        activity = (MainActivity) getActivity();
        /*} else {
            activity1 = (MoreActivity) getActivity();
        }*/
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchRootLayout = view.findViewById(R.id.search_root_layout);
        bottomNav = view.findViewById(R.id.bottomNav);

//        clevertapscreenviewd = CleverTapAPI.getDefaultInstance(getActivity());


        //admob banner ads
        MobileAds.initialize(getActivity());
        MobileAds.initialize(getContext(), initializationStatus -> {
        });
        AdView adView = new AdView(getContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.admob_banner_unit_id));
        // adView.setAdUnitId("ca-app-pub-1307905966777808/6708516251");
        //  adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");//test unit id
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //for showing banner ads to package status inactive user//visibility set to mAdView
        if (PreferenceUtils.getUserId(activity) != null) {
            if (!PreferenceUtils.getUserId(activity).equals(""))
                userPackageStatus(PreferenceUtils.getUserId(activity));

        }


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.

                mAdView.setVisibility(View.GONE);

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });


//        if (activity != null)
        sharedPreferences = activity.getSharedPreferences("push", MODE_PRIVATE);
//        else
//            sharedPreferences = activity1.getSharedPreferences("push", MODE_PRIVATE);

        boolean isDark = sharedPreferences.getBoolean("dark", false);

       /* if (isDark) {
            //bottomifyNavigationView
            bottomifyNavigationViewDark.setVisibility(View.VISIBLE);
            bottomifyNavigationViewDark.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        } else {
            //bottomifyNavigationView light
            bottomifyNavigationViewLight.setVisibility(View.VISIBLE);
            bottomifyNavigationViewLight.setBackgroundColor(getResources().getColor(R.color.white));
        }*/

        bottomNav.setItemSelected(R.id.home,
                true);
        loadFragment(new HomeFragment());
        bottomMenu();

    }


    public void userPackageStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        ActiveStatus activeStatus = response.body();
                        if (activeStatus.getStatus().equals("active")) {
                            mAdView.setVisibility(View.GONE);
                        } else {
                            mAdView.setVisibility(View.VISIBLE);

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void bottomMenu() {
        bottomNav.setOnItemSelectedListener
                (i -> {
                    switch (i) {
                        case R.id.home:
                            loadFragment(new HomeFragment());
                            break;
                        case R.id.search:
                            Intent intent = new Intent(getActivity(), SearchActivity.class);
                            startActivity(intent);

                            break;
                        case R.id.reels:
                            Intent intent1 = new Intent(getActivity(), ReelsListActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.download:

                            loadFragment(new DownloadNewFragment());
                            break;
                        case R.id.account:
                            Intent intent2 = new Intent(getActivity(), MoreActivity.class);
                            startActivity(intent2);
                            break;
                    }

                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadHomeFragment() {
        txt_home.setTextColor(getResources().getColor(R.color.red));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_gold.setTextColor(getResources().getColor(R.color.white));
        img_gold.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_watchlist.setTextColor(getResources().getColor(R.color.white));
        img_watchlist.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        loadFragment(new HomeFragment());
    }

    public void loadGold() {
        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_gold.setTextColor(getResources().getColor(R.color.red));
        img_gold.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_watchlist.setTextColor(getResources().getColor(R.color.white));
        img_watchlist.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        loadFragment(new CpGoldFragment());
    }

    public void loadWatchlist() {
        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_gold.setTextColor(getResources().getColor(R.color.white));
        img_gold.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_watchlist.setTextColor(getResources().getColor(R.color.red));
        img_watchlist.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


//        loadFragment(new FavoriteFragment());

        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    public void loadDownloadFragment() {
        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_gold.setTextColor(getResources().getColor(R.color.white));
        img_watchlist.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_watchlist.setTextColor(getResources().getColor(R.color.white));
        img_gold.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.red));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        loadFragment(new DownloadNewFragment());
    }


    public void loadAccountFragment() {

        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_gold.setTextColor(getResources().getColor(R.color.white));
        img_watchlist.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_watchlist.setTextColor(getResources().getColor(R.color.white));
        img_gold.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_account.setTextColor(getResources().getColor(R.color.red));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        Intent intent = new Intent(getActivity(), MoreActivity.class);
        startActivity(intent);

        //loadFragment(new AccountFragment());
    }


    public void loadsearchFragment() {

        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_gold.setTextColor(getResources().getColor(R.color.white));
        img_watchlist.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_watchlist.setTextColor(getResources().getColor(R.color.white));
        img_gold.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_search.setTextColor(getResources().getColor(R.color.red));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);


        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);


        //loadFragment(new AccountFragment());
    }


    //----load fragment----------------------
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;

    }


}