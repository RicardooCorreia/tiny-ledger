package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.springframework.stereotype.Component;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class HashMapAccountRepository implements AccountRepository {

    // todo :: not thread safe
    private final Map<UUID, Account> repository = new HashMap<>();

    @Override
    public Optional<Account> find(UUID id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public Account upsert(Account account) {
        repository.put(account.getId(), account);
        return account;
    }
}
