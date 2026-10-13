package ricardocorreia.personal.tinyledger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ricardocorreia.personal.tinyledger.domain.api.GetAccountDetailsUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAccountDetailsUseCaseImpl implements GetAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    @Override
    public Account get(UUID accountId) {
        return accountRepository.find(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
