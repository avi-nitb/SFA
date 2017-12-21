package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 16/8/17.
 */

public class PendingOrder
{
    private String orderID;
    private String retailerID;
    private String orderDate;

    public PendingOrder(String orderID, String retailerID, String orderDate)
    {
        this.orderID = orderID;
        this.retailerID = retailerID;
        this.orderDate = orderDate;
    }

    public String getOrderID()
    {
        return orderID;
    }

    public void setOrderID(String orderID)
    {
        this.orderID = orderID;
    }

    public String getRetailerID()
    {
        return retailerID;
    }

    public void setRetailerID(String retailerID)
    {
        this.retailerID = retailerID;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(String orderDate)
    {
        this.orderDate = orderDate;
    }
}
