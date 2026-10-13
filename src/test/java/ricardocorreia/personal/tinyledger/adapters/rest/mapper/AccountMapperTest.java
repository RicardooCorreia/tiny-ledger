package ricardocorreia.personal.tinyledger.adapters.rest.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ricardocorreia.personal.tinyledger.adapters.rest.response.AccountResponse;
import ricardocorreia.personal.tinyledger.domain.model.Account;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    private final AccountMapper subject = Mappers.getMapper(AccountMapper.class);

    @Test
    void should_map_account_to_response() {
        // given
        Account account = Instancio.create(Account.class);

        // when
        AccountResponse result = subject.map(account);

        // then
        assertThat(result)
                .isEqualTo(new AccountResponse(account.getId(), account.getName(), account.getBalance()));
    }

    @Nested
    class Coverage {

        @Test
        void should_return_null_if_input_is_null() {

            // when
            AccountResponse result = subject.map(null);

            // then
            assertThat(result)
                    .isNull();
        }
    }
}
