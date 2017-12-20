package in.etaminepgg.sfa.Models;

public class MySalesHistory
{
    private String orderId;
    private String retailerId;
    private String orderDate;
    private String skuCount;
    private String orderTotal;

    public MySalesHistory(String orderId, String retailerId, String orderDate, String skuCount, String orderTotal)
    {
        this.orderId = orderId;
        this.retailerId = retailerId;
        this.orderDate = orderDate;
        this.skuCount = skuCount;
        this.orderTotal = orderTotal;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getRetailerId()
    {
        return retailerId;
    }

    public void setRetailerId(String retailerId)
    {
        this.retailerId = retailerId;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(String orderDate)
    {
        this.orderDate = orderDate;
    }

    public String getSkuCount()
    {
        return skuCount;
    }

    public void setSkuCount(String skuCount)
    {
        this.skuCount = skuCount;
    }

    public String getOrderTotal()
    {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal)
    {
        this.orderTotal = orderTotal;
    }
}


