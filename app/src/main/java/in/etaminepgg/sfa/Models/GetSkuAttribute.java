package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jaya on 22-12-2017.
 */

public class GetSkuAttribute
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("sku_attr")
    @Expose
    private List<SkuAttr> skuAttr = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<SkuAttr> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<SkuAttr> skuAttr) {
        this.skuAttr = skuAttr;
    }



    public class SkuAttr {

        @SerializedName("global_attribute_id")
        @Expose
        private String globalAttributeId;
        @SerializedName("attribute_type")
        @Expose
        private String attributeType;
        @SerializedName("attribute_name")
        @Expose
        private String attributeName;
        @SerializedName("attribute_value")
        @Expose
        private String attributeValue;
        @SerializedName("created_on")
        @Expose
        private String createdOn;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("modified_on")
        @Expose
        private Object modifiedOn;
        @SerializedName("modified_by")
        @Expose
        private Object modifiedBy;
        @SerializedName("company_id")
        @Expose
        private Object companyId;
        @SerializedName("map_id")
        @Expose
        private String mapId;
        @SerializedName("sku_id")
        @Expose
        private String skuId;
        @SerializedName("attribute_id")
        @Expose
        private String attributeId;
        @SerializedName("cat_level")
        @Expose
        private String catLevel;
        @SerializedName("is_active")
        @Expose
        private Object isActive;

        public String getGlobalAttributeId() {
            return globalAttributeId;
        }

        public void setGlobalAttributeId(String globalAttributeId) {
            this.globalAttributeId = globalAttributeId;
        }

        public String getAttributeType() {
            return attributeType;
        }

        public void setAttributeType(String attributeType) {
            this.attributeType = attributeType;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Object getModifiedOn() {
            return modifiedOn;
        }

        public void setModifiedOn(Object modifiedOn) {
            this.modifiedOn = modifiedOn;
        }

        public Object getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(Object modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public Object getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Object companyId) {
            this.companyId = companyId;
        }

        public String getMapId() {
            return mapId;
        }

        public void setMapId(String mapId) {
            this.mapId = mapId;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = attributeId;
        }

        public String getCatLevel() {
            return catLevel;
        }

        public void setCatLevel(String catLevel) {
            this.catLevel = catLevel;
        }

        public Object getIsActive() {
            return isActive;
        }

        public void setIsActive(Object isActive) {
            this.isActive = isActive;
        }

    }
}
