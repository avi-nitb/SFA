package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jaya on 21-12-2017.
 */

public class GetSkuListAfter
{
    /*
    {
        "api_status": 1,
            "sku_ids": [
        "1"
    ]
    }*/

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("sku_ids")
    @Expose
    private List<String> skuIds = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<String> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }



}
