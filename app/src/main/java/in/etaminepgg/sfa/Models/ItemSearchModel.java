package in.etaminepgg.sfa.Models;

/**
 * Created by sameer on 23/6/17.
 */

public class ItemSearchModel {

    String itemName;
    String itemCategory;
    String itemId;
    String itemMaterial;
    String itemUsage;
    String itemPrice;

    public ItemSearchModel()
    {

    }

    public ItemSearchModel(String itemId, String itemName, String itemCategory, String itemMaterial, String itemUsage, String itemPrice)
    {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemId = itemId;
        this.itemMaterial = itemMaterial;
        this.itemUsage = itemUsage;
        this.itemPrice = itemPrice;

    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemMaterial(String itemMaterial) {
        this.itemMaterial = itemMaterial;
    }

    public void setItemUsage(String itemUsage) {
        this.itemUsage = itemUsage;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemMaterial() {
        return itemMaterial;
    }

    public String getItemUsage() {
        return itemUsage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

}
