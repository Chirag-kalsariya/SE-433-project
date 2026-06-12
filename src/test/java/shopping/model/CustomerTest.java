package shopping.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {

    @Test
    void storesTrimmedNameAndUppercaseState() {
        Customer customer = new Customer("  Alex  ", " il ");
        assertEquals("Alex", customer.getName());
        assertEquals("IL", customer.getState());
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Customer("  ", "IL"));
        assertThrows(IllegalArgumentException.class, () -> new Customer(null, "IL"));
    }

    @Test
    void rejectsBlankState() {
        assertThrows(IllegalArgumentException.class, () -> new Customer("Alex", " "));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Alex", null));
    }
}
