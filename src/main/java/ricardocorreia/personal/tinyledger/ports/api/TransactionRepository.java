package ricardocorreia.personal.tinyledger.ports.api;

import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {

    <T extends Transaction> T save(T transaction);

    List<Transaction> findFor(UUID accountId);
}
