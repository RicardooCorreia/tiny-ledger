package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.WithdrawAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawAmountUseCaseImpl implements WithdrawAmountUseCase {

    private final AccountRepository accountRepository;

    @Override
    public BigDecimal withdraw(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.find(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.withdraw(amount); // Possible bug if we change persistence layer. Kept for simplicity
    }
}
