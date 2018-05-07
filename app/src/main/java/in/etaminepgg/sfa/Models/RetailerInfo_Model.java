package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayattama Prusty on 23-Dec-17.
 */

public class RetailerInfo_Model {

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("retailer_data")
    @Expose
    private RetailerData retailerData;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
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
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("modified_by")
        @Expose
        private String modifiedBy;
        @SerializedName("modified_date")
        @Expose
        private String modifiedDate;
        @SerializedName("customer_picture")
        @Expose
        private String customerPicture;
        @SerializedName("customer_isactive")
        @Expose
        private String customerIsactive;
        @SerializedName("shop_type")
        @Expose
        private String shopType;
        @SerializedName("shop_size")
        @Expose
        private String shopSize;
        @SerializedName("owner_name")
        @Expose
        private String ownerName;
        @SerializedName("owner_mobile_number")
        @Expose
        private String ownerMobileNumber;
        @SerializedName("deals_in")
        @Expose
        private String dealsIn;
        @SerializedName("major_brands_sold")
        @Expose
        private String majorBrandsSold;
        @SerializedName("company_id")
        @Expose
        private String companyId;
        @SerializedName("created_using_mobile_or_web")
        @Expose
        private Object createdUsingMobileOrWeb;
        @SerializedName("ytd_business")
        @Expose
        private Object ytdBusiness;
        @SerializedName("sales_person_remarks")
        @Expose
        private String salesPersonRemarks;
        @SerializedName("retail_chain_name")
        @Expose
        private Object retailChainName;
        @SerializedName("mall_complex_name")
        @Expose
        private Object mallComplexName;
        @SerializedName("shop_age")
        @Expose
        private String shopAge;

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

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public String getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(String modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public String getCustomerPicture() {
            return customerPicture;
        }

        public void setCustomerPicture(String customerPicture) {
            this.customerPicture = customerPicture;
        }

        public String getCustomerIsactive() {
            return customerIsactive;
        }

        public void setCustomerIsactive(String customerIsactive) {
            this.customerIsactive = customerIsactive;
        }

        public String getShopType() {
            return shopType;
        }

        public void setShopType(String shopType) {
            this.shopType = shopType;
        }

        public String getShopSize() {
            return shopSize;
        }

        public void setShopSize(String shopSize) {
            this.shopSize = shopSize;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getOwnerMobileNumber() {
            return ownerMobileNumber;
        }

        public void setOwnerMobileNumber(String ownerMobileNumber) {
            this.ownerMobileNumber = ownerMobileNumber;
        }

        public String getDealsIn() {
            return dealsIn;
        }

        public void setDealsIn(String dealsIn) {
            this.dealsIn = dealsIn;
        }

        public String getMajorBrandsSold() {
            return majorBrandsSold;
        }

        public void setMajorBrandsSold(String majorBrandsSold) {
            this.majorBrandsSold = majorBrandsSold;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public Object getCreatedUsingMobileOrWeb() {
            return createdUsingMobileOrWeb;
        }

        public void setCreatedUsingMobileOrWeb(Object createdUsingMobileOrWeb) {
            this.createdUsingMobileOrWeb = createdUsingMobileOrWeb;
        }

        public Object getYtdBusiness() {
            return ytdBusiness;
        }

        public void setYtdBusiness(Object ytdBusiness) {
            this.ytdBusiness = ytdBusiness;
        }

        public String getSalesPersonRemarks() {
            return salesPersonRemarks;
        }

        public void setSalesPersonRemarks(String salesPersonRemarks) {
            this.salesPersonRemarks = salesPersonRemarks;
        }

        public Object getRetailChainName() {
            return retailChainName;
        }

        public void setRetailChainName(Object retailChainName) {
            this.retailChainName = retailChainName;
        }

        public Object getMallComplexName() {
            return mallComplexName;
        }

        public void setMallComplexName(Object mallComplexName) {
            this.mallComplexName = mallComplexName;
        }

        public String getShopAge() {
            return shopAge;
        }

        public void setShopAge(String shopAge) {
            this.shopAge = shopAge;
        }

    }


}
