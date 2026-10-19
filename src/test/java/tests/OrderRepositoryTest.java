package tests;

import model.Dish;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.IRepository;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    private IRepository<Order> repo;

    @BeforeEach
    void setUp() {
        repo = new OrderRepository();

        Dish dish1 = new Dish(1, "Стейк1", new String[]{}, 500.0);
        Dish dish2 = new Dish(2, "Стейк2", new String[]{}, 200.0);
        Dish dish3 = new Dish(3, "Стейк3", new String[]{}, 1400.0);

        repo.add(new Order(1, 1, List.of(dish2)));
        repo.add(new Order(2, 1, List.of(dish1, dish3)));
        repo.add(new Order(3, 1, List.of(dish1, dish3, dish2)));
    }

    // getAll
    @Test
    void getAll_Success() {
        List<Order> orders = repo.getAll();

        assertEquals(3, orders.size());
    }

    @Test
    void getAll_Empty() {
        repo = new OrderRepository();

        assertTrue(repo.getAll().isEmpty());
    }

    // add
    @Test
    void add_Success() {
        Order order = new Order(4, 1, List.of());

        repo.add(order);

        assertEquals(4, repo.getAll().size());
        assertEquals(order, repo.getById(4));
    }

    @Test
    void add_Null() {
        repo.add(null);

        assertEquals(3, repo.getAll().size());
    }

    @Test
    void add_Duplicate() {
        Order order = new Order(1, 2, List.of());

        repo.add(order);

        assertEquals(3, repo.getAll().size());
    }

    // getById
    @Test
    void getById_Success() {
        Order order = repo.getById(2);

        assertNotNull(order);
        assertEquals(2, order.id);
    }

    @Test
    void getById_NotFound() {
        assertNull(repo.getById(999));
    }

    // delete
    @Test
    void delete_Success() {
        Order deleted = repo.delete(2);

        assertNotNull(deleted);
        assertEquals(2, deleted.id);
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void delete_NotFound() {
        assertNull(repo.delete(999));
    }

    // getMaxId
    @Test
    void getMaxId_Success() {
        assertEquals(3, repo.getMaxId());
    }

    @Test
    void getMaxId_Empty() {
        repo = new OrderRepository();

        assertEquals(-1, repo.getMaxId());
    }

    // getInfo
    @Test
    void getInfo_Success() {
        assertDoesNotThrow(() -> repo.getInfo(1));
    }

    @Test
    void getInfo_NotFound() {
        assertDoesNotThrow(() -> repo.getInfo(999));
    }
}