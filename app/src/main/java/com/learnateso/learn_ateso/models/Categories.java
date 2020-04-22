package com.learnateso.learn_ateso.models;

public class Categories {
    private Category[] categories;

    public Categories() {

    }

    public Category[] getCategories() {
        return categories;
    }

    //LiveData<List<FixAppCategory>> getCategories();

    public void setMessages(Category[] categories) {
        this.categories = categories;
    }

}
