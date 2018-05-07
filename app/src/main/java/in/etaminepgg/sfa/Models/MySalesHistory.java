package in.etaminepgg.sfa.Models;

import java.util.List;



public class MySalesHistory
{
    private String orderId;
    private String retailerId;
    private String orderDate;
    private String skuCount;
    private String orderTotal;

    private List<SkuDetails_Ordered> sku_Details_ordered;


    public MySalesHistory(String orderId, String retailerId, String orderDate, String skuCount, String orderTotal)
    {
        this.orderId = orderId;
        this.retailerId = retailerId;
        this.orderDate = orderDate;
        this.skuCount = skuCount;
        this.orderTotal = orderTotal;
    }

    public MySalesHistory()
    {
    }

    public MySalesHistory(String orderId, String retailerId, String orderDate, String skuCount, String orderTotal, List<SkuDetails_Ordered> sku_Details_ordered)
    {
        this.orderId = orderId;
        this.retailerId = retailerId;
        this.orderDate = orderDate;
        this.skuCount = skuCount;
        this.orderTotal = orderTotal;
        this.sku_Details_ordered = sku_Details_ordered;
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

    public List<SkuDetails_Ordered> getSku_Details_ordered()
    {
        return sku_Details_ordered;
    }

    public void setSku_Details_ordered(List<SkuDetails_Ordered> sku_Details_ordered)
    {
        this.sku_Details_ordered = sku_Details_ordered;
    }

    public class SkuDetails_Ordered
    {

        private String sku_id;
        private String sku_name;
        private String sku_qty;
        private String sku_finalprice;

        public SkuDetails_Ordered(String sku_id, String sku_name, String sku_qty, String sku_finalprice)
        {
            this.sku_id = sku_id;
            this.sku_name = sku_name;
            this.sku_qty = sku_qty;
            this.sku_finalprice = sku_finalprice;
        }

        public String getSku_id()
        {
            return sku_id;
        }

        public void setSku_id(String sku_id)
        {
            this.sku_id = sku_id;
        }

        public String getSku_name()
        {
            return sku_name;
        }

        public void setSku_name(String sku_name)
        {
            this.sku_name = sku_name;
        }

        public String getSku_qty()
        {
            return sku_qty;
        }

        public void setSku_qty(String sku_qty)
        {
            this.sku_qty = sku_qty;
        }

        public String getSku_finalprice()
        {
            return sku_finalprice;
        }

        public void setSku_finalprice(String sku_finalprice)
        {
            this.sku_finalprice = sku_finalprice;
        }
    }
}


