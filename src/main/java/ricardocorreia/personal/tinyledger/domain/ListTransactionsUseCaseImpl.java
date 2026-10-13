package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.ListTransactionsUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListTransactionsUseCaseImpl implements ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> find(UUID accountId) {
        return transactionRepository.findFor(accountId);
    }
}
