package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jayattama Prusty on 23-Dec-17.
 */

public class RetailerList_Model {

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("retailer_data")
    @Expose
    private List<RetailerDatum> retailerData = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<RetailerDatum> getRetailerData() {
        return retailerData;
    }

    public void setRetailerData(List<RetailerDatum> retailerData) {
        this.retailerData = retailerData;
    }



    public class RetailerDatum {

        @SerializedName("customer_code")
        @Expose
        private String customerCode;
        @SerializedName("customer_companyname")
        @Expose
        private String customerCompanyname;
        @SerializedName("customer_contact_name")
        @Expose
        private String customerContactName;

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getCustomerCompanyname() {
            return customerCompanyname;
        }

        public void setCustomerCompanyname(String customerCompanyname) {
            this.customerCompanyname = customerCompanyname;
        }

        public String getCustomerContactName() {
            return customerContactName;
        }

        public void setCustomerContactName(String customerContactName) {
            this.customerContactName = customerContactName;
        }

    }
}
