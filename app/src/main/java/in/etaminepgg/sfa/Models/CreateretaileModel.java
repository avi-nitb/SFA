package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 9/1/18.
 */

public class CreateretaileModel
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("customer_code")
    @Expose
    private String customerCode;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    @SerializedName("customer_picture_path")
    @Expose
    private String customer_picture_path;

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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCustomer_picture_path()
    {
        return customer_picture_path;
    }

    public void setCustomer_picture_path(String customer_picture_path)
    {
        this.customer_picture_path = customer_picture_path;
    }
}
