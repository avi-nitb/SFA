package in.etaminepgg.sfa.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthUser_Model
{
    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("authTokenExpiryDate")
    @Expose
    private String authTokenExpiryDate;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("userId")
    @Expose
    private String userId;

    public Integer getApiStatus()
    {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus)
    {
        this.apiStatus = apiStatus;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getAuthTokenExpiryDate()
    {
        return authTokenExpiryDate;
    }

    public void setAuthTokenExpiryDate(String authTokenExpiryDate)
    {
        this.authTokenExpiryDate = authTokenExpiryDate;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

}
