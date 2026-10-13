package ricardocorreia.personal.tinyledger.domain.api;

import ricardocorreia.personal.tinyledger.domain.model.Account;

public interface CreateAccountUseCase {

    Account create(String name);
}
