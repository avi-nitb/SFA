package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jaya on 27-12-2017.
 */

public class IM_PutRetailerVisit
{

    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("retailerData")
    @Expose
    private RetailerData retailerData;

    public IM_PutRetailerVisit(String authToken, RetailerData retailerData)
    {
        this.authToken = authToken;
        this.retailerData = retailerData;
    }

    public IM_PutRetailerVisit()
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

        @SerializedName("retailer_id")
        @Expose
        private String retailerId;
        @SerializedName("emp_id")
        @Expose
        private String empId;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("visit_date")
        @Expose
        private String visitDate;
        @SerializedName("sales_person_remarks")
        @Expose
        private String salesPersonRemarks;
        @SerializedName("retailer_remarks_if_any")
        @Expose
        private String retailerRemarksIfAny;
        @SerializedName("audio_remarks")
        @Expose
        private String audioRemarks;
        @SerializedName("company_id")
        @Expose
        private String companyId;

        public RetailerData(String retailerId, String empId, String latitude, String longitude, String visitDate, String salesPersonRemarks, String retailerRemarksIfAny, String audioRemarks, String companyId)
        {
            this.retailerId = retailerId;
            this.empId = empId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.visitDate = visitDate;
            this.salesPersonRemarks = salesPersonRemarks;
            this.retailerRemarksIfAny = retailerRemarksIfAny;
            this.audioRemarks = audioRemarks;
            this.companyId = companyId;
        }

        public String getRetailerId() {
            return retailerId;
        }

        public void setRetailerId(String retailerId) {
            this.retailerId = retailerId;
        }

        public String getEmpId() {
            return empId;
        }

        public void setEmpId(String empId) {
            this.empId = empId;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(String visitDate) {
            this.visitDate = visitDate;
        }

        public String getSalesPersonRemarks() {
            return salesPersonRemarks;
        }

        public void setSalesPersonRemarks(String salesPersonRemarks) {
            this.salesPersonRemarks = salesPersonRemarks;
        }

        public String getRetailerRemarksIfAny() {
            return retailerRemarksIfAny;
        }

        public void setRetailerRemarksIfAny(String retailerRemarksIfAny) {
            this.retailerRemarksIfAny = retailerRemarksIfAny;
        }

        public String getAudioRemarks() {
            return audioRemarks;
        }

        public void setAudioRemarks(String audioRemarks) {
            this.audioRemarks = audioRemarks;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

    }
}
