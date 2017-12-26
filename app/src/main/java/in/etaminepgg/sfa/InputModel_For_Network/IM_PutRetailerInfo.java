package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayattama Prusty on 23-Dec-17.
 */

public class IM_PutRetailerInfo {
    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("retailerData")
    @Expose
    private RetailerData retailerData;

    public IM_PutRetailerInfo(String authToken, RetailerData retailerData) {
        this.authToken = authToken;
        this.retailerData = retailerData;
    }

    public IM_PutRetailerInfo() {
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
        @SerializedName("customer_geopos")
        @Expose
        private String customerGeopos;
        @SerializedName("customer_companyname")
        @Expose
        private String customerCompanyname;
        @SerializedName("customer_type")
        @Expose
        private String customerType;
        @SerializedName("customer_since")
        @Expose
        private String customerSince;
        @SerializedName("credit_rating")
        @Expose
        private String creditRating;
        @SerializedName("credit_days")
        @Expose
        private String creditDays;
        @SerializedName("credit_limit")
        @Expose
        private String creditLimit;
        @SerializedName("assigned_salesperson")
        @Expose
        private String assignedSalesperson;
        @SerializedName("assigned_supervisor")
        @Expose
        private String assignedSupervisor;
        @SerializedName("assigned_distributor")
        @Expose
        private String assignedDistributor;
        @SerializedName("customer_contact_name")
        @Expose
        private String customerContactName;
        @SerializedName("customer_contact_designation")
        @Expose
        private String customerContactDesignation;
        @SerializedName("contact_cell")
        @Expose
        private String contactCell;
        @SerializedName("contact_email")
        @Expose
        private String contactEmail;
        @SerializedName("contact_ll1")
        @Expose
        private String contactLl1;
        @SerializedName("contact_fax")
        @Expose
        private String contactFax;
        @SerializedName("location_id")
        @Expose
        private String locationId;
        @SerializedName("customer_address_1")
        @Expose
        private String customerAddress1;
        @SerializedName("customer_city")
        @Expose
        private String customerCity;
        @SerializedName("customer_pincode")
        @Expose
        private String customerPincode;
        @SerializedName("customer_region")
        @Expose
        private String customerRegion;
        @SerializedName("customer_state")
        @Expose
        private String customerState;
        @SerializedName("del_address_1")
        @Expose
        private String delAddress1;
        @SerializedName("del_address_city")
        @Expose
        private String delAddressCity;
        @SerializedName("del_address_pincode")
        @Expose
        private String delAddressPincode;
        @SerializedName("del_address_state")
        @Expose
        private String delAddressState;
        @SerializedName("approved")
        @Expose
        private String approved;
        @SerializedName("approved_by")
        @Expose
        private String approvedBy;
        @SerializedName("approved_date")
        @Expose
        private String approvedDate;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("modified_by")
        @Expose
        private String modifiedBy;

        public RetailerData(String customerCode, String customerGeopos, String
                customerCompanyname, String customerType, String customerSince, String
                creditRating, String creditDays, String creditLimit, String assignedSalesperson,
                            String assignedSupervisor, String assignedDistributor, String
                                    customerContactName, String customerContactDesignation,
                            String contactCell, String contactEmail, String contactLl1, String
                                    contactFax, String locationId, String customerAddress1,
                            String customerCity, String customerPincode, String customerRegion,
                            String customerState, String delAddress1, String delAddressCity,
                            String delAddressPincode, String delAddressState, String approved,
                            String approvedBy, String approvedDate, String createdBy, String
                                    modifiedBy) {
            this.customerCode = customerCode;
            this.customerGeopos = customerGeopos;
            this.customerCompanyname = customerCompanyname;
            this.customerType = customerType;
            this.customerSince = customerSince;
            this.creditRating = creditRating;
            this.creditDays = creditDays;
            this.creditLimit = creditLimit;
            this.assignedSalesperson = assignedSalesperson;
            this.assignedSupervisor = assignedSupervisor;
            this.assignedDistributor = assignedDistributor;
            this.customerContactName = customerContactName;
            this.customerContactDesignation = customerContactDesignation;
            this.contactCell = contactCell;
            this.contactEmail = contactEmail;
            this.contactLl1 = contactLl1;
            this.contactFax = contactFax;
            this.locationId = locationId;
            this.customerAddress1 = customerAddress1;
            this.customerCity = customerCity;
            this.customerPincode = customerPincode;
            this.customerRegion = customerRegion;
            this.customerState = customerState;
            this.delAddress1 = delAddress1;
            this.delAddressCity = delAddressCity;
            this.delAddressPincode = delAddressPincode;
            this.delAddressState = delAddressState;
            this.approved = approved;
            this.approvedBy = approvedBy;
            this.approvedDate = approvedDate;
            this.createdBy = createdBy;
            this.modifiedBy = modifiedBy;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getCustomerGeopos() {
            return customerGeopos;
        }

        public void setCustomerGeopos(String customerGeopos) {
            this.customerGeopos = customerGeopos;
        }

        public String getCustomerCompanyname() {
            return customerCompanyname;
        }

        public void setCustomerCompanyname(String customerCompanyname) {
            this.customerCompanyname = customerCompanyname;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }

        public String getCustomerSince() {
            return customerSince;
        }

        public void setCustomerSince(String customerSince) {
            this.customerSince = customerSince;
        }

        public String getCreditRating() {
            return creditRating;
        }

        public void setCreditRating(String creditRating) {
            this.creditRating = creditRating;
        }

        public String getCreditDays() {
            return creditDays;
        }

        public void setCreditDays(String creditDays) {
            this.creditDays = creditDays;
        }

        public String getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(String creditLimit) {
            this.creditLimit = creditLimit;
        }

        public String getAssignedSalesperson() {
            return assignedSalesperson;
        }

        public void setAssignedSalesperson(String assignedSalesperson) {
            this.assignedSalesperson = assignedSalesperson;
        }

        public String getAssignedSupervisor() {
            return assignedSupervisor;
        }

        public void setAssignedSupervisor(String assignedSupervisor) {
            this.assignedSupervisor = assignedSupervisor;
        }

        public String getAssignedDistributor() {
            return assignedDistributor;
        }

        public void setAssignedDistributor(String assignedDistributor) {
            this.assignedDistributor = assignedDistributor;
        }

        public String getCustomerContactName() {
            return customerContactName;
        }

        public void setCustomerContactName(String customerContactName) {
            this.customerContactName = customerContactName;
        }

        public String getCustomerContactDesignation() {
            return customerContactDesignation;
        }

        public void setCustomerContactDesignation(String customerContactDesignation) {
            this.customerContactDesignation = customerContactDesignation;
        }

        public String getContactCell() {
            return contactCell;
        }

        public void setContactCell(String contactCell) {
            this.contactCell = contactCell;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getContactLl1() {
            return contactLl1;
        }

        public void setContactLl1(String contactLl1) {
            this.contactLl1 = contactLl1;
        }

        public String getContactFax() {
            return contactFax;
        }

        public void setContactFax(String contactFax) {
            this.contactFax = contactFax;
        }

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        public String getCustomerAddress1() {
            return customerAddress1;
        }

        public void setCustomerAddress1(String customerAddress1) {
            this.customerAddress1 = customerAddress1;
        }

        public String getCustomerCity() {
            return customerCity;
        }

        public void setCustomerCity(String customerCity) {
            this.customerCity = customerCity;
        }

        public String getCustomerPincode() {
            return customerPincode;
        }

        public void setCustomerPincode(String customerPincode) {
            this.customerPincode = customerPincode;
        }

        public String getCustomerRegion() {
            return customerRegion;
        }

        public void setCustomerRegion(String customerRegion) {
            this.customerRegion = customerRegion;
        }

        public String getCustomerState() {
            return customerState;
        }

        public void setCustomerState(String customerState) {
            this.customerState = customerState;
        }

        public String getDelAddress1() {
            return delAddress1;
        }

        public void setDelAddress1(String delAddress1) {
            this.delAddress1 = delAddress1;
        }

        public String getDelAddressCity() {
            return delAddressCity;
        }

        public void setDelAddressCity(String delAddressCity) {
            this.delAddressCity = delAddressCity;
        }

        public String getDelAddressPincode() {
            return delAddressPincode;
        }

        public void setDelAddressPincode(String delAddressPincode) {
            this.delAddressPincode = delAddressPincode;
        }

        public String getDelAddressState() {
            return delAddressState;
        }

        public void setDelAddressState(String delAddressState) {
            this.delAddressState = delAddressState;
        }

        public String getApproved() {
            return approved;
        }

        public void setApproved(String approved) {
            this.approved = approved;
        }

        public String getApprovedBy() {
            return approvedBy;
        }

        public void setApprovedBy(String approvedBy) {
            this.approvedBy = approvedBy;
        }

        public String getApprovedDate() {
            return approvedDate;
        }

        public void setApprovedDate(String approvedDate) {
            this.approvedDate = approvedDate;
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

    }

}
