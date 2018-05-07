package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 4/1/18.
 */

public class IM_PutSalesOrderMaster
{

    /*{
        "authToken": "MV8yMDE4LTAxLTExIDE1OjI1OjQ0XzQzMTQxMDg\u003d",
            "salesData": {
        "retail_visit_id": "10000",
                "distributor_id":"10",
                "total_order_value":"12",
                "scheme_offered":"10",
                "total_discount":"0",
                "required_by_date":"",
                "approval_status":"2017-12-05 00:00:00",
                "approved_by":"1",
                "ord_remarks":"1",
                "created_by":"1",
                "modified_by":"1",
                "company_id":"1"
    }
    }*/


    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("salesData")
    @Expose
    private SalesData salesData;

    public IM_PutSalesOrderMaster(String authToken, SalesData salesData)
    {
        this.authToken = authToken;
        this.salesData = salesData;
    }

    public IM_PutSalesOrderMaster()
    {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public SalesData getSalesData() {
        return salesData;
    }

    public void setSalesData(SalesData salesData) {
        this.salesData = salesData;
    }


    public class SalesData {

        @SerializedName("retail_visit_id")
        @Expose
        private String retailVisitId;
        @SerializedName("distributor_id")
        @Expose
        private String distributorId;
        @SerializedName("total_order_value")
        @Expose
        private String totalOrderValue;
        @SerializedName("scheme_offered")
        @Expose
        private String schemeOffered;
        @SerializedName("total_discount")
        @Expose
        private String totalDiscount;
        @SerializedName("required_by_date")
        @Expose
        private String requiredByDate;
        @SerializedName("approval_status")
        @Expose
        private String approvalStatus;
        @SerializedName("approved_by")
        @Expose
        private String approvedBy;
        @SerializedName("ord_remarks")
        @Expose
        private String ordRemarks;

        @SerializedName("created_by")
        @Expose
        private String createdBy;

        @SerializedName("created_on")
        @Expose
        private String createdOn;

        @SerializedName("modified_by")
        @Expose
        private String modifiedBy;

        @SerializedName("company_id")
        @Expose
        private String companyId;


        @SerializedName("telephonic_order")
        @Expose
        private int telephonic_order;

        public SalesData(String retailVisitId, String distributorId, String totalOrderValue, String schemeOffered, String totalDiscount, String requiredByDate, String approvalStatus, String approvedBy, String ordRemarks, String createdBy,String createdOn, String modifiedBy, String companyId,int telephonic_order)
        {
            this.retailVisitId = retailVisitId;
            this.distributorId = distributorId;
            this.totalOrderValue = totalOrderValue;
            this.schemeOffered = schemeOffered;
            this.totalDiscount = totalDiscount;
            this.requiredByDate = requiredByDate;
            this.approvalStatus = approvalStatus;
            this.approvedBy = approvedBy;
            this.ordRemarks = ordRemarks;
            this.createdBy = createdBy;
            this.modifiedBy = modifiedBy;
            this.companyId = companyId;
            this.createdOn=createdOn;
            this.telephonic_order=telephonic_order;
        }

        public String getRetailVisitId() {
            return retailVisitId;
        }

        public void setRetailVisitId(String retailVisitId) {
            this.retailVisitId = retailVisitId;
        }

        public String getDistributorId() {
            return distributorId;
        }

        public void setDistributorId(String distributorId) {
            this.distributorId = distributorId;
        }

        public String getTotalOrderValue() {
            return totalOrderValue;
        }

        public void setTotalOrderValue(String totalOrderValue) {
            this.totalOrderValue = totalOrderValue;
        }

        public String getSchemeOffered() {
            return schemeOffered;
        }

        public void setSchemeOffered(String schemeOffered) {
            this.schemeOffered = schemeOffered;
        }

        public String getTotalDiscount() {
            return totalDiscount;
        }

        public void setTotalDiscount(String totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public String getRequiredByDate() {
            return requiredByDate;
        }

        public void setRequiredByDate(String requiredByDate) {
            this.requiredByDate = requiredByDate;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public void setApprovalStatus(String approvalStatus) {
            this.approvalStatus = approvalStatus;
        }

        public String getApprovedBy() {
            return approvedBy;
        }

        public void setApprovedBy(String approvedBy) {
            this.approvedBy = approvedBy;
        }

        public String getOrdRemarks() {
            return ordRemarks;
        }

        public void setOrdRemarks(String ordRemarks) {
            this.ordRemarks = ordRemarks;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getCreatedOn()
        {
            return createdOn;
        }

        public void setCreatedOn(String createdOn)
        {
            this.createdOn = createdOn;
        }

        public int getTelephonic_order()
        {
            return telephonic_order;
        }

        public void setTelephonic_order(int telephonic_order)
        {
            this.telephonic_order = telephonic_order;
        }
    }
}
