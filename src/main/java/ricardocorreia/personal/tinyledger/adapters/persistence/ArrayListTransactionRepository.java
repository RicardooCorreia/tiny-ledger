package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.springframework.stereotype.Component;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ArrayListTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>(); // Does not scale very well

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
