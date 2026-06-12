package shopping.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderValidatorTest {

    @Test
    void acceptsValidQuantityAndPurchaseAmount() {
        assertDoesNotThrow(() -> OrderValidator.validateQuantity(1));
        assertDoesNotThrow(() -> OrderValidator.validatePurchaseAmount(1.00));
        assertDoesNotThrow(() -> OrderValidator.validatePurchaseAmount(99_999.99));
    }

    @Test
    void rejectsInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> OrderValidator.validateQuantity(0));
        assertThrows(IllegalArgumentException.class, () -> OrderValidator.validateQuantity(-1));
    }

    @Test
    void rejectsPurchaseAmountOutsideAllowedRange() {
        IllegalArgumentException tooSmall = assertThrows(
                IllegalArgumentException.class, () -> OrderValidator.validatePurchaseAmount(0.99));
        IllegalArgumentException tooLarge = assertThrows(
                IllegalArgumentException.class, () -> OrderValidator.validatePurchaseAmount(100_000.00));

        assertTrue(tooSmall.getMessage().contains("1.00"));
        assertTrue(tooLarge.getMessage().contains("99999.99"));
    }
}
