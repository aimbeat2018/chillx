
package ott.chillx.models.home_content;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import ott.chillx.database.homeContent.converters.CountryConverter;
import ott.chillx.database.homeContent.converters.FeaturedTvConverter;
import ott.chillx.database.homeContent.converters.FeaturesGenreConverter;
import ott.chillx.database.homeContent.converters.GenreConverter;
import ott.chillx.database.homeContent.converters.GoldConverter;
import ott.chillx.database.homeContent.converters.LatestMovieConverter;
import ott.chillx.database.homeContent.converters.PopularMovieConverter;
import ott.chillx.database.homeContent.converters.PopularStarsConverter;
import ott.chillx.database.homeContent.converters.SliderTypeConverter;
import ott.chillx.database.homeContent.converters.TvSeriesConverter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "home_content_table")
public class HomeContent {
    @PrimaryKey()
    @ColumnInfo(name = "home_content_id")
    private int homeContentId;

    @ColumnInfo(name = "slider")
    @TypeConverters(SliderTypeConverter.class)
    @SerializedName("slider")
    @Expose
    private Slider slider;

    @ColumnInfo(name = "all_country")
    @TypeConverters(CountryConverter.class)
    @SerializedName("all_country")
    @Expose
    private List<AllCountry> allCountry = null;

    @ColumnInfo(name = "all_genre")
    @TypeConverters(GenreConverter.class)
    @SerializedName("all_genre")
    @Expose
    private List<AllGenre> allGenre = null;

    @ColumnInfo(name = "featured_tv_channel")
    @TypeConverters(FeaturedTvConverter.class)
    @SerializedName("featured_tv_channel")
    @Expose
    private List<FeaturedTvChannel> featuredTvChannel = null;

    @ColumnInfo(name = "latest_movies")
    @TypeConverters(LatestMovieConverter.class)
    @SerializedName("latest_movies")
    @Expose
    private List<LatestMovie> latestMovies = null;

    @ColumnInfo(name = "papular_movie")
    @TypeConverters(PopularMovieConverter.class)
    @SerializedName("papular_movie")
    @Expose
    private List<PopularMovie> papularmovie = null;

    @ColumnInfo(name = "latest_tvseries")
    @TypeConverters(TvSeriesConverter.class)
    @SerializedName("latest_tvseries")
    @Expose
    private List<LatestTvseries> latestTvseries = null;

    @ColumnInfo(name = "features_genre_and_movie")
    @TypeConverters(FeaturesGenreConverter.class)
    @SerializedName("features_genre_and_movie")
    @Expose
    private List<FeaturesGenreAndMovie> featuresGenreAndMovie = null;

    @ColumnInfo(name = "popular_stars")
    @TypeConverters(PopularStarsConverter.class)
    @SerializedName("popular_stars")
    @Expose
    private List<PopularStars> popularStarsList = null;

    @ColumnInfo(name = "latest_gold_tvseries")
    @TypeConverters(GoldConverter.class)
    @SerializedName("latest_gold_tvseries")
    @Expose
    private List<LatestGoldType> goldList = null;

    public int getHomeContentId() {
        return homeContentId;
    }

    public void setHomeContentId(int homeContentId) {
        this.homeContentId = homeContentId;
    }

    public Slider getSlider() {
        return slider;
    }

    public void setSlider(Slider slider) {
        this.slider = slider;
    }

    public List<AllCountry> getAllCountry() {
        return allCountry;
    }

    public void setAllCountry(List<AllCountry> allCountry) {
        this.allCountry = allCountry;
    }

    public List<AllGenre> getAllGenre() {
        return allGenre;
    }

    public void setAllGenre(List<AllGenre> allGenre) {
        this.allGenre = allGenre;
    }

    public List<FeaturedTvChannel> getFeaturedTvChannel() {
        return featuredTvChannel;
    }

    public void setFeaturedTvChannel(List<FeaturedTvChannel> featuredTvChannel) {
        this.featuredTvChannel = featuredTvChannel;
    }

    public List<LatestMovie> getLatestMovies() {
        return latestMovies;
    }

    public void setLatestMovies(List<LatestMovie> latestMovies) {
        this.latestMovies = latestMovies;
    }

    public List<LatestTvseries> getLatestTvseries() {
        return latestTvseries;
    }

    public void setLatestTvseries(List<LatestTvseries> latestTvseries) {
        this.latestTvseries = latestTvseries;
    }

    public List<FeaturesGenreAndMovie> getFeaturesGenreAndMovie() {
        return featuresGenreAndMovie;
    }

    public void setFeaturesGenreAndMovie(List<FeaturesGenreAndMovie> featuresGenreAndMovie) {
        this.featuresGenreAndMovie = featuresGenreAndMovie;
    }

    public List<PopularStars> getPopularStarsList() {
        return popularStarsList;
    }

    public void setPopularStarsList(List<PopularStars> popularStarsList) {
        this.popularStarsList = popularStarsList;
    }

    public List<PopularMovie> getPapularmovie() {
        return papularmovie;
    }

    public void setPapularmovie(List<PopularMovie> papularmovie) {
        this.papularmovie = papularmovie;
    }

    public List<LatestGoldType> getGoldList() {
        return goldList;
    }

    public void setGoldList(List<LatestGoldType> goldList) {
        this.goldList = goldList;
    }
}
