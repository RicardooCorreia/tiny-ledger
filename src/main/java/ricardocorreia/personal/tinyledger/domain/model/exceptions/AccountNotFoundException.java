package ricardocorreia.personal.tinyledger.domain.model.exceptions;

import java.util.UUID;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(UUID accountId) {
        super("Account with ID [%s] not found".formatted(accountId));
    }
}
