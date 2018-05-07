package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 9/1/18.
 */

public class UpdateRetailerpictureModel
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("customer_picture_path")
    @Expose
    private String customerPicturePath;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getCustomerPicturePath() {
        return customerPicturePath;
    }

    public void setCustomerPicturePath(String customerPicturePath) {
        this.customerPicturePath = customerPicturePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
