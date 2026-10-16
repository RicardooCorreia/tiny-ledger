package ricardocorreia.personal.tinyledger.domain.api;

import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.time.Instant;
import java.util.UUID;

public interface GetAccountDetailsUseCase {

    Account get(UUID account);
}
