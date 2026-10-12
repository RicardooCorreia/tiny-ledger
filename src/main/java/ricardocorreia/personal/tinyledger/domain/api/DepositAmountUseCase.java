package ricardocorreia.personal.tinyledger.domain.api;

import java.math.BigDecimal;
import java.util.UUID;

public interface DepositAmountUseCase {

    BigDecimal deposit(UUID accountId, BigDecimal amount);
}
