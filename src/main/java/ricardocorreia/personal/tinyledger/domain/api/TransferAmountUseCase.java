package ricardocorreia.personal.tinyledger.domain.api;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferAmountUseCase {

    void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);
}
