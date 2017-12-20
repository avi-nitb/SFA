package in.etaminepgg.sfa.Models;

import in.etaminepgg.sfa.Adapters.SalesOrderAdapter;

/**
 * Created by etamine on 2/8/17.
 */

public class SalesOrderSku
{
    long orderDetailId;
    String skuID;
    String skuName;
    String skuPrice;
    String skuQty;
    String skuAttributes;

    public SalesOrderSku(long orderDetailId, String skuID, String skuName, String skuPrice, String skuQty)
    {
        this.orderDetailId = orderDetailId;
        this.skuID = skuID;
        this.skuName = skuName;
        this.skuPrice = skuPrice;
        this.skuQty = skuQty;
    }

    public SalesOrderSku(long orderDetailId, String skuID, String skuName, String skuPrice, String skuQty, String skuAttributes)
    {
        this.orderDetailId = orderDetailId;
        this.skuID = skuID;
        this.skuName = skuName;
        this.skuPrice = skuPrice;
        this.skuQty = skuQty;
        this.skuAttributes = skuAttributes;
    }

    public long getOrderDetailId()
    {
        return orderDetailId;
    }

    public void setOrderDetailId(long orderDetailId)
    {
        this.orderDetailId = orderDetailId;
    }

    public String getSkuID()
    {
        return skuID;
    }

    public void setSkuID(String skuID)
    {
        this.skuID = skuID;
    }

    public String getSkuName()
    {
        return skuName;
    }

    public void setSkuName(String skuName)
    {
        this.skuName = skuName;
    }

    public String getSkuPrice()
    {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice)
    {
        this.skuPrice = skuPrice;
    }

    public String getSkuQty()
    {
        return skuQty;
    }

    public void setSkuQty(String skuQty)
    {
        this.skuQty = skuQty;
    }

    public String getSkuAttributes()
    {
        return skuAttributes;
    }

    public void setSkuAttributes(String skuAttributes)
    {
        this.skuAttributes = skuAttributes;
    }
}
