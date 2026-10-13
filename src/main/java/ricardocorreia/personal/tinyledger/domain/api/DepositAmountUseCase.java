package ricardocorreia.personal.tinyledger.domain.api;

import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface DepositAmountUseCase {

    Transaction.Deposit deposit(UUID accountId, BigDecimal amount);
}
