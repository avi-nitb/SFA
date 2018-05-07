package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 4/1/18.
 */

public class PutSalesOrderMaster
{

  /*  {
        "api_status": 1,
            "sales_data": "Sales master created successfully.",
            "sales_order_id": "10"
    }*/


    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("sales_data")
    @Expose
    private String salesData;
    @SerializedName("sales_order_id")
    @Expose
    private String salesOrderId;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getSalesData() {
        return salesData;
    }

    public void setSalesData(String salesData) {
        this.salesData = salesData;
    }

    public String getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(String salesOrderId) {
        this.salesOrderId = salesOrderId;
    }
}
