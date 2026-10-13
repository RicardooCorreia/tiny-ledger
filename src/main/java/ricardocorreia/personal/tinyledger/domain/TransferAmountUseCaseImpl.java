package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.TransferAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AmountIsNotPositiveException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.TransferToTheSameAccountException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.InstantProvider;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Transfer;

@Service
@RequiredArgsConstructor
public class TransferAmountUseCaseImpl implements TransferAmountUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final InstantProvider instantProvider;

    @Override
    public Transfer transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {

        validatePositiveAmount(amount);
        validateDifferentAccountIds(fromAccountId, toAccountId);
        Account fromAccount = accountRepository.find(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
        Account toAccount = accountRepository.find(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));
        fromAccount.transfer(toAccount, amount);
        return transactionRepository.save(new Transfer(amount, instantProvider.instant(), fromAccountId, toAccountId));
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountIsNotPositiveException(amount);
        }
    }

    private void validateDifferentAccountIds(UUID fromAccountId, UUID toAccountId) {
        if (fromAccountId.equals(toAccountId)) {
            throw new TransferToTheSameAccountException(fromAccountId);
        }
    }
}
