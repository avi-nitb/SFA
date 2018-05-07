package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 1/3/18.
 */

public class SubCategoryForCategoryHeader
{

    private String sub_category_id;
    private String sub_category_name;

    public SubCategoryForCategoryHeader(String sub_category_id, String sub_category_name)
    {
        this.sub_category_id = sub_category_id;
        this.sub_category_name = sub_category_name;
    }

    public String getSub_category_id()
    {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id)
    {
        this.sub_category_id = sub_category_id;
    }

    public String getSub_category_name()
    {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name)
    {
        this.sub_category_name = sub_category_name;
    }

    @Override
    public String toString()
    {
        return  sub_category_name ;
    }
}
