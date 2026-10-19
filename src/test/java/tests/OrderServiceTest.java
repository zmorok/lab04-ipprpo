package tests;

import model.Customer;
import model.Dish;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.IRepository;
import repository.OrderRepository;
import service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private IRepository<Order> repo;
    private OrderService service;

    @BeforeEach
    void setUp() {
        repo = new OrderRepository();
        service = new OrderService(repo, 300.0);
    }

    @Test
    void createOrder_Positive() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 1000.0);
        Dish dish = new Dish(1, "Стейк", new String[]{}, 500.0);

        Order createdOrder = service.createOrder(customer, List.of(dish));

        assertNotNull(createdOrder);
        assertEquals(500.0, customer.cash);
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void createOrder_Negative_NotEnoughCash() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 100.0);
        Dish dish = new Dish(1, "Стейк", new String[]{}, 500.0);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createOrder(customer, List.of(dish));
        });

        assertEquals(100.0, customer.cash);
        assertEquals(0, repo.getAll().size());
    }

    @Test
    void createOrder_Negative_BelowMinOrderPrice() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 1000.0);
        Dish cheapDish = new Dish(1, "Чай", new String[]{}, 100.0);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createOrder(customer, List.of(cheapDish));
        });

        assertEquals(1000.0, customer.cash);
        assertEquals(0, repo.getAll().size());
    }
}