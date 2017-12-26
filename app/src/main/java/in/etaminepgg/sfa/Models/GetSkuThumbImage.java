package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jaya on 22-12-2017.
 */

public class GetSkuThumbImage
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("sku_media")
    @Expose
    private List<SkuMedium> skuMedia = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<SkuMedium> getSkuMedia() {
        return skuMedia;
    }

    public void setSkuMedia(List<SkuMedium> skuMedia) {
        this.skuMedia = skuMedia;
    }

    public class SkuMedium {

        @SerializedName("sku_id")
        @Expose
        private String skuId;
        @SerializedName("media_type")
        @Expose
        private String mediaType;
        @SerializedName("media_file")
        @Expose
        private String mediaFile;

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public String getMediaFile() {
            return mediaFile;
        }

        public void setMediaFile(String mediaFile) {
            this.mediaFile = mediaFile;
        }

    }
}
