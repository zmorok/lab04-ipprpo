package model;

public class Dish {
    public int id;
    public String name;
    public String[] ingredients;
    public double price;

    public Dish (int id, String name, String[] ingredients, double price) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
    }
}
