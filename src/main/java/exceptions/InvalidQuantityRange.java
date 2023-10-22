package exceptions;

import static defines.Errors.INVALID_QUANTITY_RANGE;

public class InvalidQuantityRange extends Exception {
    public InvalidQuantityRange() {
        super(INVALID_QUANTITY_RANGE);
    }
}
