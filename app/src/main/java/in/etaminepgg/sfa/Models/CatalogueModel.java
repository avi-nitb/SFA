package in.etaminepgg.sfa.Models;

/**
 * Created by sameer on 24/6/17.
 */

public class CatalogueModel
{

    String item_id, item_name, item_category, item_price, scheme_name, img_path;

    public CatalogueModel()
    {

    }

    public CatalogueModel(String item_id, String item_name, String item_category, String item_price, String scheme_name, String img_path)
    {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_category = item_category;
        this.item_price = item_price;
        this.scheme_name = scheme_name;
        this.img_path = img_path;
    }

    public String getItem_id()
    {
        return item_id;
    }

    public void setItem_id(String item_id)
    {
        this.item_id = item_id;
    }

    public String getItem_name()
    {
        return item_name;
    }

    public void setItem_name(String item_name)
    {
        this.item_name = item_name;
    }

    public String getItem_category()
    {
        return item_category;
    }

    public void setItem_category(String item_category)
    {
        this.item_category = item_category;
    }

    public String getItem_price()
    {
        return item_price;
    }

    public void setItem_price(String item_price)
    {
        this.item_price = item_price;
    }

    public String getScheme_name()
    {
        return scheme_name;
    }

    public void setScheme_name(String scheme_name)
    {
        this.scheme_name = scheme_name;
    }

    public String getImg_path()
    {
        return img_path;
    }

    public void setImg_path(String img_path)
    {
        this.img_path = img_path;
    }

}
