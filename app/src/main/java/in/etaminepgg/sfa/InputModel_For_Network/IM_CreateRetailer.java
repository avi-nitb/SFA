package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by etamine on 9/1/18.
 */

public class IM_CreateRetailer
{
    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("retailerData")
    @Expose
    private RetailerData retailerData;

    public IM_CreateRetailer(String authToken, RetailerData retailerData)
    {
        this.authToken = authToken;
        this.retailerData = retailerData;
    }

    public IM_CreateRetailer()
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

        @SerializedName("customer_contact_name")
        @Expose
        private String customerContactName;

        @SerializedName("customer_companyname")
        @Expose
        private String customerCompanyname;

        @SerializedName("customer_type")
        @Expose
        private String customerType;

        @SerializedName("customer_pincode")
        @Expose
        private String customerPincode;

        @SerializedName("contact_cell")
        @Expose
        private String ownerMobileNumber;
        @SerializedName("contact_email")
        @Expose
        private String contactEmail;
        @SerializedName("customer_geopos")
        @Expose
        private String customerGeopos;
        @SerializedName("location_id")
        @Expose
        private String locationId;

        @SerializedName("created_by")
        @Expose
        private String created_by;

        @SerializedName("customer_address_1")
        @Expose
        private String customer_address_1;


        @SerializedName("created_date")
        @Expose
        private String createdDate;

        @SerializedName("customer_picture")
        @Expose
        private String customerPicture;


        @SerializedName("card_front_text")
        @Expose
        private String card_front_text;

        @SerializedName("card_back_text")
        @Expose
        private String card_back_text;

        @SerializedName("card_photos")
        @Expose
        private ArrayList<String> card_photos;

        public RetailerData(String customerCompanyname,String customerContactName , String customerType, String customerPincode, String ownerMobileNumber, String contactEmail, String customerGeopos, String locationId, String createdDate,String created_by, String customerPicture,String customer_address_1,String card_front_text,String card_back_text,ArrayList<String> card_photos)
        {
            this.customerContactName = customerContactName;
            this.customerCompanyname = customerCompanyname;
            this.customerType = customerType;
            this.customerPincode = customerPincode;
            this.ownerMobileNumber = ownerMobileNumber;
            this.contactEmail = contactEmail;
            this.customerGeopos = customerGeopos;
            this.locationId = locationId;
            this.createdDate = createdDate;
            this.created_by = created_by;
            this.customerPicture = customerPicture;
            this.customer_address_1 = customer_address_1;
            this.card_front_text=card_front_text;
            this.card_back_text=card_back_text;
            this.card_photos=card_photos;
        }

        public RetailerData()
        {
        }

        public String getCustomerContactName() {
            return customerContactName;
        }

        public void setCustomerContactName(String customerContactName) {
            this.customerContactName = customerContactName;
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

        public String getCustomerPincode() {
            return customerPincode;
        }

        public void setCustomerPincode(String customerPincode) {
            this.customerPincode = customerPincode;
        }

        public String getOwnerMobileNumber() {
            return ownerMobileNumber;
        }

        public void setOwnerMobileNumber(String ownerMobileNumber) {
            this.ownerMobileNumber = ownerMobileNumber;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getCustomerGeopos() {
            return customerGeopos;
        }

        public void setCustomerGeopos(String customerGeopos) {
            this.customerGeopos = customerGeopos;
        }

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }



        public String getCustomerPicture() {
            return customerPicture;
        }

        public void setCustomerPicture(String customerPicture) {
            this.customerPicture = customerPicture;
        }

        public String getCreated_by()
        {
            return created_by;
        }

        public void setCreated_by(String created_by)
        {
            this.created_by = created_by;
        }

        public String getCustomer_address_1()
        {
            return customer_address_1;
        }

        public void setCustomer_address_1(String customer_address_1)
        {
            this.customer_address_1 = customer_address_1;
        }

        public String getCard_front_text()
        {
            return card_front_text;
        }

        public void setCard_front_text(String card_front_text)
        {
            this.card_front_text = card_front_text;
        }

        public String getCard_back_text()
        {
            return card_back_text;
        }

        public void setCard_back_text(String card_back_text)
        {
            this.card_back_text = card_back_text;
        }

        public ArrayList<String> getCard_photos()
        {
            return card_photos;
        }

        public void setCard_photos(ArrayList<String> card_photos)
        {
            this.card_photos = card_photos;
        }
    }
}
