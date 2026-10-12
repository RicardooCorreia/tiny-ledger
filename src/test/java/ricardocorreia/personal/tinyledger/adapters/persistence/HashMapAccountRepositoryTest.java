package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class HashMapAccountRepositoryTest {

    private HashMapAccountRepository subject;

    @BeforeEach
    void setUp() {
        subject = new HashMapAccountRepository();
    }

    @Test
    void should_save_new_account_when_does_not_exist() {

        // given
        Account account = Instancio.create(Account.class);

        // when
        Account result = subject.upsert(account);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(account)
                .isEqualTo(subject.find(account.getId()).orElseThrow());
    }

    @Test
    void should_update_existing_account() {

        // given
        Account account = Instancio.create(Account.class);
        subject.upsert(account);

        Account updatedAccount = Instancio.of(Account.class)
                .set(field(Account::getId), account.getId())
                .create();

        // when
        Account result = subject.upsert(updatedAccount);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(updatedAccount)
                .isEqualTo(subject.find(account.getId()).orElseThrow());
    }

    @Test
    void should_find_existing_account() {

        // given
        Account account = Instancio.create(Account.class);
        subject.upsert(account);

        // when
        Optional<Account> result = subject.find(account.getId());

        // then
        assertThat(result)
                .isNotEmpty()
                .hasValue(account);
    }

    @Test
    void should_return_empty_optional_when_does_not_find_id() {

        // when
        Optional<Account> result = subject.find(UUID.randomUUID());

        // then
        assertThat(result)
                .isEmpty();
    }
}