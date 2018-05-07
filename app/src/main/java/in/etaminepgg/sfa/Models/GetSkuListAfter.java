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
    private List<SkuInfo> skuIds = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<SkuInfo> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<SkuInfo> skuIds) {
        this.skuIds = skuIds;
    }


    public class SkuInfo
    {
        private String sku_id;
        private String op_type;
        private String mod_type;

        public String getSku_id()
        {
            return sku_id;
        }

        public void setSku_id(String sku_id)
        {
            this.sku_id = sku_id;
        }

        public String getOp_type()
        {
            return op_type;
        }

        public void setOp_type(String op_type)
        {
            this.op_type = op_type;
        }

        public String getMod_type()
        {
            return mod_type;
        }

        public void setMod_type(String mod_type)
        {
            this.mod_type = mod_type;
        }
    }
}
