package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.DepositAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.InstantProvider;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Deposit;

@Service
@RequiredArgsConstructor
public class DepositAmountUseCaseImpl implements DepositAmountUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final InstantProvider instantProvider;

    @Override
    public Deposit deposit(UUID accountId, BigDecimal amount) { // todo :: not thread safe
        Account account = accountRepository.find(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.deposit(amount); // Possible bug if we change persistence layer. Kept for simplicity

        return transactionRepository.save(new Deposit(amount, instantProvider.instant(), accountId));
    }
}
