package ricardocorreia.personal.tinyledger.domain.model.exceptions;

import java.util.UUID;

public class TransferToTheSameAccountException extends IllegalArgumentException {
    public TransferToTheSameAccountException(UUID accountId) {
        super("From account and to account cannot be the same in a Transfer transaction. Account ID: [%s]".formatted(accountId));
    }
}
