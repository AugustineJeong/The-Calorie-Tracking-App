package com.example.the_calories_tracking_app;

public class Item {
    private int x;

    public Item(int x) {
        this.x = x;
    }

    public static void main(String[] args) {
        Item item1 = new Item(1);
        Item item2 = new Item(2);

        Data data1 = new Data();
        data1.addItem(item1);

        Data data2 = new Data();
        data2.addItem(item2);

        data1.addItem(item1);

        System.out.println(data2.numItem());
        System.out.println(data1.numItem());
    }
}
