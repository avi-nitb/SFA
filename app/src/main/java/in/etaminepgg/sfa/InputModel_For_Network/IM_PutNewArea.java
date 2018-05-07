package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by etamine on 16/1/18.
 */

public class IM_PutNewArea
{

    @Expose
    private String authToken;
    @SerializedName("AreaData")
    @Expose
    private AreaData areaData;

    public IM_PutNewArea()
    {
    }

    public IM_PutNewArea(String authToken, AreaData areaData)
    {
        this.authToken = authToken;
        this.areaData = areaData;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public AreaData getAreaData() {
        return areaData;
    }

    public void setAreaData(AreaData areaData) {
        this.areaData = areaData;
    }


    public class AreaData {

        @SerializedName("parent_id")
        @Expose
        private String parentId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("hier_level")
        @Expose
        private String hierLevel;
        @SerializedName("company_id")
        @Expose
        private String companyId;

        public AreaData(String parentId, String name, String hierLevel, String companyId)
        {
            this.parentId = parentId;
            this.name = name;
            this.hierLevel = hierLevel;
            this.companyId = companyId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHierLevel() {
            return hierLevel;
        }

        public void setHierLevel(String hierLevel) {
            this.hierLevel = hierLevel;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

    }
}
