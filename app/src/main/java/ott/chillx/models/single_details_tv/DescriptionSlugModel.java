package ott.chillx.models.single_details_tv;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DescriptionSlugModel {

    @SerializedName("description_content")
    @Expose
    private String descriptionContent;

    public String getDescriptionContent() {
        return descriptionContent;
    }

    public void setDescriptionContent(String descriptionContent) {
        this.descriptionContent = descriptionContent;
    }
}
