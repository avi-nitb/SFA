package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jaya on 02-01-2018.
 */

public class IsSkuListUpdate
{

/*
    {
        "api_status": 1,
            "Is_updated": 1,
            "date_of_last_update": "2017-05-24 12:26:10"
    }*/


    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;

    @SerializedName("Is_updated")
    @Expose
    private Integer isUpdated;

    @SerializedName("date_of_last_update")
    @Expose
    private String lastupdateddate;

    public IsSkuListUpdate(Integer apiStatus, Integer isUpdated, String lastupdateddate)
    {
        this.apiStatus = apiStatus;
        this.isUpdated = isUpdated;
        this.lastupdateddate = lastupdateddate;
    }

    public Integer getApiStatus()
    {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus)
    {
        this.apiStatus = apiStatus;
    }

    public Integer getIsUpdated()
    {
        return isUpdated;
    }

    public void setIsUpdated(Integer isUpdated)
    {
        this.isUpdated = isUpdated;
    }

    public String getLastupdateddate()
    {
        return lastupdateddate;
    }

    public void setLastupdateddate(String lastupdateddate)
    {
        this.lastupdateddate = lastupdateddate;
    }
}
