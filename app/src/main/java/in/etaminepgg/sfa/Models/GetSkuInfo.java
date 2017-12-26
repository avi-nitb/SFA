package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jaya on 21-12-2017.
 */

public class GetSkuInfo
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("sku_ids")
    @Expose
    private SkuIds skuIds;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public SkuIds getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(SkuIds skuIds) {
        this.skuIds = skuIds;
    }


    public class SkuIds {

        @SerializedName("sku_id")
        @Expose
        private String skuId;
        @SerializedName("sku_company_id")
        @Expose
        private String skuCompanyId;
        @SerializedName("sku_name")
        @Expose
        private String skuName;
        @SerializedName("sku_description")
        @Expose
        private String skuDescription;
        @SerializedName("sku_category")
        @Expose
        private String skuCategory;
        @SerializedName("sku_sub_category")
        @Expose
        private String skuSubCategory;
        @SerializedName("sku_uom")
        @Expose
        private String skuUom;
        @SerializedName("sku_price")
        @Expose
        private String skuPrice;
        @SerializedName("sku_introduced_on")
        @Expose
        private String skuIntroducedOn;
        @SerializedName("sku_new_flag")
        @Expose
        private Object skuNewFlag;
        @SerializedName("sku_new_days")
        @Expose
        private Object skuNewDays;
        @SerializedName("sku_expire_date")
        @Expose
        private String skuExpireDate;
        @SerializedName("sku_created_on")
        @Expose
        private String skuCreatedOn;
        @SerializedName("sku_created_by")
        @Expose
        private String skuCreatedBy;
        @SerializedName("sku_modified_on")
        @Expose
        private String skuModifiedOn;
        @SerializedName("sku_modified_by")
        @Expose
        private String skuModifiedBy;
        @SerializedName("sku_isactive")
        @Expose
        private String skuIsactive;

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getSkuCompanyId() {
            return skuCompanyId;
        }

        public void setSkuCompanyId(String skuCompanyId) {
            this.skuCompanyId = skuCompanyId;
        }

        public String getSkuName() {
            return skuName;
        }

        public void setSkuName(String skuName) {
            this.skuName = skuName;
        }

        public String getSkuDescription() {
            return skuDescription;
        }

        public void setSkuDescription(String skuDescription) {
            this.skuDescription = skuDescription;
        }

        public String getSkuCategory() {
            return skuCategory;
        }

        public void setSkuCategory(String skuCategory) {
            this.skuCategory = skuCategory;
        }

        public String getSkuSubCategory() {
            return skuSubCategory;
        }

        public void setSkuSubCategory(String skuSubCategory) {
            this.skuSubCategory = skuSubCategory;
        }

        public String getSkuUom() {
            return skuUom;
        }

        public void setSkuUom(String skuUom) {
            this.skuUom = skuUom;
        }

        public String getSkuPrice() {
            return skuPrice;
        }

        public void setSkuPrice(String skuPrice) {
            this.skuPrice = skuPrice;
        }

        public String getSkuIntroducedOn() {
            return skuIntroducedOn;
        }

        public void setSkuIntroducedOn(String skuIntroducedOn) {
            this.skuIntroducedOn = skuIntroducedOn;
        }

        public Object getSkuNewFlag() {
            return skuNewFlag;
        }

        public void setSkuNewFlag(Object skuNewFlag) {
            this.skuNewFlag = skuNewFlag;
        }

        public Object getSkuNewDays() {
            return skuNewDays;
        }

        public void setSkuNewDays(Object skuNewDays) {
            this.skuNewDays = skuNewDays;
        }

        public String getSkuExpireDate() {
            return skuExpireDate;
        }

        public void setSkuExpireDate(String skuExpireDate) {
            this.skuExpireDate = skuExpireDate;
        }

        public String getSkuCreatedOn() {
            return skuCreatedOn;
        }

        public void setSkuCreatedOn(String skuCreatedOn) {
            this.skuCreatedOn = skuCreatedOn;
        }

        public String getSkuCreatedBy() {
            return skuCreatedBy;
        }

        public void setSkuCreatedBy(String skuCreatedBy) {
            this.skuCreatedBy = skuCreatedBy;
        }

        public String getSkuModifiedOn() {
            return skuModifiedOn;
        }

        public void setSkuModifiedOn(String skuModifiedOn) {
            this.skuModifiedOn = skuModifiedOn;
        }

        public String getSkuModifiedBy() {
            return skuModifiedBy;
        }

        public void setSkuModifiedBy(String skuModifiedBy) {
            this.skuModifiedBy = skuModifiedBy;
        }

        public String getSkuIsactive() {
            return skuIsactive;
        }

        public void setSkuIsactive(String skuIsactive) {
            this.skuIsactive = skuIsactive;
        }

    }
}
