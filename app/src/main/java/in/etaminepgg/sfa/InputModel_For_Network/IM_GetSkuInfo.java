package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jaya on 21-12-2017.
 */

public class IM_GetSkuInfo
{

    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("sku_id")
    @Expose
    private String skuId;

    public IM_GetSkuInfo(String authToken, String skuId)
    {
        this.authToken = authToken;
        this.skuId = skuId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

}
