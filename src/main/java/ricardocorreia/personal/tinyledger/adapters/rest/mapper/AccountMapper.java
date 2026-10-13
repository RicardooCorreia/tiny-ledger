package ricardocorreia.personal.tinyledger.adapters.rest.mapper;

import org.mapstruct.Mapper;
import ricardocorreia.personal.tinyledger.adapters.rest.response.AccountResponse;
import ricardocorreia.personal.tinyledger.domain.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse map(Account account);
}
