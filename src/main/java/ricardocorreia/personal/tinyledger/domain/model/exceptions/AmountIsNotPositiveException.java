package ricardocorreia.personal.tinyledger.domain.model.exceptions;

import java.math.BigDecimal;

public class AmountIsNotPositiveException extends IllegalArgumentException {

    public AmountIsNotPositiveException(BigDecimal amount) {
        super("Input amount needs to be positive, instead got [%s]".formatted(amount));
    }
}
