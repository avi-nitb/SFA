package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by etamine on 28/2/18.
 */

public class NoOrderReasonList
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("reason_list")
    @Expose
    private List<ReasonList> reasonList = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ReasonList> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<ReasonList> reasonList) {
        this.reasonList = reasonList;
    }

    public class ReasonList {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("reason")
        @Expose
        private String reason;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("created_on")
        @Expose
        private String createdOn;
        @SerializedName("modified_by")
        @Expose
        private String modifiedBy;
        @SerializedName("modified_on")
        @Expose
        private String modifiedOn;
        @SerializedName("company_id")
        @Expose
        private String companyId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public String getModifiedOn() {
            return modifiedOn;
        }

        public void setModifiedOn(String modifiedOn) {
            this.modifiedOn = modifiedOn;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

    }
}
