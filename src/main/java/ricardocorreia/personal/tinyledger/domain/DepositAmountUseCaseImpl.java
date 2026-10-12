package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.DepositAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepositAmountUseCaseImpl implements DepositAmountUseCase {

    private final AccountRepository accountRepository;

    @Override
    public BigDecimal deposit(UUID accountId, BigDecimal amount) { // todo :: not thread safe
        Account account = accountRepository.find(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.deposit(amount); // Possible bug if we change persistence layer. Kept for simplicity
    }
}
