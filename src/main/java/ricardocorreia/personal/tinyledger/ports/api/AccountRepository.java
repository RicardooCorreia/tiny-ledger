package ricardocorreia.personal.tinyledger.ports.api;

import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Optional<Account> find(UUID id);

    Account upsert(Account account);
}
