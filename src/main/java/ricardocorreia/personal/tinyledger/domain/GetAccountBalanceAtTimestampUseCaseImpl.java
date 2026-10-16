package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.GetAccountBalanceAtTimestampUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAccountBalanceAtTimestampUseCaseImpl implements GetAccountBalanceAtTimestampUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public BigDecimal get(UUID accountId, Instant timestamp) {

        accountRepository.find(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        List<Transaction> allTransactions = transactionRepository.findFor(accountId);

        return allTransactions.stream()
                .takeWhile(transaction -> !transaction.timestamp().isAfter(timestamp))
                .reduce(BigDecimal.ZERO,
                        (balance, transaction) ->
                                switch (transaction) {
                                    case Transaction.Deposit deposit -> balance.add(deposit.amount());
                                    case Transaction.Withdrawal withdrawal -> balance.subtract(withdrawal.amount());
                                    case Transaction.Transfer transfer -> {
                                        if (transfer.fromAccountId().equals(accountId)) {
                                            yield balance.subtract(transfer.amount());
                                        } else {
                                            yield balance.add(transfer.amount());
                                        }
                                    }
                                }, BigDecimal::add);
    }
}
