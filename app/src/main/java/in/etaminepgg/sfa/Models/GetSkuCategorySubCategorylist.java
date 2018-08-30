package in.etaminepgg.sfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by etamine on 28/2/18.
 */

public class GetSkuCategorySubCategorylist
{

    @SerializedName("api_status")
    @Expose
    private Integer apiStatus;
    @SerializedName("sku_categories")
    @Expose
    private List<SkuCategory> skuCategories = null;

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public List<SkuCategory> getSkuCategories() {
        return skuCategories;
    }

    public void setSkuCategories(List<SkuCategory> skuCategories) {
        this.skuCategories = skuCategories;
    }



    public class SkuCategory {

        @SerializedName("category_id")
        @Expose
        private String categoryId;
        @SerializedName("category_name")
        @Expose
        private String categoryName;
        @SerializedName("category_description")
        @Expose
        private String categoryDescription;
        @SerializedName("subCategories")
        @Expose
        private List<SubCategory> subCategories = null;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryDescription() {
            return categoryDescription;
        }

        public void setCategoryDescription(String categoryDescription) {
            this.categoryDescription = categoryDescription;
        }

        public List<SubCategory> getSubCategories() {
            return subCategories;
        }

        public void setSubCategories(List<SubCategory> subCategories) {
            this.subCategories = subCategories;
        }


        public class SubCategory {

            @SerializedName("sub_category_id")
            @Expose
            private String subCategoryId;


            @SerializedName("category_id")
            @Expose
            private String categoryId;


            @SerializedName("sub_category_name")
            @Expose
            private String subCategoryName;

            @SerializedName("sub_category_description")
            @Expose
            private String subCategoryDescription;

            public String getSubCategoryId() {
                return subCategoryId;
            }

            public void setSubCategoryId(String subCategoryId) {
                this.subCategoryId = subCategoryId;
            }

            public String getSubCategoryName() {
                return subCategoryName;
            }

            public void setSubCategoryName(String subCategoryName) {
                this.subCategoryName = subCategoryName;
            }

            public String getSubCategoryDescription() {
                return subCategoryDescription;
            }

            public void setSubCategoryDescription(String subCategoryDescription) {
                this.subCategoryDescription = subCategoryDescription;
            }

            public String getCategoryId()
            {
                return categoryId;
            }

            public void setCategoryId(String categoryId)
            {
                this.categoryId = categoryId;
            }
        }

    }
}
