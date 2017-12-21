package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jaya on 21-12-2017.
 */

public class Input_Model_GetSkuListAfter
{

    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("date")
    @Expose
    private String date;

    public Input_Model_GetSkuListAfter(String authToken, String type, String date)
    {
        this.authToken = authToken;
        this.type = type;
        this.date = date;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
