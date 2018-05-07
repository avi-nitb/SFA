package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 2/7/17.
 */

public class Sku
{
    String skuId;
    String skuName;
    String skuPrice;
    String description;
    String skuCategory;
    String skuSubCategory;
    String skuCategoryDescription;
    String isNewSku;
    String isPromotionalSku;
    String skuSize;
    String skuMaterial;
    String skuUsage;
    String skuColor;
    String skuShape;
    String skuPhotoSource;
    String skuUom;
    String uploadStatus;

    public Sku(String skuId, String skuName, String skuPrice)
    {
        this.skuId = skuId;
        this.skuName = skuName;
        this.skuPrice = skuPrice;
    }

    public Sku(String skuId, String skuName, String skuPrice, String skuCategory,String skuCategoryDescription,String sku_photo_source)
    {
        this.skuId = skuId;
        this.skuName = skuName;
        this.skuPrice = skuPrice;
        this.skuCategory = skuCategory;
        this.skuCategoryDescription = skuCategoryDescription;
        this.skuPhotoSource = sku_photo_source;
    }


    public String getSkuId()
    {
        return skuId;
    }

    public void setSkuId(String skuId)
    {
        this.skuId = skuId;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getSkuCategory()
    {
        return skuCategory;
    }

    public void setSkuCategory(String skuCategory)
    {
        this.skuCategory = skuCategory;
    }

    public String getSkuSubCategory()
    {
        return skuSubCategory;
    }

    public void setSkuSubCategory(String skuSubCategory)
    {
        this.skuSubCategory = skuSubCategory;
    }

    public String getIsNewSku()
    {
        return isNewSku;
    }

    public void setIsNewSku(String isNewSku)
    {
        this.isNewSku = isNewSku;
    }

    public String getIsPromotionalSku()
    {
        return isPromotionalSku;
    }

    public void setIsPromotionalSku(String isPromotionalSku)
    {
        this.isPromotionalSku = isPromotionalSku;
    }

    public String getSkuSize()
    {
        return skuSize;
    }

    public void setSkuSize(String skuSize)
    {
        this.skuSize = skuSize;
    }

    public String getSkuMaterial()
    {
        return skuMaterial;
    }

    public void setSkuMaterial(String skuMaterial)
    {
        this.skuMaterial = skuMaterial;
    }

    public String getSkuUsage()
    {
        return skuUsage;
    }

    public void setSkuUsage(String skuUsage)
    {
        this.skuUsage = skuUsage;
    }

    public String getSkuColor()
    {
        return skuColor;
    }

    public void setSkuColor(String skuColor)
    {
        this.skuColor = skuColor;
    }

    public String getSkuShape()
    {
        return skuShape;
    }

    public void setSkuShape(String skuShape)
    {
        this.skuShape = skuShape;
    }

    public String getSkuPhotoSource()
    {
        return skuPhotoSource;
    }

    public void setSkuPhotoSource(String skuPhotoSource)
    {
        this.skuPhotoSource = skuPhotoSource;
    }

    public String getSkuUom()
    {
        return skuUom;
    }

    public void setSkuUom(String skuUom)
    {
        this.skuUom = skuUom;
    }

    public String getUploadStatus()
    {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus)
    {
        this.uploadStatus = uploadStatus;
    }

    public String getSkuCategoryDescription()
    {
        return skuCategoryDescription;
    }

    public void setSkuCategoryDescription(String skuCategoryDescription)
    {
        this.skuCategoryDescription = skuCategoryDescription;
    }
}


