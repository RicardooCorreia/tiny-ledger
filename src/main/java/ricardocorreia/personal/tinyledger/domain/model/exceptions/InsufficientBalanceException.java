package ricardocorreia.personal.tinyledger.domain.model.exceptions;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(UUID accountId, BigDecimal currentBalance, BigDecimal requestedAmount) {
        super("Account with ID [%s] does not have enough balance. Current: [%s], requested: [%s], difference: [%s]"
                .formatted(accountId, currentBalance, requestedAmount, requestedAmount.subtract(currentBalance)));
    }
}
