package ricardocorreia.personal.tinyledger.domain.api;

import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface GetAccountBalanceAtTimestampUseCase {

    BigDecimal get(UUID id, Instant timestamp);
}
