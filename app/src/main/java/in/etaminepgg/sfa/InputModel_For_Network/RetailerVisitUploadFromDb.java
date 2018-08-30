package in.etaminepgg.sfa.InputModel_For_Network;

import java.io.Serializable;

import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;

public class RetailerVisitUploadFromDb implements Serializable
{
   private String visitId=NONE;
   private String mobileVisitId;
   private String retailerId;
   private String mobileRetailerId;
   private String visitDate;
   private String hasOrder;
   private String feedback;
   private String latitude;
   private String longitude;

    public RetailerVisitUploadFromDb(String visitId, String mobileVisitId, String retailerId, String mobileRetailerId, String visitDate, String hasOrder, String feedback, String latitude, String longitude)
    {
        this.visitId = visitId;
        this.mobileVisitId = mobileVisitId;
        this.retailerId = retailerId;
        this.mobileRetailerId = mobileRetailerId;
        this.visitDate = visitDate;
        this.hasOrder = hasOrder;
        this.feedback = feedback;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getVisitId()
    {
        return visitId;
    }

    public void setVisitId(String visitId)
    {
        this.visitId = visitId;
    }

    public String getMobileVisitId()
    {
        return mobileVisitId;
    }

    public void setMobileVisitId(String mobileVisitId)
    {
        this.mobileVisitId = mobileVisitId;
    }

    public String getRetailerId()
    {
        return retailerId;
    }

    public void setRetailerId(String retailerId)
    {
        this.retailerId = retailerId;
    }

    public String getMobileRetailerId()
    {
        return mobileRetailerId;
    }

    public void setMobileRetailerId(String mobileRetailerId)
    {
        this.mobileRetailerId = mobileRetailerId;
    }

    public String getVisitDate()
    {
        return visitDate;
    }

    public void setVisitDate(String visitDate)
    {
        this.visitDate = visitDate;
    }

    public String getHasOrder()
    {
        return hasOrder;
    }

    public void setHasOrder(String hasOrder)
    {
        this.hasOrder = hasOrder;
    }

    public String getFeedback()
    {
        return feedback;
    }

    public void setFeedback(String feedback)
    {
        this.feedback = feedback;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
}
