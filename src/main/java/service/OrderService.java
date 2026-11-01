package service;

import java.util.List;
import model.Customer;
import model.Dish;
import model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.IRepository;

public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final IRepository<Order> repository;
    private final double minOrderPrice;

    public OrderService(IRepository<Order> repository, double minOrderPrice) {
        this.repository = repository;
        this.minOrderPrice = minOrderPrice;
    }

    public Order createOrder(Customer customer, List<Dish> dishes) {
        return createOrderInternal(customer, dishes, null);
    }

    /**
     * Arranges delivery of a food order, keeping the delivery address with the order.
     * Invalid requests are warnings; unexpected processing failures are errors.
     */
    public Order arrangeDelivery(Customer customer, List<Dish> dishes, String deliveryAddress) {
        int customerId = customer == null ? -1 : customer.id;

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            LOGGER.warn("Delivery rejected for customerId={}: delivery address is missing", customerId);
            throw new IllegalArgumentException("Адрес доставки не указан");
        }

        try {
            Order order = createOrderInternal(customer, dishes, deliveryAddress);
            LOGGER.info("Delivery created: orderId={}, customerId={}, total={}",
                    order.id, order.idCustomer, order.price);
            return order;
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Delivery rejected for customerId={}: {}", customerId, exception.getMessage());
            throw exception;
        } catch (RuntimeException exception) {
            LOGGER.error("Delivery processing failed for customerId={}", customerId, exception);
            throw exception;
        }
    }

    private Order createOrderInternal(Customer customer, List<Dish> dishes, String deliveryAddress) {
        if (customer == null || dishes == null || dishes.isEmpty()) {
            throw new IllegalArgumentException("Данные заказа заполнены неверно");
        }

        double orderPrice = 0;
        for (Dish dish : dishes) {
            if (dish == null || !Double.isFinite(dish.price) || dish.price <= 0) {
                throw new IllegalArgumentException("В заказе обнаружено некорректное блюдо");
            }
            orderPrice += dish.price;
        }

        if (orderPrice < minOrderPrice) {
            throw new IllegalArgumentException(
                    String.format("Сумма заказа (%.2f) меньше минимальной суммы (%.2f)",
                            orderPrice, minOrderPrice));
        }

        if (customer.cash < orderPrice) {
            throw new IllegalArgumentException(
                    String.format("Не хватает средств (%.2f) для оплаты заказа на сумму %.2f",
                            customer.cash, orderPrice));
        }

        int id = repository.getMaxId() + 1;
        Order order = new Order(id, customer.id, dishes, deliveryAddress);

        // Charge the customer only after the repository accepts the order.
        repository.add(order);
        customer.cash -= orderPrice;
        return order;
    }

    public String testMethod() {
        StringBuilder sb = new StringBuilder("String var2 =");

        for (int i = 1; i < 5; i++) {
            sb.append(" ").append(i);
        }

        return sb.toString();
    }
}
