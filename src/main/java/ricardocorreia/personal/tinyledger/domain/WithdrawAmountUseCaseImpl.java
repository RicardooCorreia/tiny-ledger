package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.WithdrawAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.InstantProvider;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Withdrawal;

@Service
@RequiredArgsConstructor
public class WithdrawAmountUseCaseImpl implements WithdrawAmountUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final InstantProvider instantProvider;

    @Override
    public Withdrawal withdraw(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.find(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.withdraw(amount); // Possible bug if we change persistence layer. Kept for simplicity

        return transactionRepository.save(new Withdrawal(amount, instantProvider.instant(), accountId));
    }
}
