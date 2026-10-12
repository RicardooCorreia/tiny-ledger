package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.TransferAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AmountIsNotPositiveException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferAmountUseCaseImpl implements TransferAmountUseCase {

    private final AccountRepository accountRepository;

    @Override
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {

        validatePositiveAmount(amount);
        Account fromAccount = accountRepository.find(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
        Account toAccount = accountRepository.find(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        // Possible bug if we change persistence layer. Kept for simplicity
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountIsNotPositiveException(amount);
        }
    }
}
