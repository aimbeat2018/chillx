package ott.spices.nav_fragments;

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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.volcaniccoder.bottomify.BottomifyNavigationView;

import ott.spices.AppConfig;
import ott.spices.CpGoldFragment;
import ott.spices.HomeFragment;
import ott.spices.MainActivity;
import ott.spices.MoreActivity;
import ott.spices.R;
import ott.spices.SearchActivity;
import ott.spices.network.RetrofitClient;
import ott.spices.network.apis.SubscriptionApi;
import ott.spices.network.model.ActiveStatus;
import ott.spices.reels.ReelsListActivity;
import ott.spices.utils.PreferenceUtils;
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
    LinearLayoutCompat lnr_home, lnr_reels, lnr_download, lnr_account, lnr_search;
    AppCompatImageView img_home, img_reels, img_download, img_account, img_search;
    TextView txt_home, txt_reels, txt_download, txt_account, txt_search;
    FloatingActionButton fab_goals;
    //    CleverTapAPI clevertapscreenviewd;
    private AdView mAdView;
    BottomNavigationView bottomNav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            from = bundle.getString("from");
        }
        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchRootLayout = view.findViewById(R.id.search_root_layout);

        lnr_home = view.findViewById(R.id.lnr_home);
        lnr_reels = view.findViewById(R.id.lnr_reels);
        lnr_download = view.findViewById(R.id.lnr_download);
        lnr_account = view.findViewById(R.id.lnr_account);
        lnr_search = view.findViewById(R.id.lnr_search);
        img_home = view.findViewById(R.id.img_home);
        img_reels = view.findViewById(R.id.img_reels);
        img_download = view.findViewById(R.id.img_download);
        img_account = view.findViewById(R.id.img_account);
        img_search = view.findViewById(R.id.img_search);
        txt_home = view.findViewById(R.id.txt_home);
        txt_reels = view.findViewById(R.id.txt_reels);
        txt_download = view.findViewById(R.id.txt_download);
        txt_account = view.findViewById(R.id.txt_account);
        txt_search = view.findViewById(R.id.txt_search);

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

        sharedPreferences = activity.getSharedPreferences("push", MODE_PRIVATE);

        boolean isDark = sharedPreferences.getBoolean("dark", false);

        loadHomeFragment();
//        bottomMenu();

        lnr_home.setOnClickListener(v -> loadHomeFragment());

        lnr_search.setOnClickListener(v -> loadSearchFragment());

        lnr_reels.setOnClickListener(v -> loadReels());

        lnr_download.setOnClickListener(v -> loadDownloadFragment());

        lnr_account.setOnClickListener(v -> loadAccountFragment());
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
                    switch (i.getItemId()) {
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

                    return false;
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadHomeFragment() {
        txt_home.setTextColor(getResources().getColor(R.color.red));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_reels.setTextColor(getResources().getColor(R.color.white));
        img_reels.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        loadFragment(new HomeFragment());
    }

    public void loadReels() {
        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_reels.setTextColor(getResources().getColor(R.color.red));
        img_reels.setColorFilter(ContextCompat.getColor(activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_download.setTextColor(getResources().getColor(R.color.white));
        img_download.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_account.setTextColor(getResources().getColor(R.color.white));
        img_account.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        txt_search.setTextColor(getResources().getColor(R.color.white));
        img_search.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        Intent intent = new Intent(getActivity(), ReelsListActivity.class);
        startActivity(intent);
    }

    public void loadDownloadFragment() {
        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_reels.setTextColor(getResources().getColor(R.color.white));
        img_reels.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

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

        txt_reels.setTextColor(getResources().getColor(R.color.white));
        img_reels.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

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


    public void loadSearchFragment() {

        txt_home.setTextColor(getResources().getColor(R.color.white));
        img_home.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        txt_reels.setTextColor(getResources().getColor(R.color.white));
        img_reels.setColorFilter(ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

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