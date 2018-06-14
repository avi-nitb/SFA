package in.etaminepgg.sfa.Models;

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
    String sku_free_qty;
    String sku_discount;
    String sku_price_before_discount;
    String sku_final_price;

    public SalesOrderSku(long orderDetailId, String skuID, String skuName, String skuPrice, String skuQty,String sku_free_qty,String sku_discount,String sku_price_before_discount,String sku_final_price)
    {
        this.orderDetailId = orderDetailId;
        this.skuID = skuID;
        this.skuName = skuName;
        this.skuPrice = skuPrice;
        this.skuQty = skuQty;
        this.sku_free_qty = sku_free_qty;
        this.sku_discount = sku_discount;
        this.sku_price_before_discount = sku_price_before_discount;
        this.sku_final_price = sku_final_price;
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

    public String getSku_free_qty()
    {
        return sku_free_qty;
    }

    public void setSku_free_qty(String sku_free_qty)
    {
        this.sku_free_qty = sku_free_qty;
    }

    public String getSku_discount()
    {
        return sku_discount;
    }

    public void setSku_discount(String sku_discount)
    {
        this.sku_discount = sku_discount;
    }

    public String getSku_price_before_discount()
    {
        return sku_price_before_discount;
    }

    public void setSku_price_before_discount(String sku_price_before_discount)
    {
        this.sku_price_before_discount = sku_price_before_discount;
    }

    public String getSku_final_price()
    {
        return sku_final_price;
    }

    public void setSku_final_price(String sku_final_price)
    {
        this.sku_final_price = sku_final_price;
    }
}
