package ott.chillx.database.homeContent.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ott.chillx.models.home_content.FeaturedTvChannel;

import java.lang.reflect.Type;
import java.util.List;

public class FeaturedTvConverter {
    @TypeConverter
    public static String fromArrayList(List<FeaturedTvChannel> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<FeaturedTvChannel> jsonToList(String value){
        Type listType = new TypeToken<List<FeaturedTvChannel>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
