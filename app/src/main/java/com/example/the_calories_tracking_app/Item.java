package com.example.the_calories_tracking_app;

public class Item {
    private float calories;
    private float fat;
    private float carbohydrate;
    private float protein;
    private float cholesterol;
    private float sodium;
    private float sugar;
    private float servings;


    public Item(float calories, float fat, float carbohydrate, float protein, float cholesterol,
                float sodium, float sugar, float servings) {
        this.calories = calories;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.sugar = sugar;
        this.servings = servings;
    }
}
