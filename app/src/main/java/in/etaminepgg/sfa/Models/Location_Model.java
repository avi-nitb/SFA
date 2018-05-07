package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by etamine on 8/1/18.
 */

public class Location_Model
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("locatoins_info")
    @Expose
    private List<LocatoinsInfo> locatoinsInfo = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<LocatoinsInfo> getLocatoinsInfo() {
        return locatoinsInfo;
    }

    public void setLocatoinsInfo(List<LocatoinsInfo> locatoinsInfo) {
        this.locatoinsInfo = locatoinsInfo;
    }


    public class LocatoinsInfo
    {

        @SerializedName("loc_hier_id")
        @Expose
        private String locHierId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("hier_level")
        @Expose
        private String hierLevel;
        @SerializedName("parent_id")
        @Expose
        private String parentId;
        @SerializedName("full_hier")
        @Expose
        private String fullHier;
        @SerializedName("isactive")
        @Expose
        private String isactive;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("company_id")
        @Expose
        private String companyId;

        public String getLocHierId()
        {
            return locHierId;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getHierLevel()
        {
            return hierLevel;
        }

        public void setHierLevel(String hierLevel)
        {
            this.hierLevel = hierLevel;
        }

        public String getParentId()
        {
            return parentId;
        }

        public void setParentId(String parentId)
        {
            this.parentId = parentId;
        }

        public String getFullHier()
        {
            return fullHier;
        }

        public void setFullHier(String fullHier)
        {
            this.fullHier = fullHier;
        }

        public String getIsactive()
        {
            return isactive;
        }

        public void setIsactive(String isactive)
        {
            this.isactive = isactive;
        }

        public String getCreatedBy()
        {
            return createdBy;
        }

        public void setCreatedBy(String createdBy)
        {
            this.createdBy = createdBy;
        }

        public String getCreatedDate()
        {
            return createdDate;
        }

        public void setCreatedDate(String createdDate)
        {
            this.createdDate = createdDate;
        }

        public String getCompanyId()
        {
            return companyId;
        }

        public void setCompanyId(String companyId)
        {
            this.companyId = companyId;
        }
    }
}
