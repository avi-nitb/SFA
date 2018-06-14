package in.etaminepgg.sfa.InputModel_For_Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by etamine on 10/1/18.
 */

public class IM_PutSalesorderDetails
{

    @SerializedName("authToken")
    @Expose
    private String authToken;

    @SerializedName("ord_no")
    @Expose
    private String ordNo;

    @SerializedName("total_amount")
    @Expose
    private String totalAmount;

    @SerializedName("total_discount")
    @Expose
    private String total_discount;


    @SerializedName("grand_total")
    @Expose
    private String grand_total;


    @SerializedName("company_id")
    @Expose
    private String companyId;

    @SerializedName("salesData")
    @Expose
    private List<SalesDatum> salesData = null;

    public IM_PutSalesorderDetails()
    {
    }

    public IM_PutSalesorderDetails(String authToken, String ordNo, String totalAmount,String total_discount, String companyId, List<SalesDatum> salesData,String grand_total)
    {
        this.authToken = authToken;
        this.ordNo = ordNo;
        this.totalAmount = totalAmount;
        this.total_discount=total_discount;
        this.companyId = companyId;
        this.salesData = salesData;
        this.grand_total = grand_total;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getOrdNo() {
        return ordNo;
    }

    public void setOrdNo(String ordNo) {
        this.ordNo = ordNo;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<SalesDatum> getSalesData() {
        return salesData;
    }

    public void setSalesData(List<SalesDatum> salesData) {
        this.salesData = salesData;
    }

    public String getTotal_discount()
    {
        return total_discount;
    }

    public void setTotal_discount(String total_discount)
    {
        this.total_discount = total_discount;
    }

    public String getGrand_total()
    {
        return grand_total;
    }

    public void setGrand_total(String grand_total)
    {
        this.grand_total = grand_total;
    }

    public class SalesDatum {

        @SerializedName("sku_id")
        @Expose
        private String skuId;
        @SerializedName("sku_list_price")
        @Expose
        private String skuListPrice;

        @SerializedName("sku_discounted_price")
        @Expose
        private String skudiscountedprice;


        @SerializedName("qty_ordered")
        @Expose
        private String qtyOrdered;


        @SerializedName("sku_free_qty")
        @Expose
        private String sku_free_qty;


        @SerializedName("sku_discount")
        @Expose
        private String sku_discount;



        @SerializedName("required_by_date")
        @Expose
        private String requiredByDate;




        @SerializedName("sku_attribute")
        @Expose
        private List<SkuAttribute> skuAttribute = null;

        public SalesDatum()
        {
        }

        public SalesDatum(String skuId, String skuListPrice,String skudiscountedprice, String qtyOrdered, String requiredByDate, List<SkuAttribute> skuAttribute,String sku_free_qty,String sku_discount)
        {
            this.skuId = skuId;
            this.skuListPrice = skuListPrice;
            this.skudiscountedprice = skudiscountedprice;
            this.qtyOrdered = qtyOrdered;
            this.requiredByDate = requiredByDate;
            this.skuAttribute = skuAttribute;
            this.sku_free_qty = sku_free_qty;
            this.sku_discount = sku_discount;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getSkuListPrice() {
            return skuListPrice;
        }

        public void setSkuListPrice(String skuListPrice) {
            this.skuListPrice = skuListPrice;
        }

        public String getQtyOrdered() {
            return qtyOrdered;
        }

        public void setQtyOrdered(String qtyOrdered) {
            this.qtyOrdered = qtyOrdered;
        }

        public String getRequiredByDate() {
            return requiredByDate;
        }

        public void setRequiredByDate(String requiredByDate) {
            this.requiredByDate = requiredByDate;
        }

        public List<SkuAttribute> getSkuAttribute() {
            return skuAttribute;
        }

        public void setSkuAttribute(List<SkuAttribute> skuAttribute) {
            this.skuAttribute = skuAttribute;
        }

        public String getSkudiscountedprice()
        {
            return skudiscountedprice;
        }

        public void setSkudiscountedprice(String skudiscountedprice)
        {
            this.skudiscountedprice = skudiscountedprice;
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


        public class SkuAttribute {

            @SerializedName("global_attribute_id")
            @Expose
            private String globalAttributeId;
            @SerializedName("attribute_name")
            @Expose
            private String attributeName;
            @SerializedName("attribute_value")
            @Expose
            private String attributeValue;

            public SkuAttribute(String globalAttributeId, String attributeName, String attributeValue)
            {
                this.globalAttributeId = globalAttributeId;
                this.attributeName = attributeName;
                this.attributeValue = attributeValue;
            }

            public SkuAttribute()
            {
            }

            public String getGlobalAttributeId() {
                return globalAttributeId;
            }

            public void setGlobalAttributeId(String globalAttributeId) {
                this.globalAttributeId = globalAttributeId;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public void setAttributeName(String attributeName) {
                this.attributeName = attributeName;
            }

            public String getAttributeValue() {
                return attributeValue;
            }

            public void setAttributeValue(String attributeValue) {
                this.attributeValue = attributeValue;
            }

        }

    }


}
