package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by etamine on 10/1/18.
 */

public class PutSalesOrderDetails
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sales_order_details_id")
    @Expose
    private List<String> salesOrderDetailsId = null;

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

    public List<String> getSalesOrderDetailsId() {
        return salesOrderDetailsId;
    }

    public void setSalesOrderDetailsId(List<String> salesOrderDetailsId) {
        this.salesOrderDetailsId = salesOrderDetailsId;
    }
}
