package ott.chillx.nav_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import ott.chillx.AppConfig;
import ott.chillx.MainActivity;
import ott.chillx.R;
import ott.chillx.models.CommonModels;
import ott.chillx.models.Movie;
import ott.chillx.network.RetrofitClient;
import ott.chillx.network.apis.FavouriteApi;
import ott.chillx.utils.ApiResources;
import ott.chillx.utils.NetworkInst;
import ott.chillx.utils.PreferenceUtils;
import ott.chillx.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FavoriteFragment extends Fragment {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private CommonGridWatchlistAdapter mAdapter;
    private List<CommonModels> list = new ArrayList<>();

    private ApiResources apiResources;

    private boolean isLoading = false;
    private ProgressBar progressBar;
    private int pageCount = 1, checkPass = 0;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoItem;

    private MainActivity activity;
    private LinearLayout searchRootLayout;

    private CardView searchBar;
    private ImageView menuIv, searchIv;
    private TextView pageTitle;
    String runtime = "";
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private RelativeLayout adView;
    private String userId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_movies, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.watch_list));

        initComponent(view);

        pageTitle.setText(getResources().getString(R.string.watch_list));

        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu));
            searchIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_search_white));
        }

//        loadAd();

    }


    private void initComponent(View view) {
        apiResources = new ApiResources();
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        progressBar = view.findViewById(R.id.item_progress_bar);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        tvNoItem = view.findViewById(R.id.tv_noitem);
        adView = view.findViewById(R.id.adView);

        searchRootLayout = view.findViewById(R.id.search_root_layout);
        searchBar = view.findViewById(R.id.search_bar);
        menuIv = view.findViewById(R.id.bt_menu);
        pageTitle = view.findViewById(R.id.page_title_tv);
        searchIv = view.findViewById(R.id.search_iv);

        userId = PreferenceUtils.getUserId(getContext());

        //----favorite's recycler view-----------------
        recyclerView = view.findViewById(R.id.recyclerView);
        //  recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CommonGridWatchlistAdapter(getContext(), list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    pageCount = pageCount + 1;
                    isLoading = true;
                    progressBar.setVisibility(View.VISIBLE);
                    getData(userId, pageCount);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    animateSearchBar(true);
                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    animateSearchBar(false);
                    controlsVisible = true;
                    scrolledDistance = 0;
                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.removeAllViews();
                pageCount = 1;
                list.clear();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(getContext()).isNetworkAvailable()) {
                    getData(userId, pageCount);
                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }

            }
        });


        if (new NetworkInst(getContext()).isNetworkAvailable()) {
            if (userId == null) {
                tvNoItem.setText(getString(R.string.please_login_first_to_see_favorite_list));
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            } else {
                getData(userId, pageCount);
            }
        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                activity.openDrawer();
            }
        });
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.goToSearchActivity();
            }
        });

    }




    /* private void loadAd() {
        AdsConfig adsConfig = new DatabaseHelper(getContext()).getConfigurationData().getAdsConfig();
        if (adsConfig.getAdsEnable().equals("1")) {

            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
                BannerAds.ShowAdmobBannerAds(activity, adView);

            } else if (adsConfig.getMobileAdsNetwork().equals(Constants.START_APP)) {
                BannerAds.showStartAppBanner(activity, adView);

            } else if(adsConfig.getMobileAdsNetwork().equals(Constants.NETWORK_AUDIENCE)) {
                BannerAds.showFANBanner(getActivity(), adView);
            }
        }
    }*/



    public void getData(String userID, int pageNum) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<List<Movie>> call = api.getFavoriteList(AppConfig.API_KEY, userID, pageNum);
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.code() == 200) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    if (response.body().size() == 0 && pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                        tvNoItem.setText("No items here");
                        pageCount = 1;
                    } else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        CommonModels models = new CommonModels();
                        models.setImageUrl(response.body().get(i).getThumbnailUrl());
                        models.setTitle(response.body().get(i).getTitle());
                        models.setQuality(response.body().get(i).getVideoQuality());

                        models.setMovieDuration(response.body().get(i).getRuntime());
                        //new change added in watchlist item
                        models.setDescription(response.body().get(i).getDescription());
                        // runtime=response.body().get(i).getRuntime();

                        if (response.body().get(i).getIsTvseries().equals("0")) {
                            models.setVideoType("movie");
                        } else {
                            models.setVideoType("tvseries");
                        }
                        models.setId(response.body().get(i).getVideosId());
                        list.add(models);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                if (userId == null) {
                    new ToastMsg(getActivity()).toastIconError(getString(R.string.please_login_first_to_see_favorite_list));
                } else {
                    new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                }

                if (pageCount == 1) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchRootLayout.getHeight()) : 0;
        searchRootLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

}