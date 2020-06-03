package com.example.the_calories_tracking_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    static private List<Item> itemList = new ArrayList<>();

    public void addItem(Item item) {
        itemList.add(item);
    }

    public static List<Item> getItemList() {
        return itemList;
    }

    public int numItem() {
        return itemList.size();
    }
}


