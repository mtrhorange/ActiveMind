package com.example.activemind.gameController;

import android.graphics.drawable.Drawable;

public class CategoryModel {
    private  String categoryId, categoryName;
    private int categoryImage;

    public CategoryModel(String categoryId, String categoryName, int categoryImage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public CategoryModel(){

    }

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

    public int getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        this.categoryImage = categoryImage;
    }
}
