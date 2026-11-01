package model;

import java.util.Date;
import java.util.List;

public class Order {
    public int id;
    public int idCustomer;
    public List<Dish> orderedDishes;
    public double price = 0;
    public Date orderedDate;
    public String deliveryAddress;

    public Order(int id, int idCustomer, List<Dish> orderedDishes) {
        this(id, idCustomer, orderedDishes, null);
    }

    public Order(int id, int idCustomer, List<Dish> orderedDishes, String deliveryAddress) {
        this.id = id;
        this.idCustomer = idCustomer;
        this.orderedDishes = orderedDishes;
        this.deliveryAddress = deliveryAddress;

        if (orderedDishes != null) {
            for (Dish dish : orderedDishes) {
                this.price += dish.price;
            }
        }

        this.orderedDate = new Date();
    }
}
