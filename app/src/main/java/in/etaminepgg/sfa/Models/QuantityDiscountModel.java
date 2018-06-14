package in.etaminepgg.sfa.Models;

public class QuantityDiscountModel
{

    private int skuQty;
    private int skuFreeQty;
    private float skuDiscount;
    private float skuUnitPrice;



    public QuantityDiscountModel(int skuQty, int skuFreeQty, float skuDiscount, float skuUnitPrice)
    {
        this.skuQty = skuQty;
        this.skuFreeQty = skuFreeQty;
        this.skuDiscount = skuDiscount;
        this.skuUnitPrice = skuUnitPrice;
    }

    public int getSkuQty()
    {
        return skuQty;
    }

    public void setSkuQty(int skuQty)
    {
        this.skuQty = skuQty;
    }

    public int getSkuFreeQty()
    {
        return skuFreeQty;
    }

    public void setSkuFreeQty(int skuFreeQty)
    {
        this.skuFreeQty = skuFreeQty;
    }

    public float getSkuDiscount()
    {
        return skuDiscount;
    }

    public void setSkuDiscount(float skuDiscount)
    {
        this.skuDiscount = skuDiscount;
    }

    public float getSkuUnitPrice()
    {
        return skuUnitPrice;
    }

    public void setSkuUnitPrice(float skuUnitPrice)
    {
        this.skuUnitPrice = skuUnitPrice;
    }
}
