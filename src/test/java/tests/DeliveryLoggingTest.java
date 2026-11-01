package tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import model.Customer;
import model.Dish;
import model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import repository.OrderRepository;
import service.OrderService;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryLoggingTest {
    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(OrderService.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void arrangeDeliverySuccessLogsInfo() {
        OrderRepository repository = new OrderRepository();
        OrderService service = new OrderService(repository, 300.0);
        Customer customer = customerWithCash(1000.0);

        Order order = service.arrangeDelivery(customer, List.of(steak()), "Минск, ул. Ленина, 1");

        assertEquals("Минск, ул. Ленина, 1", order.deliveryAddress);
        assertEquals(500.0, customer.cash, 0.001);
        assertSame(order, repository.getById(order.id));
        assertTrue(hasLog(Level.INFO, "Delivery created"));
    }

    @Test
    void invalidDeliveryLogsWarnWithoutChargingCustomer() {
        OrderRepository repository = new OrderRepository();
        OrderService service = new OrderService(repository, 300.0);
        Customer customer = customerWithCash(1000.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.arrangeDelivery(customer, List.of(steak()), "  "));

        assertTrue(repository.getAll().isEmpty());
        assertEquals(1000.0, customer.cash, 0.001);
        assertTrue(hasLog(Level.WARN, "delivery address is missing"));
    }

    @Test
    void insufficientFundsLogsWarn() {
        OrderService service = new OrderService(new OrderRepository(), 300.0);
        Customer customer = customerWithCash(100.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.arrangeDelivery(customer, List.of(steak()), "Минск"));

        assertEquals(100.0, customer.cash, 0.001);
        assertTrue(hasLog(Level.WARN, "Не хватает средств"));
    }

    @Test
    void repositoryFailureLogsErrorWithoutChargingCustomer() {
        OrderRepository failingRepository = new OrderRepository() {
            @Override
            public void add(Order order) {
                throw new IllegalStateException("Storage unavailable");
            }
        };
        OrderService service = new OrderService(failingRepository, 300.0);
        Customer customer = customerWithCash(1000.0);

        assertThrows(IllegalStateException.class,
                () -> service.arrangeDelivery(customer, List.of(steak()), "Минск"));

        assertEquals(1000.0, customer.cash, 0.001);
        assertTrue(hasLog(Level.ERROR, "Delivery processing failed"));
    }

    private boolean hasLog(Level level, String messageFragment) {
        return appender.list.stream()
                .anyMatch(event -> event.getLevel() == level
                        && event.getFormattedMessage().contains(messageFragment));
    }

    private Customer customerWithCash(double cash) {
        return new Customer(7, "Иванов", "Иван", "Иванович", "123", cash);
    }

    private Dish steak() {
        return new Dish(1, "Стейк", new String[]{}, 500.0);
    }
}
