package service;

import model.Customer;
import model.Dish;
import model.Order;
import repository.IRepository;

import java.util.List;

public class OrderService {
    private final IRepository<Order> repository;
    private final double minOrderPrice;

    public OrderService(IRepository<Order> repository, double minOrderPrice) {
        this.repository = repository;
        this.minOrderPrice = minOrderPrice;
    }

    public Order createOrder(Customer customer, List<Dish> dishes) {
        if (customer == null || dishes == null || dishes.isEmpty()) {
            throw new IllegalArgumentException("Данные заказа заполнены неверно");
        }

        double orderPrice = 0;
        for (Dish dish : dishes) {
            orderPrice += dish.price;
        }

        if (orderPrice < minOrderPrice) {
            throw new IllegalArgumentException(
                    String.format(
                            "Сумма заказа ({0}) меньше допустимой минимальной суммы ({1})",
                            orderPrice,
                            minOrderPrice)
                    );
        }

        if (customer.cash < orderPrice) {
            throw new IllegalArgumentException(
                    String.format(
                            "Не хватает средств ({0}) для оплаты заказа на сумму {1}",
                            customer.cash,
                            orderPrice)
            );
        }

        customer.cash -= orderPrice;
        int id = repository.getMaxId() + 1;
        Order order = new Order(id, customer.id, dishes);

        repository.add(order);
        return order;
    }
}
