package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 16/1/18.
 */

public class PutNewArea
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("loc_hier_id")
    @Expose
    private String locHierId;
    @SerializedName("location_hier")
    @Expose
    private String locationHier;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocHierId() {
        return locHierId;
    }

    public void setLocHierId(String locHierId) {
        this.locHierId = locHierId;
    }

    public String getLocationHier() {
        return locationHier;
    }

    public void setLocationHier(String locationHier) {
        this.locationHier = locationHier;
    }

}
