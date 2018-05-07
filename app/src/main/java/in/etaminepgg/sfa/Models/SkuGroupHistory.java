package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 13/2/18.
 */

public class SkuGroupHistory
{

    public String sku_id;
    public String sku_name;

    public SkuGroupHistory(String sku_id, String sku_name)
    {
        this.sku_id = sku_id;
        this.sku_name = sku_name;
    }

    @Override
    public String toString()
    {
        return  sku_name;
    }
}
