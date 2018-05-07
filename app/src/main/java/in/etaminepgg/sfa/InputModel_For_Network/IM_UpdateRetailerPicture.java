package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 9/1/18.
 */

public class IM_UpdateRetailerPicture
{

    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("retailerData")
    @Expose
    private RetailerData retailerData;

    public IM_UpdateRetailerPicture(String authToken, RetailerData retailerData)
    {
        this.authToken = authToken;
        this.retailerData = retailerData;
    }

    public IM_UpdateRetailerPicture()
    {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public RetailerData getRetailerData() {
        return retailerData;
    }

    public void setRetailerData(RetailerData retailerData) {
        this.retailerData = retailerData;
    }


    public class RetailerData {

        @SerializedName("customer_code")
        @Expose
        private String customerCode;
        @SerializedName("customer_picture")
        @Expose
        private String customerPicture;

        public RetailerData(String customerCode, String customerPicture)
        {
            this.customerCode = customerCode;
            this.customerPicture = customerPicture;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getCustomerPicture() {
            return customerPicture;
        }

        public void setCustomerPicture(String customerPicture) {
            this.customerPicture = customerPicture;
        }

    }
}
