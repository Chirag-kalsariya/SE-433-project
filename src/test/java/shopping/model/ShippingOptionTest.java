package shopping.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShippingOptionTest {

    @Test
    void standardHasCorrectDisplayNameAndCost() {
        assertEquals("Standard", ShippingOption.STANDARD.getDisplayName());
        assertEquals(10.00, ShippingOption.STANDARD.getBaseCost());
    }

    @Test
    void nextDayHasCorrectDisplayNameAndCost() {
        assertEquals("Next day", ShippingOption.NEXT_DAY.getDisplayName());
        assertEquals(25.00, ShippingOption.NEXT_DAY.getBaseCost());
    }

    @Test
    void fromInputAcceptsStandardVariants() {
        assertEquals(ShippingOption.STANDARD, ShippingOption.fromInput("Standard"));
        assertEquals(ShippingOption.STANDARD, ShippingOption.fromInput("  standard  "));
    }

    @Test
    void fromInputAcceptsNextDayVariants() {
        assertEquals(ShippingOption.NEXT_DAY, ShippingOption.fromInput("Next day"));
        assertEquals(ShippingOption.NEXT_DAY, ShippingOption.fromInput("NEXT_DAY"));
        assertEquals(ShippingOption.NEXT_DAY, ShippingOption.fromInput("nextday"));
    }

    @Test
    void fromInputRejectsNullAndInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> ShippingOption.fromInput(null));
        assertThrows(IllegalArgumentException.class, () -> ShippingOption.fromInput("overnight"));
    }
}
