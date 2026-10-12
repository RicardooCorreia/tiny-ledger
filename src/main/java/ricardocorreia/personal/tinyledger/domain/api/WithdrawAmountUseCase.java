package ricardocorreia.personal.tinyledger.domain.api;

import java.math.BigDecimal;
import java.util.UUID;

public interface WithdrawAmountUseCase {

    BigDecimal withdraw(UUID accountId, BigDecimal amount);
}
