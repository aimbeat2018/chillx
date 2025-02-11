package ott.chillx.utils;

import android.content.Context;
import android.os.Environment;

import ott.chillx.models.single_details.Country;
import ott.chillx.models.single_details.Genre;
import ott.chillx.network.model.TvCategory;
import ott.chillx.R;

import java.io.File;
import java.util.List;

public class Constants {

    public static final String ADMOB = "admob";
    public static final String START_APP = "startApp";
    public static final String NETWORK_AUDIENCE = "fan";

    public static String workId;

    //public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString()+File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator;

    public static String getDownloadDir(Context context) {
        //downloads/oxoo
       // return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;
       //return context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;

//        File dir = new File(Environment.getExternalStorageDirectory() + "/Download/" + context.getString(R.string.app_name));
        File dir = new File(Environment.getExternalStorageDirectory() + "/Download");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath();
    }


    public static final String USER_LOGIN_STATUS = "login_status";
    public static final String FAMILYCONTENTSTATUS = "family_status";
    public static final String USER_PIN = "user_pin";
    public static final String USER_AGE = "user_age";
    public static final String USER_REGISTER_AGE = "user_register_age";
    public static final String USER_PASSWORD_AVAILABLE = "pass_available";

    public static List<Genre> genreList = null;
    public static List<Country> countryList = null;
    public static List<TvCategory> tvCategoryList = null;

    //room related constants
    public static final String ROOM_DB_NAME = "continue_watching_db";

    public static final String CONTENT_ID = "content_id";
    public static final String CONTENT_TITLE = "title";
    public static final String IMAGE_URL = "image_url";
    public static final String PROGRESS = "progress";
    public static final String POSITION ="position";
    public static final String STREAM_URL = "stream_url";
    public static final String CATEGORY_TYPE = "category_type";
    public static final String SERVER_TYPE = "server_type";
    public static final String IS_FROM_CONTINUE_WATCHING = "continue_watching_bool";
    public static final String YOUTUBE = "youtube";
    public static final String YOUTUBE_LIVE = "youtube_live";


    // ======================== Exo Download Action ===================
    public static final String EXO_DOWNLOAD_ACTION_START="EXO_DOWNLOAD_START";
    public static final String EXO_DOWNLOAD_ACTION_PAUSE="EXO_DOWNLOAD_PAUSE";
    public static final String EXO_DOWNLOAD_ACTION_CANCEL="EXO_DOWNLOAD_CANCEL";

}
