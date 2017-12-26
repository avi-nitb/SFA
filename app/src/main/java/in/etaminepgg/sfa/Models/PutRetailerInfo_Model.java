package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayattama Prusty on 23-Dec-17.
 */

public class PutRetailerInfo_Model {

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("retailer_data")
    @Expose
    private String retailerData;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getRetailerData() {
        return retailerData;
    }

    public void setRetailerData(String retailerData) {
        this.retailerData = retailerData;
    }
}
