package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.springframework.stereotype.Component;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HashMapAccountRepository implements AccountRepository {

    private final Map<UUID, Account> repository = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> find(UUID id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public Account upsert(Account account) {
        repository.put(account.getId(), account);
        return account;
    }

    int size() {
        return repository.size();
    }
}
