package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 14/9/17.
 */

public class Retailer
{
    String retailerID;
    String retailerName;
    String shopName;
    String mobileNumber;

    public Retailer(String retailerID, String retailerName, String shopName, String mobileNumber)
    {
        this.retailerID = retailerID;
        this.retailerName = retailerName;
        this.shopName = shopName;
        this.mobileNumber = mobileNumber;
    }

    public String getRetailerID()
    {
        return retailerID;
    }

    public void setRetailerID(String retailerID)
    {
        this.retailerID = retailerID;
    }

    public String getRetailerName()
    {
        return retailerName;
    }

    public void setRetailerName(String retailerName)
    {
        this.retailerName = retailerName;
    }

    public String getShopName()
    {
        return shopName;
    }

    public void setShopName(String shopName)
    {
        this.shopName = shopName;
    }

    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }
}
