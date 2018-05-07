package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 13/2/18.
 */

public class RetailerGroupHistory
{

    public String retailer_id;
    public String retailer_name;

    public RetailerGroupHistory(String retailer_id, String retailer_name)
    {
        this.retailer_id = retailer_id;
        this.retailer_name = retailer_name;
    }

    @Override
    public String toString()
    {
        return  retailer_name ;
    }
}
