package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.springframework.stereotype.Component;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class LinkedQueueTransactionRepository implements TransactionRepository {

    private final Collection<Transaction> transactions = new ConcurrentLinkedQueue<>(); // Does not scale very well

    @Override
    public <T extends Transaction> T save(T transaction) {
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findFor(UUID accountId) {
        return transactions.stream()
                .filter(transaction -> transaction.relatesTo(accountId))
                .toList();
    }

    List<Transaction> getAll() {
        return new ArrayList<>(transactions);
    }
}
