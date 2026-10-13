package ricardocorreia.personal.tinyledger.domain.api;

import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface ListTransactionsUseCase {

    List<Transaction> find(UUID accountId);
}
