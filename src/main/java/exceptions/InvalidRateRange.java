package exceptions;

import static defines.Errors.INVALID_RATE_RANGE;

public class InvalidRateRange extends Exception {
    public InvalidRateRange() {
        super(INVALID_RATE_RANGE);
    }
}
